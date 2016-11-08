package com.jfireframework.sql.resultsettransfer;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.IdentityHashMap;

public class TransferContext
{
    private IdentityHashMap<Class<?>, ResultSetTransfer<?>> transferMap = new IdentityHashMap<Class<?>, ResultSetTransfer<?>>();
    
    public void add(Class<?> type, boolean resultFieldCache)
    {
        if (transferMap.get(type) == null)
        {
            transferMap.put(type, build(type, resultFieldCache));
        }
    }
    
    public ResultSetTransfer<?> get(Class<?> type)
    {
        return transferMap.get(type);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private ResultSetTransfer<?> build(Class<?> type, boolean resultFieldCache)
    {
        if (type == Integer.class || type == int.class)
        {
            return new IntegerTransfer();
        }
        else if (type == Short.class || type == short.class)
        {
            return new ShortTransfer();
        }
        else if (type == Long.class || type == long.class)
        {
            return new LongTransfer();
        }
        else if (type == Float.class || type == float.class)
        {
            return new FloatTransfer();
        }
        else if (type == Double.class || type == double.class)
        {
            return new DoubleTransfer();
        }
        else if (type == Boolean.class || type == boolean.class)
        {
            return new BooleanTransfer();
        }
        else if (type == String.class)
        {
            return new StringTransfer();
        }
        else if (type == Date.class)
        {
            return new SqlDateTransfer();
        }
        else if (type == java.util.Date.class)
        {
            return new UtilDateTransfer();
        }
        else if (type == Time.class)
        {
            return new TimeTransfer();
        }
        else if (type == Timestamp.class)
        {
            return new TimeStampTransfer();
        }
        else if (Enum.class.isAssignableFrom(type))
        {
            return new EnumTransfer(type);
        }
        else
        {
            return new BeanTransfer(type, resultFieldCache);
        }
    }
}
