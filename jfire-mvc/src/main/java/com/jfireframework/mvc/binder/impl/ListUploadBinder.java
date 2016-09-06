package com.jfireframework.mvc.binder.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.TreeValueNode;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;

public class ListUploadBinder implements DataBinder
{
    private final String prefixName;
    
    public ListUploadBinder(String prefixName)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        return request.getAttribute(UploadInterceptor.uploadFileList);
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
