package com.jfireframework.sql.function.impl;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.sql.annotation.FindBy;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.extra.dbstructure.NameStrategy;
import com.jfireframework.sql.extra.interceptor.SqlPreInterceptor;
import com.jfireframework.sql.function.Dao;
import com.jfireframework.sql.function.LockMode;
import com.jfireframework.sql.metadata.TableMetaData;
import com.jfireframework.sql.metadata.TableMetaData.FieldInfo;
import com.jfireframework.sql.resultsettransfer.field.MapField;
import com.jfireframework.sql.resultsettransfer.field.MapFieldBuilder;
import sun.misc.Unsafe;

public class DAOBeanImpl<T> implements Dao<T>
{
    private final Class<T>            entityClass;
    private final Map<String, String> findBySqlMap = new HashMap<String, String>();
    // 代表数据库主键id的field
    private final MapField            idField;
    private final long                idOffset;
    private final IdType              idType;
    private final static Unsafe       unsafe       = ReflectUtil.getUnsafe();
    private final String              tableName;
    private final SqlAndFields        getInfo;
    private final SqlAndFields        getInShareInfo;
    private final SqlAndFields        getForUpdateInfo;
    private final SqlAndFields        insertInfo;
    private final SqlAndFields        updateInfo;
    private final String              deleteSql;
    private static final Logger       LOGGER       = ConsoleLogFactory.getLogger();
    private final SqlPreInterceptor[] preInterceptors;
    
    enum IdType
    {
        INT, LONG, STRING
    }
    
    @SuppressWarnings("unchecked")
    public DAOBeanImpl(TableMetaData metaData, SqlPreInterceptor[] preInterceptors)
    {
        this.preInterceptors = preInterceptors;
        this.entityClass = (Class<T>) metaData.getEntityClass();
        NameStrategy nameStrategy = metaData.getNameStrategy();
        tableName = entityClass.getAnnotation(TableEntity.class).name();
        MapField[] allMapFields = buildMapfields(metaData.getFieldInfos(), nameStrategy);
        for (MapField mapField : allMapFields)
        {
            if (mapField.getField().isAnnotationPresent(FindBy.class))
            {
                String sql = "select * from " + tableName + " where " + mapField.getColName() + " = ?";
                findBySqlMap.put(mapField.getFieldName(), sql);
            }
        }
        Field t_idField = metaData.getIdInfo().getField();
        idType = getIdType(t_idField);
        idField = MapFieldBuilder.buildMapField(t_idField, nameStrategy);
        idOffset = unsafe.objectFieldOffset(t_idField);
        MapField[] insertOrUpdateFields = buildInsertOrUpdateFields(allMapFields);
        insertInfo = buildInsertSql(insertOrUpdateFields);
        updateInfo = buildUpdateSql(insertOrUpdateFields);
        getInfo = buildGetInfo(allMapFields);
        getForUpdateInfo = buildGetForUpdateInfo(allMapFields);
        getInShareInfo = buildGetInShareInfo(allMapFields);
        deleteSql = "delete from " + tableName + " where " + idField.getColName() + "=?";
        logSql();
    }
    
    private void logSql()
    {
        LOGGER.debug("为表{},类{}创建的插入语句是{}", tableName, entityClass.getName(), insertInfo.getSql());
        LOGGER.debug("为表{},类{}创建的更新语句是{}", tableName, entityClass.getName(), updateInfo.getSql());
        LOGGER.debug("为表{},类{}创建的获取语句是{}", tableName, entityClass.getName(), getInfo.getSql());
        LOGGER.debug("为表{},类{}创建的获取加锁语句是{}", tableName, entityClass.getName(), getForUpdateInfo.getSql());
        LOGGER.debug("为表{},类{}创建的获取共享语句是{}", tableName, entityClass.getName(), getInShareInfo.getSql());
        LOGGER.debug("为表{},类{}创建的删除语句是{}", tableName, entityClass.getName(), deleteSql);
    }
    
    private IdType getIdType(Field field)
    {
        Class<?> type = field.getType();
        if (type == String.class)
        {
            return IdType.STRING;
        }
        else if (type == Integer.class)
        {
            return IdType.INT;
        }
        else if (type == Long.class)
        {
            return IdType.LONG;
        }
        else
        {
            throw new UnsupportedOperationException("id字段只支持Integer，Long，String");
        }
        
    }
    
    private MapField[] buildInsertOrUpdateFields(MapField[] mapFields)
    {
        List<MapField> tmp = new LinkedList<MapField>();
        for (MapField each : mapFields)
        {
            if (each.saveIgnore())
            {
                continue;
            }
            tmp.add(each);
        }
        return tmp.toArray(new MapField[tmp.size()]);
        
    }
    
