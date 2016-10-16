package com.jfireframework.eventbus.handler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.jfireframework.baseutil.order.AescComparator;

public class HandlerCombination
{
    private final AescComparator     aescComparator = new AescComparator();
    private List<EventHandler<?, ?>> handlers       = new ArrayList<EventHandler<?, ?>>(8);
    private EventHandler<?, ?>[]     combination;
    
    public void addHandler(EventHandler<?, ?> handler)
    {
        handlers.add(handler);
    }
    
    public void sort()
    {
        combination = handlers.toArray(new EventHandler<?, ?>[handlers.size()]);
        Arrays.sort(combination, aescComparator);
    }
    
    public EventHandler<?, ?>[] combination()
    {
        return combination;
    }
}
