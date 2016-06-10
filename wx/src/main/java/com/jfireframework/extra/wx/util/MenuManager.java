package com.jfireframework.extra.wx.util;

import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.extra.wx.vo.MenuButton;
import com.jfireframework.extra.wx.vo.WxCfg;

public class MenuManager
{
    private static final String menuCreateUrl = "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=";
    private static final String menuGetUrl    = "https://api.weixin.qq.com/cgi-bin/menu/get?access_token=";
    
    public static String getMenu(WxCfg wxCfg)
    {
        return HttpsTool.get(menuGetUrl + TokenTool.getToken(wxCfg));
    }
    
    public static String createMenu(MenuButton[] buttons, WxCfg wxCfg)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.put("button", buttons);
        System.out.println(JsonTool.write(jsonObject));
        return HttpsTool.post(menuCreateUrl + TokenTool.getToken(wxCfg), JsonTool.write(jsonObject));
    }
    
}
