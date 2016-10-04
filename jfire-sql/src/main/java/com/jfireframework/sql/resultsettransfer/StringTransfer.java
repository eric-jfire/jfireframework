package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

public class StringTransfer extends AbstractResultsetTransfer<String>
{
    
    public StringTransfer()
    {
        super(String.class);
    }
    
    @Override
    public String transfer(ResultSet resultSet) throws Exception
    {
        if (resultSet.next())
        {
            return resultSet.getString(1);
        }
        else
        {
            return null;
        }
    }
    
    @Override
    public List<String> transferList(ResultSet resultSet) throws Exception
    {
        List<String> list = new LinkedList<String>();
        while (resultSet.next())
        {
            list.add(resultSet.getString(1));
        }
        return list;
    }
    
}
