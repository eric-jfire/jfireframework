package com.jfireframework.sql.dbstructure;

import java.sql.Connection;
import java.sql.SQLException;
import com.jfireframework.sql.function.impl.DAOBeanImpl;

public interface Structure
{
    public void createTable(Connection connection, DAOBeanImpl daoBean) throws SQLException;
    
    public void updateTable(Connection connection, DAOBeanImpl daoBean) throws SQLException;
}
