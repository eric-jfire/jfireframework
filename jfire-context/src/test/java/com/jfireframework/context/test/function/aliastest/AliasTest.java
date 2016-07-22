package com.jfireframework.context.test.function.aliastest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.annotation.Resource;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.aliasanno.AnnotationUtil;

@Testalis3(t = "sada", s = false)
public class AliasTest
{
    @Autowired(wiredName = "sad")
    private SingleDemo bi;
    
    @MyMethod(load = "ss")
    public void take()
    {
        
    }
    
    @Test
    public void test() throws NoSuchMethodException, SecurityException, NoSuchFieldException
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, AliasTest.class);
        assertEquals("sada", resource.name());
        assertFalse(resource.shareable());
        Method method = AliasTest.class.getMethod("take");
        InitMethod initMethod = AnnotationUtil.getAnnotation(InitMethod.class, method);
        assertEquals("ss", initMethod.name());
        Field field = AliasTest.class.getDeclaredField("bi");
        resource = AnnotationUtil.getAnnotation(Resource.class, field);
        assertEquals("sad", resource.name());
    }
    
    @Test
    public void test2()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.aliastest");
        SingleDemo demo = (SingleDemo) jfireContext.getBean("demo");
        assertFalse(demo == null);
    }
}
