package com.jfireframework.sql.dbstructure;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.sql.annotation.IdStrategy;
import com.jfireframework.sql.field.MapField;
import com.jfireframework.sql.function.impl.DAOBeanImpl;

public class MariaDBStructure implements Structure
{
    private Logger                                logger    = ConsoleLogFactory.getLogger();
    
    protected static Map<Class<?>, TypeAndLength> dbTypeMap = new HashMap<Class<?>, TypeAndLength>();
    
    static
    {
        dbTypeMap.put(int.class, new TypeAndLength("int", 9));
        dbTypeMap.put(long.class, new TypeAndLength("int", 11));
        dbTypeMap.put(Integer.class, new TypeAndLength("int", 9));
        dbTypeMap.put(Long.class, new TypeAndLength("int", 11));
        dbTypeMap.put(String.class, new TypeAndLength("varchar", 255));
        dbTypeMap.put(Date.class, new TypeAndLength("datetime", 0));
        dbTypeMap.put(java.util.Date.class, new TypeAndLength("datetime", 0));
        dbTypeMap.put(float.class, new TypeAndLength("float", 0));
        dbTypeMap.put(Float.class, new TypeAndLength("float", 0));
        dbTypeMap.put(double.class, new TypeAndLength("double", 0));
        dbTypeMap.put(Double.class, new TypeAndLength("double", 0));
        dbTypeMap.put(Time.class, new TypeAndLength("time", 0));
        dbTypeMap.put(Timestamp.class, new TypeAndLength("timestamp", 0));
        dbTypeMap.put(boolean.class, new TypeAndLength("tinyint", 1));
        dbTypeMap.put(Boolean.class, new TypeAndLength("tinyint", 1));
    }
    
    @Override
    public void createTable(Connection connection, DAOBeanImpl daoBean) throws SQLException
    {
        String tableName = daoBean.getTableName();
        MapField idField = daoBean.getIdField();
        IdStrategy idStrategy = daoBean.getIdStrategy();
        StringCache cache = new StringCache();
        cache.append("CREATE TABLE ").append(tableName).append(" (");
        cache.append(idField.getColName()).append(' ');
        cache.append(StructureTool.getDbType(idField, dbTypeMap));
        if (idStrategy.equals(IdStrategy.autoIncrement))
        {
            cache.append(" AUTO_INCREMENT ");
        }
        cache.append(" primary key").appendComma();
        for (MapField each : daoBean.getStructureFields())
        {
            if (each.getColName().equals(idField.getColName()))
            {
                continue;
            }
            cache.append(each.getColName()).append(' ').append(StructureTool.getDbType(each, dbTypeMap)).appendComma();
        }
        cache.deleteLast().append(")");
        logger.warn("进行表:{}的创建，创建语句是\n{}", tableName, cache.toString());
        connection.prepareStatement("DROP TABLE IF EXISTS " + tableName).execute();
        connection.prepareStatement(cache.toString()).execute();
    }
    
    @Override
    public void updateTable(Connection connection, DAOBeanImpl daoBean) throws SQLException
    {
        String tableName = daoBean.getTableName();
        MapField idField = daoBean.getIdField();
        IdStrategy idStrategy = daoBean.getIdStrategy();
        // 判断表是否存在
        try
        {
            String addColSql = "alter table " + tableName + " add ";
            String describeSql = "describe " + tableName + ' ';
            String modityColSql = "alter table " + tableName + " modify ";
            connection.prepareStatement("describe " + tableName).execute();
            // 成功执行代表表格存在
            logger.warn("为表:{}执行更新表结构操作", tableName);
            logger.warn("判断id字段：{}的信息，执行sql语句:{}", idField.getColName(), "describe " + tableName + " " + idField.getColName());
            ResultSet rs = connection.prepareStatement("describe " + tableName + " " + idField.getColName()).executeQuery();
            if (rs.next())
            {
                // 字段存在，需要执行更新操作
                if (idStrategy.equals(IdStrategy.autoIncrement))
                {
                    logger.warn("执行sql语句:{}", "alter table " + tableName + " modify " + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap) + " auto_increment");
                    connection.prepareStatement("alter table " + tableName + " modify " + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap) + " auto_increment").execute();
                }
                else
                {
                    logger.warn("执行sql语句:{}", "alter table " + tableName + " modify " + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap));
                    connection.prepareStatement("alter table " + tableName + " modify " + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap)).execute();
                }
            }
            else
            {
                // 字段不存在，需要执行新建动作
                if (idStrategy.equals(IdStrategy.autoIncrement))
                {
                    logger.warn("执行sql语句:{}", addColSql + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap) + " auto_increment");
                    connection.prepareStatement(addColSql + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap) + " auto_increment").execute();
                }
                else
                {
                    logger.warn("执行sql语句:{}", addColSql + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap));
                    connection.prepareStatement(addColSql + idField.getColName() + ' ' + StructureTool.getDbType(idField, dbTypeMap)).execute();
                }
            }
            rs.close();
            for (MapField each : daoBean.getStructureFields())
            {
                if (each.getColName().equals(idField.getColName()))
                {
                    continue;
                }
                rs = connection.prepareStatement(describeSql + each.getColName()).executeQuery();
                if (rs.next())
                {
                    logger.warn("执行sql语句:{}", modityColSql + each.getColName() + ' ' + StructureTool.getDbType(each, dbTypeMap));
                    connection.prepareStatement(modityColSql + each.getColName() + ' ' + StructureTool.getDbType(each, dbTypeMap)).execute();
                }
                else
                {
                    logger.warn("执行sql语句:{}", addColSql + each.getColName() + ' ' + StructureTool.getDbType(each, dbTypeMap));
                    connection.prepareStatement(addColSql + each.getColName() + ' ' + StructureTool.getDbType(each, dbTypeMap)).execute();
                }
            }
        }
        catch (SQLException e)
        {
            createTable(connection, daoBean);
        }
    }
    
}
