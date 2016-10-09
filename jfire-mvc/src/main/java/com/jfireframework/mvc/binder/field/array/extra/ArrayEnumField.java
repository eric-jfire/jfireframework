package com.jfireframework.mvc.binder.field.array.extra;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.binder.field.array.AbstractArrayField;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;

public class ArrayEnumField extends AbstractArrayField
{
    private Map<String, Enum<?>> instances;
    
    @SuppressWarnings("unchecked")
    public ArrayEnumField(Field field)
    {
        super(field);
        instances = (Map<String, Enum<?>>) ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) field.getType().getComponentType());
        Map<String, Enum<?>> tmp = new HashMap<String, Enum<?>>();
        for (Enum<?> each : instances.values())
        {
            tmp.put(each.name(), each);
        }
        instances.putAll(tmp);
    }
    
    @Override
    protected Object buildByString(String str)
    {
        Enum<?> instance = instances.get(str);
        return instance;
    }
    
    @Override
    protected Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response)
    {
        String value = ((StringValueNode) node).getValue();
        Enum<?> instance = instances.get(value);
        return instance;
    }
    
}
