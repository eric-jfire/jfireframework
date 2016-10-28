package com.jfireframework.mvc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ResourcesHandler
{
    private static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    
    private static final String HEADER_LAST_MODIFIED     = "Last-Modified";
    
    private static final String HEADER_EXPIRES           = "Expires";
    
    private static final String HEADER_CACHE_CONTROL     = "Cache-Control";
    private static final int    cacheSeconds             = 3600;
    private Map<String, File>   resourcesMap             = new ConcurrentHashMap<String, File>();
    private Map<String, String> rules                    = new HashMap<String, String>();
    private Set<StaticResource> rules2                   = new HashSet<ResourcesHandler.StaticResource>();
    
    class StaticResource
    {
        private final String rule;
        // web:1,classpath:2
        private final int    type;
        private final String path;
        
        public StaticResource(String rule, int type, String path)
        {
            this.rule = rule;
            this.type = type;
            this.path = path;
        }
        
        public String getPath()
        {
            return path;
        }
        
        public String getRule()
        {
            return rule;
        }
        
        public int getType()
        {
            return type;
        }
        
    }
    
    public ResourcesHandler(String app, Set<String> staticResourceDirs)
    {
        for (String each : staticResourceDirs)
        {
            if (each.startsWith("classpath:"))
            {
                String key = each.substring(10);
                String name = key.split(":")[0];
                String value = key.split(":")[1];
                StaticResource resource = new StaticResource(app + '/' + name, 2, value);
                rules2.add(resource);
            }
            else
            {
                StaticResource resource = new StaticResource(app + '/' + each, 1, each);
                rules2.add(resource);
            }
        }
    }
    
    public boolean handle(HttpServletRequest request, HttpServletResponse response)
    {
        response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + cacheSeconds * 1000L);
        String headerValue = "max-age=" + cacheSeconds;
        response.setHeader(HEADER_CACHE_CONTROL, headerValue);
        String path = request.getRequestURI();
        if (resourcesMap.containsKey(path))
        {
            long ifModifiedSince = -1;
            try
            {
                ifModifiedSince = request.getDateHeader(HEADER_IF_MODIFIED_SINCE);
            }
            catch (IllegalArgumentException ex)
            {
                headerValue = request.getHeader(HEADER_IF_MODIFIED_SINCE);
                int separatorIndex = headerValue.indexOf(';');
                if (separatorIndex != -1)
                {
                    String datePart = headerValue.substring(0, separatorIndex);
                    try
                    {
                        ifModifiedSince = Date.parse(datePart);
                    }
                    catch (IllegalArgumentException ex2)
                    {
                        ;
                    }
                }
            }
            File file = resourcesMap.get(path);
            long lastModifiedTimestamp = file.lastModified();
            if (ifModifiedSince >= (lastModifiedTimestamp / 1000 * 1000))
            {
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
            else
            {
                response.setDateHeader(HEADER_LAST_MODIFIED, lastModifiedTimestamp);
                try
                {
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] array = new byte[fileInputStream.available()];
                    fileInputStream.read(array);
                    fileInputStream.close();
                    response.setContentLength(array.length);
                    response.getOutputStream().write(array);
                    response.getOutputStream().flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            return true;
        }
        String resourcePath = null;
        for (StaticResource each : rules2)
        {
            if (path.startsWith(each.getRule()))
            {
                if (each.type == 1)
                {
                    resourcePath = each.getPath() + path.substring(each.getRule().length());
                    String realPath = request.getServletContext().getRealPath("") + resourcePath;
                    File file = new File(realPath);
                    resourcesMap.put(path, file);
                    response.setDateHeader(HEADER_LAST_MODIFIED, file.lastModified());
                    try
                    {
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] array = new byte[fileInputStream.available()];
                        fileInputStream.read(array);
                        fileInputStream.close();
                        response.setContentLength(array.length);
                        response.getOutputStream().write(array);
                        response.getOutputStream().flush();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    return true;
                }
                else
                {
                    resourcePath = each.getPath() + path.substring(each.getRule().length());
                    URL url = this.getClass().getClassLoader().getResource(resourcePath);
                    try
                    {
                        File file = new File(url.toURI());
                        resourcesMap.put(path, file);
                        response.setDateHeader(HEADER_LAST_MODIFIED, file.lastModified());
                        FileInputStream fileInputStream = new FileInputStream(file);
                        byte[] array = new byte[fileInputStream.available()];
                        fileInputStream.read(array);
                        fileInputStream.close();
                        response.setContentLength(array.length);
                        response.getOutputStream().write(array);
                        response.getOutputStream().flush();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
        // for (Entry<String, String> each : rules.entrySet())
        // {
        // if (path.startsWith(each.getKey()))
        // {
        // resourcePath = each.getValue() +
        // path.substring(each.getKey().length());
        // }
        // }
        // if (resourcePath != null)
        // {
        // String realPath = request.getServletContext().getRealPath("") +
        // resourcePath;
        // File file = new File(realPath);
        // resourcesMap.put(path, file);
        // response.setDateHeader(HEADER_LAST_MODIFIED, file.lastModified());
        // try
        // {
        // FileInputStream fileInputStream = new FileInputStream(file);
        // byte[] array = new byte[fileInputStream.available()];
        // fileInputStream.read(array);
        // fileInputStream.close();
        // response.setContentLength(array.length);
        // response.getOutputStream().write(array);
        // response.getOutputStream().flush();
        // }
        // catch (IOException e)
        // {
        // e.printStackTrace();
        // }
        // return true;
        // }
        // else
        // {
        // return false;
        // }
    }
}
