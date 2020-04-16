package com.max.homon.kit.zk.listener;

import com.alibaba.fastjson.JSON;
import com.max.homon.api.zk.IServiceListener;
import com.max.homon.core.bean.zk.ServerNode;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;

public class ZKCacheEventListener implements TreeCacheListener {

    private final String watchPath;

    private final IServiceListener listener;

    public ZKCacheEventListener(String watchPath, IServiceListener listener) {
        this.watchPath = watchPath;
        this.listener = listener;
    }

    @Override
    public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {

        ChildData data = event.getData();
        if (data == null){
            return;
        }

        String dataPath = data.getPath();
        if (dataPath.startsWith(watchPath)){
            switch (event.getType()){
                case NODE_ADDED:
                    listener.onServiceAdded(dataPath, JSON.parseObject(data.getData(), ServerNode.class));
                    break;
                case NODE_REMOVED:
                    listener.onServiceRemoved(dataPath, JSON.parseObject(data.getData(), ServerNode.class));
                    break;
                case NODE_UPDATED:
                    listener.onServiceUpdated(dataPath, JSON.parseObject(data.getData(), ServerNode.class));
                    break;
                default:
            }
        }
    }
}
