package com.jfireframework.sql.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.JustThrowException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.annotation.EnumBoundHandler;
import com.jfireframework.sql.annotation.Sql;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.function.mapper.Mapper;
import com.jfireframework.sql.metadata.MetaContext;
import com.jfireframework.sql.metadata.TableMetaData;
import com.jfireframework.sql.metadata.TableMetaData.FieldInfo;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.resultsettransfer.TransferContext;
import com.jfireframework.sql.util.MapperBuilder.SqlContext.EnumHandlerInfo;
import com.jfireframework.sql.util.enumhandler.EnumHandler;
import com.jfireframework.sql.util.enumhandler.EnumStringHandler;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

public class MapperBuilder
{
    private Logger            logger    = ConsoleLogFactory.getLogger();
    private ClassPool         classPool = ClassPool.getDefault();
    private final MetaContext metaContext;
    private TransferContext   transferContext;
    private final boolean     resultFieldCache;
    
    public MapperBuilder(MetaContext metaContext, TransferContext transferContext, boolean resultFieldCache)
    {
        this.resultFieldCache = resultFieldCache;
        this.transferContext = transferContext;
        this.metaContext = metaContext;
        ClassPool.doPruning = true;
        classPool.importPackage("com.jfireframework.sql");
        classPool.importPackage("com.jfireframework.baseutil.collection");
        classPool.importPackage("com.jfireframework.sql.function");
        classPool.importPackage("com.jfireframework.baseutil.exception");
        classPool.importPackage("java.sql");
        classPool.importPackage("java.util");
        classPool.appendClassPath(new ClassClassPath(SqlSession.class));
    }
    
