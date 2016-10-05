package com.jfireframework.sql.dbstructure;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.IdStrategy;
import com.jfireframework.sql.metadata.TableMetaData;
import com.jfireframework.sql.metadata.TableMetaData.FieldInfo;

public class MariaDBStructure implements Structure
{
    private Logger                                logger    = ConsoleLogFactory.getLogger();
    
    protected static Map<Class<?>, TypeAndLength> dbTypeMap = new HashMap<Class<?>, TypeAndLength>();
    
    static
    {
        dbTypeMap.put(int.class, new TypeAndLength("int", 9));
        dbTypeMap.put(long.class, new TypeAndLength("bigint", 11));
        dbTypeMap.put(Integer.class, new TypeAndLength("int", 9));
        dbTypeMap.put(Long.class, new TypeAndLength("bigint", 11));
        dbTypeMap.put(String.class, new TypeAndLength("varchar", 255));
        dbTypeMap.put(Date.class, new TypeAndLength("datetime", 0));
        dbTypeMap.put(java.util.Date.class, new TypeAndLength("datetime", 0));
        dbTypeMap.put(Timestamp.class, new TypeAndLength("datetime", 0));
        dbTypeMap.put(float.class, new TypeAndLength("float", 0));
        dbTypeMap.put(Float.class, new TypeAndLength("float", 0));
        dbTypeMap.put(double.class, new TypeAndLength("double", 0));
        dbTypeMap.put(Double.class, new TypeAndLength("double", 0));
        dbTypeMap.put(Time.class, new TypeAndLength("time", 0));
        dbTypeMap.put(boolean.class, new TypeAndLength("tinyint", 1));
        dbTypeMap.put(Boolean.class, new TypeAndLength("tinyint", 1));
    }
    
    @Override
    public void createTable(DataSource dataSource, TableMetaData[] metaDatas) throws SQLException
    {
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            for (TableMetaData metaData : metaDatas)
            {
                if (metaData.getIdInfo() == null)
                {
                    continue;
                }
                String tableName = metaData.getTableName();
                FieldInfo idInfo = metaData.getIdInfo();
                IdStrategy idStrategy = metaData.getIdStrategy();
                StringCache cache = new StringCache();
                cache.append("CREATE TABLE ").append(tableName).append(" (");
                cache.append(idInfo.getDbColName()).append(' ');
                TypeAndLength typeAndLength = getTypeAndLength(idInfo.getField());
                cache.append(typeAndLength.getDbType());
                if (idStrategy.equals(IdStrategy.nativeDb) && (idInfo.getField().getType() == Integer.class || idInfo.getField().getType() == Long.class))
                {
                    cache.append(" AUTO_INCREMENT ");
                }
                cache.append(" primary key").appendComma();
                for (FieldInfo each : metaData.getFieldInfos())
                {
                    if (each.getFieldName().equals(idInfo.getFieldName()) || each.isSaveIgnore() || each.isDaoIgnore())
                    {
                        continue;
                    }
                    try
                    {
                        cache.append(each.getDbColName()).append(' ').append(getTypeAndLength(each.getField()).getDbType()).appendComma();
                    }
                    catch (Exception e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                cache.deleteLast().append(")");
                logger.warn("进行表:{}的创建，创建语句是\n{}", tableName, cache.toString());
                connection.prepareStatement("DROP TABLE IF EXISTS " + tableName).execute();
                connection.prepareStatement(cache.toString()).execute();
            }
            connection.commit();
        }
        finally
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        
    }
    
    private TypeAndLength getTypeAndLength(Field field)
    {
        try
        {
            TypeAndLength typeAndLength = dbTypeMap.get(field.getType());
            if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).length() != -1)
            {
                int length = field.getAnnotation(Column.class).length();
                if (field.getType() == String.class && length > 3000)
                {
                    typeAndLength.setLength(3000);
                    typeAndLength.setType("text");
                }
            }
            return typeAndLength;
        }
        catch (Exception e)
        {
            throw new IllegalArgumentException(StringUtil.format("不识别的建表类型属性:{}.{}", field.getDeclaringClass().getName(), field.getName()));
        }
    }
    
    @Override
    public void updateTable(DataSource dataSource, TableMetaData[] metaDatas) throws SQLException
    {
        Connection connection = null;
        try
        {
            connection = dataSource.getConnection();
            connection.setAutoCommit(false);
            for (TableMetaData metaData : metaDatas)
            {
                try
                {
                    String tableName = metaData.getTableName();
                    String addColSql = "alter table " + tableName + " add ";
                    String describeSql = "describe " + tableName + ' ';
                    String modityColSql = "alter table " + tableName + " modify ";
                    connection.prepareStatement("describe " + tableName).execute();
                    FieldInfo idInfo = metaData.getIdInfo();
                    ResultSet rs = connection.prepareStatement("describe " + tableName + " " + idInfo.getDbColName()).executeQuery();
                    if (rs.next())
                    {
                        // 字段存在，需要执行更新操作
                        if (metaData.getIdStrategy() == IdStrategy.nativeDb && (idInfo.getField().getType() == Integer.class || idInfo.getField().getType() == Long.class))
                        {
                            logger.debug("执行sql语句:{}", "alter table " + tableName + " modify " + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType() + " auto_increment");
                            connection.prepareStatement("alter table " + tableName + " modify " + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType() + " auto_increment").execute();
                        }
                        else
                        {
                            logger.debug("执行sql语句:{}", "alter table " + tableName + " modify " + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType());
                            connection.prepareStatement("alter table " + tableName + " modify " + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType()).execute();
                        }
                    }
                    else
                    {
                        // 字段不存在，需要执行新建动作
                        if (metaData.getIdStrategy() == IdStrategy.nativeDb && (idInfo.getField().getType() == Integer.class || idInfo.getField().getType() == Long.class))
                        {
                            logger.warn("执行sql语句:{}", addColSql + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType() + " auto_increment");
                            connection.prepareStatement(addColSql + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType() + " auto_increment").execute();
                        }
                        else
                        {
                            logger.warn("执行sql语句:{}", addColSql + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType());
                            connection.prepareStatement(addColSql + idInfo.getDbColName() + ' ' + getTypeAndLength(idInfo.getField()).getDbType()).execute();
                        }
                    }
                    rs.close();
                    for (FieldInfo each : metaData.getFieldInfos())
                    {
                        if (each.getDbColName().equals(idInfo.getDbColName()))
                        {
                            continue;
                        }
                        rs = connection.prepareStatement(describeSql + each.getDbColName()).executeQuery();
                        if (rs.next())
                        {
                            logger.debug("执行sql语句:{}", modityColSql + each.getDbColName() + ' ' + getTypeAndLength(each.getField()));
                            connection.prepareStatement(modityColSql + each.getDbColName() + ' ' + getTypeAndLength(each.getField())).execute();
                        }
                        else
                        {
                            logger.debug("执行sql语句:{}", addColSql + each.getDbColName() + ' ' + getTypeAndLength(each.getField()));
                            connection.prepareStatement(addColSql + each.getDbColName() + ' ' + getTypeAndLength(each.getField())).execute();
                        }
                        rs.close();
                    }
                }
                catch (Exception e)
                {
                    createTable(dataSource, metaDatas);
                }
            }
            connection.commit();
        }
        finally
        {
            if (connection != null)
            {
                connection.close();
            }
        }
    }
    
}
