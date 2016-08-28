package com.jfireframework.mvc.newbinder.field.list;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class ListLongField extends ListField
{
    
    public ListLongField(Field field)
    {
        super(field);
    }
    
    @Override
    protected List<?> buildFromArray(List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        List<Long> list = new ArrayList<Long>();
        for (String each : values)
        {
            list.add(Long.valueOf(each));
        }
        return list;
    }
    
    @Override
    protected List<?> buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        List<Long> list = new ArrayList<Long>();
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            list.add(index, Long.valueOf(((StringValueNode) each.getValue()).getValue()));
        }
        return list;
    }
}