    /**
     * 创造一个Mapper的子类，该子类同时实现了用户指定的接口。并且接口的实现内容就是对注解的sql语句的执行
     *
     * @param interfaceClass 子类需要实现的接口
     * @return
     */
    public Class<?> build(Class<?> origin)
    {
        try
        {
            CtClass implClass = classPool.makeClass(origin.getName() + "_JfireSqlMapper_" + System.nanoTime());
            implClass.setSuperclass(classPool.get(Mapper.class.getName()));
            CtClass interfaceCtClass = classPool.getCtClass(origin.getName());
            implClass.setInterfaces(new CtClass[] { interfaceCtClass });
            createTargetClassMethod(implClass, origin);
            return implClass.toClass(Thread.currentThread().getContextClassLoader(), null);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    private void createTargetClassMethod(CtClass targetCtClass, Class<?> interfaceCtClass) throws Exception
    {
        
        for (Method method : interfaceCtClass.getDeclaredMethods())
        {
            try
            {
                if (method.isAnnotationPresent(Sql.class))
                {
                    Sql sql = method.getAnnotation(Sql.class);
                    if (sql.sql().startsWith("select"))
                    {
                        targetCtClass.addMethod(createQueryMethod(targetCtClass, method, sql.sql(), sql.paramNames().split(",")));
                        
                    }
                    else
                    {
                        targetCtClass.addMethod(createUpdateMethod(targetCtClass, method, sql.sql(), sql.paramNames().split(",")));
                    }
                }
                else
                {
                    throw new UnsupportedOperationException(StringUtil.format("Mapper接口内不能存在非注解的方法。请检查{}.{}", method.getDeclaringClass().getName(), method.getName()));
                }
            }
            catch (Exception e)
            {
                throw new RuntimeException(StringUtil.format("接口存在错误，请检查{}.{}", method.getDeclaringClass().getName(), method.getName()), e);
            }
        }
        
    }
    
    private CtMethod createQueryMethod(CtClass weaveClass, Method method, String sql, String[] paramNames) throws Exception
    {
        SqlContext sqlContext = new SqlContext();
        boolean isPage = false;
        boolean isList = (List.class.isAssignableFrom(method.getReturnType()) ? true : false);
        if (isList)
        {
            Verify.True(
                    ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getClass().equals(Class.class), "方法{}.{}返回类型是泛型，不允许，请指定具体的类型", method.getDeclaringClass(), method.getName()
            );
            Type returnParamType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
            // 确认方法放回不是List<T>的形式
            Verify.False(returnParamType instanceof WildcardType, "接口的返回类型不能是泛型，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
        }
        if (method.getParameterTypes().length > 0 && Page.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1]))
        {
            isPage = true;
        }
        boolean isDynamicSql = DynamicSqlTool.isDynamic(sql);
        StringCache methodBody = new StringCache(1024);
        methodBody.append("{\n");
        methodBody.append("SqlSession session = sessionFactory.getCurrentSession();\n");
        methodBody.append("if(session==null){throw new NullPointerException(\"current session 为空，请检查\");}\n");
        if (isDynamicSql)
        {
            methodBody.append(DynamicSqlTool.analyseDynamicSql(sql, paramNames, method.getParameterTypes(), metaContext, sqlContext));
            if (isList)
            {
                Type returnParamType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                transferContext.add((Class<?>) returnParamType, resultFieldCache);
                methodBody.append("if(list.size()==0){");
                if (isPage)
                {
                    methodBody.append("return ($r)session.queryList(").append(((Class<?>) returnParamType).getName()).append(".class,")//
                            .append("sql,$").append(method.getParameterTypes().length).append(",emptyParams);\n}");
                }
                else
                {
                    methodBody.append("return ($r)session.queryList(").append(((Class<?>) returnParamType).getName()).append(".class,")//
                            .append("sql,emptyParams);\n}");
                }
                methodBody.append("else{");
                if (isPage)
                {
                    methodBody.append("return ($r)session.queryList(").append(((Class<?>) returnParamType).getName()).append(".class,")//
                            .append("sql,$").append(method.getParameterTypes().length).append(",list.toArray());\n}");
                }
                else
                {
                    methodBody.append("return ($r)session.queryList(").append(((Class<?>) returnParamType).getName()).append(".class,")//
                            .append("sql,list.toArray());\n}");
                }
                methodBody.append("}");
            }
            else
            {
                Class<?> returnType = method.getReturnType();
                transferContext.add(returnType, resultFieldCache);
                methodBody.append("if(list.size()==0){");
                methodBody.append("return ($r)session.query(").append(returnType.getName()).append(".class,sql")//
                        .append(",emptyParams);}\n");
                methodBody.append("else{");
                methodBody.append("return ($r)session.queryList(").append(returnType.getName()).append(".class,")//
                        .append("sql,list.toArray());\n}");
                methodBody.append("}");
            }
        }
        else
        {
            DynamicSqlTool.analyseFormatSql(sql, paramNames, method.getParameterTypes(), metaContext, sqlContext);
            if (isList)
            {
                Type returnParamType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                transferContext.add((Class<?>) returnParamType, resultFieldCache);
                if (isPage)
                {
                    methodBody.append("return ($r)session.queryList(").append(((Class<?>) returnParamType).getName()).append(".class,\"")//
                            .append(sqlContext.getSql()).append("\",$").append(method.getParameterTypes().length).append(",");
                }
                else
                {
                    methodBody.append("return ($r)session.queryList(").append(((Class<?>) returnParamType).getName()).append(".class,\"")//
                            .append(sqlContext.getSql()).append("\",");
                }
            }
            else
            {
                Class<?> returnType = method.getReturnType();
                transferContext.add(returnType, resultFieldCache);
                methodBody.append("return ($r)session.query(").append(returnType.getName()).append(".class,\"")//
                        .append(sqlContext.getSql()).append("\",");
            }
            if (sqlContext.getQueryParams().size() == 0)
            {
                methodBody.append("emptyParams);}\n");
            }
            else
            {
                methodBody.append("new Object[]{");
                for (InvokeNameAndType each : sqlContext.getQueryParams())
                {
                    methodBody.append("($w)").append(each.getInvokeName()).appendComma();
                }
                methodBody.deleteLast().append("});}\n");
            }
        }
        CtMethod targetMethod = forCtMethod(method, weaveClass);
        createEnumBoundHandlerField(sqlContext, weaveClass);
        logger.debug("为{}.{}创建的方法体是\n{}\n", method.getDeclaringClass().getName(), method.getName(), methodBody.toString());
        targetMethod.setBody(methodBody.toString());
        return targetMethod;
    }
    
    private void createEnumBoundHandlerField(SqlContext sqlContext, CtClass ctClass) throws CannotCompileException, NotFoundException
    {
        for (EnumHandlerInfo each : sqlContext.enumHandlerInfos)
        {
            CtField ctField = new CtField(classPool.get(each.getHandlerType().getName()), each.getName(), ctClass);
            ctClass.addField(ctField, StringUtil.format("new {}({}.class)", each.getHandlerType().getName(), each.getType().getName()));
        }
    }
    
    /**
     * 创建一个ctmethod，方法签名与method一致
     *
     * @param method
     * @param ctClass
     * @return
     * @throws NotFoundException
     */
    private CtMethod forCtMethod(Method method, CtClass ctClass) throws NotFoundException
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
    
    private CtMethod createUpdateMethod(CtClass mapperClass, Method method, String sql, String[] paramNames) throws Exception
    {
        SqlContext sqlContext = new SqlContext();
        StringCache cache = new StringCache(1024);
        cache.append("{\n");
        cache.append("SqlSession session = sessionFactory.getCurrentSession();\n");
        cache.append("if(session==null){throw new NullPointerException(\"current session 为空，请检查\");}\n");
        boolean isDynamicSql = DynamicSqlTool.isDynamic(sql);
        if (isDynamicSql)
        {
            cache.append(DynamicSqlTool.analyseDynamicSql(sql, paramNames, method.getParameterTypes(), metaContext, sqlContext));
            cache.append("int updateRows=0;\n");
            cache.append("if(list.size()==0){\n");
            cache.append("updateRows = session.update(sql,emptyParams);}\n");
            cache.append("else{\n");
            cache.append("updateRows = session.update(sql,list.toArray());\n");
            cache.append("}\n");
        }
        else
        {
            DynamicSqlTool.analyseFormatSql(sql, paramNames, method.getParameterTypes(), metaContext, sqlContext);
            cache.append("int updateRows = session.update(\"").append(sqlContext.getSql()).append("\",");
            if (sqlContext.getQueryParams().isEmpty())
            {
                cache.append("emptyParams);\n");
            }
            else
            {
                cache.append("new Object[]{");
                for (InvokeNameAndType each : sqlContext.getQueryParams())
                {
                    cache.append("($w)").append(each.getInvokeName()).appendComma();
                }
                cache.deleteLast().append("});\n");
            }
        }
        Class<?> returnType = method.getReturnType();
        if (returnType == Void.class || returnType == void.class)
        {
            ;
        }
        else if (returnType == int.class)
        {
            cache.append("return updateRows;\n");
        }
        else if (returnType == long.class)
        {
            cache.append("return (long)updateRows;\n");
        }
        else if (returnType == Integer.class)
        {
            cache.append("return ($w)updateRows;\n");
        }
        else if (returnType == Long.class)
        {
            cache.append("return ($w)((long)updateRows);\n");
        }
        else
        {
            throw new UnsupportedOperationException(StringUtil.format("更新方法只支持void，int，long，Integer，Long 五种返回类型"));
        }
        cache.append("}");
        createEnumBoundHandlerField(sqlContext, mapperClass);
        CtMethod targetCtMethod = forCtMethod(method, mapperClass);
        logger.debug("为{}.{}创建的方法体是\n{}\n", method.getDeclaringClass().getName(), method.getName(), cache.toString());
        targetCtMethod.setBody(cache.toString());
        return targetCtMethod;
    }
    
    public static class SqlContext
    {
        private List<String>            injectNames      = new LinkedList<String>();
        private Set<TableMetaData>      metaContexts     = new HashSet<TableMetaData>();
        private Map<String, String>     dbColNameMap     = new HashMap<String, String>();
        private Map<String, String>     fieldNameMap     = new HashMap<String, String>();
        private Map<String, Object>     staticValueMap   = new HashMap<String, Object>();
        private String                  sql;
        private String                  countSql;
        private List<InvokeNameAndType> queryParams      = new LinkedList<MapperBuilder.InvokeNameAndType>();
        private List<EnumHandlerInfo>   enumHandlerInfos = new LinkedList<MapperBuilder.SqlContext.EnumHandlerInfo>();
        
        public boolean hasMetaContext()
        {
            return !metaContexts.isEmpty();
        }
        
        public List<String> getInjectNames()
        {
            return injectNames;
        }
        
        public void addInjectName(String inject)
        {
            injectNames.add(inject);
        }
        
        public List<InvokeNameAndType> getQueryParams()
        {
            return queryParams;
        }
        
        public void setQueryParams(List<InvokeNameAndType> queryParams)
        {
            this.queryParams = queryParams;
        }
        
        public String getCountSql()
        {
            return countSql;
        }
        
        public void setCountSql(String countSql)
        {
            this.countSql = countSql;
        }
        
        public String getSql()
        {
            return sql;
        }
        
        public void setSql(String sql)
        {
            this.sql = sql;
        }
        
        @SuppressWarnings("unchecked")
        public void addMetaData(TableMetaData metaData)
        {
            if (metaData == null)
            {
                throw new NullPointerException();
            }
            if (metaContexts.add(metaData) == false)
            {
                return;
            }
            Class<?> type = metaData.getEntityClass();
            String prefix = type.getSimpleName() + '.';
            String tablePrefix = metaData.getTableName() + ".";
            for (FieldInfo each : metaData.getFieldInfos())
            {
                dbColNameMap.put(each.getFieldName(), tablePrefix + each.getDbColName());
                dbColNameMap.put(prefix + each.getFieldName(), tablePrefix + each.getDbColName());
                fieldNameMap.put(tablePrefix + each.getDbColName(), each.getFieldName());
            }
            try
            {
                for (Entry<String, Field> each : metaData.staticFieldMap().entrySet())
                {
                    staticValueMap.put(prefix + each.getKey(), each.getValue().get(null));
                    staticValueMap.put(each.getKey(), each.getValue().get(null));
                }
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
            for (Entry<String, Field> each : metaData.enumFieldMap().entrySet())
            {
                Class<? extends Enum<?>> fieldType = (Class<? extends Enum<?>>) each.getValue().getType();
                Class<? extends EnumHandler<?>> ckass = null;
                if (fieldType.isAnnotationPresent(EnumBoundHandler.class))
                {
                    ckass = fieldType.getAnnotation(EnumBoundHandler.class).value();
                }
                else
                {
                    ckass = EnumStringHandler.class;
                }
                try
                {
                    EnumHandler<?> enumHandler = ckass.getConstructor(Class.class).newInstance(fieldType);
                    for (Enum<?> enumInstance : ReflectUtil.getAllEnumInstances(fieldType).values())
                    {
                        staticValueMap.put(fieldType.getSimpleName() + "." + enumInstance.name(), enumHandler.getValue(enumInstance));
                        staticValueMap.put(enumInstance.name(), enumHandler.getValue(enumInstance));
                    }
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
                
            }
        }
        
        @SuppressWarnings("unchecked")
        public void addAliasName(String name, TableMetaData metaData)
        {
            if (metaData == null)
            {
                throw new NullPointerException();
            }
            String prefix = name + '.';
            for (FieldInfo each : metaData.getFieldInfos())
            {
                dbColNameMap.put(each.getFieldName(), prefix + each.getDbColName());
                dbColNameMap.put(prefix + each.getFieldName(), prefix + each.getDbColName());
                fieldNameMap.put(prefix + each.getDbColName(), each.getFieldName());
            }
            try
            {
                for (Entry<String, Field> each : metaData.staticFieldMap().entrySet())
                {
                    staticValueMap.put(prefix + each.getKey(), each.getValue().get(null));
                    staticValueMap.put(each.getKey(), each.getValue().get(null));
                }
            }
            catch (Exception e)
            {
                throw new JustThrowException(e);
            }
            for (Entry<String, Field> each : metaData.enumFieldMap().entrySet())
            {
                Class<? extends Enum<?>> fieldType = (Class<? extends Enum<?>>) each.getValue().getType();
                Class<? extends EnumHandler<?>> ckass = null;
                if (fieldType.isAnnotationPresent(EnumBoundHandler.class))
                {
                    ckass = fieldType.getAnnotation(EnumBoundHandler.class).value();
                }
                else
                {
                    ckass = EnumStringHandler.class;
                }
                try
                {
                    EnumHandler<?> enumHandler = ckass.getConstructor(Class.class).newInstance(fieldType);
                    for (Enum<?> enumInstance : ReflectUtil.getAllEnumInstances(fieldType).values())
                    {
                        staticValueMap.put(fieldType.getSimpleName() + "." + enumInstance.name(), enumHandler.getValue(enumInstance));
                        staticValueMap.put(enumInstance.name(), enumHandler.getValue(enumInstance));
                    }
                }
                catch (Exception e)
                {
                    throw new JustThrowException(e);
                }
                
            }
        }
        
        public static class EnumHandlerInfo
        {
            private Class<? extends Enum<?>>        type;
            private Class<? extends EnumHandler<?>> handlerType;
            private String                          name;
            
            public EnumHandlerInfo(String name, Class<? extends Enum<?>> type, Class<? extends EnumHandler<?>> handlerType)
            {
                this.name = name;
                this.type = type;
                this.handlerType = handlerType;
            }
            
            public Class<? extends Enum<?>> getType()
            {
                return type;
            }
            
            public void setType(Class<? extends Enum<?>> type)
            {
                this.type = type;
            }
            
            public Class<? extends EnumHandler<?>> getHandlerType()
            {
                return handlerType;
            }
            
            public void setHandlerType(Class<? extends EnumHandler<?>> handlerType)
            {
                this.handlerType = handlerType;
            }
            
            public String getName()
            {
                return name;
            }
            
            public void setName(String name)
            {
                this.name = name;
            }
            
        }
        
        public void addEnumHandler(String name, Class<? extends Enum<?>> enumType, Class<? extends EnumHandler<?>> handleType)
        {
            enumHandlerInfos.add(new EnumHandlerInfo(name, enumType, handleType));
        }
        
        public List<EnumHandlerInfo> enumHandlerInfos()
        {
            return enumHandlerInfos;
        }
        
        public String getDbColName(String fieldName)
        {
            return dbColNameMap.get(fieldName);
        }
        
        public String getFieldName(String dbColName)
        {
            return fieldNameMap.get(dbColName);
        }
        
        public Object getStaticValue(String name)
        {
            return staticValueMap.get(name);
        }
        
    }
    
    /**
     * 用来存储调用的方法源代码，和最终的表达式返回的类型。
     * 比如存储类似$1.getName()这样的表达式，和String.class这样的该方法调用都返回值
     *
     * @author 林斌
     *
     */
    public static class InvokeNameAndType
    {
        private final String   origin;
        private final String   invokeName;
        private final Class<?> returnType;
        
        public InvokeNameAndType(String invokeName, Class<?> returnType, String origin)
        {
            this.invokeName = invokeName;
            this.returnType = returnType;
            this.origin = origin;
        }
        
        public String getOrigin()
        {
            return origin;
        }
        
        public String getInvokeName()
        {
            return invokeName;
        }
        
        public Class<?> getReturnType()
        {
            return returnType;
        }
        
    }
}
