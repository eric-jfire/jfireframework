package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.mvc.binder.ParamInfo;

public class BooleanBinder extends AbstractDataBinder
{
    
    public BooleanBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        String value = map.get(paramName);
        Verify.True(StringUtil.isNotBlank(value), "参数为int基本类型，页面必须要有传参，请检查传参名字是否是{}", paramName);
        return Boolean.valueOf(value);
    }
}
