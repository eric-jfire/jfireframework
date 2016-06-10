package com.jfireframework.sql.util;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.annotation.BatchUpdate;
import com.jfireframework.sql.annotation.Query;
import com.jfireframework.sql.annotation.Update;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.function.mapper.Mapper;
import com.jfireframework.sql.page.Page;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class InterfaceMapperFactory
{
    private static Logger                 logger       = ConsoleLogFactory.getLogger();
    private static ClassPool              classPool    = ClassPool.getDefault();
    public static Map<Class<?>, Class<?>> mapperBeans  = new HashMap<Class<?>, Class<?>>();
    private static Set<Class<?>>          baseClassSet = new HashSet<Class<?>>();
    
    static
    {
        baseClassSet.add(String.class);
        baseClassSet.add(Integer.class);
        baseClassSet.add(Long.class);
        baseClassSet.add(Float.class);
        baseClassSet.add(Short.class);
        baseClassSet.add(Double.class);
        baseClassSet.add(Boolean.class);
        baseClassSet.add(Byte.class);
        baseClassSet.add(int.class);
        baseClassSet.add(long.class);
        baseClassSet.add(float.class);
        baseClassSet.add(short.class);
        baseClassSet.add(double.class);
        baseClassSet.add(boolean.class);
        baseClassSet.add(char.class);
        baseClassSet.add(byte.class);
    }
    
    static
    {
        ClassPool.doPruning = true;
        classPool.insertClassPath(new ClassClassPath(InterfaceMapperFactory.class));
        classPool.importPackage("com.jfireframework.sql");
        classPool.importPackage("com.jfireframework.baseutil.collection");
        classPool.importPackage("com.jfireframework.sql.function");
        classPool.importPackage("java.sql");
        classPool.importPackage("java.util");
        classPool.insertClassPath(new ClassClassPath(SqlSession.class));
    }
    
    /**
     * 获取实现了用户接口的Mapper实现，该实现主要作用是发出注解中的sql语句
     * 
     * @param entityClass 用户的接口类
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getMapper(Class<T> entityClass)
    {
        try
        {
            return (T) mapperBeans.get(entityClass).newInstance();
        }
        catch (Exception e)
        {
            throw new JustThrowException(e);
        }
        
    }
    
    public static void buildMapper(Class<?> ckass, ClassLoader classLoader)
    {
        mapperBeans.put(ckass, createMapper(ckass, classLoader));
    }
    
    /**
     * 创造一个Mapper的子类，该子类同时实现了用户指定的接口。并且接口的实现内容就是对注解的sql语句的执行
     * 
     * @param interfaceClass 子类需要实现的接口
     * @return
     */
    private static Class<?> createMapper(Class<?> interfaceClass, ClassLoader classLoader)
    {
        try
        {
            CtClass implClass = classPool.makeClass(interfaceClass.getName() + "_JfireOrmMapper_" + System.nanoTime());
            implClass.setSuperclass(classPool.get(Mapper.class.getName()));
            CtClass interfaceCtClass = classPool.getCtClass(interfaceClass.getName());
            implClass.setInterfaces(new CtClass[] { interfaceCtClass });
            createTargetClassMethod(implClass, interfaceClass);
            return implClass.toClass(classLoader, null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 创建目标方法，实现的原则将接口方法实现，方法体为执行接口方法上的注解的sql
     * 
     * @param targetCtClass 待织入的类
     * @param interfaceCtClass 需要实现的接口
     * @throws NotFoundException
     * @throws CannotCompileException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    private static void createTargetClassMethod(CtClass targetCtClass, Class<?> interfaceCtClass) throws NotFoundException, CannotCompileException, ClassNotFoundException, NoSuchFieldException, SecurityException
    {
        
        for (Method method : interfaceCtClass.getDeclaredMethods())
        {
            try
            {
                if (method.isAnnotationPresent(Query.class))
                {
                    targetCtClass.addMethod(createQueryMethod(targetCtClass, method, method.getAnnotation(Query.class)));
                }
                if (method.isAnnotationPresent(Update.class))
                {
                    targetCtClass.addMethod(createUpdateMethod(targetCtClass, method, method.getAnnotation(Update.class)));
                }
                if (method.isAnnotationPresent(BatchUpdate.class))
                {
                    targetCtClass.addMethod(createBatchUpdateMethod(targetCtClass, method, method.getAnnotation(BatchUpdate.class)));
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(StringUtil.format("接口存在错误，请检查{}.{}", method.getDeclaringClass().getName(), method.getName()), e);
            }
        }
        
    }
    
    private static CtMethod createQueryMethod(CtClass weaveClass, Method method, Query query) throws NotFoundException, CannotCompileException, NoSuchFieldException, SecurityException
    {
        boolean isList = (method.getReturnType().isAssignableFrom(List.class) ? true : false);
        boolean isPage = false;
        if (method.getParameterTypes().length > 0 && Page.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1]))
        {
            isPage = true;
        }
        boolean isDynamicSql = DynamicSqlTool.isDynamic(query.sql());
        StringCache methodBody = new StringCache();
        methodBody.append("{\n");
        String querySql = null, queryParam = null, countSql = null, countParam = null;
        if (isDynamicSql)
        {
            methodBody.append(DynamicSqlTool.analyseDynamicSql(query.sql(), query.paramNames().split(","), method.getParameterTypes(), isPage, query.countSql().equals("") ? null : query.countSql()));
        }
        else
        {
            String[] sqlAndParam = DynamicSqlTool.analyseFormatSql(query.sql(), query.paramNames().split(","), method.getParameterTypes(), isPage, query.countSql().equals("") ? null : query.countSql());
            querySql = sqlAndParam[0];
            queryParam = sqlAndParam[1];
            countSql = sqlAndParam[2];
            countParam = sqlAndParam[3];
        }
        if (isList)
        {
            // 确认方法返回不是List<?>的形式
            Verify.True(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getClass().equals(Class.class), "方法{}.{}返回类型是泛型，不允许，请指定具体的类型", method.getDeclaringClass(), method.getName());
            Type returnParamType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            // 确认方法放回不是List<T>的形式
            Verify.False(returnParamType instanceof WildcardType, "接口的返回类型不能是泛型，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
            Class<?>[] resultTypes = null;
            if (isPage)
            {
                methodBody.append("\tjava.util.List result = ");
            }
            else
            {
                methodBody.append("\treturn ");
            }
            // 如果方法返回是List<Object[]>
            if (returnParamType instanceof GenericArrayType)
            {
                Verify.True(query.returnTypes().length > 0, "方法{}.{}的query注解中的returnType没有内容", method.getDeclaringClass(), method.getName());
                resultTypes = query.returnTypes();
                methodBody.append("($r) session.listQuery(new Class[]{");
                for (Class<?> each : resultTypes)
                {
                    methodBody.append(each.getName()).append(".class,");
                }
                methodBody.deleteLast().append("},");
            }
            // 如果方法返回是List<T>
            else if (returnParamType instanceof Class)
            {
                Class<?> resultType = (Class<?>) returnParamType;
                if (baseClassSet.contains(resultType))
                {
                    methodBody.append("($r)session.baseListQuery(").append(resultType.getName()).append(".class,");
                }
                else
                {
                    methodBody.append("($r)session.listQuery(").append(resultType.getName()).append(".class,");
                }
            }
            else
            {
                throw new RuntimeException("方法的返回参数错误");
            }
        }
        else
        {
            Class<?> resultType = method.getReturnType();
            if (baseClassSet.contains(resultType))
            {
                methodBody.append("\treturn ($r)session.baseQuery(").append(resultType.getName()).append(".class,");
            }
            else
            {
                methodBody.append("\treturn ($r)session.query(").append(resultType.getName()).append(".class,");
            }
        }
        if (isDynamicSql)
        {
            methodBody.append("builder.toString(),");
            methodBody.append("list.toArray()").append(");\n");
        }
        else
        {
            
            methodBody.append('"').append(querySql).append("\",");
            methodBody.append(queryParam).append(");\n");
        }
        if (isPage)
        {
            String var = "((com.jfireframework.sql.page.Page)$" + method.getParameterTypes().length + ")";
            methodBody.append("\t" + var + ".setData(result);\n");
            if (isDynamicSql)
            {
                methodBody.append("\tint total = ((Integer)session.baseQuery(int.class,countSql,countParam)).intValue();\n");
                
            }
            else
            {
                methodBody.append("\tint total = ((Integer)session.baseQuery(int.class,\"" + countSql + "\",").append(countParam).append(")).intValue();\n");
            }
            methodBody.append("\t" + var + ".setTotal(total);\n");
            methodBody.append("\treturn ($r)result;\n}");
            
        }
        else
        {
            methodBody.append("}");
        }
        logger.debug("为{}.{}创建的方法体是\n{}\n", method.getDeclaringClass().getName(), method.getName(), methodBody.toString());
        CtMethod targetMethod = forCtMethod(method, weaveClass);
        targetMethod.setBody(methodBody.toString());
        return targetMethod;
    }
    
    /**
     * 创建一个ctmethod，方法签名与method一致
     * 
     * @param method
     * @param ctClass
     * @return
     * @throws NotFoundException
     */
    private static CtMethod forCtMethod(Method method, CtClass ctClass) throws NotFoundException
    {
        CtClass returnType = classPool.get(method.getReturnType().getName());
        CtClass[] paramClasses = new CtClass[method.getParameterTypes().length];
        int index = 0;
        for (Class<?> each : method.getParameterTypes())
        {
            paramClasses[index++] = classPool.get(each.getName());
        }
        return new CtMethod(returnType, method.getName(), paramClasses, ctClass);
    }
    
    private static CtMethod createUpdateMethod(CtClass mapperClass, Method method, Update update) throws NoSuchFieldException, SecurityException, NotFoundException, CannotCompileException
    {
        StringCache cache = new StringCache();
        cache.append("{");
        boolean isDynamicSql = DynamicSqlTool.isDynamic(update.sql());
        String sql = null, param = null;
        if (isDynamicSql)
        {
            cache.append(DynamicSqlTool.analyseDynamicSql(update.sql(), update.paramNames().split(","), method.getParameterTypes(), false, null));
        }
        else
        {
            String[] sqlAndParam = DynamicSqlTool.analyseFormatSql(update.sql(), update.paramNames().split(","), method.getParameterTypes(), false, null);
            sql = sqlAndParam[0];
            param = sqlAndParam[1];
        }
        if (method.getReturnType().getName().equals("void"))
        {
            cache.append("session.update(");
        }
        else
        {
            Class<?> returnType = method.getReturnType();
            if (returnType == int.class || returnType == Integer.class || returnType == long.class || returnType == Long.class)
            {
                cache.append(" return ($r)session.update(");
            }
            else
            {
                throw new RuntimeException("update方法的返回只能是int或者long或者其包装类");
            }
        }
        if (isDynamicSql)
        {
            cache.append("builder.toString(),list.toArray());}");
        }
        else
        {
            cache.append('"').append(sql).append("\",");
            cache.append(param).append(");}");
        }
        CtMethod targetCtMethod = forCtMethod(method, mapperClass);
        logger.debug("为{}.{}创建的方法体是\n{}\n", method.getDeclaringClass().getName(), method.getName(), cache.toString());
        targetCtMethod.setBody(cache.toString());
        return targetCtMethod;
    }
    
    private static CtMethod createBatchUpdateMethod(CtClass mapperClass, Method method, BatchUpdate batchInsert) throws NotFoundException, CannotCompileException, NoSuchFieldException, SecurityException
    {
        String originalSql = batchInsert.sql();
        List<String> variateNames = new ArrayList<String>();
        String sql = DynamicSqlTool.getFormatSql(originalSql, variateNames);
        int length = variateNames.size();
        String[] params = new String[length];
        String[] paramNames = batchInsert.paramNames().split(",");
        Type[] types = method.getGenericParameterTypes();
        Class<?>[] paramTypes = new Class<?>[types.length];
        for (int i = 0; i < types.length; i++)
        {
            paramTypes[i] = (Class<?>) ((ParameterizedType) types[i]).getActualTypeArguments()[0];
        }
        for (int i = 0; i < length; i++)
        {
            String inject = variateNames.get(i);
            if (inject.indexOf('.') == -1)
            {
                Integer index = DynamicSqlTool.getParamNameIndex(inject, paramNames);
                Verify.notNull(index, "sql注入语句{}无法找到注入属性{}", originalSql, inject);
                params[i] = "$" + (index + 1) + ".get(i)";
            }
            else
            {
                String[] tmp = inject.split("\\.");
                Integer index = DynamicSqlTool.getParamNameIndex(tmp[0], paramNames);
                Verify.notNull(index, "sql注入语句{}无法找到注入属性{}", originalSql, inject);
                String getMethodName = ReflectUtil.buildGetMethod(inject, paramTypes[index]);
                params[i] = "((" + paramTypes[index].getName() + ")$" + (index + 1) + ".get(i))" + getMethodName;
            }
        }
        StringCache cache = new StringCache();
        // 这里的size是指总共插入的行数，所以取第一个参数的size即可。实际上是应该所有的参数的size是相同的，这里省去了对这个限制的验证
        cache.append("{int size = ((java.util.List)$1).size();");
        cache.append("java.util.List list = new ArrayList(size);");
        cache.append("for(int i=0;i<size;i++)").append('{');
        cache.append("Object[] tmp = new Object[").append(params.length).append("];");
        for (int i = 0; i < params.length; i++)
        {
            cache.append("tmp[").append(i).append("]=").append(params[i]).append(";");
        }
        cache.append("list.add(tmp);}");
        if (method.getReturnType().getName().equals("void"))
        {
            cache.append("session.batchUpdate(\"").append(sql).append("\",list);}");
        }
        else
        {
            cache.append("return ($r)session.batchUpdate(\"").append(sql).append("\",list);}");
        }
        CtMethod targetMethod = forCtMethod(method, mapperClass);
        logger.debug("创建的批量更新sql是{}", cache.toString());
        targetMethod.setBody(cache.toString());
        return targetMethod;
    }
    
}
