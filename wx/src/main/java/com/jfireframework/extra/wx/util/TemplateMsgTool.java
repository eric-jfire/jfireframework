package com.jfireframework.extra.wx.util;

import com.jfireframework.codejson.JsonTool;
import com.jfireframework.extra.wx.vo.TemplateInfo;
import com.jfireframework.extra.wx.vo.WxCfg;

public class TemplateMsgTool
{
    private static final String url = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=ACCESS_TOKEN";
    
    public static String send(TemplateInfo info, WxCfg wxCfg)
    {
        return HttpsTool.post(url.replace("ACCESS_TOKEN", TokenTool.getToken(wxCfg)), JsonTool.write(info));
    }
    
}
