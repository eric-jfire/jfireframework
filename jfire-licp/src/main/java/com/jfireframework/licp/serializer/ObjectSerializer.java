package com.jfireframework.licp.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.licp.Licp;
import com.jfireframework.licp.field.CacheField;
import com.jfireframework.licp.field.FieldFactory;
import sun.misc.Unsafe;

public class ObjectSerializer implements LicpSerializer
{
    private static final Charset           CHARSET       = Charset.forName("utf8");
    private final CacheField[]             fields;
    private static final Comparator<Field> fieldCompator = new Comparator<Field>() {
                                                             
                                                             @Override
                                                             public int compare(Field o1, Field o2)
                                                             {
                                                                 return o1.getName().compareTo(o2.getName());
                                                             }
                                                             
                                                         };
    private final Class<?>                 type;
    private final static Unsafe            unsafe        = ReflectUtil.getUnsafe();
    
    public ObjectSerializer(Class<?> type)
    {
        this.type = type;
        Field[] fields = ReflectUtil.getAllFields(type);
        List<Field> list = new LinkedList<Field>();
        for (Field each : fields)
        {
            if (Modifier.isFinal(each.getModifiers()) || Modifier.isStatic(each.getModifiers()))
            {
                continue;
            }
            list.add(each);
        }
        fields = list.toArray(new Field[list.size()]);
        Arrays.sort(fields, fieldCompator);
        CacheField[] tmp = new CacheField[fields.length];
        for (int i = 0; i < tmp.length; i++)
        {
            tmp[i] = FieldFactory.build(fields[i]);
        }
        this.fields = tmp;
    }
    
    /**
     * 如果对象为null，写入0，结束。
     * 如果对象不为null，分以下两种情况：
     * 1）如果对象类型已经注册过，则首先写入对象类型序号+1。然后开始序列化。
     * 2）如果对象类型没有注册过，首先写入1。紧接着写入类名的String的byte数组长度，然后写入byte数组内容。然后开始序列化
     */
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
        if (src == null)
        {
            buf.writeInt(Licp.EXIST);
            return;
        }
        Class<?> type = src.getClass();
        int classNo = licp.indexOf(type);
        if (classNo == 0)
        {
            buf.writeInt(1);
            byte[] nameBytes = type.getName().getBytes(CHARSET);
            buf.writeInt(nameBytes.length);
            buf.put(nameBytes);
        }
        else
        {
            buf.writeInt(classNo = 1);
        }
        for (CacheField each : fields)
        {
            each.write(src, buf, licp);
        }
    }
    
    @Override
    public Object deserialize(ByteBuf<?> buf, Licp licp)
    {
        try
        {
            Object holder = unsafe.allocateInstance(type);
            for (CacheField each : fields)
            {
                each.read(holder, buf, licp);
            }
            return holder;
        }
        catch (InstantiationException e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
}
