package com.jfireframework.licp;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.licp.serializer.LicpSerializer;
import com.jfireframework.licp.serializer.SerializerFactory;

public class Licp
{
    private ObjectCollect                                    collect      = new ObjectCollect();
    private ClassNoRegister                                  register     = new ClassNoRegister();
    public static final int                                  NULL         = 0;
    public static final int                                  EXIST        = 1;
    private static final ConcurrentHashMap<String, Class<?>> nameClassMap = new ConcurrentHashMap<String, Class<?>>();
    
    public Licp()
    {
    }
    
    public Licp(boolean cycleSupport)
    {
        if (cycleSupport)
        {
            collect = new ObjectCollect(cycleSupport);
        }
    }
    
    public void serialize(Object src, ByteBuf<?> buf)
    {
        collect.clear();
        register.clear();
        _serialize(src, buf);
    }
    
    public void register(Class<?> type)
    {
        register.register(type);
    }
    
    /**
     * 00代表为null
     * 01代表对象已经在收集器中，之后的数字代表对象在收集器中的id
     * 10代表对象不在收集器中并且对象的类型尚未注册。之后的数字代表对象类型的名称的byte数组的长度
     * 11代表对象不在收集器中并且对象的类型已经注册。之后的数字代表对象类型的注册顺序。
     * 
     * @param src
     * @param buf
     */
    public void _serialize(Object src, ByteBuf<?> buf)
    {
        if (src == null)
        {
            buf.writeInt(0);
            return;
        }
        int id = collect.put(src);
        if (id != 0)
        {
            id = ((id << 2) | 1);
            // 已经在收集器中的对象不需要序列化，只要写入序号即可
            buf.writeInt(id);
            return;
        }
        Class<?> type = src.getClass();
        int classNo = register.indexOf(type);
        if (classNo == 0)
        {
            String name = type.getName();
            int length = name.length();
            buf.writeInt(((length << 2) | 2));
            for (int i = 0; i < length; i++)
            {
                buf.writeChar(name.charAt(i));
            }
        }
        else
        {
            classNo <<= 2;
            classNo |= 3;
            buf.writeInt(classNo);
        }
        SerializerFactory.get(type).serialize(src, buf, this);
    }
    
    public void _serialize(Object src, ByteBuf<?> buf, LicpSerializer serializer)
    {
        if (src == null)
        {
            buf.writeInt(0);
            return;
        }
        int id = collect.put(src);
        if (id != 0)
        {
            id = ((id << 2) | 1);
            // 已经在收集器中的对象不需要序列化，只要写入序号即可
            buf.writeInt(id);
            return;
        }
        buf.writeInt(2);
        serializer.serialize(src, buf, this);
    }
    
    @SuppressWarnings("unchecked")
    public <T> T deserialize(ByteBuf<?> buf, Class<T> type)
    {
        collect.clear();
        register.clear();
        return (T) _deserialize(buf);
    }
    
    public Object deserialize(ByteBuf<?> buf)
    {
        collect.clear();
        register.clear();
        return _deserialize(buf);
    }
    
    public Object _deserialize(ByteBuf<?> buf)
    {
        int result = buf.readInt();
        if (result == 0)
        {
            return null;
        }
        int flag = result & 0x03;
        if (flag == 1)
        {
            result >>>= 2;
            return collect.get(result);
        }
        else if (flag == 2)
        {
            result >>>= 2;
            char[] src = new char[result];
            for (int i = 0; i < src.length; i++)
            {
                src[i] = buf.readChar();
            }
            Class<?> type = loadClass(new String(src));
            return SerializerFactory.get(type).deserialize(buf, this);
        }
        else if (flag == 3)
        {
            result >>>= 2;
            Class<?> type = loadClass(result);
            return SerializerFactory.get(type).deserialize(buf, this);
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    public Object _deserialize(ByteBuf<?> buf, LicpSerializer serializer)
    {
        int result = buf.readInt();
        if (result == 0)
        {
            return null;
        }
        int flag = result & 0x03;
        if (flag == 1)
        {
            result >>>= 2;
            return collect.get(result);
        }
        else if (flag == 2)
        {
            return serializer.deserialize(buf, this);
        }
        else
        {
            throw new UnSupportException("not here");
        }
    }
    
    public Class<?> loadClass(String name)
    {
        try
        {
            Class<?> type = nameClassMap.get(name);
            if (type == null)
            {
                type = Class.forName(name);
                nameClassMap.put(name, type);
                return type;
            }
            else
            {
                return type;
            }
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
    
    public void putObject(Object x)
    {
        collect.put(x);
    }
}
