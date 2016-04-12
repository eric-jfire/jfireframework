package com.jfireframework.sql.util;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SqlTool
{
    @SuppressWarnings("unchecked")
    public static <T> Object getValue(ResultSet resultSet, int index, Class<T> type) throws SQLException
    {
        switch (type.getSimpleName())
        {
            case "String":
                return (T) getString(resultSet, index);
            case "Double":
                return (T) getWDouble(resultSet, index);
            case "Float":
                return (T) getWFloat(resultSet, index);
            case "Integer":
                return (T) getInteger(resultSet, index);
            case "Boolean":
                return (T) getWBoolean(resultSet, index);
            case "Long":
                return (T) getWLong(resultSet, index);
            case "int":
                return resultSet.getInt(index);
            case "long":
                return resultSet.getLong(index);
            case "boolean":
                return resultSet.getBoolean(index);
            case "float":
                return resultSet.getFloat(index);
            case "double":
                return resultSet.getDouble(index);
            case "short":
                return resultSet.getShort(index);
            case "byte":
                return resultSet.getByte(index);
            default:
                throw new RuntimeException("不支持的读取类型" + type.getName());
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
