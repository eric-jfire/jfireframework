package com.jfireframework.mvc.config;

public class MvcStaticConfig
{
    public static final String paramMap                       = "Request_Param_map";
    public static final String encode                         = "UTF-8";
    /** Default Servlet name used by Tomcat, Jetty, JBoss, and GlassFish */
    public static final String COMMON_DEFAULT_SERVLET_NAME    = "default";
    
    /** Default Servlet name used by Google App Engine */
    public static final String GAE_DEFAULT_SERVLET_NAME       = "_ah_default";
    
    /** Default Servlet name used by Resin */
    public static final String RESIN_DEFAULT_SERVLET_NAME     = "resin-file";
    
    /** Default Servlet name used by WebLogic */
    public static final String WEBLOGIC_DEFAULT_SERVLET_NAME  = "FileServlet";
    
    /** Default Servlet name used by WebSphere */
    public static final String WEBSPHERE_DEFAULT_SERVLET_NAME = "SimpleFileServlet";
}
