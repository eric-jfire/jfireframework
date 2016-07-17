package com.jfireframework.extra.wx.vo;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;

public class WxJsSign
{
    private String               noncestr;
    private String               timestamp;
    private String               jsapi_ticket;
    private String               url;
    private static final Charset CHARSET = Charset.forName("utf8");
    
    public String sign()
    {
        try
        {
            StringCache cache = new StringCache();
            cache.append("jsapi_ticket=").append(jsapi_ticket)//
                    .append("&noncestr=").append(noncestr)//
                    .append("&timestamp=").append(timestamp)//
                    .append("&url=").append(url);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(cache.toString().getBytes(CHARSET));
            byte[] result = md.digest();
            return StringUtil.toHexString(result);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    public String getNoncestr()
    {
        return noncestr;
    }
    
    public void setNoncestr(String noncestr)
    {
        this.noncestr = noncestr;
    }
    
    public String getTimestamp()
    {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }
    
    public String getJsapi_ticket()
    {
        return jsapi_ticket;
    }
    
    public void setJsapi_ticket(String jsapi_ticket)
    {
        this.jsapi_ticket = jsapi_ticket;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public void setUrl(String url)
    {
        this.url = url;
    }
    
}
