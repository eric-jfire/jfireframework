package com.jfireframework.mvc.binder.field.array;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.binder.ParamInfo;

public class ArrayObjectField extends AbstractArrayField
{
    private DataBinder                                   dataBinder;
    private final ConcurrentHashMap<Integer, DataBinder> binderMap = new ConcurrentHashMap<Integer, DataBinder>();
    private final Class<?>                               fieldType;
    
    public ArrayObjectField(String prefix, Field field, Set<Class<?>> set)
    {
        super(prefix, field, set);
        fieldType = field.getType().getComponentType();
    }
    
    @Override
    protected void setFlagValue(String value, Object _array, int flag, HttpServletRequest request, Object entity, Map<String, String> map, HttpServletResponse response)
    {
        dataBinder = binderMap.get(flag);
        if (dataBinder == null)
        {
            ParamInfo info = new ParamInfo();
            info.setEntityClass(fieldType);
            info.setPrefix(matchPrefix + flag + "]");
            dataBinder = DataBinderFactory.build(info, new HashSet<Class<?>>());
            binderMap.putIfAbsent(flag, dataBinder);
        }
        if (((Object[]) _array)[flag] == null)
        {
            ((Object[]) _array)[flag] = dataBinder.binder(request, map, response);
        }
        else
        {
            // 因为对象绑定是一次性就能全部绑定完，所以如果这个位置有值了，那就意味着之前绑定过了，不在需要重复绑定
            ;
        }
    }
    
}
