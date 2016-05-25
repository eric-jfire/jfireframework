package com.jfireframework.mvc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.beetl.core.Configuration;
import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.WebAppResourceLoader;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.JsonArray;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.mvc.annotation.ActionClass;
import com.jfireframework.mvc.annotation.ActionMethod;
import com.jfireframework.mvc.config.MvcStaticConfig;
import com.jfireframework.mvc.config.ResultType;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;
import com.jfireframework.mvc.interceptor.impl.UploadInterceptor;
import com.jfireframework.mvc.util.ActionFactory;
import com.jfireframework.mvc.util.BeetlRender;
import com.jfireframework.mvc.util.HotwrapClassLoader;
import com.jfireframework.mvc.util.ReportMdActionListener;
import com.jfireframework.mvc.viewrender.RenderFactory;
import com.jfireframework.mvc.viewrender.ViewRender;

public class DispathServletHelper
{
    private static final Logger     logger = ConsoleLogFactory.getLogger();
    private ActionCenter            actionCenter;
    private final String            contextUrl;
    private final ServletContext    servletContext;
    private final RequestDispatcher staticResourceDispatcher;
    private JsonObject              config;
    private final File              monitorFile;
    private final String[]          reloadPackages;
    private final WatchService      watcher;
    private final boolean           devMode;
    private final RenderFactory     renderFactory;
    private final String            encode;
    
    public DispathServletHelper(ServletConfig servletConfig)
    {
        this.servletContext = servletConfig.getServletContext();
        staticResourceDispatcher = getStaticResourceDispatcher();
        contextUrl = servletContext.getContextPath();
        config = readConfigFile();
        encode = config.getWString("encode") == null ? "UTF8" : config.getWString("encode");
        Charset charset = Charset.forName(encode);
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
        renderFactory = new RenderFactory(charset, new BeetlRender(new GroupTemplate(loader, configuration)));
        generateActionCenter(null);
        devMode = config.contains("devMode") ? config.getBoolean("devMode") : false;
        if (devMode)
        {
            Verify.True(config.contains("reloadPackages"), "开发模式为true，此时应该配置reloadPackages内容");
            Verify.True(config.contains("monitorPath"), "开发模式为true，此时应该配置monitorPath内容");
            JsonArray jsonArray = config.getJsonArray("reloadPackages");
            reloadPackages = jsonArray.toArray(new String[jsonArray.size()]);
            monitorFile = new File(config.getWString("monitorPath"));
            watcher = generatorWatcher(monitorFile);
        }
        else
        {
            watcher = null;
            monitorFile = null;
            reloadPackages = null;
        }
        
    }
    
