package com.jfireframework.litl.function;

import java.util.Map;
import com.jfireframework.litl.template.Template;

public interface Function
{
    public void call(Object[] params, Map<String, Object> data, StringBuilder builder, Template template);
}
