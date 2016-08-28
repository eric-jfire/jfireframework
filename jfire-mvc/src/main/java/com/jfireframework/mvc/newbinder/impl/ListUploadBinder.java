package com.jfireframework.mvc.newbinder.impl;

import java.lang.annotation.Annotation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.newbinder.node.TreeValueNode;

public class ListUploadBinder implements DataBinder
{
    
    private final String prefixName;
    
    public ListUploadBinder(Class<?> ckass, String prefixName, Annotation[] annotations)
    {
        this.prefixName = prefixName;
    }
    
    @Override
    public Object bind(HttpServletRequest request, TreeValueNode treeValueNode, HttpServletResponse response)
    {
        Object value = request.getAttribute(UploadInterceptor.uploadFileList);
        return value;
    }
    
    @Override
    public String getParamName()
    {
        return prefixName;
    }
    
}
