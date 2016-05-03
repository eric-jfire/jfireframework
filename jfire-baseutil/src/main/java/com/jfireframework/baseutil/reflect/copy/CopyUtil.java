package com.jfireframework.baseutil.reflect.copy;

public interface CopyUtil<T, D>
{
    public void copy(T src, D desc);
}
