package com.jfireframework.sql.function.sqloperation;

import java.util.List;
import com.jfireframework.sql.page.Page;

public interface SqlOperator
{
    public <T> T query(Class<T> type, String sql, Object... params);
    
    public <T> List<T> queryList(Class<T> type, String sql, Object... params);
    
    public <T> List<T> queryList(Class<T> type, String sql, Page page, Object... params);
    
    public int update(String sql, Object... params);
}
