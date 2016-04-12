package com.jfireframework.context;

import java.io.File;
import java.lang.annotation.Annotation;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;

public interface JfireContext
{
    /**
     * 读取配置文件中的信息，如果配置文件中配置了PackageNames，则向容器中增加包信息。
     * 如果配置了beans，则向容器增加bean信息
     * 
     * @param configFile
     */
    public void readConfig(File configFile);
    
    /**
     * 向容器内增加需要扫描的包名
     * 
     * @param packageNames
     */
    public void addPackageNames(String... packageNames);
    
    /**
     * 增加若干个类到容器内，该类必须自己打上对应需要的注解
     * 
     * @param src
     */
    public void addBean(Class<?>... srcs);
    
    /**
     * 增加一个类到容器类，并且使用定义好的资源名称，是否多例配置
     * 
     * @param beanName
     * @param prototype
     * @param src
     */
    public void addBean(String beanName, boolean prototype, Class<?> src);
    
    /**
     * 增加若干个类配置到容器中
     * 
     * @param beanConfigs
     */
    public void addBeanConfig(BeanConfig... beanConfigs);
    
    /**
     * 初始化方法，主要负责
     * （1）分析给定的路径，并且将路径中有注解的元素解析出来，以Bean的形式放入map中
     * （2）针对有AOP增强的类，完成AOP增强
     * （3）在单线程中将单例类全部实例化，构造单例类的对象实例以供后续使用
     * 
     * @param packageName
     */
    public void initContext();
    
    /**
     * 根据Bean名称获取对象实例
     * 
     * @param name
     * @return
     */
    public Object getBean(String name);
    
    /**
     * 根据给定的类型，查找bean后实例化并且返回
     * 
     * @param src
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public <T> T getBean(Class<T> src);
    
    /**
     * 根据给定的类型，查询当前类型匹配参数的bean返回
     * 
     * @param beanClass
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public Bean getBeanInfo(Class<?> beanClass);
    
    /**
     * 根据注解名称，查询Bean信息并且返回
     * 
     * @param resName
     * @return
     */
    public Bean getBeanInfo(String resName);
    
    /**
     * 查询容器之中类持有特定注解的bean数组
     * 
     * @param annotationType
     * @return
     * @author windfire(windfire@zailanghua.com)
     */
    public Bean[] getBeanByAnnotation(Class<? extends Annotation> annotationType);
    
    /**
     * 查询容器之中类实现特定接口的bean数组
     * 
     * @param type
     * @return
     */
    public Bean[] getBeanByInterface(Class<?> type);
    
    /**
     * 向容器中增加一个单例Bean，并且该单例bean的对象实例已经提供。
     * 注意，由于该对象实例是外界提供的，故该bean无法参与aop增强
     * 
     * @param beanName 该bean的名称
     * @param entity 该单例bean的实例
     */
    public void addSingletonEntity(String beanName, Object entity);
    
    public void setClassLoader(ClassLoader classLoader);
}
