package com.jfireframework.mvc.newbinder.field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.ParamTreeNode;

public interface BinderField
{
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamTreeNode node, Object entity);
    
    public String getName();
}
