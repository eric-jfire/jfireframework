package com.jfireframework.mvc.binder.impl;

import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.reflect.trans.Transfer;
import com.jfireframework.baseutil.reflect.trans.TransferFactory;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.mvc.binder.ParamInfo;

public class BaseBinder extends AbstractDataBinder
{
    private final Transfer transfer;
    private final boolean  primitive;
    
    public BaseBinder(ParamInfo info, Set<Class<?>> cycleSet)
    {
        super(info, cycleSet);
        Class<?> type = (Class<?>) info.getEntityClass();
        primitive = type.isPrimitive();
        transfer = TransferFactory.get(type);
    }
    
    @Override
    public Object binder(HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        String value = map.get(paramName);
        if (primitive)
        {
            Verify.True(StringUtil.isNotBlank(value), "参数为基本类型，页面必须要有传参，请检查传参名字是否是{}", paramName);
        }
        return transfer.trans(value);
    }
}
