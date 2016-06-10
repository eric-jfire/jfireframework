package com.jfireframework.extra.wx.util;

import com.jfireframework.codejson.JsonTool;
import com.jfireframework.extra.wx.vo.AccessToken;
import com.jfireframework.extra.wx.vo.WxCfg;

public class TokenTool
{
    private static final String lock        = "";
    private static final String getTokenUrl = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    
    /**
     * 获取访问token
     * 
     * @return
     */
    public static String getToken(WxCfg wxCfg)
    {
        if (wxCfg.expire() == false)
        {
            return wxCfg.getToken();
        }
        else
        {
            synchronized (lock)
            {
                if (wxCfg.expire() == false)
                {
                    return wxCfg.getToken();
                }
                String url = getTokenUrl.replace("APPID", wxCfg.getAppId()).replace("APPSECRET", wxCfg.getAppSecret());
                AccessToken accessToken = JsonTool.read(AccessToken.class, HttpsTool.get(url));
                if (accessToken.getAccess_token() != null && accessToken.getAccess_token().equals("") == false)
                {
                    wxCfg.setAccessTime(System.currentTimeMillis());
                    wxCfg.setToken(accessToken.getAccess_token());
                    return accessToken.getAccess_token();
                }
                else
                {
                    throw new RuntimeException("获取微信获取token失败，失败信息是" + accessToken.getErrmsg());
                }
            }
        }
    }
}
