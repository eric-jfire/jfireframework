package com.jfireframework.baseutil.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.annotation.IgnoreField;
import com.jfireframework.baseutil.code.CodeLocation;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.collection.set.LightSet;
import com.jfireframework.baseutil.verify.Verify;
import sun.misc.Unsafe;
import sun.reflect.MethodAccessor;

@SuppressWarnings("restriction")
public final class ReflectUtil
{
    // 调用该方法用于生成method.invoke需要的实际执行者
    protected static Method acquireMethodAccessor;
    // 该属性是method.invoke的实际执行者
    protected static Field  methodAccessor;
    private static Unsafe   unsafe;
    
    private ReflectUtil()
    {
        
    }
    
    static
    {
        try
        {
            // 由反编译Unsafe类获得的信息
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            // 获取静态属性,Unsafe在启动JVM时随rt.jar装载
            unsafe = (Unsafe) field.get(null);
            acquireMethodAccessor = Method.class.getDeclaredMethod("acquireMethodAccessor");
            acquireMethodAccessor.setAccessible(true);
            methodAccessor = Method.class.getDeclaredField("methodAccessor");
            methodAccessor.setAccessible(true);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static Unsafe getUnsafe()
    {
        return unsafe;
    }
    
    /**
     * 获取字段的偏移量
     * 
     * @param fieldName
     * @param type
     * @return
     */
    public static long getFieldOffset(String fieldName, Class<?> type)
    {
        try
        {
            Field field = type.getDeclaredField(fieldName);
            field.setAccessible(true);
            Verify.False(Modifier.isStatic(field.getModifiers()), "属性{}.{}是静态属性,不应该使用该方法,请检查{}", field.getDeclaringClass(), field.getName(), CodeLocation.getCodeLocation(2));
            return unsafe.objectFieldOffset(field);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 获取method的更快的执行者MethodAccessor
     * 
     * @param src
     * @return
     */
    public static MethodAccessor fastMethod(Method src)
    {
        try
        {
            src.setAccessible(true);
            acquireMethodAccessor.invoke(src);
            return (MethodAccessor) methodAccessor.get(src);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private static final Comparator<Field> FIELD_COMPARATOR = new Comparator<Field>() {
        // 只需要去重，并且希望父类的field在返回数组中排在后面，所以比较全部返回1
        @Override
        public int compare(Field o1, Field o2)
        {
            if (o1.getName().equals(o2.getName()))
            {
                return 0;
            }
            else
            {
                return 1;
            }
        }
    };
    
    /**
     * 获取该类的所有field对象，如果子类重写了父类的field，则只包含子类的field
     * 
     * @param entityClass
     * @return
     */
    public static Field[] getAllFields(Class<?> entityClass)
    {
        Set<Field> set = new TreeSet<Field>(FIELD_COMPARATOR);
        while (entityClass != Object.class)
        {
            for (Field each : entityClass.getDeclaredFields())
            {
                set.add(each);
            }
            entityClass = entityClass.getSuperclass();
        }
        return set.toArray(new Field[set.size()]);
        
    }
    
    private final static Comparator<Method> METHOD_COMPARATOR = new Comparator<Method>() {
        
        @Override
        public int compare(Method o1, Method o2)
        {
            if (o1.getName().equals(o2.getName()) == false)
            {
                return 1;
            }
            Class<?>[] paramTypes1 = o1.getParameterTypes();
            Class<?>[] paramTypes2 = o2.getParameterTypes();
            if (paramTypes1.length != paramTypes2.length)
            {
                return 1;
            }
            for (int i = 0, n = paramTypes1.length; i < n; i++)
            {
                if (paramTypes1[i] != paramTypes2[i])
                {
                    return 1;
                }
            }
            return 0;
        }
    };
    
    /**
     * 获取该类所有方法,包含父类的方法.如果子类重载了父类的方法,则该集合中只有子类的方法
     * 
     * @param entityClass
     * @return
     */
    public static Method[] getAllMehtods(Class<?> entityClass)
    {
        Set<Method> set = new TreeSet<Method>(METHOD_COMPARATOR);
        while (entityClass != Object.class)
        {
            for (Method each : entityClass.getDeclaredMethods())
            {
                set.add(each);
            }
            entityClass = entityClass.getSuperclass();
        }
        return set.toArray(new Method[set.size()]);
    }
    
    /**
     * 从get方法或者is方法或者set方法中提取属性的名称
     * 
     * @param method
     * @return
     */
    public static String getFieldNameFromMethod(Method method)
    {
        String methodName = method.getName();
        String log = StringUtil.format("只能提取符合javabean get set规范的属性名称，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
        if (methodName.startsWith("get") || methodName.startsWith("is"))
        {
            Verify.True(method.getParameterTypes().length == 0, log);
            if (methodName.startsWith("get"))
            {
                
                return methodName.substring(3).substring(0, 1).toLowerCase() + methodName.substring(3).substring(1);
            }
            else
            {
                return methodName.substring(2).substring(0, 1).toLowerCase() + methodName.substring(2).substring(1);
            }
        }
        else if (methodName.startsWith("set"))
        {
            Verify.True(method.getParameterTypes().length == 1, log);
            return methodName.substring(3).substring(0, 1).toLowerCase() + methodName.substring(3).substring(1);
        }
        else
        {
            throw new RuntimeException(log);
        }
    }
    
    public static Class<?> getFinalReturnType(String name, Class<?> rootType) throws NoSuchFieldException, SecurityException
    {
        return (Class<?>) getBuildMethodAndType(name, rootType)[1];
    }
    
    /**
     * 给定一个字符串参数和初始类型rootType。将字符串转换成调用方法的字符串。比如一个类User有name属性。给定字符串user.name。
     * 会返回字符串.getUser()。并且同时返回最后方法的返回类型 数组0为方法调用字符串，数组1是返回类型
     * 
     * @param name
     * @param rootType
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static Object[] getBuildMethodAndType(String name, Class<?> rootType) throws NoSuchFieldException, SecurityException
    {
        StringCache cache = new StringCache();
        String[] names = name.split("\\.");
        for (int i = 1; i < names.length; i++)
        {
            cache.append('.');
            String tmp = names[i];
            if (tmp.endsWith("()"))
            {
                String methodName = tmp.substring(0, tmp.length() - 2);
                try
                {
                    Method tmpMethod = rootType.getDeclaredMethod(methodName);
                    cache.append(tmp);
                    rootType = tmpMethod.getReturnType();
                    continue;
                }
                catch (NoSuchMethodException e)
                {
                    throw new RuntimeException(e);
                }
            }
            if (tmp.endsWith(")"))
            {
                cache.append(tmp);
                return new Object[] { cache.toString(), null };
            }
            int left = tmp.indexOf('[');
            if (left != -1)
            {
                int right = tmp.indexOf(']', left);
                Verify.True(right != -1, "构建javabean的get或is方法出现异常,给定的字符串:{}不符合解析规则.请检查代码{}", name, CodeLocation.getCodeLocation(2));
                int num = Integer.valueOf(tmp.substring(left + 1, right));
                cache.append("get").append(tmp.substring(0, 1).toUpperCase()).append(tmp.substring(1, left));
                cache.append("()").append('[').append(num).append(']');
                rootType = rootType.getDeclaredField(tmp).getType().getComponentType();
            }
            else
            {
                String getMethodName = "get" + tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
                try
                {
                    Method tmpMethod = rootType.getDeclaredMethod(getMethodName);
                    cache.append(getMethodName + "()");
                    rootType = tmpMethod.getReturnType();
                }
                catch (NoSuchMethodException e)
                {
                    try
                    {
                        String isMethodName = "is" + tmp.substring(0, 1).toUpperCase() + tmp.substring(1);
                        Method tmpMethod = rootType.getDeclaredMethod(isMethodName);
                        cache.append(isMethodName + "()");
                        rootType = tmpMethod.getReturnType();
                    }
                    catch (NoSuchMethodException e2)
                    {
                        try
                        {
                            Method tmpMethod = rootType.getMethod(getMethodName);
                            cache.append(getMethodName + "()");
                            rootType = tmpMethod.getReturnType();
                        }
                        catch (NoSuchMethodException e1)
                        {
                            throw new RuntimeException("给定的参数有异常，没有对应的方法，请检查" + name);
                        }
                    }
                    
                }
            }
        }
        return new Object[] { cache.toString(), rootType };
    }
    
    /**
     * 根据给定的对象类型和字符串,返回对于该属性的获取字符串. 比如name是user.name就会生成一个字符串".getName()"
     * 并且也可以识别数组,以及boolean变量时变成is
     * 
     * @param name
     * @param rootType
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static String buildGetMethod(String name, Class<?> rootType) throws NoSuchFieldException, SecurityException
    {
        return (String) getBuildMethodAndType(name, rootType)[0];
    }
    
    /**
     * 获得对象中所有javabean方法中的get方法或者is方法
     * 
     * @param ckass
     * @return
     */
    public static Method[] listGetMethod(Class<?> ckass)
    {
        Set<MethodInfo> set = new HashSet<MethodInfo>();
        LightSet<Method> methods = new LightSet<Method>();
        do
        {
            for (Method each : ckass.getDeclaredMethods())
            {
                if (
                    Modifier.isPublic(each.getModifiers()) == false //
                            || each.isAnnotationPresent(IgnoreField.class) //
                            || each.getParameterTypes().length > 0//
                            || (each.getName().startsWith("get") | each.getName().startsWith("is")) == false //
                            || each.getReturnType().equals(Void.class)//
                            || each.getName().equals("get") //
                            || each.getName().equals("is")
                )
                {
                    continue;
                }
                if (set.add(new MethodInfo(each)))
                {
                    methods.add(each);
                }
            }
            ckass = ckass.getSuperclass();
        } while (ckass != null && ckass != Object.class);
        return methods.toArray(Method.class);
    }
    
    /**
     * 获得对象中所有javabean方法中的set方法
     * 
     * @param ckass
     * @return
     */
    public static Method[] listSetMethod(Class<?> ckass)
    {
        Set<MethodInfo> set = new HashSet<MethodInfo>();
        LightSet<Method> methods = new LightSet<Method>();
        do
        {
            for (Method each : ckass.getDeclaredMethods())
            {
                if (Modifier.isPublic(each.getModifiers()) == false || each.isAnnotationPresent(IgnoreField.class) || each.getParameterTypes().length != 1 || each.getName().startsWith("set") == false)
                {
                    continue;
                }
                if (each.getName().equals("set"))
                {
                    continue;
                }
                if (set.add(new MethodInfo(each)))
                {
                    methods.add(each);
                }
            }
            ckass = ckass.getSuperclass();
        } while (ckass != null && ckass.equals(Object.class) == false);
        return methods.toArray(Method.class);
    }
    
    /**
     * 根据field获取该这个field的get方法（比如field是name，则会返回getName，如果类型是boolean，则会返回isName（
     * ））
     * 
     * @param field
     * @return
     */
    public static Method getGetterMethod(Field field)
    {
        Class<?> target = field.getDeclaringClass();
        String tmpName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        try
        {
            if (field.getType().equals(boolean.class) || field.getType().equals(Boolean.class))
            {
                return target.getDeclaredMethod("is" + tmpName);
            }
            else
            {
                return target.getDeclaredMethod("get" + tmpName);
            }
        }
        catch (Exception e)
        {
            return null;
        }
    }
    
    /**
     * 根据field获取该这个field的set方法（比如field是name，则会返回setName，如果类型是boolean，则会返回isName（
     * ））
     * 
     * @param field
     * @return
     */
    public static Method getSetterMethod(Field field)
    {
        Class<?> target = field.getDeclaringClass();
        String tmpName = field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        try
        {
            return target.getDeclaredMethod("set" + tmpName, field.getType());
        }
        catch (Exception e)
        {
            return null;
        }
        
    }
    
    /**
     * 获取一个无参方法，方法名是name
     * 
     * @param name
     * @param ckass
     * @return
     */
    public static Method getMethodWithoutParam(String name, Class<?> ckass)
    {
        Class<?> ori = ckass;
        do
        {
            try
            {
                Method method = ckass.getDeclaredMethod(name);
                return method;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                ckass = ckass.getSuperclass();
                if (ckass == null)
                {
                    throw new RuntimeException(StringUtil.format("找不到对应的方法,请检查{}.{}", ori.getName(), name));
                }
            }
        } while (ckass.equals(Object.class) != false);
        throw new RuntimeException("找不到对应的方法");
    }
}

class MethodInfo
{
    private String     methodName;
    private Class<?>[] paramTypes;
    
    public MethodInfo(Method method)
    {
        methodName = method.getName();
        paramTypes = method.getParameterTypes();
    }
    
    @Override
    public int hashCode()
    {
        return methodName.hashCode();
    }
    
    @Override
    public boolean equals(Object target)
    {
        if (target instanceof MethodInfo)
        {
            MethodInfo info = (MethodInfo) target;
            if (info.methodName != methodName)
            {
                return false;
            }
            if (info.paramTypes.length != paramTypes.length)
            {
                return false;
            }
            for (int i = 0; i < paramTypes.length; i++)
            {
                if (info.paramTypes[i] != paramTypes[i])
                {
                    return false;
                }
            }
            return true;
        }
        else
        {
            return false;
        }
    }
    
}
