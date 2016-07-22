package com.jfireframework.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.code.CodeLocation;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.order.AescComparator;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.codejson.JsonObject;
import com.jfireframework.codejson.JsonTool;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.aop.AopUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.bean.BeanFactory;
import com.jfireframework.context.bean.annotation.FactoryCreated;
import com.jfireframework.context.bean.field.FieldFactory;
import com.jfireframework.context.config.BeanAttribute;
import com.jfireframework.context.config.BeanInfo;
import com.jfireframework.context.config.ContextConfig;
import com.jfireframework.context.event.impl.EventPublisherImpl;

public class JfireContextImpl implements JfireContext
{
    /**
     * 用来存储Beanfactory的实例
     */
    private IdentityHashMap<Class<?>, BeanFactory> factories   = new IdentityHashMap<Class<?>, BeanFactory>();
    private Map<String, BeanConfig>                configMap   = new HashMap<String, BeanConfig>();
    private Map<String, Bean>                      beanNameMap = new HashMap<String, Bean>();
    private Map<Class<?>, Bean>                    beanTypeMap = new HashMap<Class<?>, Bean>();
    private boolean                                init        = false;
    private List<String>                           classNames  = new LinkedList<String>();
    private static Logger                          logger      = ConsoleLogFactory.getLogger();
    private ClassLoader                            classLoader = JfireContextImpl.class.getClassLoader();
    private BeanUtil                               beanUtil    = new BeanUtil();
    
    public JfireContextImpl()
    {
    }
    
    public JfireContextImpl(String... packageNames)
    {
        addPackageNames(packageNames);
    }
    
    @Override
    public void addPackageNames(String... packageNames)
    {
        Verify.False(init, "不能在容器初始化后再加入需要扫描的包名");
        Verify.notNull(packageNames, "添加扫描的包名有误,不能为null.请检查{}", CodeLocation.getCodeLocation(2));
        List<String> classNames = new LinkedList<String>();
        for (String each : packageNames)
        {
            if (each == null)
            {
                continue;
            }
            for (String var : PackageScan.scan(each))
            {
                classNames.add(var);
            }
        }
        this.classNames.addAll(classNames);
        StringCache cache = new StringCache("共扫描到类：\r\n");
        for (int i = 0; i < classNames.size(); i++)
        {
            cache.append("{}\r\n");
        }
        logger.trace(cache.toString(), (Object[]) classNames.toArray(new String[classNames.size()]));
    }
    
