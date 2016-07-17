package com.jfireframework.extra.wx.vo;

import java.util.TreeMap;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.encrypt.Md5Util;

/**
 * 微信支付验证签名算法
 * @author linbin
 *
 */
public class WxPaySignMap extends TreeMap<String, String>
{
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private final String      key;
    
    public WxPaySignMap(String key)
    {
        this.key = key;
    }
    
    public String sign()
    {
        StringCache cache = new StringCache();
        for (String each : this.navigableKeySet())
        {
            cache.append(each).append('=').append(get(each)).append('&');
        }
        cache.append("key=").append(key);
        return Md5Util.md5Str(cache.toString()).toUpperCase();
    }
}
