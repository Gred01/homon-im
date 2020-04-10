package com.max.homon.kit.zk.service;

import cn.hutool.core.lang.Assert;
import com.max.homon.api.serivce.IListener;
import com.max.homon.core.base.AbstractService;
import com.max.homon.core.constant.Common;
import com.max.homon.core.exception.BizException;
import com.max.homon.kit.zk.config.ZKConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.ACLProvider;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
public final class ZKClient extends AbstractService {

    private CuratorFramework client;
    private TreeCache cache;
    private ZKConfig config;

    private Map<String, String> ephemeralNodes = new LinkedHashMap<>(4);
    private Map<String, String> ephemeralSequentialNodes = new LinkedHashMap<>(1);

    public ZKClient(ZKConfig config){
        this.config = config;
    }

    @Override
    public void init() {
        super.init();
        try{
            client = initZKConfig().build();
        }catch (Exception e){
            log.error("init zk config failure,the ex is [{}]",e);
        }
    }

    @Override
    protected void doStart(IListener listener) throws Throwable {
        //启动client
        client.start();
        Assert.state(client.blockUntilConnected(1, TimeUnit.MINUTES),"start zk failure...");

        //初始缓存
        this.initLocalCache(config.getWatchPath());

        //注册监听事件
        this.registerListener();
        listener.onSuccess(config.getHosts());
    }


    /**
     * 初始化zk配置
     *@Author Gred
     *@Date 2020/4/10 14:55
     *@Param
     **/
    private CuratorFrameworkFactory.Builder initZKConfig() throws UnsupportedEncodingException {

        log.info("init zk config start");
        CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory
                .builder()
                .connectString(config.getHosts())
                .retryPolicy(new ExponentialBackoffRetry(config.getBaseSleepTimeMs(), config.getMaxRetries(), config.getMaxSleepMs()))
                .namespace(config.getNamespace());

        if (config.getConnectionTimeout() > 0) {
            builder.connectionTimeoutMs(config.getConnectionTimeout());
        }
        if (config.getSessionTimeout() > 0) {
            builder.sessionTimeoutMs(config.getSessionTimeout());
        }

        if (config.getDigest() != null) {
            /*
             * scheme对应于采用哪种方案来进行权限管理，zookeeper实现了一个pluggable的ACL方案，可以通过扩展scheme，来扩展ACL的机制。
             * zookeeper缺省支持下面几种scheme:
             *
             * world: 默认方式，相当于全世界都能访问; 它下面只有一个id, 叫anyone, world:anyone代表任何人，zookeeper中对所有人有权限的结点就是属于world:anyone的
             * auth: 代表已经认证通过的用户(cli中可以通过addauth digest user:pwd 来添加当前上下文中的授权用户); 它不需要id, 只要是通过authentication的user都有权限（zookeeper支持通过kerberos来进行authencation, 也支持username/password形式的authentication)
             * digest: 即用户名:密码这种方式认证，这也是业务系统中最常用的;它对应的id为username:BASE64(SHA1(password))，它需要先通过username:password形式的authentication
             * ip: 使用Ip地址认证;它对应的id为客户机的IP地址，设置的时候可以设置一个ip段，比如ip:192.168.1.0/16, 表示匹配前16个bit的IP段
             * super: 在这种scheme情况下，对应的id拥有超级权限，可以做任何事情(cdrwa)
             */
            builder.authorization("digest", config.getDigest().getBytes("UTF-8"));
            builder.aclProvider(new ACLProvider() {
                @Override
                public List<ACL> getDefaultAcl() {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }

                @Override
                public List<ACL> getAclForPath(final String path) {
                    return ZooDefs.Ids.CREATOR_ALL_ACL;
                }
            });
        }

        log.info("init zk config end,config={}",config.toString());
        return builder;
    }


    // 本地缓存
    public void initLocalCache(String watchRootPath) throws Exception {
        cache = new TreeCache(client, watchRootPath);
        cache.start();
    }

    public void registerListener(){
        client.getConnectionStateListenable().addListener((cli,newState)->{
            if (newState == ConnectionState.RECONNECTED){
                log.info("zk reconnect success");
                //重新注册临时节点
                ephemeralNodes.forEach(this::regEphemeralNodes);
                //重新注册临时顺序节点
                ephemeralSequentialNodes.forEach(this::regEphemeralSeqNodes);
            }
        });
    }

