package com.jfireframework.sql.dbstructure;

import java.sql.Connection;
import java.sql.SQLException;
import com.jfireframework.sql.function.Dao;

public interface Structure
{
    public void createTable(Connection connection, Dao<?> daoBean) throws SQLException;
    
    public void updateTable(Connection connection, Dao<?> daoBean) throws SQLException;
}
