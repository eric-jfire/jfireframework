package com.jfireframework.dbunit.schema.work;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.dbunit.table.Row;
import com.jfireframework.dbunit.table.Table;

public class RestoreWork
{
    private static Logger logger = ConsoleLogFactory.getLogger();
    
    /**
     * 恢复数据库现场
     */
    public static void restoreDbData(DataSource dataSource, Table[] sortTables)
    {
        CleanWork.clearDbData(dataSource, sortTables);
        try
        {
            Connection restoreConn = dataSource.getConnection();
            try
            {
                restoreConn.setAutoCommit(false);
                for (Table each : sortTables)
                {
                    PreparedStatement pStat = restoreConn.prepareStatement(each.getInsertSql());
                    LightSet<Row> tableData = each.getRowArray();
                    for (Row row : tableData)
                    {
                        setRowData(row, pStat);
                        pStat.addBatch();
                    }
                    pStat.executeBatch();
                }
                restoreConn.commit();
            }
            catch (SQLException e)
            {
                restoreConn.rollback();
                logger.error("数据库现场恢复失败", e);
            }
            restoreConn.close();
        }
        catch (SQLException e1)
        {
            throw new RuntimeException(e1);
        }
        
    }
    
    /**
     * 将数据表行数据设置到sql预处理对象之中
     * 
     * @param row
     * @param pStatement
     * @throws SQLException
     */
    private static void setRowData(Row row, PreparedStatement pStatement) throws SQLException
    {
        String[] rowData = row.getData();
        for (int column = 0; column < rowData.length; column++)
        {
            pStatement.setString(column + 1, rowData[column]);
        }
    }
}
