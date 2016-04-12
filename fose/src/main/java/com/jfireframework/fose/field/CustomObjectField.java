package com.jfireframework.fose.field;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import com.jfireframework.baseutil.collection.ObjectCollect;
import com.jfireframework.baseutil.collection.buffer.ByteBuf;
import com.jfireframework.fose.BeanSerializerFactory;
import com.jfireframework.fose.ClassNoRegister;

@SuppressWarnings("restriction")
public class CustomObjectField extends DirectObjectField
{
    public CustomObjectField(Field field)
    {
        super(field);
    }
    
    public Class<?> getRootType()
    {
        return rootType;
    }
    
    @Override
    public void getObjects(Object host, ObjectCollect collect)
    {
        if (dimension > 0)
        {
            putEachSingleInCollectFromArray(collect, unsafe.getObject(host, offset), dimension);
        }
        else
        {
            Object value = unsafe.getObject(host, offset);
            if (value != null)
            {
                BeanSerializerFactory.getSerializer(value.getClass()).getObjects(value, collect);
            }
        }
    }
    
    @Override
    public void readSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        int value = buf.readInt();
        if (value >= 0)
        {
            unsafe.putObject(host, offset, collect.get(value));
        }
        else
        {
            unsafe.putObject(host, offset, null);
        }
    }
    
    @Override
    public void writeSingle(Object host, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        buf.writeInt(collect.indexOf(unsafe.getObject(host, offset)));
    }
    
    @Override
    public void writeOneDimensionMember(Object array, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        Object[] value = (Object[]) array;
        int length = value.length;
        buf.writeInt(length);
        for (int i = 0; i < length; i++)
        {
            buf.writeInt(collect.indexOf(value[i]));
        }
    }
    
    @Override
    public Object readOneDimensionMember(int length, ByteBuf<?> buf, ObjectCollect collect, ClassNoRegister register)
    {
        Object[] array = (Object[]) Array.newInstance(rootType, length);
        for (int i = 0; i < length; i++)
        {
            Object tmp = collect.get(buf.readInt());
            array[i] = tmp;
        }
        return array;
    }
}
