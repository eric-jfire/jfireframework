package com.jfireframework.context.test.function.builder;

import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.context.bean.BeanInstanceHolder;

public class AllHolder implements BeanInstanceHolder
{
    private Class<?>           ckass;
    private Map<Class, Object> holder = new HashMap<Class, Object>();
    
    public AllHolder()
    {
        holder.put(Person.class, new Person() {
            
            @Override
            public String getName()
            {
                return "name";
            }
        });
        holder.put(Home.class, new Home() {
            
            @Override
            public int getLength()
            {
                return 100;
            }
        });
    }
    
    @Override
    public Object getObject()
    {
        return holder.get(ckass);
    }
    
}
