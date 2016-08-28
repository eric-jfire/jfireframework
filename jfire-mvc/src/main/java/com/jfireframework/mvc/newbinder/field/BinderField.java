package com.jfireframework.mvc.newbinder.field;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.newbinder.node.ParamNode;

public interface BinderField
{
    /**
     * 从参数结点中提取信息设置该属性的值。该参数结点不为null
     * 
     * @param request
     * @param response
     * @param node
     * @param entity
     */
    public void setValue(HttpServletRequest request, HttpServletResponse response, ParamNode node, Object entity);
    
    public String getName();
}
