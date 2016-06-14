package com.jfireframework.context.bean;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.ContextInitFinish;
import com.jfireframework.context.aop.EnhanceAnnoInfo;
import com.jfireframework.context.aop.annotation.AfterEnhance;
import com.jfireframework.context.aop.annotation.AroundEnhance;
import com.jfireframework.context.aop.annotation.BeforeEnhance;
import com.jfireframework.context.aop.annotation.ThrowEnhance;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.param.ParamField;
import com.jfireframework.context.util.AnnotationUtil;
import sun.reflect.MethodAccessor;

/**
 * 容器管理的bean
 * 
 * @author 林斌（eric@jfire.cn）
 * 
 */
public class Bean
{
    /** 该bean的名称 */
    private String                               beanName;
    /** 该bean的class对象，可能为增强后的对象 */
    private Class<?>                             type;
    /** 该bean的原始class对象，供查询使用 */
    private Class<?>                             originType;
    /** 该bean需要进行属性注入的field */
    private DependencyField[]                    injectFields    = new DependencyField[0];
    /** 该bean需要进行属性初始化的field */
    private ParamField[]                         paramFields     = new ParamField[0];
    /** 该bean是否是多例 */
    private boolean                              prototype       = false;
    /* bean对象初始化过程中暂存生成的中间对象 */
    private ThreadLocal<HashMap<String, Object>> beanInstanceMap = new ThreadLocal<HashMap<String, Object>>() {
                                                                     @Override
                                                                     protected HashMap<String, Object> initialValue()
                                                                     {
                                                                         return new HashMap<String, Object>();
                                                                     }
                                                                 };
    /** 单例的引用对象 */
    private Object                               singletonInstance;
    /** 该bean是否实现了容器初始化结束接口 */
    private boolean                              hasFinishAction = false;
    /** 该bean的所有增强方法信息 */
    private LightSet<EnhanceAnnoInfo>            enHanceAnnos    = new LightSet<EnhanceAnnoInfo>();
    /** 该bean的事务方法 */
    private LightSet<Method>                     txMethods       = new LightSet<Method>();
    /** 该bean的自动关闭资源方法 */
    private LightSet<Method>                     acMethods       = new LightSet<Method>();
    private List<Method>                         cacheMethods    = new LinkedList<Method>();
    /**
     * 该Bean是否可以进行修改。如果是使用外部对象进行bean初始化，由于使用了外部对象，此时不应该再对该类进行aop操作。
     * 同样的，针对该Bean的分析也不应该进行，因为是外部对象，所以其内部的对其他对象的引用不由容器负责。因为不会生成新对象，也就没有注入分析的必要
     */
    private boolean                              canModify       = true;
    /**
     * 对象初始化后，在容器内首先先调用的方法
     */
    private MethodAccessor                       postConstructMethod;
    
    /**
     * 用bean名称和外部对象实例初始化一个bean，该bean为单例
     * 
     * @param beanName
     * @param entity
     */
    public Bean(String beanName, Object singletonInstance)
    {
        this.singletonInstance = singletonInstance;
        prototype = false;
        type = singletonInstance.getClass();
        originType = type;
        canModify = false;
        this.beanName = beanName;
    }
    
    /**
     * 使用Bean名称，Bean是否多例，类对象来初始化Bean
     * 
     * @param beanName
     * @param prototype
     * @param src
     */
    public Bean(String beanName, boolean prototype, Class<?> src)
    {
        configBean(beanName, prototype, src);
    }
    
