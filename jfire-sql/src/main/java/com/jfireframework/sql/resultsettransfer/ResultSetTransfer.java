package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.util.List;

public interface ResultSetTransfer<T>
{
    public T transfer(ResultSet resultSet, String sql) throws Exception;
    
    public List<T> transferList(ResultSet resultSet, String sql) throws Exception;
}
