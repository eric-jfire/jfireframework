package com.jfireframework.fose;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.fose.serializer.Serializer;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class Fose
{
    private ObjectCollect         collect           = new ObjectCollect();
    private Charset               charset           = Charset.forName("utf8");
    private Unsafe                unsafe            = ReflectUtil.getUnsafe();
    private byte[]                classNameBytes    = new byte[1000];
    private ClassNoRegister       classNoRegister   = new ClassNoRegister();
    private Map<Class<?>, byte[]> classNameBytesMap = new HashMap<Class<?>, byte[]>();
    
    public void register(Class<?> type)
    {
        classNoRegister.register(type);
    }
    
    private void init()
    {
        collect.clear();
        classNoRegister.clear();
    }
    
    public ObjectCollect getCollect()
    {
        return collect;
    }
    
    public void serialize(Object src, ByteBuf<?> buf)
    {
        if (src == null)
        {
            buf.writeInt(-1);
            return;
        }
        init();
        BeanSerializerFactory.getSerializer(src.getClass()).getObjects(src, collect);
        serializeObject(buf);
    }
    
    private void serializeObject(ByteBuf<?> buf)
    {
        seAllObjectsInHead(buf);
        int objectSum = collect.getCount();
        for (int i = 0; i < objectSum; i++)
        {
            Object tmp = collect.get(i);
            Serializer fieldSerializer = BeanSerializerFactory.getSerializer(tmp.getClass());
            fieldSerializer.serialize(tmp, buf, collect, classNoRegister);
        }
    }
    
    private void seAllObjectsInHead(ByteBuf<?> buf)
    {
        int objectSum = collect.getCount();
        buf.writeInt(objectSum);
        for (int i = 0; i < objectSum; i++)
        {
            Object tmp = collect.get(i);
            Class<?> tmpClass = tmp.getClass();
            if (tmpClass.isArray())
            {
                buf.writeInt(0);
                buf.writeInt(Array.getLength(tmp));
            }
            int index = classNoRegister.getIndex(tmpClass);
            if (index >= 1)
            {
                index = -1 * index;
                buf.writeInt(index);
            }
            else
            {
                
                byte[] className = classNameBytesMap.get(tmpClass);
                if (className == null)
                {
                    className = tmpClass.getName().getBytes(charset);
                    classNameBytesMap.put(tmpClass, className);
                }
                buf.writeByteArray(className);
            }
        }
    }
    
    public Object deserialize(ByteBuf<?> buf)
    {
        try
        {
            init();
            deserializAllObjectsFromHead(buf);
            int objectSum = collect.getCount();
            if (objectSum == 0)
            {
                return null;
            }
            for (int i = 0; i < objectSum; i++)
            {
                Serializer fieldSerializer = BeanSerializerFactory.getSerializer(collect.get(i).getClass());
                fieldSerializer.deserialize(collect.get(i), buf, collect, classNoRegister);
            }
            return collect.get(0);
        }
        catch (InstantiationException e)
        {
            e.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
        
    }
    
    @SuppressWarnings("unchecked")
    public <T> T deserialize(ByteBuf<?> buf, Class<T> t)
    {
        return (T) deserialize(buf);
    }
    
    /**
     * 读取开头字段，还原出所有的类实例
     * 
     * @param src
     * @return
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    private void deserializAllObjectsFromHead(ByteBuf<?> buf) throws InstantiationException, ClassNotFoundException
    {
        init();
        int objectSum = buf.readInt();
        if (objectSum == -1)
        {
            return;
        }
        collect.ensureCapacity(objectSum);
        for (int i = 0; i < objectSum; i++)
        {
            int value = buf.readInt();
            if (value > 0)
            {
                int classNameLength = value;
                buf.get(classNameBytes, classNameLength);
                String className = new String(classNameBytes, 0, classNameLength, charset);
                Class<?> type = BeanSerializerFactory.getType(className);
                Object tmp = null;
                tmp = unsafe.allocateInstance(type);
                collect.addWithoutEnsureCapacity(tmp);
                classNoRegister.addTemporaryClass(type);
            }
            else if (value < 0)
            {
                value = -1 * value;
                collect.addWithoutEnsureCapacity(unsafe.allocateInstance(classNoRegister.getType(value)));
            }
            else
            {
                int arrayLength = buf.readInt();
                int nameLength = buf.readInt();
                if (nameLength < 0)
                {
                    Class<?> type = classNoRegister.getType(nameLength * -1);
                    Object array = Array.newInstance(type.getComponentType(), arrayLength);
                    collect.add(array);
                }
                else
                {
                    buf.get(classNameBytes, nameLength);
                    String className = new String(classNameBytes, 0, nameLength, charset);
                    Class<?> type = BeanSerializerFactory.getType(className);
                    classNoRegister.addTemporaryClass(type);
                    Object array = Array.newInstance(type.getComponentType(), arrayLength);
                    collect.add(array);
                }
            }
        }
    }
    
}
