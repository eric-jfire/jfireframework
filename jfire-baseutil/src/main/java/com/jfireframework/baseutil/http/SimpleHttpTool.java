package com.jfireframework.baseutil.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.collection.ByteCache;
import com.jfireframework.baseutil.collection.StringCache;

public class SimpleHttpTool
{
    private static ThreadLocal<ByteCache>   cacheLocal  = new ThreadLocal<ByteCache>() {
                                                            protected ByteCache initialValue()
                                                            {
                                                                return new ByteCache();
                                                            }
                                                        };
    private static ThreadLocal<StringCache> stringLocal = new ThreadLocal<StringCache>() {
                                                            protected StringCache initialValue()
                                                            {
                                                                return new StringCache();
                                                            }
                                                        };
                                                        
    /**
     * 对一个url进行get请求。并且设置请求的头部信息
     * 
     * @param url
     * @param charset
     * @param requestHeaders
     * @return
     */
    public static ByteCache get(String url, Charset charset, Map<String, String> requestHeaders)
    {
        HttpURLConnection httpUrlConn;
        try
        {
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();
        }
        catch (IOException e1)
        {
            throw new RuntimeException(e1);
        }
        try
        {
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod("GET");
            if (requestHeaders != null)
            {
                for (Entry<String, String> each : requestHeaders.entrySet())
                {
                    httpUrlConn.setRequestProperty(each.getKey(), each.getValue());
                }
            }
            httpUrlConn.connect();
            InputStream inputStream = httpUrlConn.getInputStream();
            ByteCache cache = cacheLocal.get();
            cache.clear();
            cache.ensureCapacity(1024);
            int readSum = -1;
            while ((readSum = inputStream.read(cache.getDirectArray(), cache.getWriteIndex(), 1000)) != -1)
            {
                cache.setCount(cache.getWriteIndex() + readSum);
                cache.ensureCapacity(1000 + cache.getWriteIndex());
            }
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            return cache;
        }
        catch (IOException e)
        {
            httpUrlConn.disconnect();
            throw new RuntimeException(e);
        }
    }
    
    public static String post(String url, Charset charset, Map<String, String> params, Map<String, String> headers)
    {
        HttpURLConnection httpUrlConn;
        try
        {
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();
        }
        catch (IOException e1)
        {
            throw new RuntimeException(e1);
        }
        try
        {
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod("POST");
            if (headers != null)
            {
                for (Entry<String, String> each : headers.entrySet())
                {
                    httpUrlConn.setRequestProperty(each.getKey(), each.getValue());
                }
            }
            ByteCache cache = cacheLocal.get().clear();
            OutputStream outputStream = httpUrlConn.getOutputStream();
            if (params != null)
            {
                outputStream.write(buildParam(params).getBytes(charset));
            }
            InputStream inputStream = httpUrlConn.getInputStream();
            cache.clear();
            cache.ensureCapacity(1024);
            int readSum = -1;
            while ((readSum = inputStream.read(cache.getDirectArray(), cache.getWriteIndex(), 1000)) != -1)
            {
                cache.setCount(cache.getWriteIndex() + readSum);
                cache.ensureCapacity(1000);
            }
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            return cache.toString(charset);
        }
        catch (IOException e)
        {
            httpUrlConn.disconnect();
            throw new RuntimeException(e);
        }
    }
    
    private static String buildParam(Map<String, String> map)
    {
        StringCache stringCache = stringLocal.get();
        stringCache.clear();
        boolean append = false;
        for (Entry<String, String> each : map.entrySet())
        {
            if (each.getValue() != null)
            {
                stringCache.append(each.getKey()).append('=').append(each.getValue()).append('&');
                append = true;
            }
        }
        if (append)
        {
            stringCache.deleteLast();
        }
        return stringCache.toString();
    }
    
    /**
     * 向url地址post方式发送一段文本信息
     * 
     * @param url
     * @param postStr
     * @param charset
     * @return
     */
    public static String post(String url, String postStr, Charset charset)
    {
        HttpURLConnection httpUrlConn;
        try
        {
            httpUrlConn = (HttpURLConnection) new URL(url).openConnection();
        }
        catch (IOException e1)
        {
            throw new RuntimeException(e1);
        }
        try
        {
            httpUrlConn.setDoOutput(true);
            httpUrlConn.setDoInput(true);
            httpUrlConn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            httpUrlConn.setRequestMethod("POST");
            ByteCache cache = cacheLocal.get().clear();
            httpUrlConn.getOutputStream().write(postStr.getBytes(charset));
            httpUrlConn.getOutputStream().flush();
            InputStream inputStream = httpUrlConn.getInputStream();
            cache.clear();
            cache.ensureCapacity(1024);
            int readSum = -1;
            while ((readSum = inputStream.read(cache.getDirectArray(), cache.getWriteIndex(), 1000)) != -1)
            {
                cache.setCount(cache.getWriteIndex() + readSum);
                cache.ensureCapacity(1000);
            }
            // 释放资源
            inputStream.close();
            inputStream = null;
            httpUrlConn.disconnect();
            return cache.toString(charset);
        }
        catch (IOException e)
        {
            httpUrlConn.disconnect();
            throw new RuntimeException(e);
        }
    }
    
    public static String getIp()
    {
        try
        {
            String text = SimpleHttpTool.get("http://1111.ip138.com/ic.asp", Charset.forName("gb2312"), null).toString(Charset.forName("utf8"));
            int left = text.indexOf('[');
            int right = text.indexOf(']');
            return text.substring(left + 1, right);
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    public static String getPhoneInfo(String phone)
    {
        String text = SimpleHttpTool.get("http://m.ip138.com/mobile.asp?mobile=" + phone, Charset.forName("gb2312"), null).toString(Charset.forName("utf8"));
        int start = text.indexOf("<tr><td>区 号</td><td><span>") + 26;
        int end = text.indexOf("</span>", start);
        String areaCode = text.substring(start, end);
        start = text.indexOf("<tr><td>卡 类 型</td><td><span>") + 28;
        end = text.indexOf("</span>", start);
        String operator = text.substring(start, end).substring(0, 2);
        return areaCode + ',' + operator;
    }
    
    public static void main(String[] args)
    {
        System.out.println(getPhoneInfo("13705955910"));
    }
}
