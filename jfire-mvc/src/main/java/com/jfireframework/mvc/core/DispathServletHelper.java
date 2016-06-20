package com.jfireframework.mvc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.mvc.config.MvcStaticConfig;
import com.jfireframework.mvc.core.action.Action;
import com.jfireframework.mvc.core.action.ActionCenter;
import com.jfireframework.mvc.core.action.ActionCenterBulder;

public class DispathServletHelper
{
    private static final Logger     logger = ConsoleLogFactory.getLogger();
    private ActionCenter            actionCenter;
    private final ServletContext    servletContext;
    private final RequestDispatcher staticResourceDispatcher;
    private final JsonObject        config;
    private final WatchService      watcher;
    private final boolean           devMode;
    private final String            encode;
    
    public DispathServletHelper(ServletConfig servletConfig)
    {
        this.servletContext = servletConfig.getServletContext();
        staticResourceDispatcher = getStaticResourceDispatcher();
        config = readConfigFile();
        encode = config.getWString("encode") == null ? "UTF8" : config.getWString("encode");
        devMode = config.contains("devMode") ? config.getBoolean("devMode") : false;
        if (devMode)
        {
            String reloadPath = config.getWString("reloadPath");
            watcher = generatorWatcher(reloadPath);
            actionCenter = ActionCenterBulder.generate(config, servletContext, encode);
        }
        else
        {
            watcher = null;
            actionCenter = ActionCenterBulder.generate(config, servletContext, encode);
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
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
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
    
    private WatchService generatorWatcher(String reloadPath)
    {
        try
        {
            WatchService watcher = FileSystems.getDefault().newWatchService();
            Set<File> dirs = new HashSet<File>();
            getChildDirs(new File(reloadPath), dirs);
            Set<Path> paths = new HashSet<Path>();
            for (File file : dirs)
            {
                paths.add(Paths.get(file.getAbsolutePath()));
            }
            for (Path path : paths)
            {
                path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);
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
    
    public void handleStaticResourceRequest(HttpServletRequest request, HttpServletResponse response)
    {
        try
        {
            staticResourceDispatcher.forward(request, response);
        }
        catch (Exception e)
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
                        actionCenter = ActionCenterBulder.generate(config, servletContext, encode);
                        logger.debug("热部署,耗时:{}", System.currentTimeMillis() - t0);
                        if (!key.reset())
                        {
                            break;
                        }
                        break;
                    }
                    catch (Exception e)
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