    /**
     * 初始化Bean，通过注解来识别该bean的名称，是否多例，是否实现容器初始化完毕接口，以及设置类型和原始类型
     * 
     * @param src
     */
    public Bean(Class<?> src)
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, src);
        // 如果资源名称不为空，使用注解的资源名称。否则使用被注解的类的名称
        beanName = StringUtil.isNotBlank(resource.name()) ? resource.name() : src.getName();
        configBean(beanName, resource.shareable() == false, src);
    }
    
    /**
     * 配置Bean信息，设置该bean的资源名称，是否多例，以及该Bean的类型和原始类型。并且判断是否实现了容器初始化完毕接口
     * 
     * @param beanName 该bean的名称
     * @param prototype 该bean是否多例
     * @param src 该bean的类
     */
    private void configBean(String beanName, boolean prototype, Class<?> src)
    {
        this.beanName = beanName;
        type = src;
        originType = src;
        this.prototype = prototype;
        for (Class<?> each : src.getInterfaces())
        {
            if (ContextInitFinish.class.isAssignableFrom(each))
            {
                hasFinishAction = true;
            }
        }
        for (Method each : ReflectUtil.getAllMehtods(src))
        {
            if (each.isAnnotationPresent(PostConstruct.class))
            {
                Verify.True(postConstructMethod == null, "一个类只能有一个方法使用注解PostConstruct");
                Verify.True(each.getParameterTypes().length == 0, "使用PostConstruct注解的方法必须是无参方法，请检查{}.{}", each.getDeclaringClass().getName(), each.getName());
                postConstructMethod = ReflectUtil.fastMethod(each);
            }
        }
    }
    
    public Object getInstance()
    {
        HashMap<String, Object> map = beanInstanceMap.get();
        map.clear();
        return getInstance(map);
        
    }
    
    /**
     * field进行属性注入的时候使用这个方法，这样如果需要循环引用，则因为大家都在一个map中，可以避免循环引用无限循环
     * 
     * @param beanInstanceMap
     * @return
     */
    public Object getInstance(Map<String, Object> beanInstanceMap)
    {
        if (beanInstanceMap.containsKey(beanName))
        {
            return beanInstanceMap.get(beanName);
        }
        if (prototype)
        {
            return buildInstance(beanInstanceMap);
        }
        else
        {
            if (singletonInstance == null)
            {
                singletonInstance = buildInstance(beanInstanceMap);
                return singletonInstance;
            }
            else
            {
                return singletonInstance;
            }
        }
    }
    
    private Object buildInstance(Map<String, Object> beanInstanceMap)
    {
        try
        {
            Object instance = type.newInstance();
            beanInstanceMap.put(beanName, instance);
            for (DependencyField each : injectFields)
            {
                each.inject(instance, beanInstanceMap);
            }
            for (ParamField each : paramFields)
            {
                each.setParam(instance);
            }
            if (postConstructMethod != null)
            {
                postConstructMethod.invoke(instance, null);
            }
            return instance;
        }
        catch (Exception e)
        {
            throw new UnSupportException(StringUtil.format("初始化bean实例错误，实例名称:{},对象类名:{}", beanName, type.getName()), e);
        }
    }
    
    public String getBeanName()
    {
        return beanName;
    }
    
    public Class<?> getType()
    {
        return type;
    }
    
    public boolean isPrototype()
    {
        return prototype;
    }
    
    public void setInjectFields(DependencyField[] injectFields)
    {
        this.injectFields = injectFields;
    }
    
    public boolean HasFinishAction()
    {
        return hasFinishAction;
    }
    
    public void addEnhanceBean(Bean bean)
    {
        String enhanceBeanfieldName = "jfirecoreinvoker" + bean.getType().getSimpleName() + System.nanoTime();
        String path;
        int order;
        for (Method each : bean.getType().getDeclaredMethods())
        {
            
            if (AnnotationUtil.isPresent(AfterEnhance.class, each))
            {
                AfterEnhance afterEnhance = AnnotationUtil.getAnnotation(AfterEnhance.class, each);
                path = afterEnhance.value().equals("") ? each.getName() + "(*)" : afterEnhance.value();
                order = afterEnhance.order();
            }
            else if (AnnotationUtil.isPresent(AroundEnhance.class, each))
            {
                AroundEnhance aroundEnhance = AnnotationUtil.getAnnotation(AroundEnhance.class, each);
                path = aroundEnhance.value().equals("") ? each.getName() + "(*)" : aroundEnhance.value();
                order = aroundEnhance.order();
            }
            else if (AnnotationUtil.isPresent(BeforeEnhance.class, each))
            {
                BeforeEnhance beforeEnhance = AnnotationUtil.getAnnotation(BeforeEnhance.class, each);
                path = beforeEnhance.value().equals("") ? each.getName() + "(*)" : beforeEnhance.value();
                order = beforeEnhance.order();
            }
            else if (AnnotationUtil.isPresent(ThrowEnhance.class, each))
            {
                ThrowEnhance throwEnhance = AnnotationUtil.getAnnotation(ThrowEnhance.class, each);
                path = throwEnhance.value().equals("") ? each.getName() + "(*)" : throwEnhance.value();
                order = throwEnhance.order();
                EnhanceAnnoInfo enhanceAnnoInfo = new EnhanceAnnoInfo(bean, enhanceBeanfieldName, path, order, each);
                enhanceAnnoInfo.setThrowtype(throwEnhance.type());
                enHanceAnnos.add(enhanceAnnoInfo);
                continue;
            }
            else
            {
                continue;
            }
            enHanceAnnos.add(new EnhanceAnnoInfo(bean, enhanceBeanfieldName, path, order, each));
        }
    }
    
    public void setType(Class<?> type)
    {
        this.type = type;
    }
    
    public Class<?> getOriginType()
    {
        return originType;
    }
    
    public void setParamFields(ParamField[] paramFields)
    {
        this.paramFields = paramFields;
    }
    
    public void addTxMethod(Method method)
    {
        txMethods.add(method);
    }
    
    public void addAcMethod(Method method)
    {
        acMethods.add(method);
    }
    
    public LightSet<Method> getTxMethodSet()
    {
        return txMethods;
    }
    
    public LightSet<Method> getAcMethods()
    {
        return acMethods;
    }
    
    public boolean canModify()
    {
        return canModify;
    }
    
    public LightSet<EnhanceAnnoInfo> getEnHanceAnnos()
    {
        return enHanceAnnos;
    }
    
    public boolean needEnhance()
    {
        if (enHanceAnnos.size() > 0 || txMethods.size() > 0 || acMethods.size() > 0 || cacheMethods.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    public void setPostConstructMethod(MethodAccessor postConstructMethod)
    {
        this.postConstructMethod = postConstructMethod;
    }
    
    public void addCacheMethod(Method method)
    {
        cacheMethods.add(method);
    }
    
    public List<Method> getCacheMethods()
    {
        return cacheMethods;
    }
}
