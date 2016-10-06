package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class IntegerTransfer extends AbstractResultsetTransfer<Integer>
{
    
    public IntegerTransfer(Class<Integer> entityClass)
    {
        super(entityClass);
    }
    
    @Override
    public List<Integer> transferList(ResultSet resultSet) throws SQLException
    {
        List<Integer> list = new LinkedList<Integer>();
        while (resultSet.next())
        {
            list.add(resultSet.getInt(1));
        }
        return list;
    }
    
    @Override
    protected Integer valueOf(ResultSet resultSet) throws SQLException
    {
        return Integer.valueOf(resultSet.getInt(1));
    }
    
}
