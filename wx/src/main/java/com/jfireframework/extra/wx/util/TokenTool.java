package com.jfireframework.extra.wx.util;

import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.extra.wx.vo.AccessToken;
import com.jfireframework.extra.wx.vo.WxCfg;

public final class TokenTool
{
    private static final String getTokenUrl       = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
    private static final String getJsApiTicketUrl = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=ACCESS_TOKEN&type=jsapi";
    
    public static String getJsApiTicket(WxCfg wxCfg)
    {
        if (wxCfg.jsApiTicketExpire() == false)
        {
            return wxCfg.getJsApiTicket();
        }
        synchronized (wxCfg)
        {
            if (wxCfg.jsApiTicketExpire() == false)
            {
                return wxCfg.getJsApiTicket();
            }
            String token = getToken(wxCfg);
            String result = HttpsTool.get(getJsApiTicketUrl.replace("ACCESS_TOKEN", token));
            JsonObject jsonObject = ((JsonObject) JsonTool.fromString(result));
            String jsApiTicket = jsonObject.getWString("ticket");
            int expire = jsonObject.getInt("expires_in");
            wxCfg.setJsApiTicket_accessTime(System.currentTimeMillis());
            wxCfg.setJsApiTicket_expirex_in(0);
            wxCfg.setJsApiTicket(jsApiTicket);
            wxCfg.setJsApiTicket_expirex_in(expire);
            return jsApiTicket;
        }
    }
    
    /**
     * 获取访问token
     * 
     * @return
     */
    public static String getToken(WxCfg wxCfg)
    {
        if (wxCfg.tokenExpire() == false)
        {
            return wxCfg.getToken();
        }
        else
        {
            synchronized (wxCfg)
            {
                if (wxCfg.tokenExpire() == false)
                {
                    return wxCfg.getToken();
                }
                String url = getTokenUrl.replace("APPID", wxCfg.getAppId()).replace("APPSECRET", wxCfg.getAppSecret());
                AccessToken accessToken = JsonTool.read(AccessToken.class, HttpsTool.get(url));
                if (accessToken.getAccess_token() != null && accessToken.getAccess_token().equals("") == false)
                {
                    wxCfg.setToken_accessTime(System.currentTimeMillis());
                    wxCfg.setToken_expires_in(accessToken.getExpires_in());
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
