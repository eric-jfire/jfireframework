package com.jfireframework.baseutil.reflect.copy;

public interface CopyUtil<S, D>
{
    public D copy(S src, D desc);
}
