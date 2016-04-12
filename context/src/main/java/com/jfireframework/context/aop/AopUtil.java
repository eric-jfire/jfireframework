package com.jfireframework.context.aop;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.tx.AutoCloseManager;
import com.jfireframework.baseutil.tx.TransactionManager;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.aop.annotation.AutoCloseResource;
import com.jfireframework.context.aop.annotation.EnhanceClass;
import com.jfireframework.context.aop.annotation.Transaction;
import com.jfireframework.context.bean.Bean;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.AttributeInfo;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;

public class AopUtil
{
    private static ClassPool classPool = ClassPool.getDefault();
    private static CtClass   txManagerCtClass;
    private static CtClass   acManagerCtClass;
    private static Logger    logger    = ConsoleLogFactory.getLogger();
                                       
    static
    {
        initClassPool();
    }
    
    public static void initClassPool()
    {
        
        classPool = new ClassPool();
        ClassPool.doPruning = true;
        classPool.importPackage("com.jfireframework.context.aop");
        classPool.importPackage("com.jfireframework.baseutil.tx");
        try
        {
            classPool.insertClassPath(new ClassClassPath(AopUtil.class));
            classPool.insertClassPath("com.jfireframework.context.aop");
            classPool.insertClassPath("com.jfireframework.baseutil.tx");
            txManagerCtClass = classPool.get(TransactionManager.class.getName());
            acManagerCtClass = classPool.get(AutoCloseManager.class.getName());
        }
        catch (NotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 根据给定的包名以及beanNameMap，对其中的bean进行aop增强，并且将增强后的class设置到对应的Bean中。
     * 
     * @param packageNames
     * @param beanMap
     */
    public static void enhance(Map<String, Bean> beanMap, ClassLoader classLoader)
    {
        try
        {
            initTxAndAcMethods(beanMap);
            initAopbeanSet(beanMap);
            for (Bean bean : beanMap.values())
            {
                if (bean.needEnhance())
                {
                    enhanceBean(bean, classLoader);
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException("aop增强出现异常", e);
        }
        
    }
    
    /**
     * 循环所有的bean，确定每一个bean需要进行事务增强的方法和自动关闭资源的方法,并且在bean中添加这些方法的信息
     * 
     * @param beanMap
     */
    private static void initTxAndAcMethods(Map<String, Bean> beanMap)
    {
        for (Bean bean : beanMap.values())
        {
            if (bean.canModify() == false)
            {
                continue;
            }
            // 由于增强是采用子类来实现的,所以事务注解只对当前的类有效.如果当前类的父类也有事务注解,在本次增强中就无法起作用
            for (Method method : ReflectUtil.getAllMehtods(bean.getType()))
            {
                if (method.isAnnotationPresent(Transaction.class))
                {
                    Verify.False(method.isAnnotationPresent(AutoCloseResource.class), "同一个方法上不能同时有事务注解和自动关闭注解，请检查{}.{}", method.getDeclaringClass(), method.getName());
                    Verify.True(Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers()), "方法{}.{}有事务注解,访问类型必须是public或protected", method.getDeclaringClass(), method.getName());
                    bean.addTxMethod(method);
                    logger.trace("发现事务方法{}", method.toString());
                }
                else if (method.isAnnotationPresent(AutoCloseResource.class))
                {
                    Verify.True(Modifier.isPublic(method.getModifiers()) || Modifier.isProtected(method.getModifiers()), "方法{}.{}有自动关闭注解,访问类型必须是public或protected", method.getDeclaringClass(), method.getName());
                    bean.addAcMethod(method);
                    logger.trace("发现自动关闭方法{}", method.toString());
                }
            }
        }
    }
    
    /**
     * 循环所有的bean，确定所有的增强类和目标类 在目标类的aopset中存入增强类的bean
     * 
     * @param beanMap
     */
    private static void initAopbeanSet(Map<String, Bean> beanMap)
    {
        for (Bean aopBean : beanMap.values())
        {
            EnhanceClass enhanceClass = aopBean.getType().getAnnotation(EnhanceClass.class);
            if (enhanceClass != null)
            {
                String rule = enhanceClass.value();
                for (Bean targetBean : beanMap.values())
                {
                    if (targetBean.canModify())
                    {
                        if (StringUtil.match(targetBean.getOriginType().getName(), rule))
                        {
                            if (targetBean.getOriginType().isAnnotationPresent(EnhanceClass.class) == false)
                            {
                                targetBean.addEnhanceBean(aopBean);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 对目标bean进行aop增强。 增强的原理就是构造一个目标类的子类，并且子类中新增属性，属性上打上@resource注解，用来注入aopbean。
     * 这样在获取bean的时候就可以通过ioc将需要的类注入
     * 
     * @param bean
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws ClassNotFoundException
     */
    private static void enhanceBean(Bean bean, ClassLoader classLoader) throws NotFoundException, CannotCompileException, ClassNotFoundException
    {
        classPool.insertClassPath(new ClassClassPath(bean.getType()));
        CtClass parentCc = classPool.get(bean.getType().getName());
        /**
         * 名字最后跟上时间戳，这样可以保证名字唯一，也就是可以生成不同子类而不冲突
         * 其实在正常的使用中是不必的,但是在测试中,因为在同一个classloader中反复加载就会出问题
         */
        CtClass childCc = classPool.makeClass(bean.getType().getName() + "_jfire_core_Enhance_" + System.nanoTime());
        /**
         * 由于需要增强的class之前已经被加载到了classloader中,所以要增强只能通过实现一个子类的方式进行
         * 由于是对子类操作,所以能够增强的只有目标类的自己的public或者protected方法
         */
        childCc.setSuperclass(parentCc);
        createchildClassMethod(childCc, parentCc);
        if (bean.getTxMethodSet().size() > 0)
        {
            String txFieldName = "tx_" + System.nanoTime();
            addField(childCc, txManagerCtClass, txFieldName);
            addTxToMethod(childCc, txFieldName, bean.getTxMethodSet().toArray(Method.class));
        }
        if (bean.getAcMethods().size() > 0)
        {
            String acFieldName = "ac_" + System.nanoTime();
            addField(childCc, acManagerCtClass, acFieldName);
            addAcToMethod(childCc, acFieldName, bean.getAcMethods().toArray(Method.class));
        }
        if (bean.getEnHanceAnnos().size() > 0)
        {
            /**
             * aop增强,采用的是将增强类作为目标类的属性注入到目标类中.所以在开始增强前,需要确定注入属性的名称.
             * 由于一个增强类中的多个EnhanceAnnoInfo的属性名是相同的,所以在这里进行注入并且去重
             */
            HashSet<String> enHanceNameSet = new HashSet<>();
            for (EnhanceAnnoInfo info : bean.getEnHanceAnnos())
            {
                if (enHanceNameSet.contains(info.getEnhanceFieldName()) == false)
                {
                    addField(childCc, classPool.get(info.getEnhanceBean().getType().getName()), info.getEnhanceFieldName());
                    enHanceNameSet.add(info.getEnhanceFieldName());
                }
            }
        }
        LightSet<EnhanceAnnoInfo> set = new LightSet<>();
        for (CtMethod each : childCc.getDeclaredMethods())
        {
            // 针对每一个方法,取出该方法对应的所有增强,并且进行排序
            set.removeAll();
            for (EnhanceAnnoInfo enhanceAnnoInfo : bean.getEnHanceAnnos())
            {
                if (enhanceAnnoInfo.match(each))
                {
                    set.add(enhanceAnnoInfo);
                }
            }
            EnhanceAnnoInfo[] enhanceAnnoInfos = set.toArray(EnhanceAnnoInfo.class);
            Arrays.sort(enhanceAnnoInfos, new AescComparator());
            String originName = each.getName();
            for (EnhanceAnnoInfo enhanceAnnoInfo : enhanceAnnoInfos)
            {
                /**
                 * 因为后置增强和环绕增强都是在修改了原方法的名称,生成了新的同名方法来完成的.所以一开始要保存方法的原始名称
                 * 然后每一次循环,都需要使用原始的方法名称和入参类型来获得最新的方法
                 */
                CtMethod ctMethod = childCc.getDeclaredMethod(originName, each.getParameterTypes());
                switch (enhanceAnnoInfo.getType())
                {
                    case EnhanceAnnoInfo.BEFORE:
                        enhanceBefore(ctMethod, enhanceAnnoInfo);
                        break;
                    case EnhanceAnnoInfo.AFTER:
                        enhanceAfter(ctMethod, enhanceAnnoInfo, childCc);
                        break;
                    case EnhanceAnnoInfo.AROUND:
                        enhanceAround(ctMethod, enhanceAnnoInfo, childCc);
                        break;
                    case EnhanceAnnoInfo.THROW:
                        enhanceThrow(ctMethod, enhanceAnnoInfo);
                        break;
                }
            }
        }
        if (classLoader != null)
        {
            bean.setType(childCc.toClass(classLoader, null));
        }
        else
        {
            bean.setType(childCc.toClass());
            
        }
        // 进行脱离操作，减少内存占用
        parentCc.detach();
        childCc.detach();
    }
    
    private static void enhanceBefore(CtMethod targetMethod, EnhanceAnnoInfo info) throws NotFoundException, CannotCompileException
    {
        // 构建随机名称，这样可以进行多次增强
        String pointName = "point" + System.nanoTime();
        String body = "{ProceedPointImpl " + pointName + " = new ProceedPointImpl();";
        // 放入目标方法的参数
        body += pointName + ".setParam($args);";
        // 调用增强方法
        body += info.getEnhanceFieldName() + "." + info.getEnhanceMethodName() + "(" + pointName + ");";
        if (targetMethod.getReturnType().equals(CtClass.voidType))
        {
            body += "if(" + pointName + ".isPermission()==false){return;}}";
        }
        else
        {
            body += "if(" + pointName + ".isPermission()==false){return (" + getNameForType(targetMethod.getReturnType()) + ')' + pointName + ".getResult();}}";
        }
        targetMethod.insertBefore(body);
    }
    
    private static void enhanceAfter(CtMethod targetMethod, EnhanceAnnoInfo info, CtClass targetCtClass) throws CannotCompileException, NotFoundException
    {
        String pointName = "point_" + System.nanoTime();
        String body = "{ProceedPointImpl " + pointName + " = new ProceedPointImpl();";
        body += pointName + ".setParam($args);";
        // 调用增强方法
        if (targetMethod.getReturnType().equals(CtClass.voidType))
        {
            body += info.getEnhanceFieldName() + "." + info.getEnhanceMethodName() + "(" + pointName + ");}";
            targetMethod.insertAfter(body);
        }
        // 如果原方法具有返回值,原方法又有可能会被多次增强.所以后置增强的思路是将目标方法改名,新增一个与原来签名一致的方法.新增方法去调用原方法.
        // 这样即使有多次的增强,也可以按顺序进行.并且还能得到
        else
        {
            CtMethod newTargetMethod = copyMethod(targetMethod, targetCtClass);
            targetMethod.setName(targetMethod.getName() + "_" + System.nanoTime());
            body += pointName + ".setResult(" + targetMethod.getName() + "($$));";
            body += info.getEnhanceFieldName() + "." + info.getEnhanceMethodName() + "(" + pointName + ");";
            body += "return ($r)" + pointName + "." + "getResult();}";
            newTargetMethod.setBody(body);
            targetCtClass.addMethod(newTargetMethod);
        }
    }
    
    private static void enhanceAround(CtMethod targetMethod, EnhanceAnnoInfo info, CtClass targetCtClass) throws CannotCompileException, NotFoundException
    {
        CtClass objectCtClass = classPool.get(Object.class.getName());
        CtClass ProceedPointImplCtClass = classPool.get(ProceedPointImpl.class.getName());
        /**
         * 新建一个子类集成ProceedPointImpl，并且改写其中的invoke方法，使其调用目标类的目标方法（修改名字后的）
         */
        CtClass pointImpl = classPool.makeClass(ProceedPointImpl.class.getName() + "_" + System.nanoTime());
        pointImpl.setSuperclass(ProceedPointImplCtClass);
        CtMethod invoke = new CtMethod(objectCtClass, "invoke", null, pointImpl);
        invoke.setModifiers(Modifier.PUBLIC);
        pointImpl.addMethod(invoke);
        /****** end **********/
        /**
         * 将目标方法改名，并且修改ProceedPointImpl类的invoke方法，让其调用改名后的目标方法。
         * 同时增加一个目标方法的原同名同签名方法给予外界调用。
         */
        CtMethod newTargetMethod = copyMethod(targetMethod, targetCtClass);
        targetMethod.setName(targetMethod.getName() + "_" + System.nanoTime());
        newTargetMethod.setModifiers(Modifier.PUBLIC);
        targetCtClass.addMethod(newTargetMethod);
        /****** end **********/
        /*
         * 编译invoke方法的方法体
         */
        CtClass[] paramType = targetMethod.getParameterTypes();
        StringCache cache = new StringCache();
        cache.append("((").append(targetCtClass.getName()).append(")host)." + targetMethod.getName()).append('(');
        for (int i = 0; i < paramType.length; i++)
        {
            cache.append('(').append(getNameForType(paramType[i])).append(")param[").append(i).append(']').appendComma();
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append(')').append(";");
        /****** end *******/
        if (targetMethod.getReturnType().equals(CtClass.voidType))
        {
            invoke.setBody("{" + cache.toString() + ";return null;}");
            pointImpl.toClass();
        }
        else
        {
            invoke.setBody("{result = " + cache.toString() + ";return result;}");
            pointImpl.toClass();
        }
        cache.clear();
        cache.append("{ProceedPointImpl point = new " + pointImpl.getName() + "();");
        cache.append("point.setParam($args);");
        cache.append("point.setHost(this);");
        cache.append(info.getEnhanceFieldName() + "." + info.getEnhanceMethodName() + "(point);");
        if (targetMethod.getReturnType().equals(CtClass.voidType))
        {
            cache.append('}');
        }
        else
        {
            cache.append("return ($r)point.getResult();}");
        }
        newTargetMethod.setBody(cache.toString());
        pointImpl.detach();
        ProceedPointImplCtClass.detach();
    }
    
    private static void enhanceThrow(CtMethod targetMethod, EnhanceAnnoInfo info) throws NotFoundException, CannotCompileException
    {
        Class<?>[] types = info.getThrowtype();
        CtClass[] throwCcs = new CtClass[types.length];
        for (int i = 0; i < types.length; i++)
        {
            throwCcs[i] = classPool.get(types[i].getName());
        }
        String body = "{ProceedPointImpl point = new ProceedPointImpl();";
        body += "point.setE($e);";
        body += info.getEnhanceFieldName() + "." + info.getEnhanceMethodName() + "(point);";
        body += "throw $e;}";
        for (int i = 0; i < throwCcs.length; i++)
        {
            targetMethod.addCatch(body, throwCcs[i]);
        }
    }
    
    /**
     * 为事务方法增加上事务的开启，提交和回滚
     * 
     * @param targetCc 事务方法所在的类
     * @param txFieldName 事务管理器在这个类中的属性名
     * @param txMethods 事务方法
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private static void addTxToMethod(CtClass targetCc, String txFieldName, Method[] txMethods) throws NotFoundException, CannotCompileException
    {
        for (Method method : txMethods)
        {
            Transaction transaction = method.getAnnotation(Transaction.class);
            Class<?>[] types = transaction.exceptions();
            CtClass[] exCcs = new CtClass[types.length];
            for (int i = 0; i < types.length; i++)
            {
                exCcs[i] = classPool.get(types[i].getName());
            }
            CtMethod ctMethod = targetCc.getDeclaredMethod(method.getName(), getParamTypes(method));
            ctMethod.insertBefore("((TransactionManager)" + txFieldName + ").beginTransAction();");
            ctMethod.insertAfter("((TransactionManager)" + txFieldName + ").commit();");
            for (CtClass exCc : exCcs)
            {
                ctMethod.addCatch("{((TransactionManager)" + txFieldName + ").rollback();throw new RuntimeException($e);}", exCc);
            }
        }
    }
    
    /**
     * 为自动关闭方法加上资源关闭的调用
     * 
     * @param targetCc 自动关闭方法所在的类
     * @param acFieldName 自动关闭管理器在这个类中的属性名
     * @param txMethods 自动关闭方法
     * @throws NotFoundException
     * @throws CannotCompileException
     */
    private static void addAcToMethod(CtClass targetCc, String acFieldName, Method[] acMethods) throws NotFoundException, CannotCompileException
    {
        for (Method method : acMethods)
        {
            AutoCloseResource autoClose = method.getAnnotation(AutoCloseResource.class);
            Class<?>[] types = autoClose.exceptions();
            CtClass[] exCcs = new CtClass[types.length];
            for (int i = 0; i < types.length; i++)
            {
                exCcs[i] = classPool.get(types[i].getName());
            }
            CtMethod ctMethod = targetCc.getDeclaredMethod(method.getName(), getParamTypes(method));
            ctMethod.insertAfter("{((AutoCloseManager)" + acFieldName + ").close();}");
            for (CtClass exCc : exCcs)
            {
                ctMethod.addCatch("{((AutoCloseManager)" + acFieldName + ").close();throw $e;}", exCc);
            }
        }
    }
    
    private static CtClass[] getParamTypes(Method method) throws NotFoundException
    {
        CtClass[] paramClasses = new CtClass[method.getParameterTypes().length];
        int index = 0;
        for (Class<?> each : method.getParameterTypes())
        {
            paramClasses[index++] = classPool.get(each.getName());
        }
        return paramClasses;
    }
    
    /**
     * 将父类中的public和protected方法都在子类中进行简单的重载(也就是直接执行父类的对应方法),为稍后的增强进行准备.
     * 这样稍后的增强就可以在子类上对方法进行修改了.
     * 
     * @param childCc
     * @param parentCc
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws ClassNotFoundException
     */
    private static void createchildClassMethod(CtClass childCc, CtClass parentClass) throws NotFoundException, CannotCompileException, ClassNotFoundException
    {
        CtMethod[] methods = getAllMethods(parentClass);
        for (CtMethod each : methods)
        {
            if (Modifier.isPublic(each.getModifiers()) || Modifier.isProtected(each.getModifiers()))
            {
                // 为所有的public或者protected方法设置原始的方法体，也就是默认直接调用父方法
                CtMethod targetMethod = copyMethod(each, childCc);
                if (targetMethod.getReturnType().equals(CtClass.voidType))
                {
                    targetMethod.setBody("{super." + targetMethod.getName() + "($$);}");
                }
                else
                {
                    targetMethod.setBody("{return ($r)super." + targetMethod.getName() + "($$)" + ";}");
                }
                logger.trace("初始化子类方法{}.{}", targetMethod.getDeclaringClass().getName(), targetMethod.getName());
                childCc.addMethod(targetMethod);
            }
        }
    }
    
    /**
     * 向目标类中增加一个属性,并且设定该属性的属性名,以及在他的上面增加Resource注解
     * 
     * @param targetCc
     * @param fieldType
     * @param fieldName
     * @throws CannotCompileException
     */
    private static void addField(CtClass targetCc, CtClass fieldType, String fieldName) throws CannotCompileException
    {
        CtField ctField = new CtField(fieldType, fieldName, targetCc);
        ctField.setModifiers(Modifier.PUBLIC);
        ConstPool constPool = targetCc.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation stateless = new Annotation("javax.annotation.Resource", constPool);
        attr.addAnnotation(stateless);
        ctField.getFieldInfo().addAttribute(attr);
        targetCc.addField(ctField);
    }
    
    public static String getNameForType(CtClass ctClass) throws NotFoundException
    {
        if (ctClass.isArray() == false)
        {
            return ctClass.getName();
        }
        else
        {
            int dim = 0;
            while (ctClass.isArray())
            {
                dim++;
                ctClass = ctClass.getComponentType();
            }
            String name = ctClass.getName();
            for (int i = 0; i < dim; i++)
            {
                name += "[]";
            }
            return name;
        }
    }
    
    /**
     * 将一个方法进行拷贝操作，该方法依附于cc。
     * 该拷贝操作会拷贝原方法上的注解信息
     * 
     * @param targetMethod
     * @param cc
     * @return
     * @throws CannotCompileException
     */
    public static CtMethod copyMethod(CtMethod targetMethod, CtClass cc) throws CannotCompileException
    {
        CtMethod newMethod = new CtMethod(targetMethod, cc, null);
        for (Object each : targetMethod.getMethodInfo().getAttributes())
        {
            newMethod.getMethodInfo().addAttribute((AttributeInfo) each);
        }
        newMethod.getMethodInfo().setCodeAttribute(targetMethod.getMethodInfo().getCodeAttribute());
        return newMethod;
    }
    
    /**
     * 获取该类所有方法,包含父类的方法.如果子类重载了父类的方法,则该集合中只有子类的方法
     * 
     * @param entityClass
     * @return
     */
    public static CtMethod[] getAllMethods(CtClass cc) throws NotFoundException
    {
        LightSet<CtMethod> set = new LightSet<>();
        while (cc.getSimpleName().equals("Object") == false)
        {
            CtMethod[] methods = cc.getDeclaredMethods();
            checkNextMethod: for (CtMethod each : methods)
            {
                checkAlreadIn: for (CtMethod alreadIn : set)
                {
                    if (alreadIn.getName().equals(each.getName()) == false)
                    {
                        continue;
                    }
                    CtClass[] a1 = alreadIn.getParameterTypes();
                    CtClass[] a2 = each.getParameterTypes();
                    if (a1.length != a2.length)
                    {
                        continue;
                    }
                    for (int i = 0; i < a1.length; i++)
                    {
                        if (a1[i] != a2[i])
                        {
                            continue checkAlreadIn;
                        }
                    }
                    // 代码走到这里，意味着父类的方法已经被子类重载了
                    continue checkNextMethod;
                }
                set.add(each);
            }
            cc = cc.getSuperclass();
        }
        return set.toArray(CtMethod.class);
    }
    
    /**
     * 获取一个方法的参数名
     * 
     * @param method
     * @return
     */
    public static String[] getParamNames(Method method)
    {
        Verify.False(method.getDeclaringClass().isInterface(), "使用反射获取方法形参名称的时候，方法必须是在类的方法不能是接口方法，请检查{}.{}", method.getDeclaringClass(), method.getName());
        try
        {
            CtClass ctClass = classPool.get(method.getDeclaringClass().getName());
            LightSet<CtClass> set = new LightSet<>();
            for (Class<?> each : method.getParameterTypes())
            {
                set.add(classPool.get(each.getName()));
            }
            if (set.size() == 0)
            {
                return new String[0];
            }
            try
            {
                CtMethod cm = ctClass.getDeclaredMethod(method.getName(), set.toArray(CtClass.class));
                return getParamNames(cm);
            }
            catch (Exception e)
            {
                throw e;
            }
        }
        catch (NotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取方法的参数的名称
     * 
     * @param cm
     * @return
     */
    public static String[] getParamNames(CtMethod cm)
    {
        try
        {
            MethodInfo methodInfo = cm.getMethodInfo();
            CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
            Verify.notNull(codeAttribute, "获取方法参数名称异常，方法为{}.{}", cm.getDeclaringClass().getName(), cm.getName());
            LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
            Verify.notNull(attr, "获取方法参数名称异常，方法为{}.{}", cm.getDeclaringClass().getName(), cm.getName());
            String[] paramNames = new String[cm.getParameterTypes().length];
            int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
            for (int i = 0; i < paramNames.length; i++)
            {
                paramNames[i] = attr.variableName(i + pos);
            }
            return paramNames;
        }
        catch (NotFoundException e)
        {
            throw new RuntimeException(e);
        }
    }
    
}
