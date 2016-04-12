package com.jfireframework.context.aop;

import java.lang.reflect.Method;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.order.Order;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.context.aop.annotation.AfterEnhance;
import com.jfireframework.context.aop.annotation.AroundEnhance;
import com.jfireframework.context.aop.annotation.BeforeEnhance;
import com.jfireframework.context.aop.annotation.ThrowEnhance;
import com.jfireframework.context.bean.Bean;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class EnhanceAnnoInfo implements Order
{
    
    private Bean       enhanceBean;
    /** 在进行增强时候,增强类在目标类的属性名称 */
    private String     enhanceFieldName;
    /**
     * 增强方法匹配目标方法的路径 匹配采用两段匹配完成,先匹配方法名称,再匹配方法入参类型.
     * 例子1:com.jfire.Person.getName();这样的情况从开始到'('为止是方法名称匹配区.匹配均采用从左到右的形式.*
     * 可以代表任意长度的字母
     * 匹配完方法名称之后,如果通过匹配方法入参类型.例子1的情况,方法是没有入参的,要求匹配的目标方法也是没有入参的.如果想忽略入参匹配,在()中填入*
     * 例子2:com.*.getname(String int);
     * 如果是这样,则要求目标方法入参分别为String和int.入参匹配采用的名称匹配,并且不支持通配符 .
     * 要求方法入参的类型的顺序和内容与path中的一致.匹配时用的是String的indexOf方法进行匹配
     * 
     */
    private String     path;
    /** path分解得到的匹配methodName */
    private String     methodName;
    /** path分解得到的匹配入参类型字符串 */
    private String[]   paramTypeNames = new String[0];
    /***/
    private int        order;
    /***/
    private Class<?>[] throwtype;
    
    private String          enhanceMethodName;
    private int             type;
    public static final int BEFORE = 1;
    public static final int AFTER  = 2;
    public static final int AROUND = 3;
    public static final int THROW  = 4;
    
    public EnhanceAnnoInfo(Bean enhanceBean, String enhanceFieldName, String path, int order, Method enhanceMethod)
    {
        Verify.True(enhanceMethod.getParameterTypes().length == 1, "增强方法{}.{}入参个数错误,请检查", enhanceMethod.getDeclaringClass(), enhanceMethod.getName());
        Verify.True(ProceedPoint.class.isAssignableFrom(enhanceMethod.getParameterTypes()[0]), "增强方法{}.{}的入参只能是ProceedPoint", enhanceMethod.getDeclaringClass(), enhanceMethod.getName());
        int left = path.indexOf('(');
        int right = path.indexOf(')', left);
        Verify.True(left > 0, "方法{}.{}上增强注解的path值错误,缺少'('", enhanceMethod.getDeclaringClass(), enhanceMethod.getName());
        Verify.True(right > 0, "方法{}.{}上增强注解的path值错误,缺少')'", enhanceMethod.getDeclaringClass(), enhanceMethod.getName());
        enhanceMethodName = enhanceMethod.getName();
        this.enhanceBean = enhanceBean;
        this.enhanceFieldName = enhanceFieldName;
        this.path = path;
        this.order = order;
        methodName = path.substring(0, left);
        paramTypeNames = (left + 1 == right) ? new String[0] : path.substring(left + 1, right).split(" ");
        if (enhanceMethod.isAnnotationPresent(BeforeEnhance.class))
        {
            type = BEFORE;
        }
        else if (enhanceMethod.isAnnotationPresent(AfterEnhance.class))
        {
            type = AFTER;
        }
        else if (enhanceMethod.isAnnotationPresent(AroundEnhance.class))
        {
            type = AROUND;
        }
        else if (enhanceMethod.isAnnotationPresent(ThrowEnhance.class))
        {
            type = THROW;
        }
        else
        {
            Verify.error("方法{}.{}没有增强注解", enhanceMethod.getDeclaringClass(), enhanceMethod.getName());
        }
    }
    
    @Override
    public int getOrder()
    {
        return order;
    }
    
    /**
     * 将ctmethod的方法签名与path进行比对,返回匹配结果
     * 
     * @param ctMethod
     * @return
     * @throws NotFoundException
     */
    public boolean match(CtMethod ctMethod) throws NotFoundException
    {
        if (StringUtil.match(ctMethod.getName(), methodName))
        {
            CtClass[] methodParamTypes = ctMethod.getParameterTypes();
            //如果规则是xxxx(*)的形式，表明忽略目标方法的入参，此时可以返回true
            if (paramTypeNames.length == 1 && paramTypeNames[0].equals("*"))
            {
                return true;
            }
            else if (methodParamTypes.length == paramTypeNames.length)
            {
                for (int i = 0; i < methodParamTypes.length; i++)
                {
                    if (AopUtil.getNameForType(methodParamTypes[i]).contains(paramTypeNames[i]))
                    {
                        continue;
                    }
                    else
                    {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public Bean getEnhanceBean()
    {
        return enhanceBean;
    }
    
    public void setEnhanceBean(Bean enhanceBean)
    {
        this.enhanceBean = enhanceBean;
    }
    
    public String getEnhanceFieldName()
    {
        return enhanceFieldName;
    }
    
    public void setEnhanceFieldName(String enhanceFieldName)
    {
        this.enhanceFieldName = enhanceFieldName;
    }
    
    public String getPath()
    {
        return path;
    }
    
    public void setPath(String path)
    {
        this.path = path;
    }
    
    public Class<?>[] getThrowtype()
    {
        return throwtype;
    }
    
    public void setThrowtype(Class<?>[] throwtype)
    {
        this.throwtype = throwtype;
    }
    
    public void setOrder(int order)
    {
        this.order = order;
    }
    
    public String getEnhanceMethodName()
    {
        return enhanceMethodName;
    }
    
    public int getType()
    {
        return type;
    }
    
}
