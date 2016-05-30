package com.jfireframework.context.test.function;

import javax.annotation.Resource;
import org.junit.Test;
import com.jfireframework.context.util.AnnotationUtil;

@TestAlias()
public class Demo
{
    @Test
    public void test()
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, Demo.class);
        System.out.println(resource.name());
        System.out.println(resource.shareable());
    }
}
