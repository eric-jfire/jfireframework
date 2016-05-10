package com.jfireframework.context.test.function.map;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.order.Order;
import com.jfireframework.context.bean.annotation.field.MapKey;

@Resource
public class Host
{
    @Resource
    @MapKey("getOrder")
    private Map<Integer, Order> map  = new HashMap<Integer, Order>();
    
    @Resource
    private Map<String, Order>  map2 = new HashMap<String, Order>();
    
    public Map<Integer, Order> getMap()
    {
        return map;
    }
    
    public Map<String, Order> getMap2()
    {
        return map2;
    }
    
}
