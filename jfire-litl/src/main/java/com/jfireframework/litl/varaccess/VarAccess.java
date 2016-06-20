package com.jfireframework.litl.varaccess;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class VarAccess
{
    private ConcurrentHashMap<String, FieldAccesser> accesserMap = new ConcurrentHashMap<String, FieldAccesser>();
    
    private ClassPool                                classPool;
    private ClassLoader                              classLoader;
    private static final Logger                      logger      = ConsoleLogFactory.getLogger();
    private ConcurrentHashMap<String, Object>        keyMap      = new ConcurrentHashMap<String, Object>();
    
    public VarAccess(ClassPool classPool, ClassLoader classLoader)
    {
        this.classPool = classPool;
        this.classLoader = classLoader;
    }
    
    private Object getParalLock(String key)
    {
        Object lock = keyMap.get(key);
        if (lock != null)
        {
            return lock;
        }
        lock = new Object();
        if (keyMap.putIfAbsent(key, lock) == null)
        {
            return lock;
        }
        return keyMap.get(key);
    }
    
    /**
     * key的格式是 模板路径_参数名称
     * 
     * @param name
     * @param template
     * @return
     */
    public Object getValue(String key, String varName, Object target, int line)
    {
        if (target == null)
        {
            return null;
        }
        FieldAccesser fieldAccesser = accesserMap.get(key);
        if (fieldAccesser == null)
        {
            // 这个地方的锁应该分配给每一个key一个，类似于classloader中的思路
            synchronized (getParalLock(key))
            {
                if (accesserMap.containsKey(key))
                {
                    fieldAccesser = accesserMap.get(key);
                }
                else
                {
                    try
                    {
                        CtClass intercc = classPool.get(FieldAccesser.class.getName());
                        CtClass targetcc = classPool.makeClass(varName.replace("\\.", "_") + '_' + System.nanoTime());
                        targetcc.setInterfaces(new CtClass[] { intercc });
                        CtMethod ctMethod = new CtMethod(classPool.get(Object.class.getName()), "getValue", new CtClass[] { classPool.get(Object.class.getName()) }, targetcc);
                        StringCache cache = new StringCache();
                        cache.append('{');
                        cache.append("return ($r)((").append(target.getClass().getName()).append(")$1)");
                        cache.append(ReflectUtil.buildGetMethod(varName, target.getClass()));
                        cache.append(";}");
                        logger.trace("为参数:{}生成的获取代码是{}", key, cache.toString());
                        ctMethod.setBody(cache.toString());
                        targetcc.addMethod(ctMethod);
                        fieldAccesser = (FieldAccesser) targetcc.toClass(classLoader, null).newInstance();
                        accesserMap.putIfAbsent(key, fieldAccesser);
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException("", e);
                    }
                }
            }
        }
        try
        {
            return fieldAccesser.getValue(target);
        }
        catch (NullPointerException e)
        {
            return null;
        }
        
    }
}