    @Override
    public void readConfig(JsonObject config)
    {
        try
        {
            ContextConfig contextConfig = JsonTool.read(ContextConfig.class, config);
            /** 将配置文件的内容，以json方式读取，并且得到json对象 */
            String[] packageNames = contextConfig.getPackageNames();
            if (packageNames != null)
            {
                addPackageNames(packageNames);
            }
            BeanInfo[] beans = contextConfig.getBeans();
            if (beans != null)
            {
                for (BeanInfo each : beans)
                {
                    String beanName = each.getBeanName();
                    String className = each.getClassName();
                    boolean prototype = each.isPrototype();
                    addBean(beanName, prototype, classLoader.loadClass(className));
                    Map<String, String> dependencies = each.getDependencies();
                    Map<String, String> params = each.getParams();
                    if (dependencies.size() > 0 || params.size() > 0 || each.getPostConstructMethod() != null)
                    {
                        BeanConfig beanConfig = new BeanConfig(beanName);
                        beanConfig.getParamMap().putAll(params);
                        beanConfig.getDependencyMap().putAll(dependencies);
                        beanConfig.setPostConstructMethod(each.getPostConstructMethod());
                        addBeanConfig(beanConfig);
                    }
                }
            }
            if (contextConfig.getBeanConfigs() != null)
            {
                for (BeanAttribute each : contextConfig.getBeanConfigs())
                {
                    if (each.getParams().size() > 0 || each.getDependencies().size() > 0 || each.getPostConstructMethod() != null)
                    {
                        BeanConfig beanConfig = new BeanConfig(each.getBeanName());
                        beanConfig.getParamMap().putAll(each.getParams());
                        beanConfig.getDependencyMap().putAll(each.getDependencies());
                        beanConfig.setPostConstructMethod(each.getPostConstructMethod());
                        addBeanConfig(beanConfig);
                    }
                }
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.error("配置的className错误", e);
        }
    }
    
    @Override
    public void readConfig(File configFile)
    {
        try
        {
            /** 将配置文件的内容，以json方式读取，并且得到json对象 */
            FileInputStream inputStream = new FileInputStream(configFile);
            byte[] result = new byte[inputStream.available()];
            inputStream.read(result);
            inputStream.close();
            String json = new String(result, Charset.forName("utf-8"));
            readConfig((JsonObject) JsonTool.fromString(json));
        }
        catch (FileNotFoundException e)
        {
            logger.error("配置文件不存在", e);
        }
        catch (IOException e)
        {
            logger.error("解析配置文件出现异常，请检查配置文件是否按照格式要求", e);
        }
    }
    
    @Override
    public void addBean(Class<?>... srcs)
    {
        Verify.False(init, "不能在容器初始化后再加入Bean");
        for (Class<?> src : srcs)
        {
            if (AnnotationUtil.isPresent(Resource.class, src))
            {
                Bean bean = new Bean(src);
                beanNameMap.put(bean.getBeanName(), bean);
            }
        }
    }
    
    @Override
    public void addBean(String resourceName, boolean prototype, Class<?> src)
    {
        Verify.False(init, "不能在容器初始化后再加入Bean");
        Bean bean = new Bean(resourceName, prototype, src);
        beanNameMap.put(resourceName, bean);
    }
    
    @Override
    public void addBeanConfig(BeanConfig... beanConfigs)
    {
        Verify.False(init, "不能在容器初始化后再加入Bean配置");
        for (BeanConfig beanConfig : beanConfigs)
        {
            configMap.put(beanConfig.getBeanName(), beanConfig);
        }
    }
    
    @Override
    public void initContext()
    {
        addSingletonEntity(JfireContext.class.getName(), this);
        addBean(EventPublisherImpl.class);
        init = true;
        beanUtil.buildBean(classNames, beanNameMap, classLoader);
        for (Bean each : beanNameMap.values())
        {
            beanTypeMap.put(each.getOriginType(), each);
            if (configMap.containsKey(each.getBeanName()))
            {
                if (configMap.get(each.getBeanName()).getPostConstructMethod() != null)
                {
                    each.setPostConstructMethod(ReflectUtil.fastMethod(ReflectUtil.getMethodWithoutParam(configMap.get(each.getBeanName()).getPostConstructMethod(), each.getOriginType())));
                }
            }
        }
        
        /**
         * 进行aop操作，将aop增强后的class放入对应的bean中。 这步必须在分析bean之前完成。
         * 因为aop进行增强时会生成子类来替代Bean中的type.
         * 并且由于aop需要增加若干个类属性(属性上均有Resouce注解用来注入增强类)，所以注入属性数组的生成必须在aop之后
         */
        AopUtil.enhance(beanNameMap, classLoader);
        beanUtil.initDependencyAndParamFields(beanNameMap, configMap);
        // 提前实例化单例，避免第一次惩罚以及由于是在单线程中实例化，就不会出现多线程可能的单例被实例化不止一次的情况
        for (Bean bean : beanNameMap.values())
        {
            if (bean.isPrototype() == false)
            {
                bean.getInstance();
            }
        }
        /**
         * 按照order顺序运行容器初始化结束方法
         */
        List<ContextInitFinish> tmp = new LinkedList<ContextInitFinish>();
        for (Bean bean : beanNameMap.values())
        {
            if (bean.HasFinishAction())
            {
                tmp.add((ContextInitFinish) bean.getInstance());
            }
        }
        ContextInitFinish[] initFinishs = tmp.toArray(new ContextInitFinish[tmp.size()]);
        Arrays.sort(initFinishs, new AescComparator());
        for (ContextInitFinish each : initFinishs)
        {
            logger.trace("准备执行方法{}.afterContextInit", each.getClass().getName());
            try
            {
                each.afterContextInit();
            }
            catch (Exception e)
            {
                logger.error("执行方法{}.afterContextInit发生异常", each.getClass().getName(), e);
                throw new JustThrowException(e);
            }
        }
    }
    
    @Override
    public Object getBean(String name)
    {
        if (init == false)
        {
            initContext();
        }
        Bean bean = beanNameMap.get(name);
        if (bean != null)
        {
            return bean.getInstance();
        }
        else
        {
            throw new UnSupportException("bean:" + name + "不存在");
        }
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> src)
    {
        if (init == false)
        {
            initContext();
        }
        Bean bean = getBeanInfo(src);
        return (T) bean.getInstance();
    }
    
    @Override
    public Bean getBeanInfo(Class<?> beanClass)
    {
        if (init == false)
        {
            initContext();
        }
        Bean bean = beanTypeMap.get(beanClass);
        if (bean != null)
        {
            return bean;
        }
        throw new UnSupportException("bean" + beanClass.getName() + "不存在");
    }
    
    @Override
    public Bean getBeanInfo(String resName)
    {
        if (init == false)
        {
            initContext();
        }
        return beanNameMap.get(resName);
    }
    
    @Override
    public Bean[] getBeanByAnnotation(Class<? extends Annotation> annotationType)
    {
        if (init == false)
        {
            initContext();
        }
        List<Bean> beans = new LinkedList<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (AnnotationUtil.isPresent(annotationType, each.getOriginType()))
            {
                beans.add(each);
            }
        }
        return beans.toArray(new Bean[beans.size()]);
    }
    
