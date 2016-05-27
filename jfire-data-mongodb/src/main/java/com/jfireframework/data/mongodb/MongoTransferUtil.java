package com.jfireframework.data.mongodb;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bson.Document;
import org.bson.types.Binary;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.data.mongodb.TransferField.booleanField;
import com.jfireframework.data.mongodb.TransferField.bytesField;
import com.jfireframework.data.mongodb.TransferField.dateField;
import com.jfireframework.data.mongodb.TransferField.doubleField;
import com.jfireframework.data.mongodb.TransferField.floatField;
import com.jfireframework.data.mongodb.TransferField.intField;
import com.jfireframework.data.mongodb.TransferField.longField;
import com.jfireframework.data.mongodb.TransferField.shortField;
import com.jfireframework.data.mongodb.TransferField.stringField;
import sun.misc.Unsafe;

/**
 * monggo的数据转化器。可以在类和monggodb的document之间转换。
 * 注意：id字段必须为String。
 * 
 * @author linbin
 *
 * @param <T>
 */
@SuppressWarnings("restriction")
public class MongoTransferUtil<T>
{
    private final TransferField[]                      fields;
    private static final Map<Class<?>, Constructor<?>> fieldMap = new HashMap<Class<?>, Constructor<?>>();
    
    static
    {
        try
        {
            fieldMap.put(int.class, intField.class.getConstructor(Field.class));
            fieldMap.put(Integer.class, intField.class.getConstructor(Field.class));
            fieldMap.put(boolean.class, booleanField.class.getConstructor(Field.class));
            fieldMap.put(Boolean.class, booleanField.class.getConstructor(Field.class));
            fieldMap.put(long.class, longField.class.getConstructor(Field.class));
            fieldMap.put(Long.class, longField.class.getConstructor(Field.class));
            fieldMap.put(float.class, floatField.class.getConstructor(Field.class));
            fieldMap.put(Float.class, floatField.class.getConstructor(Field.class));
            fieldMap.put(double.class, doubleField.class.getConstructor(Field.class));
            fieldMap.put(Double.class, doubleField.class.getConstructor(Field.class));
            fieldMap.put(short.class, shortField.class.getConstructor(Field.class));
            fieldMap.put(Short.class, shortField.class.getConstructor(Field.class));
            fieldMap.put(byte[].class, bytesField.class.getConstructor(Field.class));
            fieldMap.put(Date.class, dateField.class.getConstructor(Field.class));
            fieldMap.put(String.class, stringField.class.getConstructor(Field.class));
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public MongoTransferUtil(Class<T> type)
    {
        List<TransferField> fields = new ArrayList<TransferField>();
        for (Field each : ReflectUtil.getAllFields(type))
        {
            int modi = each.getModifiers();
            if (Modifier.isStatic(modi) || Modifier.isFinal(modi) || each.isAnnotationPresent(MongoIgnore.class))
            {
                continue;
            }
            Class<?> fieldType = each.getType();
            Constructor<TransferField> constructor = (Constructor<TransferField>) fieldMap.get(fieldType);
            if (constructor != null)
            {
                try
                {
                    fields.add((TransferField) constructor.newInstance(each));
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
        this.fields = fields.toArray(new TransferField[0]);
    }
    
    public Document from(T target, Document document)
    {
        for (TransferField each : fields)
        {
            each.from(target, document);
        }
        return document;
    }
    
    public T transfer(Document document, T target)
    {
        for (TransferField each : fields)
        {
            each.transfer(document, target);
        }
        return target;
    }
}

@SuppressWarnings("restriction")
abstract class TransferField
{
    private static Unsafe unsafe = ReflectUtil.getUnsafe();
    protected long        offset;
    protected String      name;
    protected boolean     primitive;
    
    public TransferField(Field field)
    {
        offset = unsafe.objectFieldOffset(field);
        name = field.getName();
        primitive = field.getType().isPrimitive();
    }
    
    public abstract void transfer(Document document, Object target);
    
    public abstract void from(Object target, Document document);
    
    static class stringField extends TransferField
    {
        public stringField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            String value = document.getString(name);
            unsafe.putObject(target, offset, value);
        }
        
        @Override
        public void from(Object target, Document document)
        {
            String value = (String) unsafe.getObject(target, offset);
            document.append(name, value);
        }
        
    }
    
    static class intField extends TransferField
    {
        
        public intField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Integer value = document.getInteger(name);
            if (value == null)
            {
                return;
            }
            if (primitive)
            {
                unsafe.putInt(target, offset, value.intValue());
            }
            else
            {
                unsafe.putObject(target, offset, value);
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            if (primitive)
            {
                int value = unsafe.getInt(target, offset);
                document.append(name, value);
            }
            else
            {
                Integer value = (Integer) unsafe.getObject(target, offset);
                document.append(name, value);
            }
        }
    }
    
    static class booleanField extends TransferField
    {
        public booleanField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Boolean value = document.getBoolean(name);
            if (value == null)
            {
                return;
            }
            if (primitive)
            {
                unsafe.putBoolean(target, offset, value.booleanValue());
            }
            else
            {
                unsafe.putObject(target, offset, value);
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            if (primitive)
            {
                boolean value = unsafe.getBoolean(target, offset);
                document.append(name, value);
            }
            else
            {
                Boolean value = (Boolean) unsafe.getObject(target, offset);
                document.append(name, value);
            }
        }
    }
    
    static class longField extends TransferField
    {
        public longField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Long value = document.getLong(name);
            if (value == null)
            {
                return;
            }
            if (primitive)
            {
                unsafe.putLong(target, offset, value.longValue());
            }
            else
            {
                unsafe.putObject(target, offset, value);
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            if (primitive)
            {
                long value = unsafe.getLong(target, offset);
                document.append(name, value);
            }
            else
            {
                Long value = (Long) unsafe.getObject(target, offset);
                document.append(name, value);
            }
        }
    }
    
    static class shortField extends TransferField
    {
        public shortField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Integer value = document.getInteger(name);
            if (value == null)
            {
                return;
            }
            if (primitive)
            {
                unsafe.putShort(target, offset, value.shortValue());
            }
            else
            {
                unsafe.putObject(target, offset, Short.valueOf(value.shortValue()));
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            if (primitive)
            {
                short value = unsafe.getShort(target, offset);
                document.append(name, value);
            }
            else
            {
                Short value = (Short) unsafe.getObject(target, offset);
                document.append(name, value);
            }
        }
    }
    
    static class floatField extends TransferField
    {
        public floatField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Double value = document.getDouble(name);
            if (value == null)
            {
                return;
            }
            if (primitive)
            {
                unsafe.putFloat(target, offset, value.floatValue());
            }
            else
            {
                unsafe.putObject(target, offset, Float.valueOf(value.floatValue()));
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            if (primitive)
            {
                float value = unsafe.getFloat(target, offset);
                document.append(name, value);
            }
            else
            {
                Float value = (Float) unsafe.getObject(target, offset);
                document.append(name, value);
            }
        }
    }
    
    static class bytesField extends TransferField
    {
        
        public bytesField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Binary value = (Binary) document.get(name);
            if (value != null)
            {
                unsafe.putObject(target, offset, value.getData());
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            byte[] src = (byte[]) unsafe.getObject(target, offset);
            document.append(name, src);
        }
        
    }
    
    static class doubleField extends TransferField
    {
        public doubleField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            Double value = document.getDouble(name);
            if (value == null)
            {
                return;
            }
            if (primitive)
            {
                unsafe.putDouble(target, offset, value.doubleValue());
            }
            else
            {
                unsafe.putObject(target, offset, value);
            }
        }
        
        @Override
        public void from(Object target, Document document)
        {
            if (primitive)
            {
                document.append(name, unsafe.getDouble(target, offset));
            }
            else
            {
                document.append(name, unsafe.getObject(target, offset));
            }
        }
    }
    
    static class dateField extends TransferField
    {
        
        public dateField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            unsafe.putObject(target, offset, document.getDate(name));
        }
        
        @Override
        public void from(Object target, Document document)
        {
            document.append(name, unsafe.getObject(target, offset));
        }
        
    }
}
