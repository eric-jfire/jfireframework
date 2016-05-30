package com.jfireframework.context.util;

import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.bean.field.FieldFactory;

public class ContextUtil
{
    private static Logger logger = ConsoleLogFactory.getLogger();
    
    /**
     * 检查所有的Class名称，通过反射获取class，并且进行初始化。
     * 形成基本的bean信息（bean名称，bean类型，单例与否，是否实现完成接口的信息） 将这些信息放入beanNameMap
     * 
     * @param classNames
     * @param beanMap
     */
    public static void buildBean(LightSet<String> classNames, Map<String, Bean> beanNameMap, ClassLoader classLoader)
    {
        for (String each : classNames)
        {
            buildBean(each, beanNameMap, classLoader);
        }
    }
    
    /**
     * 对类进行分析，给出该类的信息Bean，并且填充包括bean名称，bean类型，单例与否，是否实现完成接口的信息
     * 
     * @param className
     * @param context
     * @return
     */
    private static void buildBean(String className, Map<String, Bean> beanNameMap, ClassLoader classloader)
    {
        Class<?> res = null;
        try
        {
            if (classloader == null)
            {
                res = Class.forName(className);
            }
            else
            {
                res = classloader.loadClass(className);
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException("对应的类不存在", e);
        }
        if (AnnotationUtil.isAnnotationPresent(Resource.class, res) == false)
        {
            logger.trace("类{}未使用资源注解", className);
            return;
        }
        Bean bean = new Bean(res);
        if (beanNameMap.containsKey(bean.getBeanName()))
        {
            Bean sameNameBean = beanNameMap.get(bean.getBeanName());
            Verify.True(sameNameBean.getOriginType().equals(bean.getOriginType()), "类{}和类{}使用了相同的bean名称，请检查", sameNameBean.getOriginType(), bean.getOriginType().getName());
        }
        else
        {
            logger.trace("为类{}注册bean", res.getName());
            beanNameMap.put(bean.getBeanName(), bean);
        }
        
    }
    
    /**
     * 分析所有的组件bean，将其中需要注入的属性的bean形成injectField数组以供注入使用
     * 
     * @param beanNameMap
     */
    public static void initDependencyAndParamFields(Map<String, Bean> beanNameMap, Map<String, BeanConfig> configMap)
    {
        for (Bean bean : beanNameMap.values())
        {
            if (bean.canModify())
            {
                BeanConfig beanConfig = configMap.get(bean.getBeanName());
                bean.setInjectFields(FieldFactory.buildDependencyField(bean, beanNameMap, beanConfig));
                if (beanConfig != null)
                {
                    bean.setParamFields(FieldFactory.buildParamField(bean, beanConfig));
                }
            }
        }
    }
    
}
