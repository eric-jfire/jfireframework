package com.jfireframework.fose.serializer;

import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.ClassNoRegister;
import com.jfireframework.fose.field.CacheField;
import com.jfireframework.fose.field.DirectObjectField;

/**
 * 对象序列化类
 * 
 * @author Administrator
 *         
 */
public class FieldContainerSerializer implements Serializer
{
    private CacheField[]        cacheFields;
    private DirectObjectField[] objectFields;
                                
    /*
     * (non-Javadoc)
     * 
     * @see net.lb.io.lbse.serializer.Serializer#getObjects(java.lang.Object)
     */
    @Override
    public void getObjects(Object src, ObjectCollect collect)
    {
        if (collect.add(src))
        {
            for (DirectObjectField each : objectFields)
            {
                each.getObjects(src, collect);
            }
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.lb.io.lbse.serializer.Serializer#serialize(java.lang.Object,
     * java.nio.ByteBuffer, net.lb.io.lbse.Lbse)
     */
    @Override
    public void serialize(Object src, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        for (int i = 0; i < cacheFields.length; i++)
        {
            cacheFields[i].write(src, buf, collect, register);
        }
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see net.lb.io.lbse.serializer.Serializer#deserialize(java.lang.Object,
     * java.nio.ByteBuffer, net.lb.io.lbse.Lbse)
     */
    @Override
    public void deserialize(Object target, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        for (CacheField each : cacheFields)
        {
            each.read(target, buf, collect, register);
        }
    }
    
    public void setCacheFields(CacheField[] cacheFields)
    {
        this.cacheFields = cacheFields;
    }
    
    public void setObjectFields(DirectObjectField[] objectFields)
    {
        this.objectFields = objectFields;
    }
    
}
