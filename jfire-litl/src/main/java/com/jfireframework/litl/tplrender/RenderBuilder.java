package com.jfireframework.litl.tplrender;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.reflect.ReflectUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.litl.TplCenter;
import com.jfireframework.litl.template.LineInfo;
import com.jfireframework.litl.template.Template;
import com.jfireframework.litl.varaccess.VarAccess;
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
    private ClassPool           classPool = new ClassPool();
    private final static Logger logger    = ConsoleLogFactory.getLogger();
    private ClassLoader         classLoader;
    private VarAccess           varAccess;
    private static final int    for_flag  = 1;
    private static final int    if_flag   = 2;
    
    public RenderBuilder(ClassLoader classLoader)
    {
        if (classLoader == null)
        {
            this.classLoader = Thread.currentThread().getContextClassLoader();
        }
        else
        {
            this.classLoader = classLoader;
        }
        initClassPool(classLoader);
        varAccess = new VarAccess(classPool, classLoader);
        
    }
    
    public void initClassPool(ClassLoader classLoader)
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
    public TplRender build(Map<String, Object> data, Template template) throws NotFoundException, CannotCompileException, IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException
    {
        CtClass tpl_render_interface = classPool.get(TplRender.class.getName());
        CtClass target = classPool.makeClass("tpl_render_" + System.nanoTime());
        target.setInterfaces(new CtClass[] { tpl_render_interface });
        addFieldAndConstructor(target);
        String methodBody = "{\njava.lang.StringBuilder _builder  = new StringBuilder();\n";
        int index = 0;
        StringCache contextCache = new StringCache(128);
        StringCache methodCache = new StringCache(128);
        TplCenter tplCenter = template.getTplCenter();
        Set<String> names = new HashSet<String>();
        Deque<Integer> stack = new LinkedBlockingDeque<Integer>();
        String forin_each = null;
        for (LineInfo line : template.getContent())
        {
            try
            {
                index = 0;
                String context = line.getContent();
                while (index < context.length())
                {
                    char c = context.charAt(index);
                    if (c == tplCenter.get_methodStartFlag())
                    {
                        if (context.indexOf(tplCenter.getMethodStartFlag(), index) == index)
                        {
                            String append = contextCache.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                            if (StringUtil.isNotBlank(append))
                            {
                                methodBody += "_builder.append(\"" + append + "\");\n";
                            }
                            contextCache.clear();
                            int end = context.indexOf(tplCenter.getMethodEndFlag(), index + tplCenter.getMethodStartFlag().length());
                            if (end == -1)
                            {
                                throw new UnSupportException(StringUtil.format("方法书写需要在一行内闭合，请检查第{}行", line.getLine()));
                            }
                            else
                            {
                                String tmp_method = context.substring(index + tplCenter.getMethodStartFlag().length(), end);
                                tmp_method = tmp_method.trim();
                                if (tmp_method.equals("}"))
                                {
                                    if (stack.pop() == for_flag)
                                    {
                                        forin_each = null;
                                    }
                                    continue;
                                }
                                if (tmp_method.startsWith("for("))
                                {
                                    stack.push(for_flag);
                                    int in_index = tmp_method.indexOf(" in ");
                                    Verify.True(in_index != -1, "for语句的表达式没有正确包含in。请检查模板：{}的第{}行", template.getPath(), line.getLine());
                                    int forin_end_index = tmp_method.indexOf("){", in_index);
                                    Verify.True(forin_end_index != -1, "for语句的表达式没有以){结束。请检查模板：{}的第{}行", template.getPath(), line.getLine());
                                    forin_each = tmp_method.substring(4, in_index);
                                    String forin_array = tmp_method.substring(in_index + 4, forin_end_index);
                                    StringCache cache = new StringCache();
                                    if (names.add(forin_array))
                                    {
                                        cache.append("Object[] ").append(forin_array).append(" =(Object[]) $1.get(\"").append(forin_array).append("\");\n");
                                    }
                                    else
                                    {
                                        cache.append(forin_array).append(" =(Object[]) $1.get(\"").append(forin_array).append("\");\n");
                                    }
                                    cache.append("if(").append(forin_array).append(" == null ){throw new NullPointerException(\"参数:" + forin_array + "为空，请检查模板:" + template.getPath() + "的第" + line.getLine() + "行\");}\n");
                                    cache.append("for(int i=0;i<").append(forin_array).append(".length;i++){\n");
                                    cache.append("Object ").append(forin_each).append(" = ").append(forin_array).append("[i];\n");
                                    tmp_method = cache.toString() + tmp_method.substring(forin_end_index + 2);
                                }
                                else if (tmp_method.startsWith("if("))
                                {
                                    stack.push(if_flag);
                                    String condition = tmp_method.substring(3, tmp_method.lastIndexOf("){"));
                                    tmp_method = "if(" + buildAccess(condition, stack, template, line, forin_each) + "!=null){";
                                }
                                methodCache.append(tmp_method);
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
                            String append = contextCache.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                            if (StringUtil.isNotBlank(append))
                            {
                                methodBody += "_builder.append(\"" + append + "\");\n";
                            }
                            contextCache.clear();
                            int end = context.indexOf(tplCenter.getVarEndFlag(), index + tplCenter.getVarStartFlag().length());
                            if (end == -1)
                            {
                                throw new UnSupportException(StringUtil.format("获取参数需要在一行内闭合，请检查第{}行", line.getLine()));
                            }
                            else
                            {
                                String var = context.substring(index + tplCenter.getVarStartFlag().length(), end);
                                var = var.trim();
                                methodBody += "_builder.append(" + buildAccess(var, stack, template, line, forin_each) + ");\n";
                                index = end + tplCenter.getVarEndFlag().length();
                                continue;
                            }
                        }
                    }
                    if (c == tplCenter.get_functionStartFlag())
                    {
                        if (context.indexOf(tplCenter.getFunctionStartFlag(), index) == index)
                        {
                            int end = context.indexOf(tplCenter.getFunctionEndFlag(), index + tplCenter.getFunctionStartFlag().length());
                            if (end == -1)
                            {
                                throw new UnSupportException(StringUtil.format("调用方法需要在一行内闭合，请检查第{}行", line.getLine()));
                            }
                            else
                            {
                                String function = context.substring(index + tplCenter.getFunctionStartFlag().length(), end);
                                function = function.trim();
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
                    contextCache.append(c);
                    index += 1;
                }
                if (contextCache.count() > 0)
                {
                    String append = contextCache.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("\r", "\\r").replace("\n", "\\n");
                    methodBody += "_builder.append(\"" + append + "\");\n";
                    contextCache.clear();
                }
                methodBody += "_builder.append(\"\\r\\n\");\n";
            }
            catch (Exception e)
            {
                throw new UnSupportException(StringUtil.format("渲染有异常，请检查第{}行", line.getLine()), e);
            }
        }
        methodBody += "return _builder.toString();\n}";
        CtMethod ctMethod = new CtMethod(classPool.get(String.class.getName()), "render", new CtClass[] { classPool.get(Map.class.getName()) }, target);
        logger.trace("为模板{}生成的方法体是\n{}\n", template.getPath(), methodBody);
        try
        {
            ctMethod.setBody(methodBody);
        }
        catch (Exception e)
        {
            throw new UnSupportException(StringUtil.format("为模板:{}生成渲染类错误，以下是方法体，请检查\n{}\n", template.getPath(), methodBody), e);
        }
        target.addMethod(ctMethod);
        return (TplRender) target.toClass(classLoader, null).getConstructor(Template.class, VarAccess.class).newInstance(template, varAccess);
    }
    
    /**
     * 通过传入求值表达式和相关的上下文信息。返回当前情况下的求值语句
     * 
     * @param var
     * @param stack
     * @param template
     * @param line
     * @param forin_each
     * @return
     */
    private String buildAccess(String var, Deque<Integer> stack, Template template, LineInfo line, String forin_each)
    {
        String realVar;
        String format = null;
        // 使用逗号间隔，代表着存在格式化需求
        if (var.indexOf(",") != -1)
        {
            realVar = var.split(",")[0];
            format = var.split(",")[1];
        }
        else
        {
            realVar = var;
        }
        String targetObjectName = null;
        String[] tmp_var_list = realVar.split("\\.");
        targetObjectName = tmp_var_list[0];
        if (stack.contains(for_flag) && targetObjectName.equals(forin_each))
        {
            ;
        }
        else
        {
            targetObjectName = "$1.get(\"" + tmp_var_list[0] + "\")";
        }
        if (format == null)
        {
            return "_varaccess.getValue(\"" + template.getPath() + '_' + realVar + "\",\"" + realVar + "\"," + targetObjectName + "," + line.getLine() + ")";
        }
        else
        {
            return "com.jfireframework.litl.format.FormatRegister.format(_varaccess.getValue(\"" + template.getPath() + '_' + realVar + "\",\"" + realVar + "\"," + targetObjectName + "," + line.getLine() + ")," + format + ")";
        }
    }
    
    private void addFieldAndConstructor(CtClass target) throws CannotCompileException, NotFoundException
    {
        CtField ctField = new CtField(classPool.get(Template.class.getName()), "_template", target);
        target.addField(ctField);
        ctField = new CtField(classPool.get(VarAccess.class.getName()), "_varaccess", target);
        target.addField(ctField);
        CtConstructor constructor = new CtConstructor(new CtClass[] { classPool.get(Template.class.getName()), classPool.get(VarAccess.class.getName()) }, target);
        constructor.setBody("{this._template = $1;this._varaccess=$2;}");
        target.addConstructor(constructor);
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
            }
            varInfo.rootType = ReflectUtil.getFinalReturnType(var, varType);
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
