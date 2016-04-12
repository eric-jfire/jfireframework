package com.jfireframework.context.test.function.base.maptest;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import com.jfireframework.context.JfireContext;
import com.jfireframework.context.JfireContextImpl;

public class MapTest
{
    @Test
    public void test() throws URISyntaxException
    {
        JfireContext jfireContext = new JfireContextImpl();
        jfireContext.readConfig(new File(this.getClass().getClassLoader().getResource("mapconfig.json").toURI()));
        House house = jfireContext.getBean(House.class);
        Map<String, Person> map = house.getMap();
        Assert.assertEquals(2, map.size());
        
    }
}
