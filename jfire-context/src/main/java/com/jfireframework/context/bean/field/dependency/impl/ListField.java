package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;

public class ListField extends AbstractDependencyField
{
    private Bean[] dependencyBeans;
    private String msg;
    
    public ListField(Field field, Bean[] beans)
    {
        super(field);
        dependencyBeans = beans;
        msg = StringUtil.format("属性{}.{}为空,无法进行list注入", field.getDeclaringClass().getName(), field.getName());
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void inject(Object src, Map<String, Object> beanInstanceMap)
    {
        List list = (List) unsafe.getObject(src, offset);
        Verify.exist(list, msg);
        for (Bean each : dependencyBeans)
        {
            list.add(each.getInstance(beanInstanceMap));
        }
    }
    
}
