package com.jfireframework.licp;

import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.licp.serializer.SerializerFactory;
import com.jfireframework.licp.util.ClassNoWriter;

public class Licp
{
    private ObjectCollect   collect  = new ObjectCollect();
    private ClassNoRegister register = new ClassNoRegister();
    public static final int NULL     = 0;
    public static final int EXIST    = 1;
    
    public Licp(boolean cycleSupport)
    {
        if (cycleSupport)
        {
            collect = new ObjectCollect();
        }
    }
    
    public void serialize(Object src, ByteBuf<?> buf)
    {
        if (src == null)
        {
            buf.writeInt(Licp.NULL);
            return;
        }
        ClassNoWriter.writeClassNo(src.getClass(), buf, this);
        SerializerFactory.get(src.getClass()).serialize(src, buf, this);
    }
    
    public Object deserialize(ByteBuf<?> buf)
    {
        int classNo = buf.readInt();
        if (classNo == NULL)
        {
            return null;
        }
        Class<?> type = ClassNoWriter.readClassNo(buf, classNo, this);
        return SerializerFactory.get(type).deserialize(buf, this);
    }
    
    public int addClassNo(Class<?> type)
    {
        return register.registerTemporary(type);
    }
    
    public Class<?> loadClass(String name)
    {
        try
        {
            return Class.forName(name);
        }
        catch (ClassNotFoundException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public Class<?> loadClass(int classNo)
    {
        return register.getType(classNo);
    }
    
    public int indexOf(Class<?> type)
    {
        return register.indexOf(type);
    }
}
