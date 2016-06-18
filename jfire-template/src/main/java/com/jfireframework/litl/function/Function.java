package com.jfireframework.litl.function;

import java.util.Map;
import com.jfireframework.litl.TplCenter;

public interface Function
{
    public void call(Object[] params, Map<String, Object> data, StringBuilder builder, TplCenter tplCenter);
}
