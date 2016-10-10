package com.jfireframework.sql.util;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.metadata.MetaContext;
import com.jfireframework.sql.metadata.TableMetaData;
import com.jfireframework.sql.page.MysqlPage;
import com.jfireframework.sql.util.MapperBuilder.InvokeNameAndType;
import com.jfireframework.sql.util.MapperBuilder.SqlContext;
import com.jfireframework.sql.util.enumhandler.AbstractEnumHandler;
import com.jfireframework.sql.util.enumhandler.EnumHandler;

public class DynamicSqlTool
{
    /**
     * 分析动态sql语句，并且生成动态sql情况下的前置的热编码代码部分
     * 
     * @param sql
     * @param paramNames
     * @param paramTypes
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static String analyseDynamicSql(String sql, String[] paramNames, Class<?>[] paramTypes, boolean isPage, String countSql, MetaContext metaContext, SqlContext sqlContext) throws NoSuchFieldException, SecurityException
    {
        sql = transMapSql(sql, sqlContext, metaContext);
        String bk = "\t";
        String context = bk + "com.jfireframework.baseutil.collection.StringCache builder = new StringCache();\n" + bk + "List list = new ArrayList();\n";
        int pre = 0;
        int now = 0;
        String section = null;
        while (now < sql.length())
        {
            switch (sql.charAt(now))
            {
                case '\'':
                    // 如果不是转义字符
                    now = sql.indexOf('\'', now);
                    now++;
                    break;
                case '{':
                    section = sql.substring(pre, now);
                    context += bk + "builder.append(\"" + section + "\");\n";
                    pre = now + 1;
                    now = sql.indexOf('}', pre);
                    section = sql.substring(pre, now);
                    now++;
                    pre = now;
                    context += bk + "builder.append(" + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ");\n";
                    break;
                case '[':
                    section = sql.substring(pre, now);
                    context += bk + "builder.append(\"" + section + "\");\n";
                    pre = now + 1;
                    now = sql.indexOf(']', pre);
                    section = sql.substring(pre, now).trim();
                    context += bk + "if(" + createVarIf(section, paramNames, paramTypes, sqlContext) + ")\n";
                    context += bk + "{\n";
                    bk += "\t";
                    now++;
                    pre = now;
                    break;
                case '$':
                    if (sql.charAt(now + 1) == '~')
                    {
                        section = sql.substring(pre, now);
                        context += bk + "builder.append(\"" + section + "\").append(\" (  \");\n";
                    }
                    else
                    {
                        section = sql.substring(pre, now);
                        context += bk + "builder.append(\"" + section + "\").append('?');\n";
                    }
                    pre = now + 1;
                    now++;
                    now = getEndFlag(sql, now);
                    if (sql.charAt(pre) == '~')
                    {
                        section = sql.substring(pre, now);
                        section = section.substring(1);
                        context = _handleWithTidle(context, section, paramNames, paramTypes, sql, sqlContext);
                    }
                    else
                    {
                        section = sql.substring(pre, now);
                        if (section.startsWith("%") || section.endsWith("%"))
                        {
                            context += bk + "list.add(" + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ");\n";
                        }
                        else
                        {
                            context += bk + "list.add(($w)(" + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + "));\n";
                        }
                    }
                    pre = now;
                    break;
                case '#':
                    section = sql.substring(pre, now);
                    if (section.equals("") == false)
                    {
                        context += bk + "builder.append(\"" + section + "\");\n";
                    }
                    bk = bk.substring(0, bk.length() - 1);
                    context += bk + "}\n";
                    pre = now + 1;
                    now++;
                    break;
                default:
                    now++;
                    break;
            }
        }
        section = sql.substring(pre, now);
        if (section.equals("") == false)
        {
            context += bk + "builder.append(\"" + section + "\");\n";
        }
        if (isPage)
        {
            if (paramTypes[paramTypes.length - 1] == MysqlPage.class)
            {
                if (countSql != null)
                {
                    context += bk + "String countSql = \"" + countSql + "\";\n";
                }
                else
                {
                    context += bk + "String countSql = \"select count(*) \"+builder.substring(builder.indexOf(\"from\"));\n";
                }
                context += bk + "Object[] countParam = list.toArray();\n";
                context += bk + "builder.append(\" limit ?,?\");\n";
                context += bk + "String sql = builder.toString();\n";
                context += bk + "list.add(($w)((com.jfireframework.sql.page.Page)$" + paramTypes.length + ").getStart());\n";
                context += bk + "list.add(($w)((com.jfireframework.sql.page.Page)$" + paramTypes.length + ").getPageSize());\n";
                context += bk + "Object[] queryParam = list.toArray();\n";
                return context;
            }
            else
            {
                throw new RuntimeException("暂不支持该数据的分页");
            }
        }
        else
        {
            context += bk + "String sql = builder.toString();\n";
            context += bk + "Object[] queryParam = list.toArray();\n";
            return context;
        }
    }
    
    private static String _handleWithTidle(String context, String section, String[] paramNames, Class<?>[] paramTypes, String sql, SqlContext sqlContext) throws SecurityException, NoSuchFieldException
    {
        String bk = "\t";
        Class<?> paramType = getParamType(section, paramNames, paramTypes, sql);
        if (paramType.equals(String.class))
        {
            context += bk + "{\n" + bk + "\tString[] tmp = ((String)" + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ").split(\",\");\n";
        }
        else if (paramType.equals(String[].class))
        {
            context += bk + bk + "{\n" + bk + "\n" + bk + "\tString[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(int[].class))
        {
            context += bk + "{\n" + bk + "\tint[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(Integer[].class))
        {
            context += bk + "{\n" + bk + "\tInteger[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(long[].class))
        {
            context += bk + "{\n" + bk + "\tlong[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(Long[].class))
        {
            context += bk + "{\n" + bk + "\tLong[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(float[].class))
        {
            context += bk + "{\n" + bk + "\tfloat[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(Float[].class))
        {
            context += bk + "{\n" + bk + "\tFloat[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(double[].class))
        {
            context += bk + "{\n" + bk + "\tdouble[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (paramType.equals(Double[].class))
        {
            context += bk + "{\n" + bk + "\tDouble[] tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else if (List.class.isAssignableFrom(paramType))
        {
            context += bk + "{\n" + bk + "\tjava.util.List tmp = " + buildParam(section, paramNames, paramTypes, sqlContext).getInvokeName() + ";\n";
        }
        else
        {
            throw new RuntimeException("in操作中存在不识别的类型");
        }
        bk += "\t";
        if (List.class.isAssignableFrom(paramType))
        {
            context += bk + "int length = tmp.size();\n";
        }
        else
        {
            context += bk + "int length = tmp.length;\n";
        }
        context += bk + "for(int i=0;i<length;i++){builder.append(\"?,\");}\n";
        context += bk + "builder.deleteLast().append(\")\");\n";
        if (List.class.isAssignableFrom(paramType))
        {
            context += bk + "for(int i=0;i<length;i++){list.add(tmp.get(i));}\n";
        }
        else
        {
            context += bk + "for(int i=0;i<length;i++){list.add(($w)tmp[i]);}\n";
        }
        bk = bk.substring(0, bk.length() - 1);
        context += bk + "}\n";
        return context;
    }
    
    /**
     * 给定参数字符串inject，在所有的方法入参名称中搜索可能的对应值。
     * 比如字符串为user.name。有一个参数为类user,并且参数位置在第一个。则返回的内容是$1.getName()
     * 如果只是单一参数，比如name。有一个参数为name。并且在第一个。返回的内容是$1
     * 如果字符串前后有%，最后的结果是"%"+$1.getName()+"%"这样的形式。前后会自动补上%
     * 
     * @param inject
     * @param paramNames
     * @param paramTypes
     * @param originalSql
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    @SuppressWarnings("unchecked")
    private static InvokeNameAndType buildParam(String inject, String[] paramNames, Class<?>[] paramTypes, SqlContext sqlContext) throws NoSuchFieldException, SecurityException
    {
        boolean before = false;
        boolean after = false;
        if (inject.startsWith("%"))
        {
            inject = inject.substring(1);
            before = true;
        }
        if (inject.endsWith("%"))
        {
            inject = inject.substring(0, inject.length() - 1);
            after = true;
        }
        if (inject.indexOf('.') == -1)
        {
            int index = getParamNameIndex(inject, paramNames);
            String result = "";
            if (before)
            {
                result += "\"%\"+";
            }
            if (Enum.class.isAssignableFrom(paramTypes[index]))
            {
                Class<? extends EnumHandler<?>> handlerType = AbstractEnumHandler.getEnumBoundHandler((Class<? extends Enum<?>>) paramTypes[index]);
                String fieldName = "enumHandler$" + System.nanoTime();
                sqlContext.addEnumHandler(fieldName, (Class<? extends Enum<?>>) paramTypes[index], handlerType);
                result += fieldName + ".getValue($" + (index + 1) + ")";
            }
            else
            {
                result += "$" + (index + 1);
            }
            if (after)
            {
                result += "+\"%\"";
            }
            InvokeNameAndType invokeNameAndType = new InvokeNameAndType(result, paramTypes[index], inject);
            return invokeNameAndType;
        }
        else
        {
            String[] tmp = inject.split("\\.");
            int index = getParamNameIndex(tmp[0], paramNames);
            Object[] returns = ReflectUtil.getBuildMethodAndType(inject, paramTypes[index]);
            Class<?> returnType = (Class<?>) returns[1];
            String result = "";
            if (before)
            {
                result += "\"%\"+";
            }
            if (Enum.class.isAssignableFrom(returnType))
            {
                Class<? extends EnumHandler<?>> handlerType = AbstractEnumHandler.getEnumBoundHandler((Class<? extends Enum<?>>) returnType);
                String fieldName = "enumHandler$" + System.nanoTime();
                sqlContext.addEnumHandler(fieldName, (Class<? extends Enum<?>>) returnType, handlerType);
                result += fieldName + ".getValue($" + (index + 1) + returns[0] + ")";
            }
            else
            {
                result += "$" + (index + 1) + returns[0];
            }
            if (after)
            {
                result += "+\"%\"";
            }
            InvokeNameAndType invokeNameAndType = new InvokeNameAndType(result, returnType, inject);
            return invokeNameAndType;
        }
    }
    
    /**
     * 给定字符串inject，搜索可能的参数字符串。比如字符串为user.name，有一个参数为类user。
     * 则参数字符串应该是user.getName() 返回的结果是这个方法或者这个参数的类型。如果方法的返回类型是数组，则返回的结果是这个数组的元素类型
     * 
     * @param inject
     * @param paramNames
     * @param paramTypes
     * @param originalSql
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    private static Class<?> getParamType(String inject, String[] paramNames, Class<?>[] paramTypes, String originalSql) throws NoSuchFieldException, SecurityException
    {
        if (inject.indexOf('.') == -1)
        {
            Integer index = getParamNameIndex(inject, paramNames);
            Verify.notNull(index, "sql注入语句{}无法找到注入属性{}", originalSql, inject);
            return paramTypes[index];
        }
        else
        {
            String[] tmp = inject.split("\\.");
            Integer index = getParamNameIndex(tmp[0], paramNames);
            Verify.notNull(index, "sql注入语句{}无法找到注入属性{}", originalSql, inject);
            return ReflectUtil.getFinalReturnType(inject, paramTypes[index]);
        }
    }
    
    /**
     * 通过字符比对，确定需要注入的属性是第几个参数的内部属性或者内容
     * 
     * @param inject
     * @param paramNames
     * @return
     */
    public static int getParamNameIndex(String inject, String[] paramNames)
    {
        for (int i = 0; i < paramNames.length; i++)
        {
            if (paramNames[i].equals(inject))
            {
                return i;
            }
        }
        throw new RuntimeException("给定的参数" + inject + "不在参数列表中");
    }
    
