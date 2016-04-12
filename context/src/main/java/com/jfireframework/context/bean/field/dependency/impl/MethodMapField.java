package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;
import sun.reflect.MethodAccessor;

/**
 * map依赖注入的field。该field使用依赖注入的bean中的约定方法的返回值作为map字段的key
 * 
 */
@SuppressWarnings("restriction")
public class MethodMapField extends AbstractDependencyField
{
    private Bean[]                dependencyBeans;
    private static final Object[] param = new Object[0];
    private MethodAccessor[]      methods;
    private String                msg;
    
    public MethodMapField(Field field, Bean[] beans, MethodAccessor[] methods)
    {
        super(field);
        this.dependencyBeans = beans;
        this.methods = methods;
        msg = StringUtil.format("属性{}.{}不能为空", field.getDeclaringClass(), field.getName());
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void inject(Object src, Map<String, Object> beanInstanceMap)
    {
        Map map = (Map) unsafe.getObject(src, offset);
        Verify.notNull(map, msg);
        Object entryValue;
        Object entryKey;
        int length = dependencyBeans.length;
        for (int i = 0; i < length; i++)
        {
            try
            {
                entryValue = dependencyBeans[i].getInstance();
                entryKey = methods[i].invoke(entryValue, param);
                map.put(entryKey, entryValue);
            }
            catch (IllegalArgumentException | InvocationTargetException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
