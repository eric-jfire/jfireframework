package com.jfireframework.sql.dbstructure;

import java.sql.SQLException;
import javax.sql.DataSource;
import com.jfireframework.sql.metadata.TableMetaData;

public interface Structure
{
    public void createTable(DataSource dataSource, TableMetaData[] metaDatas) throws SQLException;
    
    public void updateTable(DataSource dataSource, TableMetaData[] metaDatas) throws SQLException;
}
