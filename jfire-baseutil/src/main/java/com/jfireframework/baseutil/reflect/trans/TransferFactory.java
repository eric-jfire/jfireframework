package com.jfireframework.baseutil.reflect.trans;

import java.util.HashMap;
import java.util.Map;

public class TransferFactory
{
    private static Map<Class<?>, Transfer> map = new HashMap<Class<?>, Transfer>();
    
    static
    {
        map.put(int.class, new IntTransfer());
        map.put(long.class, new LongTransfer());
        map.put(float.class, new FloatTransfer());
        map.put(double.class, new DoubleTransfer());
        map.put(boolean.class, new BooleanTransfer());
        map.put(Integer.class, new IntTransfer());
        map.put(Long.class, new WlongTransfer());
        map.put(Double.class, new WdoubleTransfer());
        map.put(Float.class, new WfloatTransfer());
        map.put(Boolean.class, new WbooleanTransfer());
        map.put(String.class, new StringTransfer());
    }
    
    public static Transfer get(Class<?> type)
    {
        return map.get(type);
    }
}
