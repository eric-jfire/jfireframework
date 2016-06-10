package com.jfireframework.extra.wx.util;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509TrustManager;

/**
 * 证书信任管理器，该类意味着信任所有证书，只用来对微信提供服务
 * 
 * @author liufeng
 * @date 2013-08-08
 */
public class MyX509TrustManager implements X509TrustManager
{
    
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
    }
    
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
    {
    }
    
    public X509Certificate[] getAcceptedIssuers()
    {
        return null;
    }
}
