package com.jfireframework.dbunit.schema.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.dbunit.table.Table;

public class CleanWork
{
    private static Logger logger = ConsoleLogFactory.getLogger();
    
    /**
     * 清除整个数据库的数据
     * 
     * @throws SQLException
     */
    public static void clearDbData(DataSource dataSource, Table[] sortTables)
    {
        try (Connection clearConn = dataSource.getConnection())
        {
            clearConn.setAutoCommit(false);
            // 删除数据库必须要从子表开始，因为外键依赖的关系，必须要从最深的子表开始往上删除
            for (Table each : sortTables)
            {
                PreparedStatement pStatement = clearConn.prepareStatement(each.getDeleteSql());
                pStatement.executeUpdate();
            }
            clearConn.commit();
        }
        catch (Exception e)
        {
            logger.error("清除数据库现场失败，数据回滚", e);
            throw new RuntimeException(e);
        }
        
    }
}
