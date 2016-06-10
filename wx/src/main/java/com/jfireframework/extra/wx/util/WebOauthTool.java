package com.jfireframework.extra.wx.util;

import com.jfireframework.extra.wx.vo.WxCfg;

public class WebOauthTool
{
    public final static String access_token_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    
    /**
     * 使用微信Oauth获得的code或者Oauth授权信息
     * 
     * @param code
     * @return
     */
    public static String getOauthInfo(String code, WxCfg wxCfg)
    {
        String realUrl = access_token_url.replace("APPID", wxCfg.getAppId()).replace("SECRET", wxCfg.getAppSecret()).replace("CODE", code);
        return HttpsTool.get(realUrl).toString();
    }
}
