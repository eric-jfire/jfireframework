package com.jfireframework.mvc.binder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.node.TreeValueNode;

public interface DataBinder
{
    /**
     * 执行参数绑定动作，可以提取的参数从三个地方而来，request和response，以及其中提取的信息转变而来的treevaluenode
     * 
     * @param request
     * @param treeValueNode
     * @param response
     * @return
     */
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response);
    
    /**
     * 该参数的名称
     * 
     * @return
     */
    public String getParamName();
}
