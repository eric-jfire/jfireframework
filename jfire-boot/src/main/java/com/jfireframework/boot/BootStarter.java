package com.jfireframework.boot;

import java.io.InputStream;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.WebResource;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.DefaultWebXmlListener;
import org.xml.sax.InputSource;
import com.jfireframework.mvc.core.EasyMvcDispathServlet;

public class BootStarter
{
    private int          port = 80;
    private String       baseDir;
    private final String appName;
    private final String docBase;
    
    public BootStarter(String appName, Class<?> locationClass)
    {
        if (appName.charAt(0) == '/')
        {
            this.appName = appName;
        }
        else
        {
            this.appName = '/' + appName;
        }
        docBase = locationClass.getResource("/web").getPath();
    }
    
    public BootStarter(String appName, String docBase)
    {
        this.docBase = docBase;
        this.appName = appName;
    }
    
    public void start()
    {
        Tomcat tomcat = new Tomcat();
        if (baseDir != null)
        {
            tomcat.setBaseDir(baseDir);
        }
        tomcat.setPort(port);
        tomcat.getHost().setAutoDeploy(true);
        tomcat.getHost().setDeployOnStartup(true);
        Context ctx = new StandardContext();
        ctx.setPath(appName);
        ctx.setDocBase(docBase);
        System.out.println(docBase);
        ctx.addLifecycleListener(new DefaultWebXmlListener());
        ctx.setConfigFile(null);
        ctx.setParentClassLoader(EasyMvcDispathServlet.class.getClassLoader());
        WebappLoader loader = new WebappLoader(ctx.getParentClassLoader());
        loader.setDelegate(true);
        ctx.setLoader(loader);
        ContextConfig ctxCfg = new ContextConfig() {
            private InputStream inputStream = BootStarter.class.getClassLoader().getResourceAsStream("web.xml");
            
            @Override
            protected InputSource getGlobalWebXmlSource()
            {
                return new InputSource(inputStream);
            }
        };
        ctx.addLifecycleListener(ctxCfg);
        System.out.println(BootStarter.class.getClassLoader().getResource("web.xml").getPath());
        ctxCfg.setDefaultWebXml(BootStarter.class.getClassLoader().getResource("web.xml").getPath());
        tomcat.getHost().addChild(ctx);
        try
        {
            tomcat.start();
            tomcat.getServer().await();
        }
        catch (LifecycleException e)
        {
            e.printStackTrace();
        }
    }
    
    public int getPort()
    {
        return port;
    }
    
    public void setPort(int port)
    {
        this.port = port;
    }
    
    public String getBaseDir()
    {
        return baseDir;
    }
    
    public void setBaseDir(String baseDir)
    {
        this.baseDir = baseDir;
    }
    
}
