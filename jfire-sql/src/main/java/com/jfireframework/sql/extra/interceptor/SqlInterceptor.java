package com.jfireframework.sql.extra.interceptor;

public interface SqlInterceptor
{
    public static class InterceptorContext
    {
        private String   sql;
        private Object[] params;
        
        public String getSql()
        {
            return sql;
        }
        
        public void setSql(String sql)
        {
            this.sql = sql;
        }
        
        public Object[] getParams()
        {
            return params;
        }
        
        public void setParams(Object[] params)
        {
            this.params = params;
        }
        
    }
    
    public void intercept(InterceptorContext context);
}
