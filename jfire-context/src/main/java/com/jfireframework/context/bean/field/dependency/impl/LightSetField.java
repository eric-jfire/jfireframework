package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;

public class LightSetField extends AbstractDependencyField
{
    private Bean[] dependencyBeans;
    private String msg;
    
    public LightSetField(Field field, Bean... beans)
    {
        super(field);
        dependencyBeans = beans;
        msg = StringUtil.format("属性{}.{}为空,无法进行lightSet注入", field.getDeclaringClass().getName(), field.getName());
    }
    
    @SuppressWarnings({ "restriction", "unchecked", "rawtypes" })
    @Override
    public void inject(Object src, Map<String, Object> beanInstanceMap)
    {
        LightSet set = (LightSet) unsafe.getObject(src, offset);
        Verify.notNull(set, msg);
        for (Bean each : dependencyBeans)
        {
            set.add(each.getInstance(beanInstanceMap));
        }
    }
}
