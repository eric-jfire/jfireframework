package com.jfireframework.mvc.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.ServletContext;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.resourceloader.TplResLoader;
import com.jfireframework.litl.resourceloader.WebAppResLoader;
import com.jfireframework.litl.template.Template;

public class LitlKit
{
    @Resource
    private ClassLoader classLoader;
    private String      encode = "utf8";
    private Charset     charset;
    private TplCenter   tplcenter;
    
    @PostConstruct
    public void init()
    {
        charset = Charset.forName(encode);
        TplResLoader loader = new WebAppResLoader();
        tplcenter = new TplCenter(loader, classLoader);
    }
    
    public void render(String path, Map<String, Object> data,ServletContext servletContext, OutputStream os)
    {
        Template template = tplcenter.load(path);
        data.put("ctxPath", servletContext.getContextPath());
        data.put("ctx", servletContext.getContextPath());
        String value = template.render(data);
        try
        {
            os.write(value.getBytes(charset));
            os.flush();
        }
        catch (IOException e)
        {
            throw new UnSupportException("", e);
        }
    }
}
