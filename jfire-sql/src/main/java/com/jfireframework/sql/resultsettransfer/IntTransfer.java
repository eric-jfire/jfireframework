package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import com.jfireframework.baseutil.StringUtil;

public class IntTransfer extends AbstractResultsetTransfer<Integer>
{
    
    public IntTransfer()
    {
        super(Integer.class);
    }
    
    @Override
    public Integer transfer(ResultSet resultSet) throws SQLException
    {
        if (resultSet.next())
        {
            Integer result = Integer.valueOf(resultSet.getInt(1));
            if (resultSet.next())
            {
                throw new IllegalArgumentException(StringUtil.format("存在2行数据，不符合返回值要求。"));
            }
            else
            {
                return result;
            }
        }
        else
        {
            return null;
        }
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
    
}
