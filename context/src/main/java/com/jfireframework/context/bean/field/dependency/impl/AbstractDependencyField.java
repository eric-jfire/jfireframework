package com.jfireframework.context.bean.field.dependency.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public abstract class AbstractDependencyField implements DependencyField
{
    protected long          offset;
    protected Unsafe        unsafe = ReflectUtil.getUnsafe();
    protected static Logger logger = ConsoleLogFactory.getLogger();
    protected Field         field;
    
    public AbstractDependencyField(Field field)
    {
        field.setAccessible(true);
        this.offset = unsafe.objectFieldOffset(field);
        this.field = field;
    }
    
}
