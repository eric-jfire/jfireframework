package com.jfireframework.licp.serializer;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.licp.Licp;

public interface Serializer
{
    
    /**
     * 将对象src序列化到cache中。
     * 
     * @param src
     * @param cache
     * @param collect
     * @param register TODO
     */
    public void serialize(Object src, ByteBuf<?> buf, Licp licp);
    
    /**
     * 反序列化二进制字节到对象中去
     * 
     * @param target
     * @param cache
     * @param collect
     * @param register TODO
     */
    public Object deserialize(ByteBuf<?> buf, Licp licp);
    
}
