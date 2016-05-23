package com.jfireframework.data.mongodb;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import org.bson.Document;
import org.bson.types.ObjectId;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

/**
 * 数据库的_id字段和类本身的id都是保留字段，不可以更改作他用。同时，类本身的id类型为String
 * 
 * @author linbin
 *
 * @param <T>
 */
@SuppressWarnings("restriction")
public class MongoTransferUtil<T>
{
    private TransferField[] fields;
    
    public MongoTransferUtil(Class<T> type)
    {
        List<TransferField> fields = new ArrayList<TransferField>();
        for (Field each : ReflectUtil.getAllFields(type))
        {
            TransferField field = TransferField.build(each);
            if (field != null)
            {
                fields.add(field);
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
    
    public static TransferField build(Field field)
    {
        int modi = field.getModifiers();
        if (Modifier.isStatic(modi) || Modifier.isFinal(modi) || Modifier.isTransient(modi))
        {
            return null;
        }
        Class<?> type = field.getType();
        if (type == int.class || type == Integer.class)
        {
            return new intField(field);
        }
        else if (type == long.class || type == Long.class)
        {
            return new longField(field);
        }
        else if (type == float.class || type == Float.class)
        {
            return new floatField(field);
        }
        else if (type == double.class || type == Double.class)
        {
            return new doubleField(field);
        }
        else if (type == boolean.class || type == Boolean.class)
        {
            return new booleanField(field);
        }
        else if (type == short.class || type == Short.class)
        {
            return new shortField(field);
        }
        else if (type == String.class)
        {
            if (field.getName().equals("id"))
            {
                return new idField(field);
            }
            else
            {
                return new stringField(field);
            }
        }
        else
        {
            return null;
        }
    }
    
    static class idField extends TransferField
    {
        
        public idField(Field field)
        {
            super(field);
        }
        
        @Override
        public void transfer(Document document, Object target)
        {
            unsafe.putObject(target, offset, document.getObjectId("_id").toHexString());
        }
        
        @Override
        public void from(Object target, Document document)
        {
            String value = (String) unsafe.getObject(target, offset);
            if (value != null)
            {
                document.append("_id", new ObjectId(value));
            }
        }
        
    }
    
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
                double value = unsafe.getDouble(target, offset);
                document.append(name, value);
            }
            else
            {
                Double value = (Double) unsafe.getObject(target, offset);
                document.append(name, value);
            }
        }
    }
}
