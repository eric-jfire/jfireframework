package com.jfireframework.baseutil.order;

import java.util.Comparator;

public class AescComparator implements Comparator<Order>
{
    
    @Override
    public int compare(Order o1, Order o2)
    {
        if (o1.getOrder() > o2.getOrder())
        {
            return 1;
        }
        else if (o1.getOrder() == o2.getOrder())
        {
            return 0;
        }
        else
        {
            return -1;
        }
    }
    
}
