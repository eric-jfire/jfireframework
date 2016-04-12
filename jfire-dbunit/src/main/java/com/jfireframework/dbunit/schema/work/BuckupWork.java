package com.jfireframework.dbunit.schema.work;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.dbunit.table.Row;
import com.jfireframework.dbunit.table.Table;

public class BuckupWork
{
    /**
     * 执行备份工作，保存数据到内存中
     * 
     * @param data
     * @param workMode
     * @throws SQLException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void buckupDbData(DataSource dataSource, Map<String, Table> tableMap)
    {
        try (Connection queryTableData = dataSource.getConnection())
        {
            for (Table each : tableMap.values())
            {
                LightSet<String> colNameList = each.getColNameList();
                ResultSet dataSet = queryTableData.prepareStatement(each.getSelectSql()).executeQuery();
                while (dataSet.next())
                {
                    String[] rowData = getRowData(dataSet, colNameList);
                    each.addRowData(new Row(rowData));
                    each.addRowCount();
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 根据列名列表，从当前的resultset对象中取得一行的数据并且以String数组形式返回
     * 
     * @param dataSet
     * @param colNameList
     * @return
     * @throws SQLException
     */
    private static String[] getRowData(ResultSet dataSet, LightSet<String> colNameList) throws SQLException
    {
        int length = colNameList.size();
        String[] rowData = new String[length];
        int index = 0;
        for (String colName : colNameList)
        {
            rowData[index++] = dataSet.getString(colName);
        }
        return rowData;
    }
}
