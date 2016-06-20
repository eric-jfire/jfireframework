package com.jfireframework.mvc.rule;

import java.util.Map;
import com.jfireframework.baseutil.collection.set.LightSet;

public class RestfulRule
{
    
    private final String[] names;
    private final String[] rules;
    private final int      lastRuleLength;
    private final boolean  endWithAsterisk;
    private final int      valueLength;
    
    public RestfulRule(String url)
    {
        LightSet<String> set = new LightSet<String>();
        StringBuilder builder = new StringBuilder();
        int pre = 0, index = 0;
        do
        {
            index = url.indexOf("{", pre);
            if (index <= 0)
            {
                break;
            }
            else
            {
                builder.append(url.substring(pre, index)).append("*");
                pre = index + 1;
                index = url.indexOf("}", pre);
                if (index <= 0)
                {
                    throw new RuntimeException("");
                }
                else
                {
                    set.add(url.substring(pre, index));
                    pre = index + 1;
                }
            }
        } while (true);
        builder.append(url.substring(pre));
        String rule = builder.toString();
        names = set.toArray(String.class);
        rules = rule.split("\\*");
        if (rule.endsWith("*"))
        {
            endWithAsterisk = true;
            valueLength = rules.length;
        }
        else
        {
            endWithAsterisk = false;
            valueLength = rules.length - 1;
        }
        lastRuleLength = rules[rules.length - 1].length();
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
    
    public String[] getNames()
    {
        return names;
    }
    
}
