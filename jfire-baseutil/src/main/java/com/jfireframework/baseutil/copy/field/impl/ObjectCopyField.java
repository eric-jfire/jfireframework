package com.jfireframework.baseutil.copy.field.impl;

import java.lang.reflect.Field;
import com.jfireframework.baseutil.copy.field.CopyField;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import sun.misc.Unsafe;

@SuppressWarnings("restriction")
public class ObjectCopyField implements CopyField
{
    protected static Unsafe unsafe = ReflectUtil.getUnsafe();
    protected long          srcOffset;
    protected long          targetOffset;
                            
    public ObjectCopyField(Field srcField, Field targetField)
    {
        srcOffset = unsafe.objectFieldOffset(srcField);
        targetOffset = unsafe.objectFieldOffset(targetField);
    }
    
    /**
     * object类型的就直接使用这个实现即可
     */
    public void copy(Object src, Object target)
    {
        unsafe.putObject(target, targetOffset, unsafe.getObject(src, srcOffset));
    }
}
