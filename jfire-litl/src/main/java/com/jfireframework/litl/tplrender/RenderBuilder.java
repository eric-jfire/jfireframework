package com.jfireframework.litl.tplrender;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class RenderBuilder
{
    private static ClassPool    classPool = new ClassPool();
    private final static Logger logger    = ConsoleLogFactory.getLogger();
    static
    {
        initClassPool(null);
    }
    
    public static void initClassPool(ClassLoader classLoader)
    {
        ClassPool.doPruning = true;
        classPool.importPackage("com.jfireframework.litl");
        classPool.importPackage("com.jfireframework.litl.tplrender");
        if (classLoader != null)
        {
            classPool.insertClassPath(new LoaderClassPath(classLoader));
        }
        classPool.appendClassPath(new ClassClassPath(TplCenter.class));
        classPool.appendClassPath(new ClassClassPath(TplRender.class));
    }
    
    @SuppressWarnings("unchecked")
    public static TplRender build(Map<String, Object> data, Template template) throws NotFoundException, CannotCompileException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        CtClass tpl_render_interface = classPool.get(TplRender.class.getName());
        CtClass target = classPool.makeClass("tpl_render_" + System.nanoTime());
        target.setInterfaces(new CtClass[] { tpl_render_interface });
        addFieldAndConstructor(target);
        String methodBody = "{\njava.lang.StringBuilder _builder  = new StringBuilder();\n";
        for (Entry<String, Object> entry : data.entrySet())
        {
            String typeName = ReflectUtil.getTypeName(entry.getValue().getClass());
            methodBody += typeName + " " + entry.getKey() + " = (" + typeName + ")$1.get(\"" + entry.getKey() + "\");\n";
        }
        int index = 0;
        StringCache contextCache = new StringCache(128);
        StringCache methodCache = new StringCache(128);
        boolean isInMethod = false;
        boolean isInContent = false;
        TplCenter tplCenter = template.getTplCenter();
        for (LineInfo line : template.getContent())
        {
            index = 0;
            String context = line.getContent();
            while (index < context.length())
            {
                char c = context.charAt(index);
                if (isInMethod)
                {
                    int end = context.indexOf(tplCenter.getMethodEndFlag());
                    if (end == -1)
                    {
                        methodCache.append(context);
                        break;
                    }
                    else
                    {
                        methodCache.append(context.substring(0, end));
                        isInMethod = false;
                        methodBody += methodCache.toString() + ";\n";
                        methodCache.clear();
                        index = end + tplCenter.getMethodEndFlag().length();
                        continue;
                    }
                }
                if (c == tplCenter.get_methodStartFlag())
                {
                    if (context.indexOf(tplCenter.getMethodStartFlag(), index) == index)
                    {
                        isInContent = false;
                        isInMethod = true;
                        methodBody += "_builder.append(\"" + contextCache.toString() + "\");\n";
                        contextCache.clear();
                        int end = context.indexOf(tplCenter.getMethodEndFlag(), index + tplCenter.getMethodStartFlag().length());
                        if (end == -1)
                        {
                            methodCache.append(context.substring(index + tplCenter.getMethodStartFlag().length()));
                            break;
                        }
                        else
                        {
                            methodCache.append(context.substring(index + tplCenter.getMethodStartFlag().length(), end));
                            isInMethod = false;
                            methodBody += methodCache.toString() + ";\n";
                            methodCache.clear();
                            index = end + tplCenter.getMethodEndFlag().length();
                            continue;
                        }
                    }
                }
                if (c == tplCenter.get_varStartFlag())
                {
                    if (context.indexOf(tplCenter.getVarStartFlag(), index) == index)
                    {
                        methodBody += "_builder.append(\"" + contextCache.toString() + "\");\n";
                        contextCache.clear();
                        int end = context.indexOf(tplCenter.getVarEndFlag(), index + tplCenter.getVarStartFlag().length());
                        if (end == -1)
                        {
                            throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", line.getLine()));
                        }
                        else
                        {
                            context = context.trim();
                            String var = context.substring(index + tplCenter.getVarStartFlag().length(), end);
                            if (var.indexOf(",") == -1)
                            {
                                VarInfo info = analyse(var, data, line);
                                methodBody += "_builder.append(" + info.varChain + ");\n";
                                index = end + tplCenter.getVarEndFlag().length();
                                continue;
                            }
                            else
                            {
                                String[] varAndFormat = var.split(",");
                                VarInfo info = analyse(varAndFormat[0], data, line);
                                methodBody += "_builder.append(com.jfireframework.litl.format.FormatRegister.get(" + info.rootType.getName() + ".class).format(($w)" + info.varChain + "," + varAndFormat[1] + "));\n";
                                index = end + tplCenter.getVarEndFlag().length();
                                continue;
                            }
                        }
                    }
                }
                if (c == tplCenter.get_functionStartFlag())
                {
                    if (context.indexOf(tplCenter.getFunctionStartFlag(), index) == index)
                    {
                        context = context.trim();
                        int end = context.indexOf(tplCenter.getFunctionEndFlag(), index + tplCenter.getFunctionStartFlag().length());
                        if (end == -1)
                        {
                            throw new UnSupportException(StringUtil.format("调用方法需要在一行内闭合，请检查第{}行", line.getLine()));
                        }
                        else
                        {
                            String function = context.substring(index + tplCenter.getFunctionStartFlag().length(), end);
                            int start_function = function.indexOf('(');
                            int end_function = function.indexOf(')', start_function);
                            if (start_function == -1 || end_function == -1)
                            {
                                throw new UnSupportException(StringUtil.format("方法没有用(或者),请检查第{}行", line.getLine()));
                            }
                            String functionName = function.substring(0, start_function);
                            String var = function.substring(start_function + 1, end_function);
                            String[] tmp = var.split(",");
                            StringCache cache = new StringCache();
                            cache.append("new Object[]{");
                            for (String each : tmp)
                            {
                                if (isDirectParam(each))
                                {
                                    cache.append(each).append(',');
                                }
                                else
                                {
                                    VarInfo _info = analyse(each, data, line);
                                    cache.append(_info.varChain).append(',');
                                }
                            }
                            cache.append("($w)").append(line.getLine()).appendComma();
                            if (cache.isCommaLast())
                            {
                                cache.deleteLast();
                            }
                            cache.append('}');
                            methodBody += "com.jfireframework.litl.function.FunctionRegister.get(\"" + functionName + "\").call(" + cache.toString() + ",$1,_builder,_template);\n";
                            index = end + tplCenter.getFunctionEndFlag().length();
                            continue;
                        }
                    }
                }
                else
                {
                    isInContent = true;
                    contextCache.append(c);
                    index += 1;
                }
            }
            if (isInContent)
            {
                if (contextCache.count() > 0)
                {
                    methodBody += "_builder.append(\"" + contextCache.toString() + "\");\n";
                    contextCache.clear();
                }
                methodBody += "_builder.append(\"\\r\\n\");\n";
            }
        }
        methodBody += "return _builder.toString();\n}";
        CtMethod ctMethod = new CtMethod(classPool.get(String.class.getName()), "render", new CtClass[] { classPool.get(Map.class.getName()) }, target);
        logger.trace("为模板{}生成的方法体是\n{}\n", template.getPath(), methodBody);
        ctMethod.setBody(methodBody);
        target.addMethod(ctMethod);
        return (TplRender) target.toClass().getConstructor(Template.class).newInstance(template);
    }
    
    private static void addFieldAndConstructor(CtClass target) throws CannotCompileException, NotFoundException
    {
        CtField ctField = new CtField(classPool.get(Template.class.getName()), "_template", target);
        target.addField(ctField);
        CtConstructor constructor = new CtConstructor(new CtClass[] { classPool.get(Template.class.getName()) }, target);
        constructor.setBody("{this._template = $1;}");
        target.addConstructor(constructor);
    }
    
    private static boolean isDirectParam(String var)
    {
        if (var.charAt(0) == '"')
        {
            if (var.charAt(var.length() - 1) == '"')
            {
                return true;
            }
            else
            {
                throw new UnSupportException("参数有错误，少写了'\"'符号");
            }
        }
        else if (var.equals("true") || var.equals("false"))
        {
            return true;
        }
        else
        {
            try
            {
                Double.valueOf(var);
                return true;
            }
            catch (Exception e)
            {
                return false;
            }
        }
    }
    
    private static VarInfo analyse(String var, Map<String, Object> data, LineInfo info)
    {
        if (var.indexOf('[') == -1 && var.indexOf('.') == -1)
        {
            if (data.containsKey(var))
            {
                VarInfo varinfo = new VarInfo();
                varinfo.varChain = var;
                varinfo.varType = data.get(var).getClass();
                varinfo.rootType = varinfo.varType;
                return varinfo;
            }
            else
            {
                throw new UnSupportException(StringUtil.format("参数不存在，请检查第{}行，请求变量为{}", info.getLine(), var));
            }
        }
        String[] tmp = var.split("\\.");
        String varName = null;
        Class<?> varType = null;
        int array_flag = tmp[0].indexOf('[');
        String num_index = "";
        if (array_flag == -1)
        {
            varName = tmp[0];
        }
        else
        {
            if (tmp[0].indexOf(']', array_flag) != 1)
            {
                varName = tmp[0].substring(0, array_flag);
                num_index = tmp[0].substring(array_flag + 1, tmp[0].indexOf(']', array_flag));
            }
            else
            {
                throw new UnSupportException(StringUtil.format("参数错误，请检查第{}行，请求变量为{}", info.getLine(), var));
            }
        }
        boolean list = false;
        if (array_flag == -1)
        {
            varType = data.get(varName).getClass();
        }
        else
        {
            varType = data.get(varName).getClass();
            if (varType.isArray())
            {
                varType = varType.getComponentType();
            }
            else
            {
                list = true;
                varType = ((List<?>) data.get(varName)).get(0).getClass();
            }
        }
        VarInfo varInfo = new VarInfo();
        varInfo.varType = varType;
        try
        {
            if (array_flag == -1)
            {
                varInfo.varChain = varName + ReflectUtil.buildGetMethod(var, varType);
            }
            else
            {
                if (list)
                {
                    varInfo.varChain = "((" + varType.getName() + ")" + varName + ".get(" + num_index + "))" + ReflectUtil.buildGetMethod(var, varType);
                }
                else
                {
                    varInfo.varChain = "((" + varType.getName() + ")" + varName + ".[" + num_index + "])" + ReflectUtil.buildGetMethod(var, varType);
                }
                varInfo.rootType = ReflectUtil.getFinalReturnType(var, varType);
            }
            return varInfo;
        }
        catch (Exception e)
        {
            throw new UnSupportException("", e);
        }
    }
    
    static class VarInfo
    {
        String   varChain;
        Class<?> varType;
        Class<?> rootType;
    }
}
