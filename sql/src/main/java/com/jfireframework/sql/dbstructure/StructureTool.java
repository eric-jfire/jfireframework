package com.jfireframework.sql.dbstructure;

import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.sql.field.MapField;

public class StructureTool
{
    public static String getDbType(MapField field, Map<Class<?>, TypeAndLength> dbTypeMap)
    {
        try
        {
            int length = field.getDbLength();
            if (length == -1)
            {
                return dbTypeMap.get(field.getFieldType()).getDbType();
            }
            else
            {
                return dbTypeMap.get(field.getFieldType()).getDbType(length);
            }
        }
        catch (Exception e)
        {
            System.err.println(StringUtil.format("不识别的建表类型属性:{}", field.getFieldType()));
            throw new RuntimeException(e);
        }
    }
}
