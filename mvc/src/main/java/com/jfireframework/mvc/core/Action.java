package com.jfireframework.mvc.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.jfireframework.mvc.annotation.ContentType;
import com.jfireframework.mvc.annotation.RequestMethod;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.rest.RestfulRule;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.view.BeetlView;
import com.jfireframework.mvc.view.BytesView;
import com.jfireframework.mvc.view.HtmlView;
import com.jfireframework.mvc.view.JsonView;
import com.jfireframework.mvc.view.JspView;
import com.jfireframework.mvc.view.NoneView;
import com.jfireframework.mvc.view.RedirectView;
import com.jfireframework.mvc.view.StringView;
import com.jfireframework.mvc.view.View;
import sun.reflect.MethodAccessor;

/**
 * 传统action类，用来代表一个事先定义的url地址响应，该url地址中不包含*这样的通配符
 * 
 * @author 林斌（windfire@zailanghua.com）
 * 
 */
@SuppressWarnings("restriction")
public class Action
{
    /** 调用该action的对象实例 */
    protected Object         actionEntity;
    protected DataBinder[]   dataBinders;
    // 该action方法的快速反射调用工具
    protected MethodAccessor methodAccessor;
    // 该action响应的url地址
    protected String         requestUrl;
    protected View           view;
    private boolean          rest       = false;
    private RestfulRule      restfulRule;
    private boolean          readStream = false;
    private RequestMethod[]  requestMethods;
    private Method           method;
    private String           contentType;
    
    public Action(Method method)
    {
        this.method = method;
    }
    
    public Object invoke(Object[] params)
    {
        try
        {
            return methodAccessor.invoke(actionEntity, params);
        }
        catch (IllegalArgumentException | InvocationTargetException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 返回该action方法的请求路径
     * 
     * @return
     */
    public String getRequestUrl()
    {
        return requestUrl;
    }
    
    public void setRest(boolean rest)
    {
        this.rest = rest;
    }
    
    /**
     * 返回这个action持有的method对象
     * 
     * @return
     */
    public Method getMethod()
    {
        return method;
    }
    
    public boolean isRest()
    {
        return rest;
    }
    
    public DataBinder[] getDataBinders()
    {
        return dataBinders;
    }
    
    public void setDataBinders(DataBinder[] dataBinders)
    {
        this.dataBinders = dataBinders;
    }
    
    public void setActionEntity(Object actionEntity)
    {
        this.actionEntity = actionEntity;
    }
    
    public void setMethodAccessor(MethodAccessor methodAccessor)
    {
        this.methodAccessor = methodAccessor;
    }
    
    public void setRequestUrl(String requestPath)
    {
        this.requestUrl = requestPath;
    }
    
    public void setResultType(ResultType resultType, BeetlRender beetlRender)
    {
        switch (resultType)
        {
            case Json:
                view = new JsonView();
                contentType = ContentType.JSON;
                break;
            case Beetl:
                view = new BeetlView(beetlRender);
                contentType = ContentType.HTML;
                break;
            case String:
                contentType = ContentType.JSON;
                view = new StringView();
                break;
            case Jsp:
                view = new JspView();
                contentType = ContentType.HTML;
                break;
            case Html:
                view = new HtmlView();
                contentType = ContentType.HTML;
                break;
            case Redirect:
                view = new RedirectView();
                contentType = ContentType.HTML;
                break;
            case None:
                contentType = ContentType.STREAM;
                view = new NoneView();
                break;
            case Bytes:
                contentType = ContentType.STREAM;
                view = new BytesView();
                break;
            case FreeMakrer:
                throw new RuntimeException("尚未支持freemarker，建议使用beetl");
            default:
                throw new RuntimeException("方法没有指定返回类型");
        }
    }
    
    public void setRestfulRule(RestfulRule restfulRule)
    {
        this.restfulRule = restfulRule;
    }
    
    public RestfulRule getRestfulRule()
    {
        return restfulRule;
    }
    
    public boolean isReadStream()
    {
        return readStream;
    }
    
    public void setReadStream(boolean onlyServletRequest)
    {
        this.readStream = onlyServletRequest;
    }
    
    public RequestMethod[] getRequestMethods()
    {
        return requestMethods;
    }
    
    public void setRequestMethods(RequestMethod[] requestMethods)
    {
        this.requestMethods = requestMethods;
    }
    
    public View getView()
    {
        return view;
    }
    
    public String getContentType()
    {
        return contentType;
    }
    
    public void setContentType(String contentType)
    {
        this.contentType = contentType;
    }
}
