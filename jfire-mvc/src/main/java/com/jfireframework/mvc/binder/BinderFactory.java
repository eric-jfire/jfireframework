package com.jfireframework.mvc.binder;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.jfireframework.mvc.annotation.CookieValue;
import com.jfireframework.mvc.annotation.HeaderValue;
import com.jfireframework.mvc.binder.impl.ArrayBinder;
import com.jfireframework.mvc.binder.impl.BaseBinder;
import com.jfireframework.mvc.binder.impl.CookieBinder;
import com.jfireframework.mvc.binder.impl.DateBinder;
import com.jfireframework.mvc.binder.impl.HeaderBinder;
import com.jfireframework.mvc.binder.impl.HttpServletRequestBinder;
import com.jfireframework.mvc.binder.impl.HttpServletResponseBinder;
import com.jfireframework.mvc.binder.impl.HttpSessionBinder;
import com.jfireframework.mvc.binder.impl.ListBinder;
import com.jfireframework.mvc.binder.impl.ListUploadBinder;
import com.jfireframework.mvc.binder.impl.ObjectDataBinder;
import com.jfireframework.mvc.binder.impl.SqlDateBinder;
import com.jfireframework.mvc.binder.impl.StringBinder;
import com.jfireframework.mvc.binder.impl.UploadBinder;
import com.jfireframework.mvc.binder.impl.WrapperBinder;

public class BinderFactory
{
    public static final DataBinder[] build(Class<?>[] ckasss, Type[] types, String[] names, Annotation[][] annotationArray)
    {
        List<DataBinder> binders = new ArrayList<DataBinder>();
        nextBinder: for (int i = 0; i < ckasss.length; i++)
        {
            Class<?> target = ckasss[i];
            String prefixName = names[i];
            Annotation[] annotations = annotationArray[i];
            for (Annotation each : annotations)
            {
                if (each instanceof CookieValue)
                {
                    binders.add(new CookieBinder(target, prefixName, annotations));
                    continue nextBinder;
                }
                else if (each instanceof HeaderValue)
                {
                    binders.add(new HeaderBinder(target, prefixName, annotations));
                    continue nextBinder;
                }
            }
            if (target.isPrimitive())
            {
                binders.add(new BaseBinder(target, prefixName, annotations));
            }
            else if (
                target == Integer.class //
                        || target == Short.class //
                        || target == Long.class //
                        || target == Float.class //
                        || target == Double.class //
                        || target == Boolean.class //
                        || target == Byte.class //
                        || target == Character.class
            )
            {
                binders.add(new WrapperBinder(target, prefixName, annotations));
            }
            else if (target == String.class)
            {
                binders.add(new StringBinder(target, prefixName, annotations));
            }
            else if (target == Date.class)
            {
                binders.add(new DateBinder(target, prefixName, annotations));
            }
            else if (target == java.sql.Date.class)
            {
                binders.add(new SqlDateBinder(target, prefixName, annotations));
            }
            else if (target == HttpServletRequest.class)
            {
                binders.add(new HttpServletRequestBinder(target, prefixName, annotations));
            }
            else if (target == HttpServletResponse.class)
            {
                binders.add(new HttpServletResponseBinder(target, prefixName, annotations));
            }
            else if (target == HttpSession.class)
            {
                binders.add(new HttpSessionBinder(target, prefixName, annotations));
            }
            else if (target == UploadItem.class)
            {
                binders.add(new UploadBinder(target, prefixName, annotations));
            }
            else if (target.isArray())
            {
                binders.add(ArrayBinder.valueOf(target, prefixName, annotations));
            }
            else if (List.class.isAssignableFrom(target))
            {
                Class<?> paramType = (Class<?>) ((ParameterizedType) types[i]).getActualTypeArguments()[0];
                if (paramType == UploadItem.class)
                {
                    binders.add(new ListUploadBinder(prefixName));
                }
                else
                {
                    binders.add(ListBinder.valueOf(paramType, prefixName, annotations));
                }
            }
            else
            {
                binders.add(new ObjectDataBinder(target, prefixName, annotations));
            }
        }
        return binders.toArray(new DataBinder[binders.size()]);
    }
}
