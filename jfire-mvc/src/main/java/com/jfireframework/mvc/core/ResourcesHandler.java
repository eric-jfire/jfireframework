package com.jfireframework.mvc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.jfireframework.baseutil.exception.JustThrowException;

public class ResourcesHandler
{
    private static final String         HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    private static final String         HEADER_LAST_MODIFIED     = "Last-Modified";
    private static final String         HEADER_EXPIRES           = "Expires";
    private static final String         HEADER_CACHE_CONTROL     = "Cache-Control";
    private static final int            cacheSeconds             = 3600;
    private Map<String, StaticResource> resourcesMap             = new ConcurrentHashMap<String, StaticResource>();
    private Set<ResourceRule>           rules                    = new HashSet<ResourcesHandler.ResourceRule>();
    
    class ResourceRule
    {
        private final String rule;
        // web:1,classpath:2
        private final int    type;
        private final String path;
        
        public ResourceRule(String rule, int type, String path)
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
    
    interface StaticResource
    {
        public long lastModity();
        
        public byte[] content();
    }
    
    class FileResource implements StaticResource
    {
        private final File file;
        
        public FileResource(File file)
        {
            this.file = file;
        }
        
        @Override
        public long lastModity()
        {
            return file.lastModified();
        }
        
        @Override
        public byte[] content()
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] array = new byte[fileInputStream.available()];
                fileInputStream.read(array);
                fileInputStream.close();
                return array;
            }
            catch (IOException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    class ClasspathStreamResource implements StaticResource
    {
        
        private String resPath;
        
        public ClasspathStreamResource(String resPath)
        {
            this.resPath = resPath;
        }
        
        @Override
        public long lastModity()
        {
            return 1;
        }
        
        @Override
        public byte[] content()
        {
            try
            {
                InputStream inputStream = ClasspathStreamResource.class.getClassLoader().getResourceAsStream(resPath);
                byte[] array = new byte[inputStream.available()];
                inputStream.read(array);
                inputStream.close();
                return array;
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
        }
        
    }
    
    public ResourcesHandler(String app, String[] staticResourceMaps)
    {
        for (String each : staticResourceMaps)
        {
            if (each.startsWith("classpath:"))
            {
                String key = each.substring(10);
                String name = key.split(":")[0];
                String value = key.split(":")[1];
                ResourceRule resource = new ResourceRule(app + '/' + name, 2, value);
                rules.add(resource);
            }
            else
            {
                ResourceRule resource = new ResourceRule(app + '/' + each, 1, each);
                rules.add(resource);
            }
        }
    }
    
    private void cachedHeader(HttpServletResponse response)
    {
        response.setDateHeader(HEADER_EXPIRES, System.currentTimeMillis() + cacheSeconds * 1000L);
        String headerValue = "max-age=" + cacheSeconds;
        response.setHeader(HEADER_CACHE_CONTROL, headerValue);
    }
    
    private void handleCachedResource(HttpServletRequest request, HttpServletResponse response, StaticResource resource)
    {
        long ifModifiedSince = -1;
        try
        {
            ifModifiedSince = request.getDateHeader(HEADER_IF_MODIFIED_SINCE);
        }
        catch (IllegalArgumentException ex)
        {
            String headerValue = request.getHeader(HEADER_IF_MODIFIED_SINCE);
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
        long lastModifiedTimestamp = resource.lastModity();
        if (ifModifiedSince >= (lastModifiedTimestamp / 1000 * 1000))
        {
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
        }
        else
        {
            response.setDateHeader(HEADER_LAST_MODIFIED, lastModifiedTimestamp);
            try
            {
                byte[] array = resource.content();
                response.setContentLength(array.length);
                response.getOutputStream().write(array);
            }
            catch (IOException e)
            {
                throw new JustThrowException(e);
            }
        }
    }
    
    public boolean handle(HttpServletRequest request, HttpServletResponse response)
    {
        cachedHeader(response);
        String path = request.getRequestURI();
        StaticResource resource = resourcesMap.get(path);
        if (resource != null)
        {
            handleCachedResource(request, response, resource);
            return true;
        }
        String resourcePath = null;
        for (ResourceRule each : rules)
        {
            if (path.startsWith(each.getRule()))
            {
                if (each.type == 1)
                {
                    resourcePath = each.getPath() + path.substring(each.getRule().length());
                    String realPath = request.getServletContext().getRealPath("") + resourcePath;
                    File file = new File(realPath);
                    resource = new FileResource(file);
                }
                else
                {
                    resourcePath = each.getPath() + path.substring(each.getRule().length());
                    resource = new ClasspathStreamResource(resourcePath);
                }
                resourcesMap.put(path, resource);
                response.setDateHeader(HEADER_LAST_MODIFIED, resource.lastModity());
                try
                {
                    byte[] array = resource.content();
                    response.setContentLength(array.length);
                    response.getOutputStream().write(array);
                    return true;
                }
                catch (IOException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
        return false;
    }
}
