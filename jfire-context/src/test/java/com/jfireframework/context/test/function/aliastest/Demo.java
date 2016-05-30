package com.jfireframework.context.test.function.aliastest;

import javax.annotation.Resource;
import static org.junit.Assert.*;
import java.lang.reflect.Method;
import org.junit.Test;
import com.jfireframework.context.util.AnnotationUtil;

@Testalis3(t = "sada", s = false)
public class Demo
{
    
    @MyMethod(load = "ss")
    public void take()
    {
        
    }
    
    @Test
    public void test() throws NoSuchMethodException, SecurityException
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, Demo.class);
        assertEquals("sada", resource.name());
        assertFalse(resource.shareable());
        Method method = Demo.class.getMethod("take");
        InitMethod initMethod = AnnotationUtil.getAnnotation(InitMethod.class, method);
        assertEquals("ss", initMethod.name());
    }
}
