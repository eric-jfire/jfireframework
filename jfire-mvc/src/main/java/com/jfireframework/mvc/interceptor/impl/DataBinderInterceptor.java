package com.jfireframework.mvc.interceptor.impl;

import java.util.Map.Entry;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.node.TreeValueNode;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.interceptor.ActionInterceptor;

@Resource
public class DataBinderInterceptor implements ActionInterceptor
{
    public static final String                      DATABINDERKEY = "databinder_key" + System.currentTimeMillis();
    private static final ThreadLocal<TreeValueNode> nodeLocal     = new ThreadLocal<TreeValueNode>() {
                                                                      @Override
                                                                      protected TreeValueNode initialValue()
                                                                      {
                                                                          return new TreeValueNode();
                                                                      }
                                                                  };
    
    @Override
    public int getOrder()
    {
        return 12;
    }
    
    @Override
    public boolean interceptor(HttpServletRequest request, HttpServletResponse response, Action action)
    {
        TreeValueNode node = nodeLocal.get();
        node.clear();
        if (action.isReadStream() == false)
        {
            for (Entry<String, String[]> each : request.getParameterMap().entrySet())
            {
                for (String value : each.getValue())
                {
                    if (value.equals("") == false)
                    {
                        node.put(each.getKey(), value);
                    }
                }
            }
        }
        if (action.isRest())
        {
            action.getRestfulRule().getObtain(request.getRequestURI(), node);
        }
        request.setAttribute(DATABINDERKEY, buildParams(action, request, node, response));
        return true;
    }
    
    private Object[] buildParams(Action action, HttpServletRequest request, TreeValueNode node, HttpServletResponse response)
    {
        DataBinder[] dataBinders = action.getDataBinders();
        int length = dataBinders.length;
        Object[] param = new Object[length];
        for (int i = 0; i < length; i++)
        {
            param[i] = dataBinders[i].bind(request, node, response);
        }
        return param;
    }
    
    @Override
    public String pathRule()
    {
        return "*";
    }
    
    @Override
    public String tokenRule()
    {
        return null;
    }
    
}
