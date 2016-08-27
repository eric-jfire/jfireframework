package com.jfireframework.mvc.newbinder.field.array;

import java.lang.reflect.Field;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.node.ArrayNode;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class ArrayWIntegerField extends AbstractBinderField
{
    
    public ArrayWIntegerField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        if (node instanceof ArrayNode)
        {
            ArrayNode arrayNode = (ArrayNode) node;
            Integer[] array = new Integer[arrayNode.getArray().size()];
            int index = 0;
            for (String each : arrayNode.getArray())
            {
                array[index] = Integer.valueOf(each);
                index += 1;
            }
            unsafe.putObject(entity, offset, array);
        }
        else if (node instanceof TreeValueNode)
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
            Integer[] array = new Integer[max + 1];
            for (Entry<String, ParamNode> each : treeValueNode.entrySet())
            {
                int tmp = Integer.valueOf(each.getKey());
                array[tmp] = Integer.valueOf(((StringValueNode) each.getValue()).getValue());
            }
            unsafe.putObject(entity, offset, array);
        }
    }
    
}
