package com.jfireframework.mvc.viewrender.impl;

import java.io.IOException;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.beetl.core.Template;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;
import com.jfireframework.mvc.util.WebAppBeetlKit;
import com.jfireframework.mvc.viewrender.ViewRender;

@Resource
public class BeetlRender implements ViewRender
{
    @Resource
    private WebAppBeetlKit beetlKit;
    
    @Override
    public void render(HttpServletRequest request, HttpServletResponse response, Object result) throws Throwable
    {
        ModelAndView viewAndModel = (ModelAndView) result;
        response.setContentType("text/html");
        if (viewAndModel.cached())
        {
            response.getOutputStream().write(viewAndModel.getDirectBytes());
        }
        else if (viewAndModel.isDirect())
        {
            synchronized (viewAndModel)
            {
                if (viewAndModel.cached())
                {
                    response.getOutputStream().write(viewAndModel.getDirectBytes());
                }
                else
                {
                    JfireMvcResponseWrapper wrapper = new JfireMvcResponseWrapper(response, viewAndModel);
                    render(viewAndModel, request, wrapper);
                    wrapper.getOutputStream().flush();
                    viewAndModel.setDirectBytes(viewAndModel.getCache().toArray());
                }
            }
        }
        else
        {
            render(viewAndModel, request, response);
        }
    }
    
    /**
     * @param key 模板资源id
     * @param request
     * @param response
     * @param args 其他参数，将会传给modifyTemplate方法
     */
    public void render(ModelAndView vm, HttpServletRequest request, HttpServletResponse response)
    {
        
        WebVariable webVariable = new WebVariable();
        webVariable.setRequest(request);
        webVariable.setResponse(response);
        vm.addObject("session", new SessionWrapper(request, request.getSession(false)));
        vm.addObject("servlet", webVariable);
        vm.addObject("request", request);
        vm.addObject("ctxPath", request.getContextPath());
        try
        {
            beetlKit.render(vm, request.getServletContext(), response.getOutputStream());
            response.getOutputStream().flush();
        }
        catch (IOException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    /**
     * 可以添加更多的绑定
     * 
     * @param template 模板
     * @param key 模板的资源id
     * @param request
     * @param response
     * @param args 调用render的时候传的参数
     */
    protected void modifyTemplate(Template template, String key, HttpServletRequest request, HttpServletResponse response, Object... args)
    {
        
    }
    
}
