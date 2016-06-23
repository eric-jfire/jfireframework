package com.jfireframework.mvc.util;

import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.resourceloader.WebAppResLoader;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;

public class LitlKit
{
    private TplCenter   tplCenter;
    @Resource
    private ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    private String      encode      = "utf8";
    private Charset     charset;
    
    @PostConstruct
    public void init()
    {
        TplResLoader loader = new WebAppResLoader();
        tplCenter = new TplCenter(loader, classLoader);
        VarAccess.initClassPool(classLoader);
        charset = Charset.forName(encode);
    }
    
    public void render(String key, Map<String, Object> data, ServletContext servletContext, OutputStream outputStream)
    {
        Template template = tplCenter.load(key);
        data.put("ctxPath", servletContext.getContextPath());
        data.put("ctx", servletContext.getContextPath());
        String value = template.render(data);
        try
        {
            outputStream.write(value.getBytes(charset));
            outputStream.flush();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
    }
    
}
