package com.jfireframework.sql.page;

import java.sql.Connection;
import java.sql.SQLException;
import com.jfireframework.sql.resultsettransfer.TransferContext;

public interface PageParse
{
    public void doQuery(Object[] params, Connection connection, String sql, Class<?> type, TransferContext transferContext, Page page) throws SQLException;
}
