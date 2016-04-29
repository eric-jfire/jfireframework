package com.jfireframework.mvc.rest;

import java.util.Map;
import com.jfireframework.mvc.core.Action;

public class RestfulRule
{
    
    public static void main(String[] args)
    {
        String[] tmp = "user/*/*".split("\\*");
        for (String each : tmp)
        {
            System.out.println(each);
        }
    }
    
    private String[] names;
    private String[] rules;
    private int      lastRuleLength;
    private boolean  endWithAsterisk = false;
    private int      valueLength;
    private Action   action;
    private String   url;
    
    public RestfulRule(String rule)
    {
        url = rule;
        rules = rule.split("\\*");
        if (rule.endsWith("*"))
        {
            endWithAsterisk = true;
            valueLength = rules.length;
        }
        else
        {
            valueLength = rules.length - 1;
        }
        lastRuleLength = rules[rules.length - 1].length();
    }
    
    public RestfulRule(String rule, String[] names, Action action)
    {
        this(rule);
        this.names = names;
        this.action = action;
    }
    
    public String getUrl()
    {
        return url;
    }
    
    public boolean match(String url)
    {
        int index = 0;
        for (String rule : rules)
        {
            index = url.indexOf(rule, index);
            if (index < 0)
            {
                return false;
            }
            index += rule.length();
        }
        if (endWithAsterisk)
        {
            if (url.indexOf("/", index) < 0)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            if (index == url.length())
            {
                return true;
            }
            else
            {
                return false;
            }
        }
    }
    
    public void getObtain(String rul, Map<String, String> map)
    {
        String[] values = getObtain(rul);
        for (int i = 0; i < valueLength; i++)
        {
            map.put(names[i], values[i]);
        }
        
    }
    
    public String[] getObtain(String url)
    {
        int pre = 0;
        int index = 0;
        int i = 0;
        String[] values = new String[valueLength];
        for (String each : rules)
        {
            index = url.indexOf(each, pre);
            if (index == 0)
            {
                pre += each.length();
                continue;
            }
            else
            {
                values[i++] = url.substring(pre, index);
                pre = index + each.length();
            }
        }
        if (endWithAsterisk)
        {
            values[i] = url.substring(index + lastRuleLength);
        }
        return values;
    }
    
    public String[] getRules()
    {
        return rules;
    }
    
    public void setRules(String[] rules)
    {
        this.rules = rules;
    }
    
    public String[] getNames()
    {
        return names;
    }
    
    public Action getAction()
    {
        return action;
    }
    
}
