package com.jfireframework.dbunit.schema.work;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import com.jfireframework.dbunit.table.Table;

public class BuildSchemaStructureWork
{
    
    /**
     * 备份数据库结构信息,刷新框架内存储的数据库信息,并且对数据库内的表进行依赖顺序排序
     * 
     * @param url
     * @param sqlDriver
     * @throws SQLException
     */
    public static Table[] buildSchemaStructure(DataSource dataSource, Map<String, Table> tableMap) throws SQLException
    {
        tableMap.clear();
        Connection queryMetaData = dataSource.getConnection();
        DatabaseMetaData metaData = queryMetaData.getMetaData();
        buildTableSelf(metaData, tableMap);
        buildrelatedtablesForAll(tableMap);
        Table[] tables = AnalyseWork.sortSchema(tableMap);
        queryMetaData.close();
        return tables;
    }
    
    /**
     * 使用查询出来的数据库表元信息构建所有的表情况（包括表名，列名，父表名）
     * 
     * @param resultSet
     * @param metaData
     * @param tableMap
     * @throws SQLException
     */
    private static void buildTableSelf(DatabaseMetaData metaData, Map<String, Table> tableMap) throws SQLException
    {
        ResultSet resultSet = metaData.getTables(null, null, "%", new String[] { "TABLE" });
        while (resultSet.next())
        {
            Table self = new Table();
            self.setTableName(resultSet.getString("TABLE_NAME"));
            self.buildStructure(metaData);
            tableMap.put(self.getTableName(), self);
        }
    }
    
    private static void buildrelatedtablesForAll(Map<String, Table> tableMap)
    {
        for (Map.Entry<String, Table> entry : tableMap.entrySet())
        {
            Table each = entry.getValue();
            for (String fatherTableName : each.getFatherTableNames())
            {
                Table fatherTable = tableMap.get(fatherTableName);
                each.addFatherTable(fatherTable);
                fatherTable.addChildTable(each);
            }
        }
    }
    
}
