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

public class ListCharacterField extends ListField
{
    
    public ListCharacterField(Field field)
    {
        super(field);
    }
    
    @Override
    protected List<?> buildFromArray(List<String> values, HttpServletRequest request, HttpServletResponse response)
    {
        List<Character> list = new ArrayList<Character>();
        for (String each : values)
        {
            list.add(each.charAt(0));
        }
        return list;
    }
    
    @Override
    protected List<?> buildFromTree(Set<Entry<String, ParamNode>> set, HttpServletRequest request, HttpServletResponse response)
    {
        List<Character> list = new ArrayList<Character>();
        for (Entry<String, ParamNode> each : set)
        {
            int index = Integer.valueOf(each.getKey());
            list.add(index, ((StringValueNode) each.getValue()).getValue().charAt(0));
        }
        return list;
    }
}