    private JsonObject readConfigFile()
    {
        try (FileInputStream inputStream = new FileInputStream(new File(this.getClass().getClassLoader().getResource("mvc.json").toURI())))
        {
            byte[] src = new byte[inputStream.available()];
            inputStream.read(src);
            String value = new String(src, Charset.forName("utf8"));
            return (JsonObject) JsonTool.fromString(value);
        }
        catch (URISyntaxException | IOException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    private void generateActionCenter(JfireContext jfireContext)
    {
        AopUtil.initClassPool();
        jfireContext = jfireContext == null ? new JfireContextImpl() : jfireContext;
        jfireContext.readConfig(config);
        jfireContext.addPackageNames("com.jfireframework.sql");
        jfireContext.addSingletonEntity("servletContext", servletContext);
        jfireContext.addSingletonEntity("beetlRender", renderFactory.getViewRender(ResultType.Beetl));
        BeetlRender beetlRender = (BeetlRender) renderFactory.getViewRender(ResultType.Beetl);
        jfireContext.addSingletonEntity(beetlRender.getClass().getName(), beetlRender);
        addDefaultInterceptors(jfireContext);
        boolean report = config.getWBoolean("report") == null ? false : config.getBoolean("report");
        if (report)
        {
            jfireContext.addBean(ReportMdActionListener.class);
        }
        actionCenter = new ActionCenter(generateActions(contextUrl, jfireContext).toArray(new Action[0]));
    }
    
    /**
     * 初始化Beancontext容器，并且抽取其中的ActionClass注解的类，将action实例化
     */
    private List<Action> generateActions(String contextUrl, JfireContext jfireContext)
    {
        Bean[] beans = jfireContext.getBeanByAnnotation(ActionClass.class);
        Bean[] listenerBeans = jfireContext.getBeanByInterface(ActionInitListener.class);
        LightSet<ActionInitListener> tmp = new LightSet<>();
        for (Bean each : listenerBeans)
        {
            tmp.add((ActionInitListener) each.getInstance());
        }
        ActionInitListener[] listeners = tmp.toArray(ActionInitListener.class);
        List<Action> list = new ArrayList<>();
        for (Bean each : beans)
        {
            list.addAll(generateActions(each, listeners, jfireContext));
        }
        return list;
    }
    
    /**
     * 创建某一个bean下面的所有action
     * 
     * @param bean
     * @param listeners
     * @param contextUrl
     * @param jfireContext
     * @return
     */
    private List<Action> generateActions(Bean bean, ActionInitListener[] listeners, JfireContext jfireContext)
    {
        Class<?> src = bean.getOriginType();
        ActionClass actionClass = src.getAnnotation(ActionClass.class);
        String modelUrl = null;
        if (actionClass.value().equals("/"))
        {
            modelUrl = contextUrl;
        }
        else
        {
            modelUrl = actionClass.value().equals("") ? '/' + src.getSimpleName() : '/' + actionClass.value();
            modelUrl = contextUrl + modelUrl;
        }
        Verify.False(modelUrl.contains("*"), "顶级url不能包含*");
        // 这里需要使用原始的类来得到方法，因为如果使用增强后的子类，就无法得到正确的方法名称以及方法上的注解信息
        Method[] methods = ReflectUtil.getAllMehtods(bean.getOriginType());
        List<Action> list = new ArrayList<>();
        for (Method each : methods)
        {
            if (each.isAnnotationPresent(ActionMethod.class))
            {
                Action action = ActionFactory.buildAction(each, modelUrl, bean, jfireContext);
                list.add(action);
                for (ActionInitListener listener : listeners)
                {
                    listener.init(action);
                }
            }
        }
        return list;
    }
    
    private void addDefaultInterceptors(JfireContext jfireContext)
    {
        jfireContext.addBean(DataBinderInterceptor.class);
        jfireContext.addBean(UploadInterceptor.class);
    }
    
    private RequestDispatcher getStaticResourceDispatcher()
    {
        RequestDispatcher requestDispatcher = null;
        if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.COMMON_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.RESIN_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.WEBLOGIC_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else if ((requestDispatcher = servletContext.getNamedDispatcher(MvcStaticConfig.WEBSPHERE_DEFAULT_SERVLET_NAME)) != null)
        {
        }
        else
        {
            throw new UnSupportException("找不到默认用来处理静态资源的处理器");
        }
        return requestDispatcher;
    }
    
    private WatchService generatorWatcher(File monitorDir)
    {
        Set<File> dirs = new HashSet<>();
        getChildDirs(monitorDir, dirs);
        Set<Path> paths = new HashSet<>();
        for (File each : dirs)
        {
            paths.add(Paths.get(each.getAbsolutePath()));
        }
        try
        {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            for (Path each : paths)
            {
                each.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
            }
            return watcher;
        }
        catch (IOException e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    private void getChildDirs(File parentDir, Set<File> dirSets)
    {
        dirSets.add(parentDir);
        for (File each : parentDir.listFiles())
        {
            if (each.isDirectory())
            {
                getChildDirs(each, dirSets);
            }
        }
    }
    
    public String encode()
    {
        return encode;
    }
    
    public Action getAction(HttpServletRequest request)
    {
        return actionCenter.getAction(request);
    }
    
    public ViewRender getViewRender(ResultType resultType)
    {
        return renderFactory.getViewRender(resultType);
    }
    
    public void handleStaticResourceRequest(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            staticResourceDispatcher.forward(request, response);
        }
        catch (ServletException | IOException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public void preHandleDevMode()
    {
        if (devMode)
        {
            while (true)
            {
                WatchKey key = watcher.poll();
                if (key == null)
                {
                    break;
                }
                for (WatchEvent<?> event : key.pollEvents())
                {
                    Kind<?> kind = event.kind();
                    if (kind == StandardWatchEventKinds.OVERFLOW)
                    {// 事件可能lost or discarded
                        continue;
                    }
                    try
                    {
                        long t0 = System.currentTimeMillis();
                        config = readConfigFile();
                        ClassLoader classLoader = new HotwrapClassLoader(monitorFile, reloadPackages);
                        JfireContext jfireContext = (JfireContext) classLoader.loadClass("com.jfireframework.context.JfireContextImpl").newInstance();
                        jfireContext.addSingletonEntity(ClassLoader.class.getSimpleName(), classLoader);
                        jfireContext.setClassLoader(classLoader);
                        generateActionCenter(jfireContext);
                        logger.debug("热部署,耗时:{}", System.currentTimeMillis() - t0);
                        if (!key.reset())
                        {
                            break;
                        }
                        break;
                    }
                    catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
                    {
                        if (!key.reset())
                        {
                            break;
                        }
                        throw new JustThrowException(e);
                    }
                }
            }
        }
    }
}
