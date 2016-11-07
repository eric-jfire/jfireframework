package com.jfireframework.sql.field.impl;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import com.jfireframework.sql.dbstructure.NameStrategy;

public class ByteArrayField extends AbstractMapField
{
    
    public ByteArrayField(Field field, NameStrategy nameStrategy)
    {
        super(field, nameStrategy);
    }
    
    @Override
    public void setEntityValue(Object entity, ResultSet resultSet) throws SQLException
    {
        Blob blob = resultSet.getBlob(dbColName);
        if (blob != null)
        {
            byte[] array = blob.getBytes(1, (int) blob.length());
            unsafe.putObject(entity, offset, array);
        }
        else
        {
            unsafe.putObject(entity, offset, null);
        }
    }
    
    @Override
    public void setStatementValue(PreparedStatement statement, Object entity, int index) throws SQLException
    {
        byte[] array = (byte[]) unsafe.getObject(entity, offset);
        if (array == null)
        {
            statement.setNull(index, Types.BLOB);
        }
        else
        {
            Blob blob = statement.getConnection().createBlob();
            blob.setBytes(1, array);
            statement.setBlob(index, blob);
        }
    }
    
}
