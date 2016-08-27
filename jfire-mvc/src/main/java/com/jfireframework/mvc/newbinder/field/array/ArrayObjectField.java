package com.jfireframework.mvc.newbinder.field.array;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.impl.ObjectDataBinder;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class ArrayObjectField extends AbstractBinderField
{
    private final ObjectDataBinder binder;
    private final Class<?>         ckass;
    
    public ArrayObjectField(Field field)
    {
        super(field);
        ckass = field.getType().getComponentType();
        binder = new ObjectDataBinder(ckass, "");
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        TreeValueNode treeValueNode = (TreeValueNode) node;
        int max = 0;
        for (String each : treeValueNode.keySet())
        {
            int tmp = Integer.valueOf(each);
            if (max < tmp)
            {
                max = tmp;
            }
        }
        Object[] array = (Object[]) Array.newInstance(ckass, max + 1);
        for (Entry<String, ParamNode> each : treeValueNode.entrySet())
        {
            int tmp = Integer.valueOf(each.getKey());
            array[tmp] = binder.binder(request, (TreeValueNode) each.getValue(), response);
        }
        unsafe.putObject(entity, offset, array);
    }
    
}
