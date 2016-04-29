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
        for (int i = 0; i < rules.length; i++)
        {
            index = url.indexOf(rules[i], index);
            if (index < 0)
            {
                return false;
            }
            if (i == 0 && index != 0)
            {
                return false;
            }
            index += rules[i].length();
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
        for (int j = 0; j < rules.length; j++)
        {
            index = url.indexOf(rules[j], pre);
            if (j == 0)
            {
                pre += rules[j].length() + index;
                continue;
            }
            else
            {
                values[i++] = url.substring(pre, index);
                pre = index + rules[j].length();
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
