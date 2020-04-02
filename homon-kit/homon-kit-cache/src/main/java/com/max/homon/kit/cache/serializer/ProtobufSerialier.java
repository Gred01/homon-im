package com.max.homon.kit.cache.serializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.max.homon.core.utils.ProtostuffUtils;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
* 自定义protobuf序列化工具
 * 因为无法序列化Object，暂时弃用，后续考虑
*@Author Gred
*@Date 2020/4/2 10:30
*@version 1.0
**/
@Deprecated
public class ProtobufSerialier<T> implements RedisSerializer<T> {

    static final byte[] EMPTY_ARRAY = new byte[0];

    private final Class<T> clz;

    private ObjectMapper objectMapper = new ObjectMapper();

    public ProtobufSerialier(Class<T> clz) {
        this.clz = clz;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if(Objects.isNull(t)){
            return EMPTY_ARRAY;
        }
        return ProtostuffUtils.serialize(t);
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (this.isEmpty(bytes)) {
            return null;
        }

        return ProtostuffUtils.deserialize(bytes,clz);
    }


    private boolean isEmpty(@Nullable byte[] data) {
        return (data == null || data.length == 0);
    }

}
