package com.jfireframework.mvc.newbinder.field.array;

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
    
    public AbstractArrayField(Field field)
    {
        super(field);
    }
    
    
    @Override
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity)
    {
        
        if (node instanceof ArrayNode)
        {
            ArrayNode arrayNode = (ArrayNode) node;
            unsafe.putObject(entity, offset, buildFromArray(arrayNode.getArray().size(), arrayNode.getArray()));
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
            unsafe.putObject(entity, offset, buildFromTree(max + 1, treeValueNode.entrySet()));
        }
    }
    
    protected abstract Object buildFromArray(int size, List<String> values);
    
    protected abstract Object buildFromTree(int size, Set<Entry<String, ParamNode>> set);
}
