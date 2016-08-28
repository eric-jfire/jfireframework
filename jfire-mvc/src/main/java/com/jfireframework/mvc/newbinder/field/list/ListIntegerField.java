package com.jfireframework.mvc.newbinder.field.list;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.node.ParamNode;
import com.jfireframework.mvc.newbinder.node.StringValueNode;

public class ListIntegerField extends ListField
{
    
    public ListIntegerField(Field field)
    {
        super(field);
    }
    
    @Override
    protected List<?> buildFromArray(List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        List<Integer> list = new ArrayList<Integer>();
        for (String each : values)
        {
            list.add(Integer.valueOf(each));
        }
        return list;
    }
    
    @Override
    protected List<?> buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        List<Integer> list = new ArrayList<Integer>();
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            list.add(index, Integer.valueOf(((StringValueNode) each.getValue()).getValue()));
        }
        return list;
    }
    
}
