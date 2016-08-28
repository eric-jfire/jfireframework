package com.jfireframework.mvc.rule;

import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.verify.Verify;

public class HeaderRule
{
    private final headerAndValue[] values;
    private final String[]         includeHeaders;
    private final boolean          ignore;
    private final String[]         rules;
    
    class headerAndValue
    {
        final String header;
        final String value;
        
        public headerAndValue(String header, String value)
        {
            Verify.True(StringUtil.isNotBlank(header), "header注解中键值对不能为空");
            Verify.True(StringUtil.isNotBlank(value), "header注解中键值对不能为空");
            this.header = header;
            this.value = value;
        }
    }
    
    public HeaderRule(String[] rules)
    {
        this.rules = rules;
        if (rules.length == 0)
        {
            includeHeaders = null;
            values = null;
            ignore = true;
        }
        else
        {
            ignore = false;
            List<String> tmp1 = new LinkedList<String>();
            List<headerAndValue> tmp2 = new LinkedList<headerAndValue>();
            for (String rule : rules)
            {
                // 是成对的规则，那么就代表这个header是值判断类型
                if (rule.indexOf("=") != -1)
                {
                    headerAndValue value = new headerAndValue(rule.split("=")[0], rule.split("=")[1]);
                    tmp2.add(value);
                }
                else
                {
                    tmp1.add(rule);
                }
            }
            includeHeaders = tmp1.toArray(new String[tmp1.size()]);
            values = tmp2.toArray(new headerAndValue[tmp2.size()]);
        }
    }
    
    public boolean permit(HttpServletRequest request)
    {
        if (ignore)
        {
            return true;
        }
        else
        {
            for (String each : includeHeaders)
            {
                if (request.getHeader(each) == null)
                {
                    return false;
                }
            }
            for (headerAndValue each : values)
            {
                if (each.value.equals(request.getHeader(each.header)))
                {
                    ;
                }
                else
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    @Override
    public int hashCode()
    {
        return 0;
    }
    
    @Override
    public boolean equals(Object target)
    {
        if (target instanceof HeaderRule)
        {
            HeaderRule tmp = (HeaderRule) target;
            String[] rules1 = rules;
            String[] rules2 = tmp.rules;
            if (rules1.length == rules2.length)
            {
                for (int i = 0; i < rules1.length; i++)
                {
                    if (rules1[i].equals(rules2[2]) == false)
                    {
                        return false;
                    }
                }
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
}
