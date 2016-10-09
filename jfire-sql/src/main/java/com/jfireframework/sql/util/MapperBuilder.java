package com.jfireframework.sql.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.annotation.Query;
import com.jfireframework.sql.annotation.Update;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.function.mapper.Mapper;
import com.jfireframework.sql.metadata.MetaContext;
import com.jfireframework.sql.metadata.TableMetaData;
import com.jfireframework.sql.metadata.TableMetaData.FieldInfo;
import com.jfireframework.sql.page.Page;
import com.jfireframework.sql.resultsettransfer.BooleanTransfer;
import com.jfireframework.sql.resultsettransfer.DoubleTransfer;
import com.jfireframework.sql.resultsettransfer.FixationBeanTransfer;
import com.jfireframework.sql.resultsettransfer.FloatTransfer;
import com.jfireframework.sql.resultsettransfer.IntegerTransfer;
import com.jfireframework.sql.resultsettransfer.LongTransfer;
import com.jfireframework.sql.resultsettransfer.ShortTransfer;
import com.jfireframework.sql.resultsettransfer.SqlDateTransfer;
import com.jfireframework.sql.resultsettransfer.StringTransfer;
import com.jfireframework.sql.resultsettransfer.TimeStampTransfer;
import com.jfireframework.sql.resultsettransfer.TimeTransfer;
import com.jfireframework.sql.resultsettransfer.UtilDateTransfer;
import com.jfireframework.sql.resultsettransfer.VariableBeanTransfer;
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
    
    public MapperBuilder(MetaContext metaContext)
    {
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
                if (method.isAnnotationPresent(Query.class))
                {
                    targetCtClass.addMethod(createQueryMethod(targetCtClass, method, method.getAnnotation(Query.class)));
                }
                else if (method.isAnnotationPresent(Update.class))
                {
                    targetCtClass.addMethod(createUpdateMethod(targetCtClass, method, method.getAnnotation(Update.class)));
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
    
    private CtMethod createQueryMethod(CtClass weaveClass, Method method, Query query) throws Exception
    {
        SqlContext sqlContext = new SqlContext();
        boolean isList = (List.class.isAssignableFrom(method.getReturnType()) ? true : false);
        boolean isPage = false;
        if (method.getParameterTypes().length > 0 && Page.class.isAssignableFrom(method.getParameterTypes()[method.getParameterTypes().length - 1]))
        {
            isPage = true;
        }
        boolean isDynamicSql = DynamicSqlTool.isDynamic(query.sql());
        StringCache methodBody = new StringCache(1024);
        methodBody.append("{\n");
        methodBody.append("SqlSession session = sessionFactory.getCurrentSession();\n");
        methodBody.append("if(session==null){throw new NullPointerException(\"current session 为空，请检查\");}\n");
        methodBody.append("java.sql.Connection connection = session.getConnection();\n");
        methodBody.append("java.sql.PreparedStatement pStat = null;\n");
        if (isDynamicSql)
        {
            methodBody.append(DynamicSqlTool.analyseDynamicSql(query.sql(), query.paramNames().split(","), method.getParameterTypes(), isPage, query.countSql().equals("") ? null : query.countSql(), metaContext, sqlContext));
            methodBody.append("try{\n");
            methodBody.append("pStat = connection.prepareStatement(sql);\n");
            String fieldName;
            if (isList)
            {
                Verify.True(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getClass().equals(Class.class), "方法{}.{}返回类型是泛型，不允许，请指定具体的类型", method.getDeclaringClass(), method.getName());
                Type returnParamType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                // 确认方法放回不是List<T>的形式
                Verify.False(returnParamType instanceof WildcardType, "接口的返回类型不能是泛型，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
                fieldName = createTransferField(weaveClass, (Class<?>) returnParamType, method, metaContext, sqlContext);
            }
            else
            {
                fieldName = createTransferField(weaveClass, method.getReturnType(), method, metaContext, sqlContext);
            }
            methodBody.append("for(int i=0;i<queryParam.length;i++){\n");
            methodBody.append("pStat.setObject(i+1,queryParam[i]);\n");
            methodBody.append("}\n");
            logCode(methodBody, "sql", "queryParam");
            methodBody.append("java.sql.ResultSet resultSet = pStat.executeQuery();\n");
            if (isList)
            {
                methodBody.append("java.util.List result = " + fieldName + ".transferList(resultSet);\n");
                if (isPage)
                {
                    int pageIndex = method.getParameterTypes().length;
                    String var = "((com.jfireframework.sql.page.Page)$" + pageIndex + ")";
                    methodBody.append(var + ".setData(result);\n");
                    methodBody.append("pStat = connection.prepareStatement(countSql);\n");
                    methodBody.append("for(int i=0;i<countParam.length;i++){\n");
                    methodBody.append("pStat.setObject(i+1,countParam[i]);\n");
                    methodBody.append("}\n");
                    logCode(methodBody, "countSql", "countParam");
                    methodBody.append("resultSet = pStat.executeQuery();\n");
                    methodBody.append("resultSet.next();\n");
                    methodBody.append("int total = resultSet.getInt(1);\n");
                    methodBody.append(var + ".setTotal(total);\n");
                    methodBody.append("return ($r)result;\n");
                }
                else
                {
                    methodBody.append("return ($r)result;\n");
                }
            }
            else
            {
                if (method.getReturnType().isPrimitive())
                {
                    methodBody.append("return ").append(fieldName).append(".primitiveValue(resultSet);\n");
                }
                else
                {
                    methodBody.append("Object result = " + fieldName + ".transfer(resultSet);\n");
                    methodBody.append("return ($r)result;\n");
                }
            }
            methodBody.append("}catch(Exception e){throw new JustThrowException(e);}\n");
            methodBody.append("finally{\n");
            methodBody.append("if (pStat != null){try{pStat.close();}catch(Exception e1){throw new JustThrowException(e1);}}}\n");
            methodBody.append("}\n");
        }
        else
        {
            DynamicSqlTool.analyseFormatSql(query.sql(), query.paramNames().split(","), method.getParameterTypes(), isPage, query.countSql().equals("") ? null : query.countSql(), metaContext, sqlContext);
            methodBody.append("String sql = \"").append(sqlContext.getSql()).append("\";\n");
            methodBody.append("try{\n");
            methodBody.append("pStat = connection.prepareStatement(sql);\n");
            if (isList)
            {
                Verify.True(((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0].getClass().equals(Class.class), "方法{}.{}返回类型是泛型，不允许，请指定具体的类型", method.getDeclaringClass(), method.getName());
                Type returnParamType = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
                // 确认方法放回不是List<T>的形式
                Verify.False(returnParamType instanceof WildcardType, "接口的返回类型不能是泛型，请检查{}.{}", method.getDeclaringClass().getName(), method.getName());
                String fieldName = createTransferField(weaveClass, (Class<?>) returnParamType, method, metaContext, sqlContext);
                setPrestatementParam(methodBody, sqlContext.getQueryParams());
                if (isPage)
                {
                    int index = sqlContext.getQueryParams().size() + 1;
                    int pageIndex = method.getParameterTypes().length;
                    methodBody.append("pStat.setInt(").append(index).append(",").append("$" + pageIndex + ".getStart()").append(");\n");
                    index += 1;
                    methodBody.append("pStat.setInt(").append(index).append(",").append("$" + pageIndex + ".getPageSize()").append(");\n");
                }
                else
                {
                    ;
                }
                // 插入log记录代码
                {
                    if (isPage)
                    {
                        int pageIndex = method.getParameterTypes().length;
                        methodBody.append("if(log.isLogOn(").append("sql").append(")){\n");
                        methodBody.append("log.log(").append("sql").append(",new Object[]{");
                        for (InvokeNameAndType each : sqlContext.getQueryParams())
                        {
                            methodBody.append("($w)").append(each.getInvokeName()).appendComma();
                        }
                        methodBody.append("($w)").append("$" + pageIndex + ".getStart()").appendComma().append("($w)$" + pageIndex + ".getPageSize()");
                        methodBody.append("});}\n");
                    }
                    else
                    {
                        logCode(methodBody, sqlContext.getQueryParams(), "sql");
                    }
                }
                methodBody.append("java.sql.ResultSet resultSet = pStat.executeQuery();\n");
                methodBody.append("java.util.List result = " + fieldName + ".transferList(resultSet);\n");
                if (isPage)
                {
                    int pageIndex = method.getParameterTypes().length;
                    String var = "((com.jfireframework.sql.page.Page)$" + pageIndex + ")";
                    methodBody.append(var + ".setData(result);\n");
                    methodBody.append("String countSql = \"").append(sqlContext.getCountSql()).append("\";\n");
                    methodBody.append("pStat = connection.prepareStatement(countSql);\n");
                    setPrestatementParam(methodBody, sqlContext.getQueryParams());
                    // 插入log记录代码
                    {
                        logCode(methodBody, sqlContext.getQueryParams(), "countSql");
                    }
                    methodBody.append("resultSet = pStat.executeQuery();\n");
                    methodBody.append("resultSet.next();\n");
                    methodBody.append("int total = resultSet.getInt(1);\n");
                    methodBody.append(var + ".setTotal(total);\n");
                    methodBody.append("return ($r)result;\n");
                }
                else
                {
                    methodBody.append("return ($r)result;\n");
                }
            }
            else
            {
                Class<?> returnType = method.getReturnType();
                String fieldName = createTransferField(weaveClass, returnType, method, metaContext, sqlContext);
                setPrestatementParam(methodBody, sqlContext.getQueryParams());
                logCode(methodBody, sqlContext.getQueryParams(), "sql");
                methodBody.append("java.sql.ResultSet resultSet = pStat.executeQuery();\n");
                if (returnType.isPrimitive())
                {
                    methodBody.append("return ").append(fieldName).append(".primitiveValue(resultSet);\n");
                }
                else
                {
                    methodBody.append("Object result = " + fieldName + ".transfer(resultSet);\n");
                    methodBody.append("return ($r)result;\n");
                }
            }
            methodBody.append("}catch(Exception e){throw new JustThrowException(e);}\n");
            methodBody.append("finally{\n");
            methodBody.append("if (pStat != null){try{pStat.close();}catch(Exception e1){throw new JustThrowException(e1);}}}\n");
            methodBody.append("}\n");
        }
        logger.debug("为{}.{}创建的方法体是\n{}\n", method.getDeclaringClass().getName(), method.getName(), methodBody.toString());
        CtMethod targetMethod = forCtMethod(method, weaveClass);
        targetMethod.setBody(methodBody.toString());
        return targetMethod;
    }
    
    private void logCode(StringCache methodBody, String sqlParamName, String paramsName)
    {
        methodBody.append("if(log.isLogOn(").append(sqlParamName).append(")){\n");
        methodBody.append("log.log(").append(sqlParamName).append(",").append(paramsName).append(");}\n");
    }
    
    private void logCode(StringCache methodBody, List<InvokeNameAndType> invokeNameAndTypes, String sqlParamName)
    {
        methodBody.append("if(log.isLogOn(").append(sqlParamName).append(")){\n");
        if (invokeNameAndTypes == null || invokeNameAndTypes.isEmpty())
        {
            methodBody.append("log.log(").append(sqlParamName).append(",null);}\n");
        }
        else
        {
            methodBody.append("log.log(").append(sqlParamName).append(",new Object[]{");
            for (InvokeNameAndType each : invokeNameAndTypes)
            {
                methodBody.append("($w)").append(each.getInvokeName()).appendComma();
            }
            if (methodBody.isCommaLast())
            {
                methodBody.deleteLast();
            }
            methodBody.append("});}\n");
        }
    }
    
    private String createTransferField(CtClass ctClass, Class<?> returnType, Method method, MetaContext metaContext, SqlContext sqlContext) throws CannotCompileException, NotFoundException
    {
        String fieldName = method.getName() + "$" + System.nanoTime();
        String fieldInitStatement = null;
        CtField ctField;
        if (isBaseType(returnType))
        {
            Class<?> transferType = getTransferType(returnType);
            ctField = new CtField(classPool.get(transferType.getName()), fieldName, ctClass);
            fieldInitStatement = StringUtil.format("new {}()", transferType.getName());
        }
        else
        {
            if (method.getAnnotation(Query.class).selectFields().equals("") == false)
            {
                ctField = new CtField(classPool.get(FixationBeanTransfer.class.getName()), fieldName, ctClass);
                fieldInitStatement = StringUtil.format("new com.jfireframework.sql.resultsettransfer.FixationBeanTransfer({}.class,\"{}\")", returnType.getName(), method.getAnnotation(Query.class).selectFields());
            }
            else
            {
                TableMetaData tableMetaData = metaContext.get(returnType.getSimpleName());
                sqlContext.addMetaData(tableMetaData);
                if (sqlContext.getSql() != null)
                {
                    String sql = sqlContext.getSql();
                    int start = sql.indexOf("select") + 6;
                    int end = sql.indexOf("from");
                    String var = sql.substring(start, end).trim();
                    if (var.equals("*"))
                    {
                        ctField = new CtField(classPool.get(FixationBeanTransfer.class.getName()), fieldName, ctClass);
                        fieldInitStatement = StringUtil.format("new com.jfireframework.sql.resultsettransfer.FixationBeanTransfer({}.class,\"*\")", returnType.getName());
                    }
                    else
                    {
                        if (var.contains("{") || var.contains("}"))
                        {
                            ctField = new CtField(classPool.get(VariableBeanTransfer.class.getName()), fieldName, ctClass);
                            fieldInitStatement = StringUtil.format("new com.jfireframework.sql.resultsettransfer.VariableBeanTransfer({}.class)", returnType.getName());
                        }
                        else
                        {
                            String[] tmp = var.split(",");
                            StringCache cache = new StringCache(128);
                            for (int i = 0; i < tmp.length; i++)
                            {
                                String value = tmp[i].trim();
                                if (value.contains("as"))
                                {
                                    int index = value.indexOf("as") + 2;
                                    value = value.substring(index).trim();
                                }
                                else
                                {
                                    ;
                                }
                                cache.append(tableMetaData.getFieldName(value)).appendComma();
                            }
                            cache.deleteLast();
                            ctField = new CtField(classPool.get(FixationBeanTransfer.class.getName()), fieldName, ctClass);
                            fieldInitStatement = StringUtil.format("new com.jfireframework.sql.resultsettransfer.FixationBeanTransfer({}.class,\"{}\")", returnType.getName(), cache.toString());
                        }
                    }
                }
                else
                {
                    ctField = new CtField(classPool.get(VariableBeanTransfer.class.getName()), fieldName, ctClass);
                    fieldInitStatement = StringUtil.format("new com.jfireframework.sql.resultsettransfer.VariableBeanTransfer({}.class)", returnType.getName());
                }
            }
        }
        ctClass.addField(ctField, fieldInitStatement);
        logger.debug("为方法:{}.{}创建的属性内容是{}", method.getDeclaringClass().getName(), method.getName(), fieldInitStatement);
        return fieldName;
    }
    
    private boolean isBaseType(Class<?> type)
    {
        if (type.isPrimitive())
        {
            return true;
        }
        if (
            type == Integer.class //
                    || type == Short.class //
                    || type == Long.class //
                    || type == Float.class //
                    || type == Double.class //
                    || type == Boolean.class //
                    || type == Date.class//
                    || type == java.util.Date.class //
                    || type == String.class //
                    || type == Time.class//
                    || type == Timestamp.class
        )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
    
    private Class<?> getTransferType(Class<?> type)
    {
        if (type == Integer.class || type == int.class)
        {
            return IntegerTransfer.class;
        }
        else if (type == Short.class || type == short.class)
        {
            return ShortTransfer.class;
        }
        else if (type == Long.class || type == long.class)
        {
            return LongTransfer.class;
        }
        else if (type == Float.class || type == float.class)
        {
            return FloatTransfer.class;
        }
        else if (type == Double.class || type == double.class)
        {
            return DoubleTransfer.class;
        }
        else if (type == Boolean.class || type == boolean.class)
        {
            return BooleanTransfer.class;
        }
        else if (type == String.class)
        {
            return StringTransfer.class;
        }
        else if (type == Date.class)
        {
            return SqlDateTransfer.class;
        }
        else if (type == java.util.Date.class)
        {
            return UtilDateTransfer.class;
        }
        else if (type == Time.class)
        {
            return TimeTransfer.class;
        }
        else if (type == Timestamp.class)
        {
            return TimeStampTransfer.class;
        }
        else
        {
            // 程序不会走到这里
            return null;
        }
    }
    
    private void setPrestatementParam(StringCache methodBody, List<InvokeNameAndType> invokeNameAndTypes)
    {
        int index = 1;
        for (InvokeNameAndType invokeNameAndType : invokeNameAndTypes)
        {
            Class<?> returnType = invokeNameAndType.getReturnType();
            if (returnType == int.class)
            {
                methodBody.append("pStat.setInt(");
            }
            else if (returnType == boolean.class)
            {
                methodBody.append("pStat.setBoolean(");
            }
            else if (returnType == long.class)
            {
                methodBody.append("pStat.setLong(");
            }
            else if (returnType == short.class)
            {
                methodBody.append("pStat.setShort(");
            }
            else if (returnType == float.class)
            {
                methodBody.append("pStat.setFloat(");
            }
            else if (returnType == double.class)
            {
                methodBody.append("pStat.setDouble(");
            }
            else if (returnType == String.class)
            {
                methodBody.append("pStat.setString(");
            }
            else
            {
                methodBody.append("pStat.setObject(");
            }
            methodBody.append(index).append(",").append(invokeNameAndType.getInvokeName()).append(");\n");
            index += 1;
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
    
    private CtMethod createUpdateMethod(CtClass mapperClass, Method method, Update update) throws Exception
    {
        SqlContext sqlContext = new SqlContext();
        StringCache cache = new StringCache(1024);
        cache.append("{\n");
        cache.append("SqlSession session = sessionFactory.getCurrentSession();\n");
        cache.append("if(session==null){throw new NullPointerException(\"current session 为空，请检查\");}\n");
        cache.append("java.sql.Connection connection = session.getConnection();\n");
        cache.append("java.sql.PreparedStatement pStat = null;\n");
        boolean isDynamicSql = DynamicSqlTool.isDynamic(update.sql());
        if (isDynamicSql)
        {
            cache.append(DynamicSqlTool.analyseDynamicSql(update.sql(), update.paramNames().split(","), method.getParameterTypes(), false, null, metaContext, sqlContext));
            cache.append("try{\n");
            cache.append("pStat = connection.prepareStatement(sql);\n");
            cache.append("for(int i=0;i<queryParam.length;i++){\n");
            cache.append("pStat.setObject(i+1,queryParam[i]);\n");
            cache.append("}\n");
            logCode(cache, "sql", "queryParam");
        }
        else
        {
            DynamicSqlTool.analyseFormatSql(update.sql(), update.paramNames().split(","), method.getParameterTypes(), false, null, metaContext, sqlContext);
            cache.append("String sql = \"").append(sqlContext.getSql()).append("\";\n");
            cache.append("try{\n");
            cache.append("pStat = connection.prepareStatement(sql);\n");
            setPrestatementParam(cache, sqlContext.queryParams);
        }
        logCode(cache, sqlContext.getQueryParams(), "sql");
        cache.append("int updateRows = pStat.executeUpdate();\n");
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
        cache.append("}catch(Exception e){throw new JustThrowException(e);}\n");
        cache.append("finally{\n");
        cache.append("if (pStat != null){try{pStat.close();}catch(Exception e1){throw new JustThrowException(e1);}}}\n");
        cache.append("}\n");
        CtMethod targetCtMethod = forCtMethod(method, mapperClass);
        logger.debug("为{}.{}创建的方法体是\n{}\n", method.getDeclaringClass().getName(), method.getName(), cache.toString());
        targetCtMethod.setBody(cache.toString());
        return targetCtMethod;
    }
    
    public static class SqlContext
    {
        private List<String>            injectNames    = new LinkedList<String>();
        private Set<TableMetaData>      metaContexts   = new HashSet<TableMetaData>();
        private Map<String, String>     dbColNameMap   = new HashMap<String, String>();
        private Map<String, String>     fieldNameMap   = new HashMap<String, String>();
        private Map<String, Field>      statifFieldMap = new HashMap<String, Field>();
        private String                  sql;
        private String                  countSql;
        private List<InvokeNameAndType> queryParams;
        
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
                if (each.isDaoIgnore())
                {
                    continue;
                }
                dbColNameMap.put(each.getFieldName(), tablePrefix + each.getDbColName());
                dbColNameMap.put(prefix + each.getFieldName(), tablePrefix + each.getDbColName());
                fieldNameMap.put(tablePrefix + each.getDbColName(), each.getFieldName());
            }
            for (Entry<String, Field> each : metaData.staticFieldMap().entrySet())
            {
                statifFieldMap.put(prefix + each.getKey(), each.getValue());
                statifFieldMap.put(each.getKey(), each.getValue());
            }
        }
        
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
            for (Entry<String, Field> each : metaData.staticFieldMap().entrySet())
            {
                statifFieldMap.put(prefix + each.getKey(), each.getValue());
                statifFieldMap.put(each.getKey(), each.getValue());
            }
        }
        
        public String getDbColName(String fieldName)
        {
            return dbColNameMap.get(fieldName);
        }
        
        public String getFieldName(String dbColName)
        {
            return fieldNameMap.get(dbColName);
        }
        
        public Field getStaticField(String name)
        {
            return statifFieldMap.get(name);
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
