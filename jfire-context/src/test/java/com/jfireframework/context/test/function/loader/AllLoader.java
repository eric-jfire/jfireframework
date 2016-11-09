package com.jfireframework.context.test.function.loader;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.context.bean.load.BeanLoadFactory;

@Resource(name = "allLoader")
public class AllLoader implements BeanLoadFactory
{
    private Class<?>           ckass;
    private Map<Class, Object> holder = new HashMap<Class, Object>();
    
    public AllLoader()
    {
        holder.put(
                Person.class, new Person() {
                    
                    @Override
                    public String getName()
                    {
                        return "name";
                    }
                }
        );
        holder.put(
                Home.class, new Home() {
                    
                    @Override
                    public int getLength()
                    {
                        return 100;
                    }
                }
        );
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T, E extends T> E load(Class<T> ckass)
    {
        return (E) holder.get(ckass);
    }
    
}
