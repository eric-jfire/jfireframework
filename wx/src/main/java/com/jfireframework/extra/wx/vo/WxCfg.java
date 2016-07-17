package com.jfireframework.extra.wx.vo;

public class WxCfg
{
    private final String    appId;
    private final String    appSecret;
    private volatile String token;
    private volatile long   token_expires_in;
    private volatile long   token_accessTime;
    private volatile String jsApiTicket;
    private volatile long   jsApiTicket_expirex_in;
    private volatile long   jsApiTicket_accessTime;
    
    public WxCfg(String appId, String appSecret)
    {
        this.appId = appId;
        this.appSecret = appSecret;
    }
    
    public boolean tokenExpire()
    {
        return System.currentTimeMillis() - token_accessTime > token_expires_in;
    }
    
    public boolean jsApiTicketExpire()
    {
        return System.currentTimeMillis() - jsApiTicket_accessTime > jsApiTicket_expirex_in;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public String getAppSecret()
    {
        return appSecret;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public long getToken_expires_in()
    {
        return token_expires_in;
    }
    
    public void setToken_expires_in(long token_expires_in)
    {
        this.token_expires_in = token_expires_in * 1000;
    }
    
    public long getToken_accessTime()
    {
        return token_accessTime;
    }
    
    public void setToken_accessTime(long token_accessTime)
    {
        this.token_accessTime = token_accessTime;
    }
    
    public String getJsApiTicket()
    {
        return jsApiTicket;
    }
    
    public void setJsApiTicket(String jsApiTicket)
    {
        this.jsApiTicket = jsApiTicket;
    }
    
    public long getJsApiTicket_expirex_in()
    {
        return jsApiTicket_expirex_in;
    }
    
    public void setJsApiTicket_expirex_in(long jsApiTicket_expirex_in)
    {
        this.jsApiTicket_expirex_in = jsApiTicket_expirex_in * 1000;
    }
    
    public long getJsApiTicket_accessTime()
    {
        return jsApiTicket_accessTime;
    }
    
    public void setJsApiTicket_accessTime(long jsApiTicket_accessTime)
    {
        this.jsApiTicket_accessTime = jsApiTicket_accessTime;
    }
    
}
