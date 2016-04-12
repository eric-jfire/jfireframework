package com.jfireframework.baseutil.copy.field;

public interface CopyField
{
    /**
     * 复制源对象的目标属性的值到目标对象的目标属性中
     * 
     * @param src
     * @param target
     * @author windfire(windfire@zailanghua.com)
     */
    public void copy(Object src, Object target);
}
