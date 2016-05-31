package com.jfireframework.context.bean.field;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.bean.annotation.field.MapKey;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.dependency.impl.BeanNameMapField;
import com.jfireframework.context.bean.field.dependency.impl.DefaultBeanField;
import com.jfireframework.context.bean.field.dependency.impl.InterfaceField;
import com.jfireframework.context.bean.field.dependency.impl.LightSetField;
import com.jfireframework.context.bean.field.dependency.impl.ListField;
import com.jfireframework.context.bean.field.dependency.impl.MethodMapField;
import com.jfireframework.context.bean.field.dependency.impl.NoActionField;
import com.jfireframework.context.bean.field.dependency.impl.ValueMapField;
import com.jfireframework.context.bean.field.param.ParamField;
import com.jfireframework.context.bean.field.param.impl.BooleanField;
import com.jfireframework.context.bean.field.param.impl.FloatField;
import com.jfireframework.context.bean.field.param.impl.IntField;
import com.jfireframework.context.bean.field.param.impl.IntegerField;
import com.jfireframework.context.bean.field.param.impl.LongField;
import com.jfireframework.context.bean.field.param.impl.StringArrayField;
import com.jfireframework.context.bean.field.param.impl.StringField;
import com.jfireframework.context.bean.field.param.impl.WBooleanField;
import com.jfireframework.context.bean.field.param.impl.WFloatField;
import com.jfireframework.context.bean.field.param.impl.WLongField;
import com.jfireframework.context.util.AnnotationUtil;
import sun.reflect.MethodAccessor;

public class FieldFactory
{
    private static Logger logger = ConsoleLogFactory.getLogger();
    
    /**
     * 根据配置信息和field上的注解信息,返回该bean所有的依赖注入的field
     * 
     * @param bean
     * @param beanNameMap
     * @param beanConfig
     * @return
     */
    public static DependencyField[] buildDependencyField(Bean bean, Map<String, Bean> beanNameMap, BeanConfig beanConfig)
    {
        Field[] fields = ReflectUtil.getAllFields(bean.getType());
        LightSet<DependencyField> set = new LightSet<DependencyField>();
        Map<String, String> dependencyMap = null;
        if (beanConfig != null)
        {
            dependencyMap = beanConfig.getDependencyMap();
        }
        // 优先以配置中的为准
        for (Field field : fields)
        {
            if (dependencyMap != null && dependencyMap.containsKey(field.getName()))
            {
                String dependencyStr = dependencyMap.get(field.getName());
                set.add(buildDependFieldsByConfig(field, beanNameMap, dependencyStr));
            }
            else if (AnnotationUtil.isPresent(Resource.class, field))
            {
                set.add(buildDependencyFieldByAnno(field, bean, beanNameMap));
            }
        }
        return set.toArray(DependencyField.class);
    }
    
    /**
     * 使用配置信息为bean生成依赖注入信息
     * 
     * @param bean
     * @param beanNameMap
     * @param dependencyMap
     * @return
     */
    private static DependencyField buildDependFieldsByConfig(Field field, Map<String, Bean> beanNameMap, String dependencyStr)
    {
        Class<?> type = field.getType();
        if (LightSet.class.equals(type) || List.class.equals(type))
        {
            return buildListOrLightsetFieldByConfig(field, dependencyStr, beanNameMap);
        }
        else if (Map.class.equals(type))
        {
            return buildMapFieldByConfig(field, dependencyStr, beanNameMap);
        }
        else
        {
            return buildInterfaceOrDefaultFieldByConfig(field, dependencyStr, beanNameMap);
        }
    }
    
    private static DependencyField buildDependencyFieldByAnno(Field field, Bean bean, Map<String, Bean> beanNameMap)
    {
        Class<?> type = field.getType();
        if (LightSet.class.equals(type) || List.class.equals(type))
        {
            return buildListOrLightsetFieldByAnno(field, beanNameMap);
        }
        else if (Map.class.equals(type))
        {
            return buildMapFieldByAnno(field, beanNameMap);
        }
        else if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
        {
            return buildInterfaceField(field, beanNameMap);
        }
        else
        {
            return buildDefaultField(field, beanNameMap);
        }
    }
    
