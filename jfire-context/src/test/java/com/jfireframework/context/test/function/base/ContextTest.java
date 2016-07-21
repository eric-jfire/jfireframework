package com.jfireframework.context.test.function.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import java.io.File;
import java.net.URISyntaxException;
import javax.annotation.Resource;
import org.junit.Test;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.context.ContextInitFinish;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.test.function.base.data.House;
import com.jfireframework.context.test.function.base.data.ImmutablePerson;
import com.jfireframework.context.test.function.base.data.MutablePerson;

public class ContextTest
{
    private Logger logger = ConsoleLogFactory.getLogger();
    
    static
    {
        ConsoleLogFactory.addLoggerCfg("com.jfireframework.context", ConsoleLogFactory.TRACE);
    }
    
    /**
     * 测试构造方法,并且测试单例的正确性与否
     */
    @Test
    public void testConstruction()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.base", null);
        baseTest(jfireContext);
    }
    
    private void baseTest(JfireContext jfireContext)
    {
        assertEquals(4, jfireContext.getBeanByAnnotation(Resource.class).length);
        ImmutablePerson immutablePerson = jfireContext.getBean(ImmutablePerson.class);
        ImmutablePerson person2 = (ImmutablePerson) jfireContext.getBean(ImmutablePerson.class.getName());
        assertEquals(immutablePerson, person2);
        MutablePerson mutablePerson = jfireContext.getBean(MutablePerson.class);
        MutablePerson mutablePerson2 = jfireContext.getBean(MutablePerson.class);
        assertNotEquals(mutablePerson, mutablePerson2);
        logger.debug(mutablePerson.getHome().getName());
        assertEquals(mutablePerson.getHome(), immutablePerson.getHome());
        assertEquals("林斌的房子", jfireContext.getBean(House.class).getName());
        assertEquals(1, jfireContext.getBeanByInterface(ContextInitFinish.class).length);
    }
    
    /**
     * 测试手动加入beanconfig,对对象的参数属性进行设置
     */
    @Test
    public void testParam()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.base");
        BeanConfig beanConfig = new BeanConfig(ImmutablePerson.class.getName());
        beanConfig.putParam("name", "林斌");
        beanConfig.putParam("age", "25");
        beanConfig.putParam("boy", "true");
        beanConfig.putParam("arrays", "12,1212,1212121");
        jfireContext.addBeanConfig(beanConfig);
        testParam(jfireContext);
        assertEquals("林斌的房子", jfireContext.getBean(House.class).getName());
        ImmutablePerson person = jfireContext.getBean(ImmutablePerson.class);
        String[] arrays = person.getArrays();
        assertEquals("12", arrays[0]);
        assertEquals("1212", arrays[1]);
        assertEquals("1212121", arrays[2]);
    }
    
    private void testParam(JfireContext jfireContext)
    {
        ImmutablePerson person = jfireContext.getBean(ImmutablePerson.class);
        assertEquals(person.getAge(), 25);
        assertEquals(person.getName(), "林斌");
        assertEquals(person.getBoy(), true);
    }
    
    @Test
    public void testDirect()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(House.class);
        jfireContext.addBean(MutablePerson.class);
        jfireContext.addBean(ImmutablePerson.class);
        baseTest(jfireContext);
    }
    
    @Test
    public void testDirect2()
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.addBean(House.class.getName(), false, House.class);
        jfireContext.addBean(MutablePerson.class);
        jfireContext.addBean(ImmutablePerson.class);
        baseTest(jfireContext);
    }
    
    @Test
    public void testInit()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.base");
        assertEquals(1, jfireContext.getBeanByInterface(ContextInitFinish.class).length);
    }
    
    @Test
    public void testInit2()
    {
        JfireContext jfireContext = new JfireContextImpl("com.jfireframework.context.test.function.base");
        Bean bean = jfireContext.getBeanInfo(House.class);
        assertEquals("林斌的房子", ((House) bean.getInstance()).getName());
        bean = jfireContext.getBeanInfo(House.class.getName());
        assertEquals("林斌的房子", ((House) bean.getInstance()).getName());
    }
    
    @Test
    public void testConfig() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.readConfig(new File(this.getClass().getClassLoader().getResource("config.json").toURI()));
        baseTest(jfireContext);
        testParam(jfireContext);
    }
    
    @Test
    public void testConfig2() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.readConfig(new File(this.getClass().getClassLoader().getResource("config2.json").toURI()));
        baseTest(jfireContext);
        testParam(jfireContext);
    }
    
}
