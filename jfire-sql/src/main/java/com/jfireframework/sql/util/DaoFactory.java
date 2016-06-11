package com.jfireframework.sql.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.function.DAOBean;
import com.jfireframework.sql.function.impl.DAOBeanImpl;

public class DaoFactory
{
    private static Map<Class<?>, DAOBean> daoMap = new HashMap<Class<?>, DAOBean>();
    
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
    
    public static Map<Class<?>, DAOBean> getDaoBeans()
    {
        return daoMap;
    }
}
