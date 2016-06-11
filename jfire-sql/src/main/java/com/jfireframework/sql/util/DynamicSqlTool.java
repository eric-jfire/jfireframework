package com.jfireframework.sql.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.metadata.MetaData;
import com.jfireframework.sql.page.MysqlPage;

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
    public static String analyseDynamicSql(String sql, String[] paramNames, Class<?>[] paramTypes, boolean isPage, String countSql) throws NoSuchFieldException, SecurityException
    {
        sql = transMapSql(sql);
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
                    context += bk + "builder.append(" + buildParam(section, paramNames, paramTypes, sql) + ");\n";
                    break;
                case '[':
                    section = sql.substring(pre, now);
                    context += bk + "builder.append(\"" + section + "\");\n";
                    pre = now + 1;
                    now = sql.indexOf(']', pre);
                    section = sql.substring(pre, now).trim();
                    context += bk + "if(" + createVarIf(section, paramNames, paramTypes) + ")\n";
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
                        Class<?> paramType = getParamType(section, paramNames, paramTypes, sql);
                        if (paramType.equals(String.class))
                        {
                            context += bk + "{\n" + bk + "\tString[] tmp = ((String)" + buildParam(section, paramNames, paramTypes, sql) + ").split(\",\");\n";
                        }
                        else if (paramType.equals(String[].class))
                        {
                            context += bk + bk + "{\n" + bk + "\n" + bk + "\tString[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(int[].class))
                        {
                            context += bk + "{\n" + bk + "\tint[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(Integer[].class))
                        {
                            context += bk + "{\n" + bk + "\tInteger[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(long[].class))
                        {
                            context += bk + "{\n" + bk + "\tlong[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(Long[].class))
                        {
                            context += bk + "{\n" + bk + "\tLong[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(float[].class))
                        {
                            context += bk + "{\n" + bk + "\tfloat[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(Float[].class))
                        {
                            context += bk + "{\n" + bk + "\tFloat[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(double[].class))
                        {
                            context += bk + "{\n" + bk + "\tdouble[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (paramType.equals(Double[].class))
                        {
                            context += bk + "{\n" + bk + "\tDouble[] tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
                        }
                        else if (List.class.isAssignableFrom(paramType))
                        {
                            context += bk + "{\n" + bk + "\tjava.util.List tmp = " + buildParam(section, paramNames, paramTypes, sql) + ";\n";
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
                            context += bk + "for(int i=0;i<length;i++){list.add(($w)tmp.get(i));}\n";
                        }
                        else
                        {
                            context += bk + "for(int i=0;i<length;i++){list.add(($w)tmp[i]);}\n";
                        }
                        bk = bk.substring(0, bk.length() - 1);
                        context += bk + "}\n";
                    }
                    else
                    {
                        section = sql.substring(pre, now);
                        if (section.startsWith("%") || section.endsWith("%"))
                        {
                            context += bk + "list.add(" + buildParam(section, paramNames, paramTypes, sql) + ");\n";
                        }
                        else
                        {
                            context += bk + "list.add(($w)(" + buildParam(section, paramNames, paramTypes, sql) + "));\n";
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
                context += bk + "builder.append(\" limit ?,?\");\n";
                context += bk + "Object[] countParam = list.toArray();\n";
                context += bk + "list.add(($w)((com.jfireframework.sql.page.Page)$" + paramTypes.length + ").getStart());\n";
                context += bk + "list.add(($w)((com.jfireframework.sql.page.Page)$" + paramTypes.length + ").getPageSize());\n";
                return context;
            }
            else
            {
                throw new RuntimeException("暂不支持该数据的分页");
            }
        }
        else
        {
            return context;
        }
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
    private static String buildParam(String inject, String[] paramNames, Class<?>[] paramTypes, String originalSql) throws NoSuchFieldException, SecurityException
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
            result += "$" + (index + 1);
            if (after)
            {
                result += "+\"%\"";
            }
            return result;
        }
        else
        {
            String[] tmp = inject.split("\\.");
            int index = getParamNameIndex(tmp[0], paramNames);
            String getMethodName = ReflectUtil.buildGetMethod(inject, paramTypes[index]);
            String result = "";
            if (before)
            {
                result += "\"%\"+";
            }
            result += "$" + (index + 1) + getMethodName;
            if (after)
            {
                result += "+\"%\"";
            }
            return result;
        }
    }
    
    /**
     * 给定字符串inject，搜索可能的参数字符串。比如字符串为user.name，有一个参数为类user。
     * 则参数字符串应该是user.getName()
     * 返回的结果是这个方法或者这个参数的类型。如果方法的返回类型是数组，则返回的结果是这个数组的元素类型
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
    public static String transMapSql(String sql)
    {
        Map<String, String> asNameMap = new HashMap<String, String>();
        try
        {
            String rootSimpleClassName = null;
            int end = 0;
            int index = 0;
            boolean as = false;
            while (index < sql.length())
            {
                char c = sql.charAt(index);
                if (c == '\'')
                {
                    
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
                        continue;
                    }
                    while (tmp.charAt(0) == '(')
                    {
                        tmp = tmp.substring(1).trim();
                    }
                    if (tmp.equals(""))
                    {
                        continue;
                    }
                    if (tmp.indexOf(".") != -1 || tmp.indexOf("$") != -1)
                    {
                        ;
                    }
                    else if (tmp.charAt(0) >= 'A' && tmp.charAt(0) <= 'Z')
                    {
                        rootSimpleClassName = tmp;
                        Verify.notNull(MapBeanFactory.getMetaData(rootSimpleClassName), "sql:{}存在错误，类{}没有被注解MapTabel或TableEntity标记。", sql, rootSimpleClassName);
                    }
                    else if (tmp.equals("as") && end < sql.length() && sql.charAt(end) == ' ')
                    {
                        as = true;
                    }
                    else if (as == true)
                    {
                        asNameMap.put(tmp, rootSimpleClassName);
                        as = false;
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
                    index++;
                    continue;
                }
                else
                {
                    index++;
                    continue;
                }
            }
            if (rootSimpleClassName == null)
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
                    if (var.indexOf(".") != -1 && var.indexOf("$") == -1)
                    {
                        String[] tmp = var.split("\\.");
                        Verify.True(tmp.length == 2, "sql有错误，请检查{},关注：{}", sql, var);
                        MetaData metaData = MapBeanFactory.getMetaData(tmp[0]);
                        if (metaData == null && asNameMap.containsKey(tmp[0]))
                        {
                            metaData = MapBeanFactory.getMetaData(asNameMap.get(tmp[0]));
                        }
                        Verify.notNull(metaData, "sql存在错误，请检查{},类{}不存在映射，关注{}", sql, tmp[0], var);
                        Verify.notNull(metaData.getColumnName(tmp[1]), "sql存在错误，请检查{},类{}的属性{}不存在映射，关注{}", sql, metaData.getSimpleClassName(), tmp[1], var);
                        if (tmp[0].charAt(0) >= 'A' && tmp[0].charAt(0) <= 'Z')
                        {
                            cache.append(metaData.getTableName()).append('.').append(metaData.getColumnName(tmp[1]));
                        }
                        else
                        {
                            cache.append(tmp[0]).append('.').append(metaData.getColumnName(tmp[1]));
                        }
                    }
                    else if (var.charAt(0) >= 'A' && var.charAt(0) <= 'Z')
                    {
                        String replaceName = MapBeanFactory.getMetaData(var).getTableName();
                        cache.append(replaceName);
                    }
                    else if (var.equals("as"))
                    {
                        as = true;
                        cache.append("as");
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
                            String replaceName = MapBeanFactory.getMetaData(rootSimpleClassName).getColumnName(var);
                            if (replaceName == null)
                            {
                                cache.append(var);
                            }
                            else
                            {
                                cache.append(replaceName);
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
            if (sql.charAt(start) == '>' || sql.charAt(start) == '<' || sql.charAt(start) == '!' || sql.charAt(start) == '=' || sql.charAt(start) == ' ' || sql.charAt(start) == ',' || sql.charAt(start) == '#' || sql.charAt(start) == '+' || sql.charAt(start) == '-' || sql.charAt(start) == '(' || sql.charAt(start) == ')' || sql.charAt(start) == ']' || sql.charAt(start) == '[')
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
    public static String[] analyseFormatSql(String originalSql, String[] paramNames, Class<?>[] paramTypes, boolean isPage, String annoCountSql) throws NoSuchFieldException, SecurityException
    {
        String querySql, queryParam, countSql = null, countParam = null;
        List<String> variateNames = new ArrayList<String>();
        String formatSql = getFormatSql(originalSql, variateNames);
        querySql = formatSql;
        if (isPage)
        {
            if (MysqlPage.class == paramTypes[paramTypes.length - 1])
            {
                if (annoCountSql == null)
                {
                    int index = formatSql.indexOf("from");
                    countSql = "select count(*) " + formatSql.substring(index);
                }
                else
                {
                    countSql = annoCountSql;
                }
                countParam = buildParams(formatSql, variateNames.toArray(new String[0]), paramNames, paramTypes);
                String pageParamName = "page_" + System.currentTimeMillis();
                variateNames.add(pageParamName + ".start");
                variateNames.add(pageParamName + ".pageSize");
                formatSql += " limit ?,?";
                querySql = formatSql;
                String[] newParamNames = new String[paramTypes.length];
                System.arraycopy(paramNames, 0, newParamNames, 0, paramTypes.length);
                newParamNames[newParamNames.length - 1] = pageParamName;
                queryParam = buildParams(formatSql, variateNames.toArray(new String[0]), newParamNames, paramTypes);
            }
            else
            {
                throw new RuntimeException("暂不支持该数据库的分页");
            }
        }
        else
        {
            queryParam = buildParams(formatSql, variateNames.toArray(new String[0]), paramNames, paramTypes);
        }
        return new String[] { querySql, queryParam, countSql, countParam };
    }
    
    /**
     * 将给定的sql语句转换为格式化的sql语句。将其中的{变量名}替换为?。并且将{}中的内容增加到paramNames中
     * 返回格式化后的sql语句
     * 
     * @param sql
     * @param paramNames
     * @return
     */
    public static String getFormatSql(String sql, List<String> variateNames)
    {
        sql = transMapSql(sql);
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
                    variateNames.add(sql.substring(variateStart, now));
                    break;
                default:
                    formatSql.append(c);
                    now++;
                    break;
            }
        }
        return formatSql.toString();
    }
    
    /**
     * 根据格式化sql中的注入字段，和方法形参名称数组，返回Object[]形式的参数数组
     * 
     * @param originalSql
     * @param length
     * @param injects
     * @param paramNames
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     */
    private static String buildParams(String originalSql, String[] injects, String[] paramNames, Class<?>[] paramTypes) throws NoSuchFieldException, SecurityException
    {
        int length = injects.length;
        if (length == 0)
        {
            return "new Object[0]";
        }
        String[] params = new String[length];
        for (int i = 0; i < length; i++)
        {
            String inject = injects[i];
            params[i] = buildParam(inject, paramNames, paramTypes, originalSql);
        }
        StringCache cache = new StringCache();
        cache.append("new Object[]{");
        for (int i = 0; i < length; i++)
        {
            cache.append("($w)(").append(params[i]).append(')').appendComma();
        }
        if (cache.isCommaLast())
        {
            cache.deleteLast();
        }
        cache.append("}");
        return cache.toString();
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
    private static String createVarIf(String conditionStatment, String[] paramNames, Class<?>[] types) throws NoSuchFieldException, SecurityException
    {
        conditionStatment = conditionStatment.trim();
        StringCache cache = new StringCache();
        char c;
        int flag = 0;
        String condition = null;
        // 变量的类型
        Class<?> varType = null;
        // 变量名经过转换后的内容。比如user.name转换后可能是$1.getName()
        String transVar = null;
        String param = null;
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
                    flag = conditionStatment.indexOf(')', flag) + 1;
                    Verify.False(flag == -1, "sql语句存在异常，请检查{}", conditionStatment);
                    var = conditionStatment.substring(varStart, flag);
                    
                }
                else
                {
                    var = conditionStatment.substring(varStart, flag);
                }
                transVar = buildParam(var, paramNames, types, conditionStatment);
                varType = getParamType(var, paramNames, types, conditionStatment);
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
                if (transVar != null && c != ' ' && c != ')' && flag < conditionStatment.length() - 1)
                {
                    if (c == '(' || conditionStatment.charAt(flag + 1) == '|' || conditionStatment.charAt(flag + 1) == '&')
                    {
                        if (varType != null)
                        {
                            createStatement("null", cache, transVar, varType, "!=");
                        }
                        else
                        {
                            createStatement(param, cache, transVar, null, condition);
                        }
                        cache.append(' ');
                        transVar = null;
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
                createStatement(param, cache, transVar, varType, condition);
                transVar = null;
                condition = null;
                param = null;
                flag = end + 1;
                continue;
            }
            // 如果都不是上面的那些字符，就意味着可能是数字或者是布尔值。（在输入正确的情况下，故意输错不说。）
            else
            {
                Verify.notNull(transVar, "sql语句错误，请检查是否参数'{}'前面是否缺少了$", param);
                int paramStart = flag;
                flag = getEndFlag(conditionStatment, flag);
                param = conditionStatment.substring(paramStart, flag);
                createStatement(param, cache, transVar, varType, condition);
                transVar = null;
                condition = null;
                param = null;
                continue;
            }
        }
        if (transVar != null && condition == null && param == null)
        {
            if (varType != null)
            {
                createStatement("null", cache, transVar, varType, "!=");
            }
            else
            {
                createStatement(param, cache, transVar, null, condition);
            }
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
