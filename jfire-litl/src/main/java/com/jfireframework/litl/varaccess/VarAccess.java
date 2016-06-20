package com.jfireframework.litl.varaccess;

import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

public class VarAccess
{
    private ConcurrentHashMap<String, FieldAccesser> accesserMap = new ConcurrentHashMap<String, FieldAccesser>();
    
    private ClassPool                                classPool;
    private ClassLoader                              classLoader;
    
    public VarAccess(ClassPool classPool, ClassLoader classLoader)
    {
        this.classPool = classPool;
        this.classLoader = classLoader;
    }
    
    /**
     * key的格式是 模板路径_参数名称
     * 
     * @param name
     * @param template
     * @return
     */
    public Object getValue(String key, String varName, Object target)
    {
        FieldAccesser fieldAccesser = accesserMap.get(key);
        if (fieldAccesser == null)
        {
            synchronized (accesserMap)
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
                        cache.append("($r)((").append(target.getClass().getName()).append(")$1)");
                        cache.append(ReflectUtil.buildGetMethod(varName, target.getClass()));
                        cache.append(";}");
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
        return fieldAccesser.getValue(target);
        
    }
}
