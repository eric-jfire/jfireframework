package com.jfireframework.boot;

import java.io.InputStream;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Wrapper;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.ContextConfig;
import org.apache.catalina.startup.Tomcat;
import org.apache.catalina.startup.Tomcat.DefaultWebXmlListener;
import org.xml.sax.InputSource;
import com.jfireframework.mvc.core.EasyMvcDispathServlet;

public class BootStarter
{
    private final int    port;
    private final String baseDir;
    private final String appName;
    private final String docBase;
    
    public BootStarter(BootConfig config)
    {
        port = config.getPort();
        baseDir = config.getBaseDir();
        appName = config.getAppName();
        docBase = config.getDocBase();
    }
    
    public void start()
    {
        Tomcat tomcat = new Tomcat();
        if (baseDir != null)
        {
            tomcat.setBaseDir(baseDir);
        }
        tomcat.setPort(port);
        tomcat.getHost().setAutoDeploy(false);
        tomcat.getHost().setDeployOnStartup(true);
        Context ctx = new StandardContext();
        Wrapper mvcServlet = ctx.createWrapper();
        mvcServlet.setName("easymvcservlet");
        mvcServlet.setServletClass(EasyMvcDispathServlet.class.getName());
        mvcServlet.setLoadOnStartup(1);
        mvcServlet.setOverridable(true);
        ctx.addChild(mvcServlet);
        ctx.addServletMapping("/*", "easymvcservlet");
        ctx.setPath(appName);
        ctx.setDocBase(docBase);
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
    
}
