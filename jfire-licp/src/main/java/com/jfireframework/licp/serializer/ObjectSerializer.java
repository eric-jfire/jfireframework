package com.jfireframework.licp.serializer;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
            if (Modifier.isStatic(each.getModifiers()))
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
    
    @Override
    public void serialize(Object src, ByteBuf<?> buf, Licp licp)
    {
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
            // 在这个地方把对象放入。在外面放入就来不及了
            licp.putObject(holder);
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
