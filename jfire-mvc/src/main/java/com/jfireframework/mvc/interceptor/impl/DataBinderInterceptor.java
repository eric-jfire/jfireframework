package com.jfireframework.mvc.interceptor.impl;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.core.Action;
import com.jfireframework.mvc.interceptor.ActionInterceptor;

@Resource
public class DataBinderInterceptor implements ActionInterceptor
{
    public static final String                            DATABINDERKEY = "databinder_key" + System.currentTimeMillis();
    private static final ThreadLocal<Map<String, String>> mapLocal      = new ThreadLocal<Map<String, String>>() {
                                                                            protected Map<String, String> initialValue()
                                                                            {
                                                                                return new HashMap<String, String>();
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
        if (request.getAttribute(DATABINDERKEY) != null)
        {
            String value = (String) request.getAttribute(DATABINDERKEY);
            String[] params = value.split("&");
            Map<String, String> map = mapLocal.get();
            map.clear();
            for (String each : params)
            {
                String[] kv = each.split("=");
                if (kv.length == 2)
                {
                    map.put(kv[0], kv[1]);
                }
                else
                {
                    map.put(kv[0], null);
                }
            }
            if (action.isRest())
            {
                action.getRestfulRule().getObtain(request.getRequestURI(), map);
            }
            request.setAttribute(DATABINDERKEY, buildParams(action, request, map, response));
            return true;
        }
        else
        {
            Map<String, String> map = mapLocal.get();
            map.clear();
            if (action.isReadStream() == false)
            {
                Enumeration<String> names = request.getParameterNames();
                String name = null;
                while (names.hasMoreElements())
                {
                    name = names.nextElement();
                    map.put(name, request.getParameter(name));
                }
            }
            if (action.isRest())
            {
                action.getRestfulRule().getObtain(request.getRequestURI(), map);
            }
            request.setAttribute(DATABINDERKEY, buildParams(action, request, map, response));
            return true;
        }
    }
    
    private Object[] buildParams(Action action, HttpServletRequest request, Map<String, String> map, HttpServletResponse response)
    {
        DataBinder[] dataBinders = action.getDataBinders();
        int length = dataBinders.length;
        Object[] param = new Object[length];
        for (int i = 0; i < length; i++)
        {
            param[i] = dataBinders[i].binder(request, map, response);
        }
        return param;
    }
    
    @Override
    public String rule()
    {
        return "*";
    }
    
}
