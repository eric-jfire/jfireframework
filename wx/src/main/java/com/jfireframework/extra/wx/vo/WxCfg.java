package com.jfireframework.extra.wx.vo;

public class WxCfg
{
    private String appId;
    private String appSecret;
    private String token;
    private int    expires_in;
    private long   accessTime;
    
    public WxCfg(String appId, String appSecret)
    {
        this.appId = appId;
        this.appSecret = appSecret;
    }

    public boolean expire()
    {
        return System.currentTimeMillis() - accessTime > expires_in;
    }
    
    public String getAppId()
    {
        return appId;
    }
    
    public void setAppId(String appId)
    {
        this.appId = appId;
    }
    
    public String getAppSecret()
    {
        return appSecret;
    }
    
    public void setAppSecret(String appSecret)
    {
        this.appSecret = appSecret;
    }
    
    public String getToken()
    {
        return token;
    }
    
    public void setToken(String token)
    {
        this.token = token;
    }
    
    public int getExpires_in()
    {
        return expires_in;
    }
    
    public void setExpires_in(int expires_in)
    {
        this.expires_in = expires_in;
    }
    
    public long getAccessTime()
    {
        return accessTime;
    }
    
    public void setAccessTime(long accessTime)
    {
        this.accessTime = accessTime;
    }
    
}
