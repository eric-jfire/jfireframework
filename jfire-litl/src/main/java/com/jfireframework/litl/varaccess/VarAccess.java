package com.jfireframework.litl.varaccess;

import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.litl.template.LineInfo;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

public class VarAccess
{
    
    private static ClassPool classPool = ClassPool.getDefault();
    
    public static void initClassPool(ClassLoader classLoader)
    {
        classPool = new ClassPool();
        classPool.insertClassPath(new LoaderClassPath(classLoader));
        ClassPool.doPruning = true;
    }
    
    private Object                 lock   = new Object();
    private final String           templatePath;
    private final String           var;
    private final LineInfo         lineInfo;
    private volatile FieldAccesser fieldAccesser;
    private final boolean          safe;
    private static final Logger    logger = ConsoleLogFactory.getLogger();
    
    public VarAccess(String templatePath, String var, LineInfo lineInfo)
    {
        this.templatePath = templatePath;
        this.lineInfo = lineInfo;
        if (var.charAt(var.length() - 1) == '!')
        {
            safe = true;
            this.var = var.substring(0, var.length() - 1);
        }
        else
        {
            safe = false;
            this.var = var;
        }
    }
    
    public boolean safeMode()
    {
        return safe;
    }
    
    /**
     * key的格式是 模板路径_参数名称
     * 
     * @param name
     * @param template
     * @return
     */
    public Object getValue(Object target)
    {
        if (target == null)
        {
            if (safe)
            {
                return null;
            }
            else
            {
                throw new NullPointerException(StringUtil.format("请检查模板{}的第{}行，参数{}为null", templatePath, lineInfo.getLine(), var));
            }
        }
        if (fieldAccesser == null)
        {
            synchronized (lock)
            {
                if (fieldAccesser == null)
                {
                    try
                    {
                        CtClass intercc = classPool.get(FieldAccesser.class.getName());
                        CtClass targetcc = classPool.makeClass(var.replace("\\.", "_") + '_' + System.nanoTime());
                        targetcc.setInterfaces(new CtClass[] { intercc });
                        CtMethod ctMethod = new CtMethod(classPool.get(Object.class.getName()), "getValue", new CtClass[] { classPool.get(Object.class.getName()) }, targetcc);
                        StringCache cache = new StringCache();
                        cache.append('{');
                        cache.append("return ($w)((").append(target.getClass().getName()).append(")$1)");
                        cache.append(ReflectUtil.buildGetMethod(var, target.getClass()));
                        cache.append(";}");
                        logger.trace("为模板:{}第{}行的参数:{}生成的代码是{}", templatePath, lineInfo.getLine(), var, cache.toString());
                        ctMethod.setBody(cache.toString());
                        targetcc.addMethod(ctMethod);
                        fieldAccesser = (FieldAccesser) targetcc.toClass().newInstance();
                    }
                    catch (Exception e)
                    {
                        throw new UnSupportException(StringUtil.format("请检查模板{}的第{}行", templatePath, lineInfo.getLine()), e);
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
            if (safe)
            {
                return null;
            }
            else
            {
                throw new NullPointerException(StringUtil.format("请检查模板{}的第{}行，参数为null", templatePath, lineInfo.getLine()));
            }
        }
        
    }
}
