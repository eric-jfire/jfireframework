package com.jfireframework.context.bean.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.aop.EnhanceAnnoInfo;
import com.jfireframework.context.aop.annotation.AfterEnhance;
import com.jfireframework.context.aop.annotation.AroundEnhance;
import com.jfireframework.context.aop.annotation.BeforeEnhance;
import com.jfireframework.context.aop.annotation.ThrowEnhance;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.param.ParamField;
import com.jfireframework.context.config.BeanInfo;
import sun.reflect.MethodAccessor;

/**
 * 容器管理的bean
 * 
 * @author 林斌（eric@jfire.cn）
 * 
 */
public abstract class AbstractBean implements Bean
{
    /** 该bean的名称 */
    protected String                                     beanName;
    /** 该bean的class对象，可能为增强后的对象 */
    protected Class<?>                                   type;
    /** 该bean的原始class对象，供查询使用 */
    protected Class<?>                                   originType;
    /** 该bean需要进行属性注入的field */
    protected DependencyField[]                          injectFields    = new DependencyField[0];
    /** 该bean需要进行属性初始化的field */
    protected ParamField[]                               paramFields     = new ParamField[0];
    /** 该bean是否是多例 */
    protected boolean                                    prototype       = false;
    /* bean对象初始化过程中暂存生成的中间对象 */
    protected final ThreadLocal<HashMap<String, Object>> beanInstanceMap = new ThreadLocal<HashMap<String, Object>>() {
                                                                             @Override
                                                                             protected HashMap<String, Object> initialValue()
                                                                             {
                                                                                 return new HashMap<String, Object>();
                                                                             }
                                                                         };
    /** 单例的引用对象 */
    protected Object                                     singletonInstance;
    /** 该bean是否实现了容器初始化结束接口 */
    protected boolean                                    hasFinishAction = false;
    /** 该bean的所有增强方法信息 */
    protected List<EnhanceAnnoInfo>                      enHanceAnnos    = new LinkedList<EnhanceAnnoInfo>();
    /** 该bean的事务方法 */
    protected List<Method>                               txMethods       = new LinkedList<Method>();
    /** 该bean的自动关闭资源方法 */
    protected List<Method>                               resMethod       = new LinkedList<Method>();
    protected List<Method>                               cacheMethods    = new LinkedList<Method>();
    /**
     * 该bean是否可以进行增强。如果是外部直接设置的对象，则不可以进行增强
     */
    protected boolean                                    canEnhance      = true;
    /**
     * 该bean是否可以进行依赖注入和参数注入
     */
    protected boolean                                    canInject       = true;
    /**
     * 对象初始化后，在容器内首先先调用的方法
     */
    protected MethodAccessor                             postConstructMethod;
    protected BeanInfo                                   beanInfo;
    
    @Override
    public String getBeanName()
    {
        return beanName;
    }
    
    @Override
    public Class<?> getType()
    {
        return type;
    }
    
    @Override
    public boolean isPrototype()
    {
        return prototype;
    }
    
    @Override
    public void setInjectFields(DependencyField[] injectFields)
    {
        this.injectFields = injectFields;
    }
    
    @Override
    public boolean HasFinishAction()
    {
        return hasFinishAction;
    }
    
    @Override
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
    
    @Override
    public void setType(Class<?> type)
    {
        this.type = type;
    }
    
    @Override
    public Class<?> getOriginType()
    {
        return originType;
    }
    
    @Override
    public void setParamFields(ParamField[] paramFields)
    {
        this.paramFields = paramFields;
    }
    
    @Override
    public void addTxMethod(Method method)
    {
        txMethods.add(method);
    }
    
    @Override
    public void addResMethod(Method method)
    {
        resMethod.add(method);
    }
    
    @Override
    public List<Method> getTxMethodSet()
    {
        return txMethods;
    }
    
    @Override
    public List<Method> getResMethods()
    {
        return resMethod;
    }
    
    @Override
    public boolean canEnhance()
    {
        return canEnhance;
    }
    
    @Override
    public boolean canInject()
    {
        return canInject;
    }
    
    @Override
    public List<EnhanceAnnoInfo> getEnHanceAnnos()
    {
        return enHanceAnnos;
    }
    
    @Override
    public boolean needEnhance()
    {
        if (enHanceAnnos.size() > 0 || txMethods.size() > 0 || resMethod.size() > 0 || cacheMethods.size() > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    @Override
    public void setPostConstructMethod(MethodAccessor postConstructMethod)
    {
        this.postConstructMethod = postConstructMethod;
    }
    
    @Override
    public void addCacheMethod(Method method)
    {
        cacheMethods.add(method);
    }
    
    @Override
    public List<Method> getCacheMethods()
    {
        return cacheMethods;
    }
    
    @Override
    public BeanInfo getBeanInfo()
    {
        return beanInfo;
    }
    
    @Override
    public void setBeanInfo(BeanInfo beanInfo)
    {
        this.beanInfo = beanInfo;
    }
    
    @Override
    public void decorateSelf(Map<String, Bean> beanNameMap, Map<Class<?>, Bean> beanTypeMap)
    {
        ;
    }
}