    /**
     * 将sql语句中的类映射和字段映射替换为各自映射的数据库表名和字段名
     * 
     * @param sql
     * @return
     */
    public static String transMapSql(String sql, SqlContext sqlContext, MetaContext metaContext)
    {
        try
        {
            String simpleClassName = null;
            int end = 0;
            int index = 0;
            boolean as = false;
            boolean preIsTableName = false;
            while (index < sql.length())
            {
                char c = sql.charAt(index);
                if (c == '\'')
                {
                    preIsTableName = false;
                    end = sql.indexOf('\'', index);
                    end++;
                    index = end;
                    continue;
                }
                else if (c == ' ' || c == ',' || c == '(' || c == '+' || c == '=' || c == '-' || c == '!' || c == '>' || c == '<')
                {
                    index++;
                    end = getEndFlag(sql, index);
                    String tmp = sql.substring(index, end).trim();
                    if (tmp.equals(""))
                    {
                        preIsTableName = false;
                        continue;
                    }
                    while (tmp.charAt(0) == '(')
                    {
                        tmp = tmp.substring(1).trim();
                    }
                    if (tmp.equals(""))
                    {
                        preIsTableName = false;
                        continue;
                    }
                    if (tmp.indexOf(".") != -1 || tmp.indexOf("$") != -1)
                    {
                        preIsTableName = false;
                    }
                    else if (tmp.charAt(0) >= 'A' && tmp.charAt(0) <= 'Z')
                    {
                        preIsTableName = true;
                        simpleClassName = tmp;
                        sqlContext.addMetaData(metaContext.get(simpleClassName));
                    }
                    else if (tmp.equals("as") && end < sql.length() && sql.charAt(end) == ' ')
                    {
                        as = true;
                    }
                    else if (as == true)
                    {
                        if (preIsTableName)
                        {
                            TableMetaData tableMetaData = metaContext.get(simpleClassName);
                            if (tableMetaData != null)
                            {
                                sqlContext.addAliasName(tmp, metaContext.get(simpleClassName));
                            }
                            else
                            {
                                throw new NullPointerException(StringUtil.format("无法识别类{}", simpleClassName));
                            }
                            preIsTableName = false;
                            simpleClassName = null;
                        }
                        else
                        {
                            ;
                        }
                        as = false;
                    }
                    else
                    {
                        preIsTableName = false;
                    }
                    index = end;
                    continue;
                }
                else if (c == 'a')
                {
                    // 确保as是一个独立的单词
                    if (index + 2 < sql.length() && sql.charAt(index - 1) == ' ' && sql.charAt(index + 1) == 's' && sql.charAt(index + 2) == ' ')
                    {
                        as = true;
                    }
                    else
                    {
                        preIsTableName = false;
                    }
                    index++;
                    continue;
                }
                else
                {
                    preIsTableName = false;
                    index++;
                    continue;
                }
            }
            if (sqlContext.hasMetaContext() == false)
            {
                return sql;
            }
            StringCache cache = new StringCache();
            int length = sql.length();
            index = 0;
            as = false;
            while (index < length)
            {
                char c = sql.charAt(index);
                if (c == '\'')
                {
                    end = sql.indexOf('\'', index);
                    end++;
                    cache.append(sql.substring(index, end));
                    index = end;
                    continue;
                }
                else if (c == ' ' || c == ',' || c == '(' || c == '+' || c == '=' || c == '-' || c == '!' || c == '>' || c == '<')
                {
                    cache.append(c);
                    index++;
                    end = getEndFlag(sql, index);
                    String var = sql.substring(index, end).trim();
                    if (var.equals(""))
                    {
                        index = end;
                        continue;
                    }
                    if (var.equals("as"))
                    {
                        as = true;
                        cache.append("as");
                        index = end;
                        continue;
                    }
                    while (var.charAt(0) == '(')
                    {
                        var = var.substring(1).trim();
                    }
                    if (var.equals(""))
                    {
                        cache.append(sql.substring(index, end).trim());
                        index = end;
                        continue;
                    }
                    if (var.indexOf(".") != -1 && var.indexOf("$") == -1 && as == false)
                    {
                        String[] tmp = var.split("\\.");
                        Verify.True(tmp.length == 2, "sql有错误，请检查{},关注：{}", sql, var);
                        if (sqlContext.getDbColName(var) != null)
                        {
                            cache.append(sqlContext.getDbColName(var));
                        }
                        else if (sqlContext.getStaticValue(var) != null)
                        {
                            cache.append(sqlContext.getStaticValue(var));
                        }
                        else
                        {
                            throw new IllegalArgumentException(StringUtil.format("字段{}不存在数据库映射，请检查{}", var, sql));
                        }
                    }
                    else if (var.charAt(0) >= 'A' && var.charAt(0) <= 'Z')
                    {
                        cache.append(metaContext.get(var).getTableName());
                    }
                    else
                    {
                        if (as)
                        {
                            as = false;
                            cache.append(var);
                        }
                        else
                        {
                            if (sqlContext.getDbColName(var) != null)
                            {
                                cache.append(sqlContext.getDbColName(var));
                            }
                            else if (sqlContext.getStaticValue(var) != null)
                            {
                                cache.append(sqlContext.getStaticValue(var));
                            }
                            else
                            {
                                cache.append(var);
                            }
                        }
                    }
                    index = end;
                }
                else
                {
                    cache.append(c);
                    index++;
                }
            }
            return cache.toString();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * 从start处开始，在sql中遇到一些特定字符则返回当前的位置
     * 
     * @param sql
     * @param start
     * @return
     */
    private static int getEndFlag(String sql, int start)
    {
        while (start < sql.length())
        {
            char c = sql.charAt(start);
            if (c == '>' || c == '<' || c == '!' || c == '=' || c == ' ' || c == ',' //
                    || c == '#' || c == '+' || c == '-' || c == '(' || c == ')' || c == ']' || c == '[')
            {
                break;
            }
            start++;
        }
        return start;
    }
    
    /**
     * 分析格式化的sql语句，根据格式化语句和方法形参名称数组得出标准sql语句，和对应的object[]形的参数数组
     * 
     * @param originalSql
     * @param paramNames
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    public static void analyseFormatSql(String originalSql, String[] paramNames, Class<?>[] paramTypes, boolean isPage, String annoCountSql, MetaContext metaContext, SqlContext sqlContext) throws NoSuchFieldException, SecurityException
    {
        String querySql, countSql = null;
        getFormatSql(originalSql, metaContext, sqlContext);
        querySql = sqlContext.getSql();
        if (isPage)
        {
            if (MysqlPage.class == paramTypes[paramTypes.length - 1])
            {
                if (annoCountSql == null)
                {
                    int index = querySql.indexOf("from");
                    countSql = "select count(*) " + querySql.substring(index);
                }
                else
                {
                    countSql = annoCountSql;
                }
                sqlContext.setCountSql(countSql);
                List<InvokeNameAndType> invokeNameAndTypes = buildParams(sqlContext.getInjectNames(), paramNames, paramTypes, sqlContext);
                sqlContext.setQueryParams(invokeNameAndTypes);
                querySql += " limit ?,?";
                sqlContext.setSql(querySql);
            }
            else
            {
                throw new RuntimeException("暂不支持该数据库的分页");
            }
        }
        else
        {
            List<InvokeNameAndType> invokeNameAndTypes = buildParams(sqlContext.getInjectNames(), paramNames, paramTypes, sqlContext);
            sqlContext.setQueryParams(invokeNameAndTypes);
        }
    }
    
    /**
     * 将给定的sql语句转换为格式化的sql语句。将其中的{变量名}替换为?。并且将{}中的内容增加到paramNames中 返回格式化后的sql语句
     * 
     * @param sql
     * @param paramNames
     * @return
     */
    public static void getFormatSql(String sql, MetaContext metaContext, SqlContext sqlContext)
    {
        sql = transMapSql(sql, sqlContext, metaContext);
        StringCache formatSql = new StringCache();
        int length = sql.length();
        char c;
        int now = 0;
        int variateStart = 0;
        while (now < length)
        {
            c = sql.charAt(now);
            switch (c)
            {
                case '$':
                    variateStart = now + 1;
                    now = getEndFlag(sql, now);
                    formatSql.append('?');
                    sqlContext.addInjectName(sql.substring(variateStart, now));
                    break;
                default:
                    formatSql.append(c);
                    now++;
                    break;
            }
        }
        sqlContext.setSql(formatSql.toString());
    }
    
    /**
     * 根据格式化sql中的注入字段，和方法形参名称数组，返回解析后的List<InvokeNameAndType>内容
     * 
     * @param originalSql
     * @param length
     * @param injects
     * @param paramNames
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    private static List<InvokeNameAndType> buildParams(List<String> injects, String[] paramNames, Class<?>[] paramTypes, SqlContext sqlContext) throws NoSuchFieldException, SecurityException
    {
        List<InvokeNameAndType> list = new LinkedList<MapperBuilder.InvokeNameAndType>();
        int length = injects.size();
        if (length == 0)
        {
            return list;
        }
        for (String inject : injects)
        {
            list.add(buildParam(inject, paramNames, paramTypes, sqlContext));
        }
        return list;
    }
    
    /**
     * 检查是否是动态sql
     * 
     * @param sql
     * @return
     */
    public static boolean isDynamic(String sql)
    {
        Stack<Character> stack = new Stack<Character>();
        int now = 0;
        int quote = 0;
        boolean dynamic = false;
        while (now < sql.length())
        {
            char c = sql.charAt(now);
            switch (c)
            {
                case '\'':
                    quote = quote == 0 ? 1 : 0;
                    break;
                case '[':
                    if (quote != 1)
                    {
                        stack.push(c);
                    }
                    break;
                case ']':
                    if (quote != -1)
                    {
                        Verify.True(stack.peek() == '[', "sql语句存在问题，缺少[。请检查{},并且关注{}", sql, sql.subSequence(0, now));
                        stack.push(c);
                    }
                    break;
                case '{':
                    if (quote != 1)
                    {
                        stack.push(c);
                    }
                    break;
                case '}':
                    if (quote != -1)
                    {
                        Verify.True(stack.pop() == '{', "sql语句存在问题，缺少{。请检查{},并且关注{}", sql, sql.subSequence(0, now));
                        dynamic = true;
                    }
                    break;
                case '#':
                    if (quote != 1)
                    {
                        Verify.True(stack.pop() == ']', "sql语句存在问题，缺少]。请检查{},并且关注{}", sql, sql.subSequence(0, now));
                        Verify.True(stack.pop() == '[', "sql语句存在问题，缺少[。请检查{},并且关注{}", sql, sql.subSequence(0, now));
                        dynamic = true;
                    }
                    break;
                case '~':
                    if (quote != 1)
                    {
                        Verify.True(now < sql.length() - 1, "sql语句存在错误，符号~不应该在最后一个，请检查{}", sql);
                        Verify.True(sql.charAt(now - 1) == '$', "sql语句存在错误，符号~前面是$。请检查{}，并关注{}", sql, sql.substring(0, now));
                        dynamic = true;
                    }
                    break;
                default:
                    break;
            }
            now++;
        }
        Verify.True(stack.size() == 0, "sql语句存在问题，少写了#来结束动态sql条件");
        return dynamic;
    }
    
    /**
     * 将[]包围起来的条件语句进行解析，生成放在if条件语句中的编译代码。比如[user.age >15]会被生成(user !=null) &&
     * (user.getAge() !=null) && (user.getAge().intValue()>15)
     * 
     * @param conditionStatment 条件语句如[user.age >15]
     * @param paramNames 接口方法所有的入参名称
     * @param types 接口方法所有的入参类型
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    private static String createVarIf(String conditionStatment, String[] paramNames, Class<?>[] types, SqlContext sqlContext) throws NoSuchFieldException, SecurityException
    {
        conditionStatment = conditionStatment.trim();
        StringCache cache = new StringCache();
        char c;
        int flag = 0;
        InvokeNameAndType invokeNameAndType = null;
        String condition = null;
        String param = null;
        // 表达式的写法只支持变量在前，参数在后。也就是说只能写$user.age >24 而不能写24<$user.age
        while (flag < conditionStatment.length())
        {
            c = conditionStatment.charAt(flag);
            if (c == '$')
            {
                int varStart = flag + 1;
                flag = getEndFlag(conditionStatment, flag);
                String var = null;
                if (flag < conditionStatment.length() - 1 && conditionStatment.charAt(flag) == '(' && conditionStatment.charAt(flag + 1) == ')')
                {
                    flag += 2;
                    var = conditionStatment.substring(varStart, flag);
                }
                else if (flag < conditionStatment.length() - 1 && conditionStatment.charAt(flag) == '(')
                {
                    throw new IllegalArgumentException(StringUtil.format("动态sql功能只支持无参方法，请检查{}", conditionStatment));
                }
                else
                {
                    var = conditionStatment.substring(varStart, flag);
                }
                invokeNameAndType = buildParam(var, paramNames, types, sqlContext);
                continue;
            }
            else if (c == '>' || c == '<' || c == '!' || c == '=')
            {
                if (conditionStatment.charAt(flag + 1) == '=')
                {
                    condition = String.valueOf(c + "=");
                    flag += 2;
                    continue;
                }
                else
                {
                    condition = String.valueOf(c);
                    flag++;
                    continue;
                }
            }
            else if (c == ' ' || c == '(' || c == ')' || c == '|' || c == '&')
            {
                // 在遇到(,||,&& 时代表条件的结束。此时可以生成一个表达式
                if (invokeNameAndType != null && c != ' ' && c != ')' && flag < conditionStatment.length() - 1)
                {
                    if (c == '(' || conditionStatment.charAt(flag + 1) == '|' || conditionStatment.charAt(flag + 1) == '&')
                    {
                        if (param == null && condition == null)
                        {
                            createStatement("null", cache, invokeNameAndType.getInvokeName(), invokeNameAndType.getReturnType(), "!=");
                        }
                        else
                        {
                            createStatement(param, cache, invokeNameAndType.getInvokeName(), invokeNameAndType.getReturnType(), condition);
                        }
                        cache.append(' ');
                        invokeNameAndType = null;
                        condition = null;
                        param = null;
                    }
                }
                cache.append(c);
                flag++;
                continue;
            }
            else if (c == '\'')
            {
                int end = conditionStatment.indexOf('\'', flag);
                param = conditionStatment.substring(flag + 1, end);
                createStatement(param, cache, invokeNameAndType.getInvokeName(), invokeNameAndType.getReturnType(), condition);
                invokeNameAndType = null;
                condition = null;
                param = null;
                flag = end + 1;
                continue;
            }
            // 如果都不是上面的那些字符，就意味着可能是数字或者是布尔值。（在输入正确的情况下，故意输错不说。）
            else
            {
                Verify.notNull(invokeNameAndType, "sql语句错误，请检查是否参数'{}'前面是否缺少了$", invokeNameAndType.getOrigin());
                int paramStart = flag;
                flag = getEndFlag(conditionStatment, flag);
                param = conditionStatment.substring(paramStart, flag);
                createStatement(param, cache, invokeNameAndType.getInvokeName(), invokeNameAndType.getReturnType(), condition);
                invokeNameAndType = null;
                condition = null;
                param = null;
                continue;
            }
        }
        if (invokeNameAndType != null && condition == null && param == null)
        {
            createStatement("null", cache, invokeNameAndType.getInvokeName(), invokeNameAndType.getReturnType(), "!=");
        }
        return cache.toString();
    }
    
    /**
     * 创建一个条件判断，使用变量名，条件，参数三个属性。并且将生成的条件判断加入到formatsql中。
     * 
     * @param param
     * @param formatSql
     * @param transVar
     * @param varType
     * @param condition
     */
    private static void createStatement(String param, StringCache formatSql, String transVar, Class<?> varType, String condition)
    {
        // 如果是user.name，需要判断user！=null 并且user.getName() != null。必须逐层验证
        int flag = 0;
        formatSql.append(" (");
        while ((flag = transVar.indexOf('.', flag)) != -1)
        {
            formatSql.append("($w)").append(transVar.substring(0, flag)).append(" != null && ");
            flag++;
        }
        if (param != null && param.equals("null"))
        {
            if (condition.equals("==") || condition.equals("!="))
            {
                formatSql.append(transVar).append(" ").append(condition).append(" null )");
                return;
            }
            else
            {
                throw new RuntimeException(StringUtil.format("条件语句存在错误，参数为null时，条件只能是'='或'!='"));
            }
        }
        if (varType == null)
        {
            formatSql.append(transVar);
            if (condition == null)
            {
                formatSql.append("==true )");
            }
            else
            {
                formatSql.append(condition).append(param).append(" )");
            }
            return;
        }
        if (varType.isPrimitive())
        {
            if (varType == char.class)
            {
                formatSql.append(transVar).append(condition).append("'").append(param).append("' )");
                
            }
            else
            {
                formatSql.append(transVar).append(condition).append(param).append(" )");
            }
            return;
        }
        formatSql.append(transVar).append(" != null && ");
        if (varType == String.class)
        {
            if (condition.equals("=="))
            {
                formatSql.append(transVar).append(".equals(\"").append(param).append("\") )");
            }
            else if (condition.equals("!="))
            {
                formatSql.append(transVar).append(".equals(\"").append(param).append("\")==false )");
            }
        }
        else if (varType == Integer.class)
        {
            formatSql.append(transVar).append(".intValue() ").append(condition).append(param).append(" )");
        }
        else if (varType == Long.class)
        {
            formatSql.append(transVar).append(".longValue() ").append(condition).append(param).append(" )");
        }
        else if (varType == Short.class)
        {
            formatSql.append(transVar).append(".shortValue() ").append(condition).append(param).append(" )");
        }
        else if (varType == Double.class)
        {
            formatSql.append(transVar).append(".doubleValue() ").append(condition).append(param).append(" )");
        }
        else if (varType == Float.class)
        {
            formatSql.append(transVar).append(".floatValue() ").append(condition).append(param).append(" )");
        }
        else if (varType == Long.class)
        {
            formatSql.append(transVar).append(".longValue() ").append(condition).append(param).append(" )");
        }
        else
        {
            throw new RuntimeException("不能识别的处理类型" + varType);
        }
    }
}
