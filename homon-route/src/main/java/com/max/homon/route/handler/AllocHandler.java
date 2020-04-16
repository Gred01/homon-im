package com.max.homon.route.handler;

import com.max.homon.api.zk.IServiceListener;
import com.max.homon.api.zk.IServiceNode;
import com.max.homon.core.bean.zk.CompareNode;
import com.max.homon.core.enums.ZKPaths;
import com.max.homon.kit.netty.servie.task.WheelTimer;
import com.max.homon.kit.zk.service.ZKServiceRegistryAndDiscovery;
import io.netty.util.Timeout;
import io.netty.util.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
* 获取分配的服务节点
*@Author Gred
*@Date 2020/4/12 22:34
*@version 1.0
**/
@Slf4j
@Component
public class AllocHandler {

    @Autowired(required = false)
    private ZKServiceRegistryAndDiscovery registryAndDiscovery;
    @Autowired
    private ISortHandler sortHandler;

    private List<CompareNode> serviceNodes;

    @PostConstruct
    private void init(){
        //先刷新节点数据
        refresh();

        //监听节点变化
        registryAndDiscovery.subscribe(ZKPaths.CONN_SERVER.getPath(),new RefreshListener());

        //定时拉取节点信息
        WheelTimer.I.get().newTimeout(new RefreshTask(),1, TimeUnit.MINUTES);
    }

    public List<CompareNode> getServiceNodes() {
        return serviceNodes;
    }

    /**
    * 刷新节点信息
    *@Author Gred
    *@Date 2020/4/12 23:30
    *@version 1.0
    **/
    private void refresh(){
        //1.从缓存中拿取可用的长链接服务器节点
        List<IServiceNode> nodes = registryAndDiscovery.lookup(ZKPaths.CONN_SERVER.getPath());
        //2.对serverNodes可以按某种规则排序,以便实现负载均衡,比如:随机,轮询,链接数量等
        serviceNodes = sortHandler.sorted(nodes);
    }

    /**
    * 节点信息监听器
    *@Author Gred
    *@Date 2020/4/12 23:27
    *@version 1.0
    **/
    public class RefreshListener implements IServiceListener {

        @Override
        public void onServiceAdded(String path, IServiceNode node) {
            refresh();
        }

        @Override
        public void onServiceUpdated(String path, IServiceNode node) {
            refresh();
        }

        @Override
        public void onServiceRemoved(String path, IServiceNode node) {
            refresh();
        }
    }

    /**
    * 定时刷新任务
    *@Author Gred
    *@Date 2020/4/12 23:27
    *@version 1.0
    **/
    public class RefreshTask implements TimerTask {

        @Override
        public void run(Timeout timeout) throws Exception {

            try {
                refresh();
            } catch (Exception e){
                log.error("[负载均衡节点][刷新节点信息失败][error={}]",e);
            }

            timeout.timer().newTimeout(this,1,TimeUnit.MINUTES);
        }
    }

}
