package com.jfireframework.dbunit.table;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.collection.set.LightSet;

/**
 * 数据库表的映射类
 * 
 * @author windfire(windfire@zailanghua.com)
 * 
 */
public class Table implements Comparable<Table>
{
    // 表的名字
    private String           tableName;
    // 该表所持有的外键对应的外键表名称
    private LightSet<String> fatherTableNames = new LightSet<String>();
    private LightSet<Table>  fatherTables     = new LightSet<Table>();
    // 持有该表主键作为外键的表名称
    private LightSet<Table>  childTables      = new LightSet<Table>();
    private LightSet<String> colNameList      = new LightSet<String>();
    private LightSet<Row>    rowArray         = new LightSet<Row>();
    private int              orderNum         = 0;
    private int              rowCount         = 0;
    private String           selectSql;
    private String           deleteSql;
    private String           insertSql;
    
    /**
     * 使用数据库元数据信息，分析表结构，并且存储在对象实例之中
     * 
     * @param metaData
     * @throws SQLException
     */
    public void buildStructure(DatabaseMetaData metaData) throws SQLException
    {
        buildColList(metaData);
        buildFatherTableNames(metaData);
        buildSql();
    }
    
    public boolean isCalcuted()
    {
        return orderNum != 0;
    }
    
    public void initOrderNum()
    {
        orderNum = 1;
    }
    
    public void addOrder()
    {
        orderNum++;
    }
    
    /**
     * 增加一个行计数
     */
    public void addRowCount()
    {
        rowCount++;
    }
    
    /**
     * 返回总行数
     * 
     * @return
     */
    public int getRowSum()
    {
        return rowCount;
    }
    
    /**
     * 通过表元数据信息，将父表加入到自身信息中
     * 
     * @param metaData
     * @throws SQLException
     */
    private void buildFatherTableNames(DatabaseMetaData metaData) throws SQLException
    {
        // 获取表所持有的外键的表的表名
        ResultSet fkSet = metaData.getImportedKeys(null, null, tableName);
        while (fkSet.next())
        {
            fatherTableNames.add(fkSet.getString("PKTABLE_NAME"));
        }
    }
    
    /**
     * 通过数据库元数据信息，给自身生成列名和列类型信息
     * 
     * @param metaData
     * @throws SQLException
     */
    private void buildColList(DatabaseMetaData metaData) throws SQLException
    {
        ResultSet columnSet = metaData.getColumns(null, null, tableName, "%");
        while (columnSet.next())
        {
            String columnName = columnSet.getString("COLUMN_NAME");
            colNameList.add(columnName);
        }
    }
    
    /**
     * 增加一个持有该表主键作为外键的表
     * 
     */
    public void addChildTable(Table childTable)
    {
        childTables.add(childTable);
    }
    
    public void addFatherTable(Table fatherTable)
    {
        fatherTables.add(fatherTable);
    }
    
    /**
     * 增加一行数据，数据的顺序和列名的顺序对应
     * 
     * @param data
     */
    public void addRowData(Row row)
    {
        rowArray.add(row);
    }
    
    public LightSet<String> getColNameList()
    {
        return colNameList;
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public void setTableName(String tableName)
    {
        this.tableName = tableName;
    }
    
    public LightSet<Row> getRowArray()
    {
        return rowArray;
    }
    
    public LightSet<Table> getFatherTables()
    {
        return fatherTables;
    }
    
    public LightSet<String> getFatherTableNames()
    {
        return fatherTableNames;
    }
    
    public int getOrder()
    {
        return orderNum;
    }
    
    @Override
    public int compareTo(Table table)
    {
        if (orderNum > table.getOrder())
        {
            return 1;
        }
        else if (orderNum == table.getOrder())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    
    private void buildSql()
    {
        deleteSql = "delete from " + tableName;
        selectSql = "select * from " + tableName;
        StringCache cache = new StringCache();
        cache.append("insert into ").append(tableName).append(" (");
        for (String columnName : colNameList)
        {
            cache.append(columnName).appendComma();
        }
        cache.deleteLast();
        cache.append(" ) values(");
        for (int i = 0; i < colNameList.size(); i++)
        {
            cache.append("?,");
        }
        cache.deleteLast();
        cache.append(" )");
        insertSql = cache.toString();
        cache.clear();
    }
    
    public String getSelectSql()
    {
        return selectSql;
    }
    
    public String getDeleteSql()
    {
        return deleteSql;
    }
    
    public String getInsertSql()
    {
        return insertSql;
    }
    
}
