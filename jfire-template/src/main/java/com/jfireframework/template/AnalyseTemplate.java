package com.jfireframework.template;

import java.util.HashMap;
import java.util.Map;
import com.jfireframework.baseutil.collection.StringCache;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class AnalyseTemplate
{
    private static ClassPool classPool = ClassPool.getDefault();
    private static Logger    logger    = ConsoleLogFactory.getLogger();
    
    static
    {
        ClassPool.doPruning = true;
        classPool.importPackage("java.util");
        classPool.importPackage("com.jfireframework.baseutil.collection");
    }
    
    private static Map<String, TemplateOutput> outMap = new HashMap<String, TemplateOutput>();
    
    public static TemplateOutput analyse(String str, String templateName) throws NotFoundException, CannotCompileException, InstantiationException, IllegalAccessException
    {
        CtClass templateClass = classPool.makeClass(templateName + "templateout_" + System.nanoTime());
        CtClass superClass = classPool.get(AbstraceTemplateOutput.class.getName());
        templateClass.setSuperclass(superClass);
        int flag = 0, index = 0;
        int length = str.length();
        StringCache cache = new StringCache("{com.jfireframework.baseutil.collection.StringCache cache = ((com.jfireframework.baseutil.collection.StringCache)cacheLocal.get()).clear();\n");
        boolean readParam = false;
        while (index < length)
        {
            if (str.charAt(index) == '$')
            {
                if (index + 1 < length && str.charAt(index + 1) == '{')
                {
                    cache.append("cache.append(\"" + str.substring(flag, index) + "\");\n");
                    index += 2;
                    flag = index;
                    readParam = true;
                    continue;
                }
            }
            else if (str.charAt(index) == '}')
            {
                if (readParam)
                {
                    cache.append("cache.append($1.get(\"" + str.substring(flag, index) + "\"));\n");
                    index++;
                    flag = index;
                    readParam = false;
                    continue;
                }
            }
            else if (str.charAt(index) == '<')
            {
                if (index + 1 < length && str.charAt(index + 1) == '%')
                {
                    cache.append("cache.append(\"" + str.substring(flag, index) + "\");\n");
                    index += 2;
                    flag = index;
                    continue;
                }
            }
            else if (str.charAt(index) == '%')
            {
                if (index + 1 < length && str.charAt(index + 1) == '>')
                {
                    cache.append(str.substring(flag, index));
                    index += 2;
                    flag = index;
                    continue;
                }
            }
            index++;
        }
        cache.append("return cache.toString();\n");
        cache.append("}");
        System.out.println(cache.toString());
        CtMethod ctMethod = new CtMethod(classPool.get(String.class.getName()), "output", new CtClass[] { classPool.get(Map.class.getName()) }, templateClass);
        ctMethod.setBody(cache.toString());
        templateClass.addMethod(ctMethod);
        TemplateOutput output = (TemplateOutput) templateClass.toClass().newInstance();
        return output;
    }
    
    public static void main(String[] args) throws InstantiationException, IllegalAccessException, NotFoundException, CannotCompileException
    {
        String tmplStr = "你好，${name}" + "<%for(int i=0;i<10;i++){%>" + "此时i=<%print(i);%><br>" + "<%}%>";
        Map<String, String> param = new HashMap<String, String>();
        param.put("name", "eric");
        TemplateOutput output = AnalyseTemplate.analyse(tmplStr, "demo");
        String result = output.output(param);
        System.out.println(result);
    }
}
