package com.max.homon.core.bean.zk;

import com.max.homon.api.zk.IServiceNode;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;
import java.util.UUID;

@Data
@Accessors(chain = true)
public class ServerNode implements IServiceNode {

    private String host;

    private int port;

    private Map<String, Object> attrs;

    private transient String name;

    private transient String nodeId;

    private transient boolean persistent;

    @Override
    public String serviceName() {
        return name;
    }

    @Override
    public String nodeId() {
        if (nodeId == null) {
            nodeId = UUID.randomUUID().toString();
        }
        return nodeId;
    }

}