    private MapField[] buildMapfields(FieldInfo[] infos, NameStrategy nameStrategy)
    {
        List<MapField> list = new ArrayList<MapField>(infos.length);
        for (FieldInfo each : infos)
        {
            list.add(MapFieldBuilder.buildMapField(each.getField(), nameStrategy));
        }
        return list.toArray(new MapField[list.size()]);
    }
    
    private SqlAndFields buildInsertSql(MapField[] insertOrUpdateFields)
    {
        StringCache cache = new StringCache();
        /******** 生成insertSql *******/
        cache.append("insert into ").append(tableName).append(" ( ");
        for (MapField each : insertOrUpdateFields)
        {
            cache.append(each.getColName()).append(',');
        }
        cache.deleteLast().append(") values (");
        cache.appendStrsByComma("?", insertOrUpdateFields.length);
        cache.append(')');
        return new SqlAndFields(cache.toString(), insertOrUpdateFields);
    }
    
    private SqlAndFields buildUpdateSql(MapField[] insertOrUpdateFields)
    {
        StringCache cache = new StringCache();
        cache.append("update ").append(tableName).append(" set ");
        List<MapField> tmps = new LinkedList<MapField>();
        for (MapField each : insertOrUpdateFields)
        {
            if (each.getColName().equals(idField.getColName()))
            {
                continue;
            }
            cache.append(each.getColName()).append("=?,");
            tmps.add(each);
        }
        cache.deleteLast().append(" where ").append(idField.getColName()).append("=?");
        tmps.add(idField);
        return new SqlAndFields(cache.toString(), tmps.toArray(new MapField[tmps.size()]));
    }
    
    private SqlAndFields buildGetInfo(MapField[] getFields)
    {
        StringCache cache = new StringCache();
        /******** 生成getSql ******/
        cache.clear();
        cache.append("select ");
        for (MapField each : getFields)
        {
            if (each.loadIgnore())
            {
                continue;
            }
            cache.append(each.getColName()).append(",");
        }
        cache.deleteLast().append(" from ").append(tableName).append(" where ").append(idField.getColName()).append("=?");
        return new SqlAndFields(cache.toString(), getFields);
    }
    
    private SqlAndFields buildGetInShareInfo(MapField[] getFields)
    {
        StringCache cache = new StringCache();
        /******** 生成getSql ******/
        cache.clear();
        cache.append("select ");
        for (MapField each : getFields)
        {
            if (each.loadIgnore())
            {
                continue;
            }
            cache.append(each.getColName()).append(",");
        }
        cache.deleteLast().append(" from ").append(tableName).append(" where ").append(idField.getColName()).append("=? lock in share mode");
        return new SqlAndFields(cache.toString(), getFields);
    }
    
    private SqlAndFields buildGetForUpdateInfo(MapField[] allFields)
    {
        StringCache cache = new StringCache();
        /******** 生成getSql ******/
        cache.clear();
        cache.append("select ");
        for (MapField each : allFields)
        {
            if (each.loadIgnore())
            {
                continue;
            }
            cache.append(each.getColName()).append(",");
        }
        cache.deleteLast().append(" from ").append(tableName).append(" where ").append(idField.getColName()).append("=? for update");
        return new SqlAndFields(cache.toString(), allFields);
    }
    
