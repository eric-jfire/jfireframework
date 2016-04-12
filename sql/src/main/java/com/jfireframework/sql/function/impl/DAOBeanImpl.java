package com.jfireframework.sql.function.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.annotation.Column;
import com.jfireframework.sql.annotation.Id;
import com.jfireframework.sql.annotation.IdStrategy;
import com.jfireframework.sql.annotation.SqlIgnore;
import com.jfireframework.sql.annotation.TableEntity;
import com.jfireframework.sql.field.MapField;
import com.jfireframework.sql.field.impl.IntegerField;
import com.jfireframework.sql.field.impl.StringField;
import com.jfireframework.sql.field.impl.WLongField;
import com.jfireframework.sql.function.DAOBean;
import com.jfireframework.sql.function.LockMode;
import com.jfireframework.sql.util.DaoFactory;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class DAOBeanImpl implements DAOBean
{
    private static Logger             logger          = ConsoleLogFactory.getLogger();
    private Class<?>                  entityClass;
    // 存储类的属性名和其对应的Mapfield映射关系
    private Map<String, MapField>     fieldMap        = new HashMap<>();
    // 代表数据库主键id的field
    private MapField                  idField;
    private long                      idOffset;
    private static Unsafe             unsafe          = ReflectUtil.getUnsafe();
    private String                    tableName;
    private IdStrategy                idStrategy;
    private SqlAndFields              deleteInfo;
    private SqlAndFields              batchDeleteInfo;
    private SqlAndFields              getInfo;
    private SqlAndFields              getInShareInfo;
    private SqlAndFields              getForUpdateInfo;
    private SqlAndFields              insertInfo;
    private SqlAndFields              updateInfo;
    private Map<String, SqlAndFields> selectUpdateMap = new ConcurrentHashMap<>();
    private Map<String, SqlAndFields> selectGetMap    = new ConcurrentHashMap<>();
                                                      
    public DAOBeanImpl(Class<?> entityClass)
    {
        this.entityClass = entityClass;
        Field[] fields = ReflectUtil.getAllFields(entityClass);
        LightSet<MapField> set = new LightSet<>();
        for (Field each : fields)
        {
            if (each.isAnnotationPresent(SqlIgnore.class) || Map.class.isAssignableFrom(each.getType()) || List.class.isAssignableFrom(each.getType()) || each.getType().isInterface() || each.getType().isArray() || Modifier.isStatic(each.getModifiers()))
            {
                continue;
            }
            if (each.isAnnotationPresent(Column.class))
            {
                if (each.getAnnotation(Column.class).daoIgnore())
                {
                    continue;
                }
            }
            set.add(DaoFactory.buildMapField(each));
            if (each.isAnnotationPresent(Id.class))
            {
                idField = DaoFactory.buildMapField(each);
                idOffset = unsafe.objectFieldOffset(each);
                idStrategy = each.getAnnotation(Id.class).idStrategy();
                if (idStrategy == IdStrategy.autoDecision)
                {
                    Class<?> type = each.getType();
                    if (type.equals(Integer.class) || type.equals(int.class) || type.equals(Long.class) || type.equals(long.class))
                    {
                        idStrategy = IdStrategy.autoIncrement;
                    }
                    else
                    {
                        idStrategy = IdStrategy.nativeDb;
                    }
                }
            }
        }
        Verify.notNull(idField, "使用TableEntity映射的表必须由id字段，请检查{}", entityClass.getName());
        LightSet<MapField> tmp = new LightSet<>();
        for (MapField each : set)
        {
            if (each.saveIgnore())
            {
                continue;
            }
            tmp.add(each);
        }
        MapField[] insertOrUpdateFields = tmp.toArray(MapField.class);
        for (MapField each : insertOrUpdateFields)
        {
            fieldMap.put(each.getFieldName(), each);
        }
        MapField[] getFields = set.toArray(MapField.class);
        buildSql(insertOrUpdateFields, getFields);
    }
    
    private void buildSql(MapField[] insertOrUpdateFields, MapField[] getFields)
    {
        TableEntity tableEntity = entityClass.getAnnotation(TableEntity.class);
        tableName = tableEntity.name();
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
        insertInfo = new SqlAndFields();
        insertInfo.setSql(cache.toString());
        insertInfo.setFields(insertOrUpdateFields);
        /******** 生成insertSql *******/
        /******** 生成updatesql *******/
        cache.clear();
        cache.append("update ").append(tableName).append(" set ");
        LightSet<MapField> tmps = new LightSet<>();
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
        updateInfo = new SqlAndFields();
        updateInfo.setSql(cache.toString());
        updateInfo.setFields(tmps.toArray(MapField.class));
        /******** 生成updatesql *******/
        /******** 生成deletesql *****/
        cache.clear();
        cache.append("delete from ").append(tableName).append("  where ").append(idField.getColName()).append("=?");
        deleteInfo = new SqlAndFields();
        deleteInfo.setSql(cache.toString());
        batchDeleteInfo = new SqlAndFields();
        batchDeleteInfo.setSql("delete from " + tableName + " where " + idField.getColName() + " in (");
        /******** 生成deletesql *****/
        /******** 生成getSql ******/
        cache.clear();
        cache.append("select ");
        for (MapField each : getFields)
        {
            cache.append(each.getColName()).append(",");
        }
        cache.deleteLast().append(" from ").append(tableName).append(" where ").append(idField.getColName()).append("=?");
        getInfo = new SqlAndFields();
        getInfo.setSql(cache.toString());
        getInfo.setFields(getFields);
        getInShareInfo = new SqlAndFields();
        getInShareInfo.setSql(cache.toString() + " LOCK IN SHARE MODE");
        getInShareInfo.setFields(getFields);
        getForUpdateInfo = new SqlAndFields();
        getForUpdateInfo.setSql(cache.toString() + " FOR UPDATE");
        getForUpdateInfo.setFields(getFields);
        /******** 生成getSql ******/
        logger.debug("为类：{}生成的\rsave  语句是: {},\rdelete语句是: {},\rupdate语句是: {},\rget   语句是: {}\r", entityClass.getName(), insertInfo.getSql(), deleteInfo.getSql(), updateInfo.getSql(), getInfo.getSql());
    }
    
    @Override
    public boolean delete(Object entity, Connection connection)
    {
        try (PreparedStatement pstat = connection.prepareStatement(deleteInfo.getSql()))
        {
            pstat.setObject(1, unsafe.getObject(entity, idOffset));
            pstat.executeUpdate();
            return true;
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getById(Object pk, Connection connection)
    {
        try (PreparedStatement pStat = connection.prepareStatement(getInfo.getSql()))
        {
            logger.trace("执行的sql是{}", getInfo.getSql());
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
        catch (SQLException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getById(Object pk, Connection connection, String fieldNames)
    {
        SqlAndFields sqlAndFields = selectGetMap.get(fieldNames);
        if (sqlAndFields == null)
        {
            StringCache cache = new StringCache("select ");
            LightSet<MapField> set = new LightSet<>();
            for (String each : fieldNames.split(","))
            {
                MapField tmp = fieldMap.get(each);
                set.add(tmp);
                cache.append(tmp.getColName()).append(", ");
            }
            cache.append(idField.getColName());
            set.add(idField);
            cache.append(" from ").append(tableName).append(" where ").append(idField.getColName()).append(" = ?");
            sqlAndFields = new SqlAndFields();
            sqlAndFields.setSql(cache.toString());
            sqlAndFields.setFields(set.toArray(MapField.class));
        }
        logger.trace("执行的sql语句是{}", sqlAndFields.getSql());
        try (PreparedStatement pStat = connection.prepareStatement(sqlAndFields.getSql()))
        {
            pStat.setObject(1, pk);
            ResultSet resultSet = pStat.executeQuery();
            if (resultSet.next())
            {
                Object entity = entityClass.newInstance();
                for (MapField each : sqlAndFields.getFields())
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
        catch (SQLException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public <T> void save(T entity, Connection connection)
    {
        Object idValue = unsafe.getObject(entity, idOffset);
        if (idValue == null)
        {
            // id值为null，执行插入操作
            insert(entity, connection);
        }
        else
        {
            // id有值，执行更新操作
            try (PreparedStatement pStat = connection.prepareStatement(updateInfo.getSql()))
            {
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
        }
        
    }
    
    @Override
    public <T> void batchInsert(List<T> entitys, Connection connection)
    {
        try (PreparedStatement pStat = connection.prepareStatement(insertInfo.getSql()))
        {
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
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public <T> int update(T entity, Connection connection, String fieldNames)
    {
        SqlAndFields sqlAndFields = selectUpdateMap.get(fieldNames);
        if (sqlAndFields == null)
        {
            StringCache cache = new StringCache("update ");
            cache.append(tableName).append(" set ");
            LightSet<MapField> set = new LightSet<>();
            for (String each : fieldNames.split(","))
            {
                MapField tmp = fieldMap.get(each);
                set.add(tmp);
                cache.append(tmp.getColName()).append("=?, ");
            }
            cache.deleteEnds(2).append(" where ").append(idField.getColName()).append("=?");
            sqlAndFields = new SqlAndFields();
            sqlAndFields.setSql(cache.toString());
            sqlAndFields.setFields(set.toArray(MapField.class));
        }
        logger.trace("执行的sql语句是{}", sqlAndFields.getSql());
        try (PreparedStatement pStat = connection.prepareStatement(sqlAndFields.getSql()))
        {
            int index = 1;
            for (MapField each : sqlAndFields.getFields())
            {
                each.setStatementValue(pStat, entity, index);
                index++;
            }
            idField.setStatementValue(pStat, entity, index);
            return pStat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public <T> void insert(T entity, Connection connection)
    {
        try (PreparedStatement pStat = connection.prepareStatement(insertInfo.getSql(), Statement.RETURN_GENERATED_KEYS))
        {
            int index = 1;
            for (MapField each : insertInfo.getFields())
            {
                each.setStatementValue(pStat, entity, index);
                index++;
            }
            pStat.executeUpdate();
            ResultSet resultSet = pStat.getGeneratedKeys();
            resultSet.next();
            if (idField instanceof IntegerField)
            {
                unsafe.putObject(entity, idOffset, resultSet.getInt(1));
            }
            else if (idField instanceof StringField)
            {
                unsafe.putObject(entity, idOffset, resultSet.getString(1));
            }
            else if (idField instanceof WLongField)
            {
                unsafe.putObject(entity, idOffset, resultSet.getLong(1));
            }
            else
            {
                throw new RuntimeException(StringUtil.format("id字段暂时支持Integer,Long,String.请检查{}", entity.getClass().getName()));
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T getById(Object pk, Connection connection, LockMode mode)
    {
        String sql = mode == LockMode.SHARE ? getInShareInfo.getSql() : getForUpdateInfo.getSql();
        try (PreparedStatement pStat = connection.prepareStatement(sql))
        {
            logger.trace("执行的sql是{}", sql);
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
        catch (SQLException | InstantiationException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public int deleteByIds(String ids, Connection connection)
    {
        StringCache cache = new StringCache(batchDeleteInfo.getSql());
        ArrayList<String> params = new ArrayList<>(16);
        for (String id : ids.split(","))
        {
            cache.append("?,");
            params.add(id);
        }
        if (params.size() == 0)
        {
            throw new RuntimeException("ids中不包含实际的id值，请加上判断");
        }
        cache.deleteLast().append(')');
        try (PreparedStatement pStat = connection.prepareStatement(cache.toString()))
        {
            logger.trace("执行的sql是{}", cache.toString());
            int index = 1;
            for (String each : params)
            {
                pStat.setObject(index++, each);
            }
            return pStat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public int deleteByIds(int[] ids, Connection connection)
    {
        StringCache cache = new StringCache(batchDeleteInfo.getSql());
        ArrayList<Integer> params = new ArrayList<>(16);
        for (int id : ids)
        {
            cache.append("?,");
            params.add(id);
        }
        if (params.size() == 0)
        {
            throw new RuntimeException("ids中不包含实际的id值，请加上判断");
        }
        cache.deleteLast().append(')');
        try (PreparedStatement pStat = connection.prepareStatement(cache.toString()))
        {
            logger.trace("执行的sql是{}", cache.toString());
            int index = 1;
            for (Integer each : params)
            {
                pStat.setObject(index++, each);
            }
            return pStat.executeUpdate();
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public String getTableName()
    {
        return tableName;
    }
    
    public IdStrategy getIdStrategy()
    {
        return idStrategy;
    }
    
    public MapField getIdField()
    {
        return idField;
    }
    
    /**
     * 得到描述数据库结构的field字段
     * 
     * @return
     */
    public MapField[] getStructureFields()
    {
        return insertInfo.getFields();
    }
}

class SqlAndFields
{
    private String     sql;
    private MapField[] fields;
                       
    public String getSql()
    {
        return sql;
    }
    
    public void setSql(String sql)
    {
        this.sql = sql;
    }
    
    public MapField[] getFields()
    {
        return fields;
    }
    
    public void setFields(MapField[] fields)
    {
        this.fields = fields;
    }
    
}
