package com.jfireframework.mvc.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.servlet.AsyncContext;
import javax.servlet.DispatcherType;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpUpgradeHandler;
import javax.servlet.http.Part;

public class MockRequest implements HttpServletRequest
{
    private Map<String, String> map;
    
    public MockRequest(Map<String, String> map)
    {
        this.map = map;
    }
    
    @Override
    public AsyncContext getAsyncContext()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Object getAttribute(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Enumeration<String> getAttributeNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getCharacterEncoding()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getContentLength()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public long getContentLengthLong()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public String getContentType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public DispatcherType getDispatcherType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public ServletInputStream getInputStream() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getLocalAddr()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getLocalName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getLocalPort()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public Locale getLocale()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Enumeration<Locale> getLocales()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getParameter(String arg0)
    {
        String value = map.get(arg0);
        if (value == null)
        {
            return null;
        }
        else
        {
            return value;
        }
    }
    
    @Override
    public Map<String, String[]> getParameterMap()
    {
        return new HashMap<>();
    }
    
    @Override
    public Enumeration<String> getParameterNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String[] getParameterValues(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getProtocol()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public BufferedReader getReader() throws IOException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRealPath(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRemoteAddr()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRemoteHost()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getRemotePort()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public RequestDispatcher getRequestDispatcher(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getScheme()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getServerName()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getServerPort()
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public ServletContext getServletContext()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isAsyncStarted()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isAsyncSupported()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isSecure()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void removeAttribute(String arg0)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setAttribute(String arg0, Object arg1)
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void setCharacterEncoding(String arg0) throws UnsupportedEncodingException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public AsyncContext startAsync() throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public AsyncContext startAsync(ServletRequest arg0, ServletResponse arg1) throws IllegalStateException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean authenticate(HttpServletResponse arg0) throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public String changeSessionId()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getAuthType()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getContextPath()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Cookie[] getCookies()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public long getDateHeader(String arg0)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public String getHeader(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Enumeration<String> getHeaderNames()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Enumeration<String> getHeaders(String arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public int getIntHeader(String arg0)
    {
        // TODO Auto-generated method stub
        return 0;
    }
    
    @Override
    public String getMethod()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Part getPart(String arg0) throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Collection<Part> getParts() throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getPathInfo()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getPathTranslated()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getQueryString()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRemoteUser()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRequestURI()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public StringBuffer getRequestURL()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getRequestedSessionId()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getServletPath()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public HttpSession getSession()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public HttpSession getSession(boolean arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public Principal getUserPrincipal()
    {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public boolean isRequestedSessionIdFromCookie()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isRequestedSessionIdFromURL()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isRequestedSessionIdFromUrl()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isRequestedSessionIdValid()
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public boolean isUserInRole(String arg0)
    {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public void login(String arg0, String arg1) throws ServletException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void logout() throws ServletException
    {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> arg0) throws IOException, ServletException
    {
        // TODO Auto-generated method stub
        return null;
    }
    
}
