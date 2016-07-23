package com.jfireframework.sql.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import com.jfireframework.baseutil.exception.UnSupportException;

public class SqlTool
{
    @SuppressWarnings("unchecked")
    public static <T> Object getValue(ResultSet resultSet, int index, Class<T> type) throws SQLException
    {
        if (type == String.class)
        {
            return (T) getString(resultSet, index);
        }
        else if (type == Double.class)
        {
            return (T) getWDouble(resultSet, index);
        }
        else if (type == Float.class)
        {
            return (T) getWFloat(resultSet, index);
        }
        else if (type == Integer.class)
        {
            return (T) getInteger(resultSet, index);
        }
        else if (type == Boolean.class)
        {
            return (T) getWBoolean(resultSet, index);
        }
        else if (type == Long.class)
        {
            return (T) getWLong(resultSet, index);
        }
        else if (type == int.class)
        {
            return resultSet.getInt(index);
        }
        else if (type == long.class)
        {
            return resultSet.getLong(index);
        }
        else if (type == boolean.class)
        {
            return resultSet.getBoolean(index);
        }
        else if (type == float.class)
        {
            return resultSet.getFloat(index);
        }
        else if (type == short.class)
        {
            return resultSet.getShort(index);
        }
        else if (type == double.class)
        {
            return resultSet.getDouble(index);
        }
        else if (type == byte.class)
        {
            return resultSet.getByte(index);
        }
        else
        {
            throw new UnSupportException("不支持的读取类型" + type.getName());
        }
    }
    
    private static String getString(ResultSet resultSet, int index) throws SQLException
    {
        return resultSet.getString(index);
    }
    
    private static Integer getInteger(ResultSet resultSet, int index) throws SQLException
    {
        int value = resultSet.getInt(index);
        if (resultSet.wasNull())
        {
            return null;
        }
        else
        {
            return value;
        }
    }
    
    private static Long getWLong(ResultSet resultSet, int index) throws SQLException
    {
        long value = resultSet.getLong(index);
        if (resultSet.wasNull())
        {
            return null;
        }
        else
        {
            return value;
        }
    }
    
    private static Float getWFloat(ResultSet resultSet, int index) throws SQLException
    {
        float value = resultSet.getFloat(index);
        if (resultSet.wasNull())
        {
            return null;
        }
        else
        {
            return value;
        }
    }
    
    private static Double getWDouble(ResultSet resultSet, int index) throws SQLException
    {
        double value = resultSet.getDouble(index);
        if (resultSet.wasNull())
        {
            return null;
        }
        else
        {
            return value;
        }
    }
    
    private static Boolean getWBoolean(ResultSet resultSet, int index) throws SQLException
    {
        int value = resultSet.getInt(index);
        if (resultSet.wasNull())
        {
            return null;
        }
        else
        {
            if (value == 0)
            {
                return false;
            }
            else
            {
                return true;
            }
        }
    }
    
}
