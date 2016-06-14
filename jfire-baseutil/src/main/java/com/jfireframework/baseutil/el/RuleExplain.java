package com.jfireframework.baseutil.el;

import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.verify.Verify;

public class RuleExplain
{
    /**
     * 将表达式进行解析。比如user.age >15会被生成(user !=null) &&
     * (user.getAge() !=null) && (user.getAge().intValue()>15)
     * 
     * @param conditionStatment 条件语句如[user.age >15]
     * @param paramNames 接口方法所有的入参名称
     * @param types 接口方法所有的入参类型
     * @return
     * @throws NoSuchFieldException
     * @throws SecurityException
     */
    public static String createVarIf(String conditionStatment, String[] paramNames, Class<?>[] types) throws NoSuchFieldException, SecurityException
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
                transVar = buildParam(var, paramNames, types);
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
    private static String buildParam(String inject, String[] paramNames, Class<?>[] paramTypes) throws NoSuchFieldException, SecurityException
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
    
    private static String buildPrimitiveValue(String prefix, Class<?> varType)
    {
        if (varType == Integer.class)
        {
            return prefix + ".intValue()";
        }
        else if (varType == Long.class)
        {
            return prefix + ".longValue()";
        }
        else if (varType == Short.class)
        {
            return prefix + ".shortValue()";
        }
        else if (varType == Double.class)
        {
            return prefix + ".doubleValue()";
        }
        else if (varType == Float.class)
        {
            return prefix + ".floatValue()";
        }
        else if (varType == Long.class)
        {
            return prefix + ".longValue()";
        }
        else
        {
            throw new RuntimeException("不能识别的处理类型" + varType);
        }
    }
    
    private String createValue(String expression, String[] names, Class<?>[] types) throws SecurityException, NoSuchFieldException
    {
        StringCache cache = new StringCache();
        cache.getClass().isPrimitive()
        int length = expression.length();
        int index = 0;
        while (index < length)
        {
            char c = expression.charAt(index);
            if (c == '$')
            {
                int next = expression.indexOf(' ');
                String value;
                if (next == -1)
                {
                    next = length;
                }
                value = expression.substring(index + 1, next);
                cache.append(buildParam(value, names, types));
                index = next;
            }
            else if (c == '\'')
            {
                int next = expression.indexOf('\'', index + 1);
                if (next == -1)
                {
                    throw new UnSupportException("key的规则有问题，缺少了一边的'\"'");
                }
                cache.append('"');
                for (int i = index; i < next; i++)
                {
                    cache.append(expression.charAt(i));
                }
                cache.append('"');
                index = next + 1;
                break;
            }
            else
            {
                cache.append(c);
                index += 1;
                break;
            }
        }
        return cache.toString();
    }
    
}
