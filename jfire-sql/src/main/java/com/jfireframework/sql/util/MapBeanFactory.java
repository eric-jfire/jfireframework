package com.jfireframework.sql.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.function.MapBean;
import com.jfireframework.sql.function.impl.MapBeanImpl;
import com.jfireframework.sql.metadata.MetaData;

public class MapBeanFactory
{
    private static Map<Class<?>, MapBeanImpl<?>> mapBeans    = new HashMap<>();
    private static Map<String, MetaData>         metaDataMap = new HashMap<>();
                                                             
    /**
     * 获取对应类型的MapBean
     * 
     * @param entityClass
     * @return
     */
    @SuppressWarnings({ "unchecked" })
    public static <T> MapBean<T> getBean(Class<T> entityClass)
    {
        return (MapBeanImpl<T>) mapBeans.get(entityClass);
    }
    
    public static void build(Set<String> set, ClassLoader classLoader)
    {
        for (String each : set)
        {
            Class<?> ckass;
            try
            {
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
                    metaDataMap.put(ckass.getSimpleName(), new MetaData(ckass));
                    mapBeans.put(ckass, new MapBeanImpl<>(ckass));
                }
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException(e);
            }
            
        }
    }
    
    public static MetaData getMetaData(String name)
    {
        return metaDataMap.get(name);
    }
    
}
