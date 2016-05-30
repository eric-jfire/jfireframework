package com.jfireframework.context.test.function;

import javax.annotation.Resource;
import org.junit.Test;
import com.jfireframework.baseutil.time.Timewatch;
import com.jfireframework.context.util.AnnotationUtil;

@Testalis3(t = "sada", s = false)
public class Demo
{
    @Test
    public void test()
    {
        Timewatch timewatch = new Timewatch();
        for (int i = 0; i < 100; i++)
        {
            Resource resource = AnnotationUtil.getAnnotation(Resource.class, Demo.class);
            resource.name();
            resource.shareable();
        }
        timewatch.end();
        System.out.println(timewatch.getTotal());
    }
}
