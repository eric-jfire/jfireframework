package com.jfireframework.mvc.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Enumeration;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.exception.BeetlException;
import org.beetl.ext.web.SessionWrapper;
import org.beetl.ext.web.WebVariable;
import com.jfireframework.mvc.core.ViewAndModel;

public class BeetlRender
{
    
    GroupTemplate gt = null;
    
    public BeetlRender(GroupTemplate gt)
    {
        this.gt = gt;
        gt.getConf().setDirectByteOutput(true);
    }
    
    public void render(String key, Map<String, Object> data, HttpServletRequest request, OutputStream outputStream)
    {
        ViewAndModel vm = new ViewAndModel(key);
        vm.setDataMap(data);
        render(vm, request, outputStream);
    }
    
    public void render(ViewAndModel vm, HttpServletRequest request, OutputStream outputStream)
    {
        String ajaxId = null;
        Template template = null;
        String key = vm.getModelName();
        Map<String, Object> data = vm.getData();
        try
        {
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
            template.binding(data);
            template.binding("session", new SessionWrapper(request, request.getSession(false)));
            template.binding("servlet", webVariable);
            template.binding("request", request);
            template.binding("ctxPath", request.getContextPath());
            template.renderTo(outputStream);
            outputStream.flush();
        }
        catch (BeetlException e)
        {
            handleBeetlException(e);
        }
        catch (IOException e)
        {
            handleClientError(e);
        }
    }
    
    public void render(String key, Map<String, Object> data, ServletContext servletContext, OutputStream outputStream)
    {
        ViewAndModel vm = new ViewAndModel(key);
        vm.setDataMap(data);
        render(vm, servletContext, outputStream);
    }
    
    public void render(ViewAndModel vm, ServletContext servletContext, OutputStream outputStream)
    {
        String ajaxId = null;
        String key = vm.getModelName();
        Map<String, Object> data = vm.getData();
        Template template = null;
        try
        {
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
            WebVariable webVariable = new WebVariable();
            template.binding(data);
            template.binding("servlet", webVariable);
            template.binding("ctxPath", servletContext.getContextPath());
            template.renderTo(outputStream);
            outputStream.flush();
        }
        catch (BeetlException e)
        {
            handleBeetlException(e);
        }
        catch (IOException e)
        {
            handleClientError(e);
        }
    }
    
    /**
     * @param key 模板资源id
     * @param request
     * @param response
     * @param args 其他参数，将会传给modifyTemplate方法
     */
    public void render(ViewAndModel vm, HttpServletRequest request, HttpServletResponse response)
    {
        Writer writer = null;
        String key = vm.getModelName();
        Map<String, Object> data = vm.getData();
        OutputStream os = null;
        String ajaxId = null;
        Template template = null;
        try
        {
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
            os = response.getOutputStream();
            template.renderTo(os);
        }
        catch (IOException e)
        {
            handleClientError(e);
        }
        catch (BeetlException e)
        {
            handleBeetlException(e);
        }
        finally
        {
            try
            {
                if (writer != null)
                    writer.flush();
                if (os != null)
                {
                    os.flush();
                }
            }
            catch (IOException e)
            {
                handleClientError(e);
            }
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
    protected void modifyTemplate(Template template, String key, HttpServletRequest request,
            HttpServletResponse response, Object... args)
    {
        
    }
    
    /**
     * 处理客户端抛出的IO异常
     * 
     * @param ex
     */
    protected void handleClientError(IOException ex)
    {
        // do nothing
    }
    
    /**
     * 处理客户端抛出的IO异常
     * 
     * @param ex
     */
    protected void handleBeetlException(BeetlException ex)
    {
        throw ex;
    }
}
