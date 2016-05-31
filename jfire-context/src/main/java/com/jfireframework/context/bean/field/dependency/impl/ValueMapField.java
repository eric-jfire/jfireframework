package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;

/**
 * Map注入。该map注入由配置文件定义。是key和bean都在配置文件中定义完毕的
 * 
 * @author eric(eric@jfire.cn)
 * 
 */
public class ValueMapField extends AbstractDependencyField
{
    private Bean[]   dependencyBeans;
    private Object[] keys;
    private String   msg;
    private int      length;
    
    public ValueMapField(Field field, Bean[] beans, Object[] keys)
    {
        super(field);
        this.dependencyBeans = beans;
        this.keys = keys;
        length = keys.length;
        msg = StringUtil.format("属性{}.{}不能为空", field.getDeclaringClass(), field.getName());
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void inject(Object src, Map<String, Object> beanInstanceMap)
    {
        Map map = (Map) unsafe.getObject(src, offset);
        Verify.notNull(map, msg);
        for (int i = 0; i < length; i++)
        {
            try
            {
                map.put(keys[i], dependencyBeans[i].getInstance());
            }
            catch (IllegalArgumentException e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
