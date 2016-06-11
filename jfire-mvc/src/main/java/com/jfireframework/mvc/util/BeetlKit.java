package com.jfireframework.mvc.util;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.ServletContext;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.beetl.core.resource.WebAppResourceLoader;
import org.beetl.ext.web.WebVariable;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.mvc.core.ModelAndView;

public class BeetlKit
{
    GroupTemplate gt = null;
    
    public BeetlKit()
    {
        WebAppResourceLoader loader = new WebAppResourceLoader();
        Configuration configuration = null;
        try
        {
            configuration = Configuration.defaultConfiguration();
        }
        catch (IOException e)
        {
            throw new JustThrowException(e);
        }
        gt = new GroupTemplate(loader, configuration);
        gt.getConf().setDirectByteOutput(true);
    }
    
    public void render(String key, Map<String, Object> data, ServletContext servletContext, OutputStream outputStream)
    {
        ModelAndView vm = new ModelAndView(key);
        vm.setDataMap(data);
        render(vm, servletContext, outputStream);
    }
    
    public void render(ModelAndView vm, ServletContext servletContext, OutputStream outputStream)
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
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
}