    private static DependencyField buildListOrLightsetFieldByConfig(Field field, String dependencyStr, Map<String, Bean> beanNameMap)
    {
        String[] dependencyBeanNames = dependencyStr.split(";");
        Bean[] beans = new Bean[dependencyBeanNames.length];
        Class<?> beanInterface = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        for (int i = 0; i < beans.length; i++)
        {
            Bean dependencyBean = beanNameMap.get(dependencyBeanNames[i]);
            Verify.exist(dependencyBean, "配置文件中注入配置{}中的beanName:{}不存在", dependencyStr, dependencyBeanNames[i]);
            Verify.True(beanInterface.isAssignableFrom(dependencyBean.getType()), "配置文件中注入配置{}中的beanName:{}没有实现接口:{}", dependencyStr, dependencyBeanNames[i], beanInterface);
            beans[i] = dependencyBean;
        }
        if (LightSet.class.equals(field.getType()))
        {
            return new LightSetField(field, beans);
        }
        else
        {
            return new ListField(field, beans);
        }
    }
    
    private static DependencyField buildMapFieldByConfig(Field field, String dependencyStr, Map<String, Bean> beanNameMap)
    {
        Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        Verify.matchType(types[0], Class.class, "map依赖字段，要求key必须指明类型，而当前类型是{}", types[0]);
        Verify.matchType(types[1], Class.class, "map依赖字段，要求value必须指明类型，而当前类型是{}", types[1]);
        Class<?> keyClass = (Class<?>) (types[0]);
        Class<?> valueClass = (Class<?>) (types[1]);
        if (dependencyStr.startsWith("version1!"))
        {
            dependencyStr = dependencyStr.substring(9);
            String methodName = dependencyStr.split(":")[0];
            String[] dependencyBeanNames = dependencyStr.split(":")[1].split(";");
            Bean[] beans = new Bean[dependencyBeanNames.length];
            for (int i = 0; i < beans.length; i++)
            {
                Bean dependencyBean = beanNameMap.get(dependencyBeanNames[i]);
                Verify.exist(dependencyBean, "配置文件中注入配置{}中的beanName:{}不存在", dependencyStr, dependencyBeanNames[i]);
                Verify.True(valueClass.isAssignableFrom(dependencyBean.getType()), "配置文件中注入配置{}中的beanName:{}和类:{}类型不符", dependencyStr, dependencyBeanNames[i], valueClass);
                beans[i] = dependencyBean;
            }
            return new MethodMapField(field, beans, initMapKeyMethods(beans, methodName, field.getDeclaringClass(), keyClass));
        }
        else if (dependencyStr.startsWith("version2!"))
        {
            dependencyStr = dependencyStr.substring(9);
            Verify.True(keyClass.equals(String.class), "只用Resource注解进行map注入时，key就是注入的bean的名称，所以要求key是String类型。请检查{}.{}", field.getDeclaringClass(), field.getName());
            String[] keyAndValues = dependencyStr.split("\\|");
            Bean[] beans = new Bean[keyAndValues.length];
            Object[] keys = new Object[keyAndValues.length];
            for (int i = 0; i < beans.length; i++)
            {
                String key = keyAndValues[i].split(":")[0];
                String value = keyAndValues[i].split(":")[1];
                Bean dependencyBean = beanNameMap.get(value);
                Verify.exist(dependencyBean, "属性{}.{}进行map注入，配置信息{}中指定的bean{}不存在", field.getDeclaringClass(), field.getName(), dependencyStr, value);
                beans[i] = dependencyBean;
                String _simpleName = keyClass.getSimpleName();
                if (_simpleName.equals("Integer"))
                {
                    keys[i] = Integer.valueOf(key);
                    
                }
                else if (_simpleName.equals("String"))
                {
                    keys[i] = key;
                }
                else if (_simpleName.equals("Long"))
                {
                    keys[i] = Long.valueOf(key);
                }
                else
                {
                    throw new RuntimeException("不识别的类型");
                }
            }
            return new ValueMapField(field, beans, keys);
        }
        else if (dependencyStr.startsWith("version3!"))
        {
            dependencyStr = dependencyStr.substring(9);
            Verify.True(keyClass.equals(String.class), "只用Resource注解进行map注入时，key就是注入的bean的名称，所以要求key是String类型。请检查{}.{}", field.getDeclaringClass(), field.getName());
            LightSet<Bean> beans = new LightSet<Bean>();
            for (String each : dependencyStr.split(","))
            {
                beans.add(beanNameMap.get(each));
            }
            return new BeanNameMapField(field, beans.toArray(Bean.class));
        }
        else
        {
            throw new RuntimeException(StringUtil.format("属性{}.{}进行map注入，本质信息不正确。请检查{}", field.getDeclaringClass(), field.getName(), dependencyStr));
        }
    }
    
