package com.jfireframework.extra.wx.util;

import com.jfireframework.codejson.JsonTool;
import com.jfireframework.extra.wx.vo.MediaQuery;
import com.jfireframework.extra.wx.vo.WxCfg;

public class MediaManager
{
    private static final String mediaList = "https://api.weixin.qq.com/cgi-bin/material/batchget_material?access_token=";
    
    public static String mediaList(WxCfg wxCfg, MediaQuery query)
    {
        return HttpsTool.post(mediaList + TokenTool.getToken(wxCfg), JsonTool.write(query));
    }
    
}
