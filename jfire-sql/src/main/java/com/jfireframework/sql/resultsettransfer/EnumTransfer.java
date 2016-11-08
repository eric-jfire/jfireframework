package com.jfireframework.sql.resultsettransfer;

import java.sql.ResultSet;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.sql.util.enumhandler.AbstractEnumHandler;
import com.jfireframework.sql.util.enumhandler.EnumHandler;

public class EnumTransfer extends AbstractResultsetTransfer<Enum<?>>
{
    private final EnumHandler<?> enumHandler;
    
    @SuppressWarnings("unchecked")
    public EnumTransfer(Class<?> type)
    {
        try
        {
            enumHandler = AbstractEnumHandler.getEnumBoundHandler((Class<? extends Enum<?>>) type).getConstructor(Class.class).newInstance(type);
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    @Override
    protected Enum<?> valueOf(ResultSet resultSet, String sql) throws Exception
    {
        return enumHandler.getInstance(resultSet);
    }
    
}
