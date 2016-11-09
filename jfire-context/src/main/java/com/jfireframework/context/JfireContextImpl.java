package com.jfireframework.context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
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
import com.jfireframework.context.bean.field.FieldFactory;
import com.jfireframework.context.bean.impl.DefaultBean;
import com.jfireframework.context.bean.impl.LoadByBean;
import com.jfireframework.context.bean.impl.OuterEntityBean;
import com.jfireframework.context.bean.load.LoadBy;
import com.jfireframework.context.config.BeanInfo;
import com.jfireframework.context.config.ContextConfig;

public class JfireContextImpl implements JfireContext
{
    private Map<String, BeanInfo> configMap   = new HashMap<String, BeanInfo>();
    private Map<String, Bean>     beanNameMap = new HashMap<String, Bean>();
    private Map<Class<?>, Bean>   beanTypeMap = new HashMap<Class<?>, Bean>();
    private boolean               init        = false;
    private List<String>          classNames  = new LinkedList<String>();
    private static Logger         logger      = ConsoleLogFactory.getLogger();
    private ClassLoader           classLoader = JfireContextImpl.class.getClassLoader();
    private BeanUtil              beanUtil    = new BeanUtil();
    private Map<String, String>   properties  = new HashMap<String, String>();
    
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
        if (packageNames.length == 0)
        {
            return;
        }
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
            /** 将配置文件的内容，以json方式读取，并且得到json对象 */
            ContextConfig contextConfig = JsonTool.read(ContextConfig.class, config);
            addPackageNames(contextConfig.getPackageNames());
            readProperties(contextConfig.getProperties());
            handleBeanInfos(contextConfig.getBeans());
        }
        catch (ClassNotFoundException e)
        {
            logger.error("配置的className错误", e);
        }
    }
    
    private void handleBeanInfos(BeanInfo[] infos) throws ClassNotFoundException
    {
        for (BeanInfo info : infos)
        {
            String className = info.getClassName();
            // 如果有className就是定义一个全新的bean，否则的话，就是单纯的给已经存在的bean做配置
            if (StringUtil.isNotBlank(className))
            {
                String beanName = info.getBeanName();
                boolean prototype = info.isPrototype();
                addBean(beanName, prototype, classLoader.loadClass(className));
            }
            else
            {
                ;
            }
            if (configMap.put(info.getBeanName(), info) != null)
            {
                throw new UnSupportException(StringUtil.format("bean:{}配置存在两份", info.getBeanName()));
            }
        }
    }
    
    private void readProperties(String[] paths)
    {
        for (String path : paths)
        {
            if (path.startsWith("classpath:"))
            {
                try
                {
                    path = path.substring(10);
                    InputStream in = this.getClass().getClassLoader().getResourceAsStream(path);
                    Properties properties = new Properties();
                    properties.load(in);
                    in.close();
                    addProperties(properties);
                }
                catch (IOException e)
                {
                    throw new JustThrowException(e);
                }
            }
            else
            {
                // 暂时不支持别的模式
                ;
            }
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
                Bean bean = new DefaultBean(src);
                beanNameMap.put(bean.getBeanName(), bean);
            }
        }
    }
    
    @Override
    public void addBean(String resourceName, boolean prototype, Class<?> src)
    {
        Verify.False(init, "不能在容器初始化后再加入Bean");
        Bean bean = new DefaultBean(resourceName, src, prototype);
        beanNameMap.put(resourceName, bean);
    }
    
    @Override
    public void addBeanInfo(BeanInfo... beanInfos)
    {
        Verify.False(init, "不能在容器初始化后再加入Bean配置");
        try
        {
            handleBeanInfos(beanInfos);
        }
        catch (ClassNotFoundException e)
        {
            throw new JustThrowException(e);
        }
    }
    
    @Override
    public void initContext()
    {
        addSingletonEntity(JfireContext.class.getName(), this);
        init = true;
        replaceValueFromPropertiesToBeancfg();
        beanUtil.buildBean(classNames);
        for (Bean each : beanNameMap.values())
        {
            // 这个时候来放入typeMap，才是bean最全的时候
            beanTypeMap.put(each.getOriginType(), each);
            BeanInfo beanInfo = configMap.get(each.getBeanName());
            if (beanInfo != null)
            {
                each.setBeanInfo(beanInfo);
                configMap.remove(each.getBeanName());
                if (beanInfo.getPostConstructMethod() != null)
                {
                    each.setPostConstructMethod(ReflectUtil.fastMethod(ReflectUtil.getMethodWithoutParam(beanInfo.getPostConstructMethod(), each.getOriginType())));
                }
            }
        }
        for (BeanInfo each : configMap.values())
        {
            logger.warn("存在配置没有可识别的bean，请检查配置文件，其中需要配置的beanName为:{}", each.getBeanName());
        }
        /**
         * 进行aop操作，将aop增强后的class放入对应的bean中。 这步必须在分析bean之前完成。
         * 因为aop进行增强时会生成子类来替代Bean中的type.
         * 并且由于aop需要增加若干个类属性(属性上均有Resouce注解用来注入增强类)，所以注入属性数组的生成必须在aop之后
         */
        AopUtil.enhance(beanNameMap, classLoader);
        beanUtil.initDependencyAndParamFields();
        for (Bean bean : beanNameMap.values())
        {
            bean.decorateSelf(beanNameMap, beanTypeMap);
        }
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
    
    private void replaceValueFromPropertiesToBeancfg()
    {
        for (BeanInfo config : configMap.values())
        {
            for (Entry<String, String> entry : config.getParams().entrySet())
            {
                String value = entry.getValue();
                if (value.startsWith("${"))
                {
                    int end = value.indexOf("}||");
                    if (end != -1)
                    {
                        String name = value.substring(2, end);
                        if (properties.get(name) != null)
                        {
                            entry.setValue(properties.get(name));
                        }
                        else
                        {
                            String defaultValue = value.substring(end + 3);
                            entry.setValue(defaultValue);
                        }
                    }
                    else
                    {
                        String name = value.substring(2, value.length() - 1);
                        entry.setValue(properties.get(name));
                    }
                }
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
        Bean bean = new OuterEntityBean(beanName, entity);
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
        Thread.currentThread().setContextClassLoader(classLoader);
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
        public void buildBean(List<String> classNames)
        {
            for (String each : classNames)
            {
                buildBean(each);
            }
        }
        
        /**
         * 对类进行分析，给出该类的信息Bean，并且填充包括bean名称，bean类型，单例与否，是否实现完成接口的信息
         * 
         * @param className
         * @param context
         * @return
         */
        private void buildBean(String className)
        {
            Class<?> res = null;
            try
            {
                res = classLoader.loadClass(className);
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
            if (AnnotationUtil.isPresent(LoadBy.class, res))
            {
                LoadBy loadBy = AnnotationUtil.getAnnotation(LoadBy.class, res);
                bean = new LoadByBean(res, loadBy.factoryBeanName());
            }
            else if (res.isInterface() == false)
            {
                bean = new DefaultBean(res);
            }
            else
            {
                throw new UnSupportException(StringUtil.format("在接口上只有Resource注解是无法实例化bean的.请检查{}", res.getName()));
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
        public void initDependencyAndParamFields()
        {
            for (Bean bean : beanNameMap.values())
            {
                if (bean.canInject())
                {
                    BeanInfo beanConfig = bean.getBeanInfo();
                    bean.setInjectFields(FieldFactory.buildDependencyField(bean, beanNameMap, beanTypeMap, beanConfig));
                    if (beanConfig != null)
                    {
                        bean.setParamFields(FieldFactory.buildParamField(bean, beanConfig, classLoader));
                    }
                }
            }
        }
    }
    
    @Override
    public void addProperties(Properties... properties)
    {
        for (Properties each : properties)
        {
            for (Entry<Object, Object> entry : each.entrySet())
            {
                this.properties.put((String) entry.getKey(), (String) entry.getValue());
            }
        }
    }
}
