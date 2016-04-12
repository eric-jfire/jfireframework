package com.jfireframework.context.bean.field.param.impl;

import java.lang.reflect.Field;

public class IntArrayField extends AbstractParamField
{
    
    public IntArrayField(Field field, String value)
    {
        super(field, value);
        String[] tmp = value.split(",");
        int[] array = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++)
        {
            array[i] = Integer.valueOf(tmp[i]).intValue();
        }
        this.value = array;
    }
    
}
