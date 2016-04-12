package com.jfireframework.context.test.function.map;

import javax.annotation.Resource;
import com.jfireframework.baseutil.order.Order;

@Resource
public class Order2 implements Order
{
    
    @Override
    public int getOrder()
    {
        return 2;
    }
    
}