    @Override
    public void addSingletonEntity(String beanName, Object entity)
    {
        Verify.False(init, "不能在容器初始化后还加入bean,请检查{}", CodeLocation.getCodeLocation(2));
        Bean bean = new Bean(beanName, entity);
        beanNameMap.put(beanName, bean);
    }
    
    @Override
    public Bean[] getBeanByInterface(Class<?> type)
    {
        if (init == false)
        {
            initContext();
        }
        List<Bean> list = new LinkedList<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (type.isAssignableFrom(each.getOriginType()))
            {
                list.add(each);
            }
        }
        return list.toArray(new Bean[list.size()]);
    }
    
    @Override
    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }
    
    public ClassLoader getClassLoader()
    {
        return classLoader;
    }
    
    class BeanUtil
    {
        private Logger logger = ConsoleLogFactory.getLogger();
        
        /**
         * 检查所有的Class名称，通过反射获取class，并且进行初始化。
         * 形成基本的bean信息（bean名称，bean类型，单例与否，是否实现完成接口的信息） 将这些信息放入beanNameMap
         * 
         * @param classNames
         * @param beanMap
         */
        public void buildBean(List<String> classNames, Map<String, Bean> beanNameMap, ClassLoader classLoader)
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
        private void buildBean(String className, Map<String, Bean> beanNameMap, ClassLoader classloader)
        {
            Class<?> res = null;
            try
            {
                res = classloader.loadClass(className);
            }
            catch (ClassNotFoundException e)
            {
                throw new RuntimeException("对应的类不存在", e);
            }
            if (AnnotationUtil.isPresent(Resource.class, res) == false)
            {
                logger.trace("类{}未使用资源注解", className);
                return;
            }
            Bean bean = null;
            if (AnnotationUtil.isPresent(FactoryCreated.class, res))
            {
                FactoryCreated factoryCreated = AnnotationUtil.getAnnotation(FactoryCreated.class, res);
                Class<? extends BeanFactory> ckass = factoryCreated.value();
                BeanFactory beanFactory = factories.get(ckass);
                if (beanFactory == null)
                {
                    try
                    {
                        beanFactory = ckass.newInstance();
                        factories.put(ckass, beanFactory);
                    }
                    catch (Exception e)
                    {
                        throw new JustThrowException(e);
                    }
                }
                bean = beanFactory.parse(res);
            }
            else if (res.isInterface() == false)
            {
                bean = new Bean(res);
            }
            else
            {
                throw new UnSupportException(StringUtil.format("在接口上只有Resource注解是无法实例化bean的，请配合FactoryCreated注解.请检查{}", res.getName()));
            }
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
        public void initDependencyAndParamFields(Map<String, Bean> beanNameMap, Map<String, BeanConfig> configMap)
        {
            for (Bean bean : beanNameMap.values())
            {
                if (bean.canModify())
                {
                    BeanConfig beanConfig = configMap.get(bean.getBeanName());
                    bean.setInjectFields(FieldFactory.buildDependencyField(bean, beanNameMap, beanTypeMap, beanConfig));
                    if (beanConfig != null)
                    {
                        bean.setParamFields(FieldFactory.buildParamField(bean, beanConfig));
                    }
                }
            }
        }
    }
}
