package com.jfireframework.mvc.binder.field.extra;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.mvc.binder.field.AbstractBinderField;
import com.jfireframework.mvc.binder.node.ParamNode;
import com.jfireframework.mvc.binder.node.StringValueNode;

public class EnumField extends AbstractBinderField
{
    private Map<String, Enum<?>> instances;
    
    @SuppressWarnings("unchecked")
    public EnumField(Field field)
    {
        super(field);
        instances = (Map<String, Enum<?>>) ReflectUtil.getAllEnumInstances((Class<? extends Enum<?>>) field.getType());
        Map<String, Enum<?>> tmp = new HashMap<String, Enum<?>>();
        for (Enum<?> each : instances.values())
        {
            tmp.put(String.valueOf(each.ordinal()), each);
        }
        instances.putAll(tmp);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        String value = ((StringValueNode) node).getValue();
        Enum<?> instance = instances.get(value);
        unsafe.putObject(entity, offset, instance);
    }
    
}
