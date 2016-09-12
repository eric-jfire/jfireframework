package com.jfireframework.context.bean.field;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.aliasanno.AnnotationUtil;
import com.jfireframework.context.bean.Bean;
import com.jfireframework.context.bean.BeanConfig;
import com.jfireframework.context.bean.annotation.field.CanBeNull;
import com.jfireframework.context.bean.annotation.field.MapKey;
import com.jfireframework.context.bean.field.dependency.DependencyField;
import com.jfireframework.context.bean.field.dependency.impl.BeanNameMapField;
import com.jfireframework.context.bean.field.dependency.impl.DefaultBeanField;
import com.jfireframework.context.bean.field.dependency.impl.ListField;
import com.jfireframework.context.bean.field.dependency.impl.MethodMapField;
import com.jfireframework.context.bean.field.dependency.impl.NullInjectField;
import com.jfireframework.context.bean.field.dependency.impl.ValueMapField;
import com.jfireframework.context.bean.field.param.AbstractParamField;
import com.jfireframework.context.bean.field.param.ParamField;
import sun.reflect.MethodAccessor;

public class FieldFactory
{
    
    /**
     * 根据配置信息和field上的注解信息,返回该bean所有的依赖注入的field
     * 
     * @param bean
     * @param beanNameMap
     * @param beanConfig
     * @return
     */
    public static DependencyField[] buildDependencyField(Bean bean, Map<String, Bean> beanNameMap, Map<Class<?>, Bean> beanTypeMap, BeanConfig beanConfig)
    {
        Field[] fields = ReflectUtil.getAllFields(bean.getType());
        List<DependencyField> list = new LinkedList<DependencyField>();
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
                list.add(buildDependFieldsByConfig(field, beanNameMap, dependencyStr));
            }
            else if (AnnotationUtil.isPresent(Resource.class, field))
            {
                list.add(buildDependencyFieldByAnno(field, bean, beanNameMap, beanTypeMap));
            }
        }
        return list.toArray(new DependencyField[list.size()]);
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
        if (List.class == type)
        {
            return buildListByConfig(field, dependencyStr, beanNameMap);
        }
        else if (Map.class == type)
        {
            return buildMapFieldByConfig(field, dependencyStr, beanNameMap);
        }
        else
        {
            return buildDefaultFieldByConfig(field, dependencyStr, beanNameMap);
        }
    }
    
    private static DependencyField buildDependencyFieldByAnno(Field field, Bean bean, Map<String, Bean> beanNameMap, Map<Class<?>, Bean> beanTypeMap)
    {
        Class<?> type = field.getType();
        if (type == List.class)
        {
            return buildListFieldByAnno(field, beanNameMap);
        }
        else if (type == Map.class)
        {
            return buildMapFieldByAnno(field, beanNameMap);
        }
        else if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
        {
            return buildInterfaceField(field, beanNameMap, beanTypeMap);
        }
        else
        {
            return buildDefaultField(field, beanNameMap, beanTypeMap);
        }
    }
    
    private static DependencyField buildListByConfig(Field field, String dependencyStr, Map<String, Bean> beanNameMap)
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
        return new ListField(field, beans);
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
            Verify.True(keyClass == String.class, "使用version2方式注入的时候是使用名称作为key，所以要求key是String类型。请检查{}.{}", field.getDeclaringClass(), field.getName());
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
                if (keyClass == Integer.class)
                {
                    keys[i] = Integer.valueOf(key);
                }
                else if (keyClass == String.class)
                {
                    keys[i] = key;
                }
                else if (keyClass == Long.class)
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
            List<Bean> beans = new LinkedList<Bean>();
            for (String each : dependencyStr.split(","))
            {
                beans.add(beanNameMap.get(each));
            }
            return new BeanNameMapField(field, beans.toArray(new Bean[beans.size()]));
        }
        else
        {
            throw new UnSupportException(StringUtil.format("属性{}.{}进行map注入，本质信息不正确。请检查{}", field.getDeclaringClass(), field.getName(), dependencyStr));
        }
    }
    
    private static DependencyField buildDefaultFieldByConfig(Field field, String dependencyStr, Map<String, Bean> beanNameMap)
    {
        Bean dependencyBean = beanNameMap.get(dependencyStr);
        Verify.notNull(dependencyBean, "配置文件中注入配置{}的bean不存在", dependencyStr);
        Class<?> type = field.getType();
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers()))
        {
            Verify.True(type.isAssignableFrom(dependencyBean.getOriginType()), "配置文件中注入的bean:{}不是接口:{}的实现", dependencyStr, type);
        }
        else
        {
            Verify.True(type == dependencyBean.getOriginType(), "配置文件中注入的bean:{}不是类{}的类型", dependencyStr, type);
        }
        return new DefaultBeanField(field, dependencyBean);
    }
    
    private static DependencyField buildListFieldByAnno(Field field, Map<String, Bean> beanNameMap)
    {
        ParameterizedType type = (ParameterizedType) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        Class<?> beanInterface = (Class<?>) type.getRawType();
        List<Bean> tmp = new LinkedList<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (beanInterface.isAssignableFrom(each.getOriginType()))
            {
                tmp.add(each);
            }
        }
        return new ListField(field, tmp.toArray(new Bean[tmp.size()]));
    }
    
    private static DependencyField buildMapFieldByAnno(Field field, Map<String, Bean> beanNameMap)
    {
        Type[] types = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
        Verify.matchType(types[0], Class.class, "map依赖字段，要求key必须指明类型，而当前类型是{}", types[0]);
        Verify.matchType(types[1], Class.class, "map依赖字段，要求value必须指明类型，而当前类型是{}", types[1]);
        Class<?> keyClass = (Class<?>) (types[0]);
        Class<?> valueClass = (Class<?>) (types[1]);
        List<Bean> tmp = new LinkedList<Bean>();
        for (Bean each : beanNameMap.values())
        {
            if (valueClass.isAssignableFrom(each.getOriginType()))
            {
                tmp.add(each);
            }
        }
        Bean[] beans = tmp.toArray(new Bean[tmp.size()]);
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
    
    /**
     * 注入一个bean，首先按照名称来寻找，无法找到的情况下使用接口类型来寻找匹配。再找不到报错
     * 
     * @param field
     * @param beanNameMap
     * @return
     */
    private static DependencyField buildInterfaceField(Field field, Map<String, Bean> beanNameMap, Map<Class<?>, Bean> beanTypeMap)
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, field);
        Class<?> type = field.getType();
        if (resource.name().equals("") == false)
        {
            Bean nameBean = beanNameMap.get(resource.name());
            if (AnnotationUtil.isPresent(CanBeNull.class, field))
            {
                if (nameBean == null)
                {
                    return new NullInjectField(field);
                }
                else
                {
                    Verify.True(type.isAssignableFrom(nameBean.getOriginType()), "bean:{}不是接口:{}的实现", nameBean.getOriginType().getName(), type.getName());
                    return new DefaultBeanField(field, nameBean);
                }
            }
            else
            {
                Verify.exist(nameBean, "属性{}.{}指定需要bean:{}注入，但是该bean不存在，请检查", field.getDeclaringClass().getName(), field.getName(), resource.name());
                Verify.True(type.isAssignableFrom(nameBean.getOriginType()), "bean:{}不是接口:{}的实现", nameBean.getOriginType().getName(), type.getName());
                return new DefaultBeanField(field, nameBean);
            }
        }
        else
        {
            // 寻找实现了该接口的bean,如果超过1个,则抛出异常
            int find = 0;
            Bean implBean = null;
            for (Class<?> each : beanTypeMap.keySet())
            {
                if (type.isAssignableFrom(each))
                {
                    find++;
                    implBean = beanTypeMap.get(each);
                }
            }
            if (find != 0)
            {
                Verify.True(find == 1, "接口或抽象类{}的实现多于一个,无法自动注入{}.{},请在resource注解上注明需要注入的bean的名称", type.getName(), field.getDeclaringClass().getName(), field.getName());
                return new DefaultBeanField(field, implBean);
            }
            else if (AnnotationUtil.isPresent(CanBeNull.class, field))
            {
                return new NullInjectField(field);
            }
            else
            {
                throw new NullPointerException(StringUtil.format("属性{}.{}没有可以注入的bean,属性类型为{}", field.getDeclaringClass().getName(), field.getName(), field.getType().getName()));
            }
        }
    }
    
    /**
     * 构建默认情况的注入bean。首先按照bean的名称来寻找，如果找不到，则按照类型来寻找。再找不到，则报错
     * 
     * @param field
     * @param beanNameMap
     * @return
     */
    private static DependencyField buildDefaultField(Field field, Map<String, Bean> beanNameMap, Map<Class<?>, Bean> beanTypeMap)
    {
        Resource resource = AnnotationUtil.getAnnotation(Resource.class, field);
        if (resource.name().equals("") == false)
        {
            Bean nameBean = beanNameMap.get(resource.name());
            if (nameBean != null)
            {
                Verify.True(field.getType() == nameBean.getOriginType(), "bean:{}不是类:{}的实例", nameBean.getBeanName(), field.getType().getName());
                return new DefaultBeanField(field, nameBean);
            }
            else
            {
                if (AnnotationUtil.isPresent(CanBeNull.class, field))
                {
                    return new NullInjectField(field);
                }
                else
                {
                    throw new NullPointerException(StringUtil.format("无法注入{}.{},没有任何可以注入的内容", field.getDeclaringClass().getName(), field.getName()));
                }
            }
        }
        else
        {
            String beanName = field.getType().getName();
            Bean nameBean = beanNameMap.get(beanName);
            if (nameBean != null)
            {
                Verify.True(field.getType() == nameBean.getOriginType(), "bean:{}不是类:{}的实例", nameBean.getBeanName(), field.getType().getName());
                return new DefaultBeanField(field, nameBean);
            }
            else
            {
                Bean typeBean = beanTypeMap.get(field.getType());
                if (typeBean != null)
                {
                    return new DefaultBeanField(field, typeBean);
                }
                else
                {
                    if (AnnotationUtil.isPresent(CanBeNull.class, field))
                    {
                        return new NullInjectField(field);
                    }
                    else
                    {
                        throw new NullPointerException(StringUtil.format("无法注入{}.{},没有任何可以注入的内容", field.getDeclaringClass().getName(), field.getName()));
                    }
                }
            }
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
    public static ParamField[] buildParamField(Bean bean, BeanConfig beanConfig, ClassLoader classLoader)
    {
        Map<String, String> map = beanConfig.getParamMap();
        Field[] fields = ReflectUtil.getAllFields(bean.getType());
        List<ParamField> list = new LinkedList<ParamField>();
        for (Field field : fields)
        {
            if (map.containsKey(field.getName()))
            {
                list.add(buildParamField(field, map.get(field.getName()), classLoader));
            }
        }
        return list.toArray(new ParamField[list.size()]);
    }
    
    private static ParamField buildParamField(Field field, String value, ClassLoader classLoader)
    {
        return AbstractParamField.build(field, value, classLoader);
    }
}