    /**
     * 获取数据,先从本地获取，本地找不到，从远程获取
     *
     * @param key
     * @return
     */
    public String get(final String key) {
        if (null == cache) {
            return null;
        }
        ChildData data = cache.getCurrentData(key);
        if (null != data) {
            return null == data.getData() ? null : new String(data.getData(), Common.UTF_8);
        }
        return getFromRemote(key);
    }

    /**
     * 从远程获取数据
     *
     * @param key
     * @return
     */
    public String getFromRemote(final String key) {
        if (isExisted(key)) {
            try {
                return new String(client.getData().forPath(key), Common.UTF_8);
            } catch (Exception ex) {
                log.error("getFromRemote:{}", key, ex);
            }
        }
        return null;
    }

    /**
     * 获取子节点
     *
     * @param key
     * @return
     */
    public List<String> getChildrenKeys(final String key) {
        try {
            if (!isExisted(key)) {
                return Collections.emptyList();
            }
            List<String> result = client.getChildren().forPath(key);
            result.sort(Comparator.reverseOrder());
            return result;
        } catch (Exception ex) {
            log.error("getChildrenKeys:{}", key, ex);
            return Collections.emptyList();
        }
    }

    /**
     * 判断路径是否存在
     *
     * @param key
     * @return
     */
    public boolean isExisted(final String key) {
        try {
            return null != client.checkExists().forPath(key);
        } catch (Exception ex) {
            log.error("isExisted:{}", key, ex);
            return false;
        }
    }

    /**
     * 持久化数据
     *
     * @param key
     * @param value
     */
    public void registerPersist(final String key, final String value) {
        try {
            if (isExisted(key)) {
                update(key, value);
            } else {
                client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(key, value.getBytes());
            }
        } catch (Exception ex) {
            log.error("persist:{},{}", key, value, ex);
            throw new BizException(ex);
        }
    }

    /**
     * 更新数据
     *
     * @param key
     * @param value
     */
    public void update(final String key, final String value) {
        try {
            client.inTransaction().check().forPath(key).and().setData().forPath(key, value.getBytes(Common.UTF_8)).and().commit();
        } catch (Exception ex) {
            log.error("update:{},{}", key, value, ex);
            throw new BizException(ex);
        }
    }

    /*** 重新注册临时节点 ***/
    private void reRegEphemeralNodes(final String key,final String value){
        regEphemeralNodes(key, value,false);
    }

    /*** 注册临时节点 ***/
    private void regEphemeralNodes(final String key,final String value){
        regEphemeralNodes(key, value,true);
    }

    /*** 注册临时节点 ***/
    private void regEphemeralNodes(final String key,final String value,final boolean isLocalCache){
        try {
            if (isExisted(key)) {
                client.delete().deletingChildrenIfNeeded().forPath(key);
            }
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL)
                    .forPath(key, value.getBytes(Common.UTF_8));
            if (isLocalCache) {
                ephemeralNodes.put(key, value);
            }
        } catch (Exception ex) {
            log.error("persistEphemeral:{},{}", key, value, ex);
            throw new BizException(ex);
        }
    }

    /*** 重新注册临时顺序节点 ***/
    private void reRegEphemeralSeqNodes(final String key,final String value){
        regEphemeralNodes(key, value,false);
    }

    /*** 注册临时顺序节点 ***/
    private void regEphemeralSeqNodes(final String key,final String value){
        regEphemeralNodes(key, value,true);
    }

    /*** 注册临时顺序节点 ***/
    private void regEphemeralSeqNodes(final String key,final String value,final boolean isLocalCache){
        try {
            client.create().creatingParentsIfNeeded()
                    .withMode(CreateMode.EPHEMERAL_SEQUENTIAL)
                    .forPath(key, value.getBytes());
            if (isLocalCache) {
                ephemeralSequentialNodes.put(key, value);
            }
        } catch (Exception ex) {
            log.error("persistEphemeralSequential:{},{}", key, value, ex);
            throw new BizException(ex);
        }
    }


    /**
     * 删除数据
     *
     * @param key
     */
    public void remove(final String key) {
        try {
            client.delete().deletingChildrenIfNeeded().forPath(key);
        } catch (Exception ex) {
            log.error("removeAndClose:{}", key, ex);
            throw new BizException(ex);
        }
    }

    public CuratorFramework getClient() {
        return client;
    }

    public void setClient(CuratorFramework client) {
        this.client = client;
    }

    public ZKConfig getConfig() {
        return config;
    }

    public void setConfig(ZKConfig config) {
        this.config = config;
    }
}
