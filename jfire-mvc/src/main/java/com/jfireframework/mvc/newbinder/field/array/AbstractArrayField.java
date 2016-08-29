package com.jfireframework.mvc.newbinder.field.array;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.node.ArrayNode;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public abstract class AbstractArrayField extends AbstractBinderField
{
    private final Class<?> ckass;
    
    public AbstractArrayField(Field field)
    {
        super(field);
        ckass = field.getType().getComponentType();
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        
        if (node instanceof ArrayNode)
        {
            ArrayNode arrayNode = (ArrayNode) node;
            unsafe.putObject(entity, offset, buildFromArray(arrayNode.getArray().size(), arrayNode.getArray(), request, response));
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
            unsafe.putObject(entity, offset, buildFromTree(max + 1, treeValueNode.entrySet(), request, response));
        }
    }
    
    protected Object buildFromArray(int size, List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        Object array = Array.newInstance(ckass, size);
        int index = 0;
        for (String each : values)
        {
            Array.set(array, index, buildByString(each));
            index += 1;
        }
        return array;
    }
    
    protected abstract Object buildByString(String str);
    
    protected Object buildFromTree(int size, Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        Object array = Array.newInstance(ckass, size);
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            Array.set(array, index, buildByNode(each.getValue(), request, response));
        }
        return array;
    }
    
    protected abstract Object buildByNode(ParamNode node, HttpServletRequest request, HttpServletResponse response);
    
}
