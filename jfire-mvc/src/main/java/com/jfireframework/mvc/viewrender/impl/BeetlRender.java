package com.jfireframework.mvc.viewrender.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.WebAppResourceLoader;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.core.ModelAndView;
import com.jfireframework.mvc.util.JfireMvcResponseWrapper;
import com.jfireframework.mvc.viewrender.ViewRender;

public class BeetlRender implements ViewRender
{
    
    GroupTemplate gt = null;
    
    public BeetlRender(Charset charset, ClassLoader classLoader)
    {
        WebAppResourceLoader loader = new WebAppResourceLoader();
        Configuration configuration = null;
        try
        {
            configuration = Configuration.defaultConfiguration();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        configuration.setDirectByteOutput(true);
        gt = new GroupTemplate(loader, configuration);
        gt.getConf().setDirectByteOutput(true);
    }
    
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
        String key = vm.getModelName();
        Map<String, Object> data = vm.getData();
        String ajaxId = null;
        Template template = null;
        try
        {
            OutputStream os = response.getOutputStream();
            int ajaxIdIndex = key.lastIndexOf("#");
            if (ajaxIdIndex != -1)
            {
                ajaxId = key.substring(ajaxIdIndex + 1);
                key = key.substring(0, ajaxIdIndex);
                template = gt.getAjaxTemplate(key, ajaxId);
            }
            else
            {
                template = gt.getTemplate(key);
            }
            Enumeration<String> attrs = request.getAttributeNames();
            while (attrs.hasMoreElements())
            {
                String attrName = attrs.nextElement();
                template.binding(attrName, request.getAttribute(attrName));
            }
            WebVariable webVariable = new WebVariable();
            webVariable.setRequest(request);
            webVariable.setResponse(response);
            template.binding(data);
            template.binding("session", new SessionWrapper(request, request.getSession(false)));
            template.binding("servlet", webVariable);
            template.binding("request", request);
            template.binding("ctxPath", request.getContextPath());
            template.renderTo(os);
            os.flush();
            os.close();
        }
        catch (Exception e)
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
