package com.jfireframework.mvc.newbinder.field.list;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.field.AbstractBinderField;
import com.jfireframework.mvc.newbinder.node.ArrayNode;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public abstract class ListField extends AbstractBinderField
{
    
    public ListField(Field field)
    {
        super(field);
    }
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        if (node instanceof ArrayNode)
        {
            ArrayNode arrayNode = (ArrayNode) node;
            unsafe.putObject(entity, offset, buildFromArray(arrayNode.getArray(), request, response));
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
            unsafe.putObject(entity, offset, buildFromTree(treeValueNode.entrySet(), request, response));
        }
    }
    
    protected abstract List<?> buildFromArray(List<String> values, HttpServletRequest request, HttpServletResponse response);
    
    protected abstract List<?> buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response);
}