    @Override
    public int delete(Object entity, Connection connection)
    {
        PreparedStatement pstat = null;
        try
        {
            pstat = connection.prepareStatement(deleteSql);
            switch (idType)
            {
                case INT:
                    pstat.setInt(1, (Integer) unsafe.getObject(entity, idOffset));
                    break;
                case LONG:
                    pstat.setLong(1, (Long) unsafe.getObject(entity, idOffset));
                    break;
                case STRING:
                    pstat.setString(1, (String) unsafe.getObject(entity, idOffset));
                    break;
            }
            return pstat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (pstat != null)
            {
                try
                {
                    pstat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T getById(Object pk, Connection connection)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(getInfo.getSql(), pk);
        }
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(getInfo.getSql());
            switch (idType)
            {
                case INT:
                    pStat.setInt(1, (Integer) pk);
                    break;
                case LONG:
                    pStat.setLong(1, (Long) pk);
                    break;
                case STRING:
                    pStat.setString(1, (String) pk);
                    break;
            }
            ResultSet resultSet = pStat.executeQuery();
            if (resultSet.next())
            {
                Object entity = entityClass.newInstance();
                for (MapField each : getInfo.getFields())
                {
                    each.setEntityValue(entity, resultSet);
                }
                return (T) entity;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public void save(T entity, Connection connection)
    {
        Object idValue = unsafe.getObject(entity, idOffset);
        if (idValue == null)
        {
            insert(entity, connection);
        }
        else
        {
            for (SqlPreInterceptor each : preInterceptors)
            {
                each.preIntercept(updateInfo.getSql(), entity);
            }
            // id有值，执行更新操作
            PreparedStatement pStat = null;
            try
            {
                pStat = connection.prepareStatement(updateInfo.getSql());
                int index = 1;
                for (MapField each : updateInfo.getFields())
                {
                    each.setStatementValue(pStat, entity, index);
                    index++;
                }
                pStat.executeUpdate();
            }
            catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
            finally
            {
                if (pStat != null)
                {
                    try
                    {
                        pStat.close();
                    }
                    catch (SQLException e)
                    {
                        throw new JustThrowException(e);
                    }
                }
            }
        }
        
    }
    
    @Override
    public int update(T entity, Connection connection)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(updateInfo.getSql(), entity);
        }
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(updateInfo.getSql());
            int index = 1;
            for (MapField each : updateInfo.getFields())
            {
                each.setStatementValue(pStat, entity, index);
                index++;
            }
            return pStat.executeUpdate();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public void batchInsert(List<T> entitys, Connection connection)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(insertInfo.getSql(), entitys);
        }
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(insertInfo.getSql());
            for (Object entity : entitys)
            {
                int index = 1;
                for (MapField field : insertInfo.getFields())
                {
                    field.setStatementValue(pStat, entity, index);
                    index++;
                }
                pStat.addBatch();
            }
            pStat.executeBatch();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public void insert(T entity, Connection connection)
    {
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(insertInfo.getSql(), entity);
        }
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(insertInfo.getSql(), Statement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (MapField each : insertInfo.getFields())
            {
                each.setStatementValue(pStat, entity, index);
                index++;
            }
            pStat.executeUpdate();
            ResultSet resultSet = pStat.getGeneratedKeys();
            if (resultSet.next())
            {
                switch (idType)
                {
                    case INT:
                        unsafe.putObject(entity, idOffset, resultSet.getInt(1));
                        break;
                    case LONG:
                        unsafe.putObject(entity, idOffset, resultSet.getLong(1));
                    case STRING:
                        unsafe.putObject(entity, idOffset, resultSet.getString(1));
                        break;
                }
            }
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public T getById(Object pk, Connection connection, LockMode mode)
    {
        String sql = mode == LockMode.SHARE ? getInShareInfo.getSql() : getForUpdateInfo.getSql();
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(sql, pk);
        }
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(sql);
            pStat.setObject(1, pk);
            ResultSet resultSet = pStat.executeQuery();
            if (resultSet.next())
            {
                Object entity = entityClass.newInstance();
                for (MapField each : getInfo.getFields())
                {
                    each.setEntityValue(entity, resultSet);
                }
                return (T) entity;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
    @Override
    public String getTableName()
    {
        return tableName;
    }
    
    @Override
    public MapField getIdField()
    {
        return idField;
    }
    
    /**
     * 得到描述数据库结构的field字段
     * 
     * @return
     */
    @Override
    public MapField[] getStructureFields()
    {
        return insertInfo.getFields();
    }
    
    @Override
    public T findBy(String name, Object param, Connection connection)
    {
        String findBy = findBySqlMap.get(name);
        if (findBy == null)
        {
            throw new NullPointerException("没有对应条件的findBy");
        }
        for (SqlPreInterceptor each : preInterceptors)
        {
            each.preIntercept(findBy, param);
        }
        PreparedStatement pStat = null;
        try
        {
            pStat = connection.prepareStatement(findBy);
            pStat.setObject(1, param);
            ResultSet resultSet = pStat.executeQuery();
            if (resultSet.next())
            {
                T entity = entityClass.newInstance();
                for (MapField each : getInfo.getFields())
                {
                    each.setEntityValue(entity, resultSet);
                }
                idField.setEntityValue(entity, resultSet);
                if (resultSet.next())
                {
                    throw new IllegalArgumentException("查询存在两个或以上的数据，不符合要求");
                }
                return entity;
            }
            else
            {
                return null;
            }
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        finally
        {
            if (pStat != null)
            {
                try
                {
                    pStat.close();
                }
                catch (SQLException e)
                {
                    throw new JustThrowException(e);
                }
            }
        }
    }
    
}

class SqlAndFields
{
    private final String     sql;
    private final MapField[] fields;
    
    public SqlAndFields(String sql, MapField[] fields)
    {
        this.sql = sql;
        this.fields = fields;
    }
    
    public String getSql()
    {
        return sql;
    }
    
    public MapField[] getFields()
    {
        return fields;
    }
    
}