    private static DependencyField buildInterfaceOrDefaultFieldByConfig(Field field, String dependencyStr, Map<String, Bean> beanNameMap)
    {
        Bean dependencyBean = beanNameMap.get(dependencyStr);
        Verify.notNull(dependencyBean, "配置文件中注入配置{}的bean不存在", dependencyStr);
        Class<?> type = field.getType();
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
        {
            Verify.True(type.isAssignableFrom(dependencyBean.getOriginType()), "配置文件中注入的bean:{}不是接口:{}的实现", dependencyStr, type);
            return new InterfaceField(field, dependencyBean);
        }
        else
        {
            Verify.True(type.isAssignableFrom(dependencyBean.getOriginType()), "配置文件中注入的bean:{}不是类{}的类型", dependencyStr, type);
            return new DefaultBeanField(field, dependencyBean);
        }
    }
    
    private static DependencyField buildListOrLightsetFieldByAnno(Field field, Map<String, Bean> beanNameMap)
    {
        Class<?> beanInterface = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        LightSet<Bean> tmp = new LightSet<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (beanInterface.isAssignableFrom(each.getOriginType()))
            {
                tmp.add(each);
            }
        }
        if (tmp.size() > 0)
        {
            if (LightSet.class.equals(field.getType()))
            {
                return new LightSetField(field, tmp.toArray(Bean.class));
            }
            else
            {
                return new ListField(field, tmp.toArray(Bean.class));
            }
        }
        else
        {
            return new NoActionField(field);
        }
    }
    
    private static DependencyField buildMapFieldByAnno(Field field, Map<String, Bean> beanNameMap)
    {
        Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        Verify.matchType(types[0], Class.class, "map依赖字段，要求key必须指明类型，而当前类型是{}", types[0]);
        Verify.matchType(types[1], Class.class, "map依赖字段，要求value必须指明类型，而当前类型是{}", types[1]);
        Class<?> keyClass = (Class<?>) (types[0]);
        Class<?> valueClass = (Class<?>) (types[1]);
        LightSet<Bean> tmp = new LightSet<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (valueClass.isAssignableFrom(each.getOriginType()))
            {
                tmp.add(each);
            }
        }
        Bean[] beans = tmp.toArray(Bean.class);
        if (AnnotationUtil.isPresent(MapKey.class, field))
        {
            String methodName = AnnotationUtil.getAnnotation(MapKey.class, field).value();
            return new MethodMapField(field, beans, initMapKeyMethods(beans, methodName, field.getDeclaringClass(), keyClass));
            
        }
        else
        {
            Verify.True(keyClass.equals(String.class), "只用Resource注解进行map注入时，key就是注入的bean的名称，所以要求key是String类型。请检查{}.{}", field.getDeclaringClass(), field.getName());
            return new BeanNameMapField(field, beans);
        }
    }
    
    private static DependencyField buildInterfaceField(Field field, Map<String, Bean> beanNameMap)
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, field);
        Class<?> type = field.getType();
        // 如果指明了beanName,则寻找对应的beanName进行注入准备
        if (StringUtil.isNotBlank(resource.name()))
        {
            Bean implBean = beanNameMap.get(resource.name());
            Verify.notNull(implBean, "资源{}不存在,注入错误", resource.name());
            Verify.True(type.isAssignableFrom(implBean.getOriginType()), "bean:{}不是接口:{}的实现", implBean.getOriginType().getName(), type.getName());
            return new InterfaceField(field, implBean);
        }
        // 否则寻找实现了该接口的bean,如果超过1个,则抛出异常
        else
        {
            int find = 0;
            Bean implBean = null;
            for (Bean each : beanNameMap.values())
            {
                if (type.isAssignableFrom(each.getOriginType()))
                {
                    find++;
                    implBean = each;
                }
            }
            if (find != 0)
            {
                Verify.True(find == 1, "接口或抽象类{}的实现多于一个,无法自动注入{}.{},请在resource注解上注明需要注入的bean的名称", type.getName(), field.getDeclaringClass().getName(), field.getName());
                return new InterfaceField(field, implBean);
            }
            else
            {
                logger.warn("接口或抽象类{}没有实现,请注意", type);
                return new NoActionField(field);
            }
        }
    }
    
    private static DependencyField buildDefaultField(Field field, Map<String, Bean> beanNameMap)
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, field);
        String beanName = resource.name().equals("") ? field.getType().getName() : resource.name();
        Bean implBean = beanNameMap.get(beanName);
        if (implBean == null)
        {
            logger.warn("属性{}.{}没有对应的bean可以注入", field.getDeclaringClass().getName(), field.getName());
            return new NoActionField(field);
        }
        else
        {
            try
            {
                Verify.True(field.getType().isAssignableFrom(implBean.getOriginType()), "bean:{}不是类:{}的实例", implBean.getOriginType().getName(), field.getType().getName());
            }
            catch (Exception e)
            {
                System.out.println(field.getType().getClassLoader());
                System.out.println(implBean.getOriginType().getClassLoader());
                throw new RuntimeException(e);
            }
            return new DefaultBeanField(field, implBean);
        }
    }
    
    private static MethodAccessor[] initMapKeyMethods(Bean[] beans, String methodName, Class<?> hasMapFieldClass, Class<?> keyClass)
    {
        MethodAccessor[] methods = new MethodAccessor[beans.length];
        Class<?> target;
        Method method = null;
        for (int i = 0; i < beans.length; i++)
        {
            target = beans[i].getType();
            while (target != Object.class)
            {
                method = null;
                try
                {
                    method = target.getDeclaredMethod(methodName);
                    break;
                }
                catch (Exception e)
                {
                    target = target.getSuperclass();
                    continue;
                }
            }
            Verify.notNull(method, "类{}需要进行map注入，类{}是其中的一个value，但是缺少无参{}方法", hasMapFieldClass, beans[i].getOriginType(), methodName);
            Verify.False(method.getReturnType().equals(Void.class), "类{}需要进行map注入，类{}是其中的一个value，但是方法{}没有返回值", hasMapFieldClass, beans[i].getOriginType(), methodName);
            methods[i] = ReflectUtil.fastMethod(method);
        }
        return methods;
    }
    
    /**
     * 根据配置文件，返回该bean所有的条件输入注入的field
     * 
     * @param bean
     * @param beanConfig
     * @return
     */
    public static ParamField[] buildParamField(Bean bean, BeanConfig beanConfig)
    {
        Map<String, String> map = beanConfig.getParamMap();
        Field[] fields = ReflectUtil.getAllFields(bean.getType());
        LightSet<ParamField> set = new LightSet<ParamField>();
        for (Field field : fields)
        {
            if (map.containsKey(field.getName()))
            {
                set.add(buildParamField(field, map.get(field.getName())));
            }
        }
        return set.toArray(ParamField.class);
    }
    
    private static ParamField buildParamField(Field field, String value)
    {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(String.class))
        {
            return new StringField(field, value);
        }
        else if (fieldType.equals(Integer.class))
        {
            return new IntegerField(field, value);
        }
        else if (fieldType.equals(int.class))
        {
            return new IntField(field, value);
        }
        else if (fieldType.equals(Long.class))
        {
            return new WLongField(field, value);
        }
        else if (fieldType.equals(long.class))
        {
            return new LongField(field, value);
        }
        else if (fieldType.equals(Boolean.class))
        {
            return new WBooleanField(field, value);
        }
        else if (fieldType.equals(boolean.class))
        {
            return new BooleanField(field, value);
        }
        else if (fieldType.equals(Float.class))
        {
            return new FloatField(field, value);
        }
        else if (fieldType.equals(Float.class))
        {
            return new WFloatField(field, value);
        }
        else if (fieldType.equals(String[].class))
        {
            return new StringArrayField(field, value);
        }
        else
        {
            throw new RuntimeException(StringUtil.format("属性类型{}还未支持，请联系框架作者eric@jfire.com", fieldType));
        }
    }
}
