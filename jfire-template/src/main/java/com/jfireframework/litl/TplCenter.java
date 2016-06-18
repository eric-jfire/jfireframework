package com.jfireframework.litl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import com.jfireframework.baseutil.LineReader;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

public class TplCenter
{
    private ConcurrentHashMap<String, TplRender> renderMap          = new ConcurrentHashMap<String, TplRender>();
    private ClassPool                            classPool          = new ClassPool();
    private Logger                               logger             = ConsoleLogFactory.getLogger();
    private String                               methodEndFlag      = "#>";
    private String                               methodStartFlag    = "<#";
    private char                                 _startFlag         = '<';
    private String                               varStartFlag       = "${";
    private char                                 _varStartFlag      = '$';
    private String                               varEndFlag         = "}";
    private String                               functionStartFlag  = "<~";
    private char                                 _functionStartFlag = '<';
    private String                               functionEndFlag    = "~>";
    private final File                           root;
    
    public TplCenter(File root)
    {
        this.root = root;
        ClassPool.doPruning = true;
        initClassPool(null);
    }
    
    public void initClassPool(ClassLoader classLoader)
    {
        classPool.importPackage("com.jfireframework.litl");
        if (classLoader != null)
        {
            classPool.insertClassPath(new LoaderClassPath(classLoader));
        }
        classPool.appendClassPath(new ClassClassPath(TplCenter.class));
    }
    
    public TplRender get(String key, Map<String, Object> data)
    {
        TplRender render = renderMap.get(key);
        if (render != null)
        {
            return render;
        }
        synchronized (renderMap)
        {
            render = renderMap.get(key);
            if (render != null)
            {
                return render;
            }
            LineReader lineReader = new LineReader(new File(root, key), Charset.forName("utf8"));
            TreeMap<Integer, String> context = new TreeMap<Integer, String>();
            String value = null;
            int line = 1;
            while ((value = lineReader.readLine()) != null)
            {
                context.put(line, value);
                line += 1;
            }
            try
            {
                render = build(context, data);
                renderMap.put(key, render);
                return render;
            }
            catch (Exception e)
            {
                throw new UnSupportException("", e);
            }
        }
    }
    
    @SuppressWarnings("unchecked")
    public TplRender build(TreeMap<Integer, String> tplSrc, Map<String, Object> data) throws SecurityException, NoSuchFieldException, NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException
    {
        CtClass tpl_render_interface = classPool.getCtClass(TplRender.class.getName());
        CtClass target = classPool.makeClass("tpl_render_" + System.nanoTime());
        target.setInterfaces(new CtClass[] { tpl_render_interface });
        CtField ctField = new CtField(classPool.get(TplCenter.class.getName()), "_tplCenter", target);
        target.addField(ctField);
        CtConstructor constructor = new CtConstructor(new CtClass[] { classPool.get(TplCenter.class.getName()) }, target);
        constructor.setBody("{this._tplCenter = $1;}");
        target.addConstructor(constructor);
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
        for (Entry<Integer, String> line : tplSrc.entrySet())
        {
            index = 0;
            String context = line.getValue();
            while (index < context.length())
            {
                char c = context.charAt(index);
                if (isInMethod)
                {
                    int end = context.indexOf(methodEndFlag);
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
                        index = end + methodEndFlag.length();
                        continue;
                    }
                }
                if (c == _startFlag)
                {
                    if (context.indexOf(methodStartFlag, index) == index)
                    {
                        isInContent = false;
                        isInMethod = true;
                        methodBody += "_builder.append(\"" + contextCache.toString() + "\");\n";
                        contextCache.clear();
                        int end = context.indexOf(methodEndFlag, index + methodStartFlag.length());
                        if (end == -1)
                        {
                            methodCache.append(context.substring(index + methodStartFlag.length()));
                            break;
                        }
                        else
                        {
                            methodCache.append(context.substring(index + methodStartFlag.length(), end));
                            isInMethod = false;
                            methodBody += methodCache.toString() + ";\n";
                            methodCache.clear();
                            index = end + methodEndFlag.length();
                            continue;
                        }
                    }
                }
                if (c == _varStartFlag)
                {
                    if (context.indexOf(varStartFlag, index) == index)
                    {
                        methodBody += "_builder.append(\"" + contextCache.toString() + "\");\n";
                        contextCache.clear();
                        int end = context.indexOf(varEndFlag, index + varStartFlag.length());
                        if (end == -1)
                        {
                            throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", line.getKey()));
                        }
                        else
                        {
                            context = context.trim();
                            String var = context.substring(index + methodStartFlag.length(), end);
                            if (var.indexOf(",") == -1)
                            {
                                VarInfo info = analyse(var, data, line);
                                methodBody += "_builder.append(" + info.varChain + ");\n";
                                index = end + varEndFlag.length();
                                continue;
                            }
                            else
                            {
                                String[] varAndFormat = var.split(",");
                                VarInfo info = analyse(varAndFormat[0], data, line);
                                methodBody += "_builder.append(com.jfireframework.litl.format.FormatRegister.get(" + info.rootType.getName() + ".class).format(" + info.varChain + "," + varAndFormat[1] + "));\n";
                                index = end + varEndFlag.length();
                                continue;
                            }
                        }
                    }
                }
                if (c == _functionStartFlag)
                {
                    if (context.indexOf(functionStartFlag, index) == index)
                    {
                        context = context.trim();
                        int end = context.indexOf(functionEndFlag, index + functionStartFlag.length());
                        if (end == -1)
                        {
                            throw new UnSupportException(StringUtil.format("调用方法需要在一行内闭合，请检查第{}行", line.getKey()));
                        }
                        else
                        {
                            String function = context.substring(index + functionStartFlag.length(), end);
                            int start_function = function.indexOf('(');
                            int end_function = function.indexOf(')', start_function);
                            if (start_function == -1 || end_function == -1)
                            {
                                throw new UnSupportException(StringUtil.format("方法没有用(或者),请检查第{}行", line.getKey()));
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
                            if (cache.isCommaLast())
                            {
                                cache.deleteLast();
                            }
                            cache.append('}');
                            methodBody += "com.jfireframework.litl.function.FunctionRegister.get(\"" + functionName + "\").call(" + cache.toString() + ",$1,_builder,_tplCenter);\n";
                            index = end + functionEndFlag.length();
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
                methodBody += "_builder.append(\"\\r\\n\");\n";
            }
        }
        methodBody += "return _builder.toString();\n}";
        CtMethod ctMethod = new CtMethod(classPool.get(String.class.getName()), "render", new CtClass[] { classPool.get(Map.class.getName()) }, target);
        logger.trace("输入的方法体是\n{}\n", methodBody);
        ctMethod.setBody(methodBody);
        target.addMethod(ctMethod);
        return (TplRender) target.toClass().getConstructor(TplCenter.class).newInstance(this);
        
    }
    
    private boolean isDirectParam(String var)
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
    
    private VarInfo analyse(String var, Map<String, Object> data, Entry<Integer, String> line)
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
                throw new UnSupportException(StringUtil.format("参数不存在，请检查第{}行，请求变量为{}", line.getKey(), var));
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
                throw new UnSupportException(StringUtil.format("参数错误，请检查第{}行，请求变量为{}", line.getKey(), var));
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
    
    class VarInfo
    {
        String   varChain;
        Class<?> varType;
        Class<?> rootType;
    }
}
