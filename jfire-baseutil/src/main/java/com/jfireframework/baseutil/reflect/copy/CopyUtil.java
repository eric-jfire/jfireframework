package com.jfireframework.baseutil.reflect.copy;

public interface CopyUtil<T, D>
{
    public D copy(T src, D desc);
}
