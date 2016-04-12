package com.jfireframework.sql.util;

import java.lang.reflect.Field;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.field.MapField;
import com.jfireframework.sql.field.impl.BooleanField;
import com.jfireframework.sql.field.impl.CalendarField;
import com.jfireframework.sql.field.impl.DateField;
import com.jfireframework.sql.field.impl.DoubleField;
import com.jfireframework.sql.field.impl.FloatField;
import com.jfireframework.sql.field.impl.IntField;
import com.jfireframework.sql.field.impl.IntegerField;
import com.jfireframework.sql.field.impl.LongField;
import com.jfireframework.sql.field.impl.StringField;
import com.jfireframework.sql.field.impl.TimeField;
import com.jfireframework.sql.field.impl.TimestampField;
import com.jfireframework.sql.field.impl.WBooleanField;
import com.jfireframework.sql.field.impl.WDoubleField;
import com.jfireframework.sql.field.impl.WFloatField;
import com.jfireframework.sql.field.impl.WLongField;
import com.jfireframework.sql.function.DAOBean;
import com.jfireframework.sql.function.impl.DAOBeanImpl;

public class DaoFactory
{
    private static Map<Class<?>, DAOBean> daoMap = new HashMap<>();
    
    public static DAOBean getDaoBean(Class<?> entityClass)
    {
        return daoMap.get(entityClass);
    }
    
    public static void buildDaoBean(Set<String> set, ClassLoader classLoader)
    {
        for (String each : set)
        {
            try
            {
                Class<?> ckass;
                if (classLoader == null)
                {
                    ckass = Class.forName(each);
                }
                else
                {
                    ckass = classLoader.loadClass(each);
                }
                if (ckass.isAnnotationPresent(TableEntity.class))
                {
                    if (hasIdField(ckass))
                    {
                        daoMap.put(ckass, new DAOBeanImpl(ckass));
                    }
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
        }
        
    }
    
    private static boolean hasIdField(Class<?> ckass)
    {
        Field[] fields = ReflectUtil.getAllFields(ckass);
        for (Field each : fields)
        {
            if (each.isAnnotationPresent(Id.class))
            {
                return true;
            }
        }
        return false;
    }
    
    public static MapField buildMapField(Field field)
    {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(Integer.class))
        {
            return new IntegerField(field);
        }
        else if (fieldType.equals(String.class))
        {
            return new StringField(field);
        }
        else if (fieldType.equals(Float.class))
        {
            return new WFloatField(field);
        }
        else if (fieldType.equals(Long.class))
        {
            return new WLongField(field);
        }
        else if (fieldType.equals(Boolean.class))
        {
            return new WBooleanField(field);
        }
        else if (fieldType.equals(Double.class))
        {
            return new WDoubleField(field);
        }
        else if (fieldType.equals(int.class))
        {
            return new IntField(field);
        }
        else if (fieldType.equals(long.class))
        {
            return new LongField(field);
        }
        else if (fieldType.equals(float.class))
        {
            return new FloatField(field);
        }
        else if (fieldType.equals(double.class))
        {
            return new DoubleField(field);
        }
        else if (fieldType.equals(boolean.class))
        {
            return new BooleanField(field);
        }
        else if (fieldType.equals(Date.class))
        {
            return new DateField(field);
        }
        else if (fieldType.equals(java.util.Date.class))
        {
            return new DateField(field);
        }
        else if (fieldType.equals(Time.class))
        {
            return new TimeField(field);
        }
        else if (fieldType.equals(Timestamp.class))
        {
            return new TimestampField(field);
        }
        else if (fieldType.equals(Calendar.class))
        {
            return new CalendarField(field);
        }
        else
        {
            Verify.error("属性{}.{}的类型尚未支持", field.getDeclaringClass(), field.getName());
            return null;
        }
    }
    
    public static Map<Class<?>, DAOBean> getDaoBeans()
    {
        return daoMap;
    }
}
