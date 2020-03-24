package com.max.homon.kit.netty.servie.message;

import com.max.homon.core.enums.DispatchPolicy;
import com.max.homon.kit.netty.annotation.CmdHandler;
import com.max.homon.kit.netty.api.connection.IConnection;
import com.max.homon.kit.netty.api.message.IMessageDispatcher;
import com.max.homon.kit.netty.api.message.IMessageHandler;
import com.max.homon.kit.netty.protocol.Command;
import com.max.homon.kit.netty.protocol.Packet;
import com.max.homon.kit.netty.utils.SpringContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;


/**
* 消息分发逻辑处理
*@Author Gred
*@Date 2020/3/16 23:01
*@version 1.0
**/
@Slf4j
@Component
@DependsOn("springContextHolder")
public final class MessageDispatcher implements IMessageDispatcher {

    /*** 注册的处理类 ***/
    private Map<Command, IMessageHandler> handlers;

    private final DispatchPolicy unsupportedPolicy;

    public MessageDispatcher() {
        unsupportedPolicy = DispatchPolicy.IGNORE;
    }

    @PostConstruct
    public void init(){
        Map<String,Object> temps = SpringContextHolder.getApplicationContext().getBeansWithAnnotation(CmdHandler.class);
        if (CollectionUtils.isEmpty(temps)){
            log.debug("[当前未注册任何处理类]");
            return;
        }
        handlers = new LinkedHashMap<>(8);
        temps.forEach((key,value)->{
            CmdHandler cmdHandler =
                    SpringContextHolder.getApplicationContext().findAnnotationOnBean(key, CmdHandler.class);
            handlers.put(cmdHandler.cmd(), (IMessageHandler) value);
        });
    }

    @Override
    public void onDispatcher(Packet packet, IConnection connection) {
        IMessageHandler handler = handlers.get(Command.toCMD(packet.cmd));
        if (handler != null){
            try {
                handler.handle(packet, connection);
            } catch (Throwable throwable) {
                log.error("dispatch message ex, packet={}, connect={}, body={}, error={}"
                        , packet, connection, Arrays.toString(packet.body), throwable.getMessage());
                //todo 关闭链接并推送错误消息
            }
        }else{
            if (unsupportedPolicy != DispatchPolicy.IGNORE) {
                log.error("dispatch message failure, cmd={} unsupported, packet={}, connect={}, body={}"
                        , Command.toCMD(packet.cmd), packet, connection);
                if (unsupportedPolicy ==  DispatchPolicy.REJECT) {
                    //todo 关闭链接并推送错误消息
                }
            }
        }
    }

    public void register(Command command, IMessageHandler handler) {

        if (handlers.containsKey(command)){
            return;
        }
        handlers.put(command, handler);
    }

    /**
    * 注册
    *@Author Gred
    *@Date 2020/3/16 23:11
    *@version 1.0
    **/
    public void register(Command command, Supplier<IMessageHandler> handlerSupplier) {
        this.register(command, handlerSupplier, true);
    }

    public void register(Command cmd, Supplier<IMessageHandler> handlerSupplier,boolean enabled){
        if (enabled){
            this.register(cmd,handlerSupplier.get());
        }
    }

    public IMessageHandler unRegister(Command command) {
        return handlers.remove(command);
    }


}
