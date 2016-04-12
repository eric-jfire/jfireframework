package com.jfireframework.mvc.core;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.annotation.RequestMethod;
import com.jfireframework.mvc.rest.RestfulRule;

public class ActionCenter
{
    private HashMap<String, Action> getActions    = new HashMap<>();
    private RestfulRule[]           rest_get_rules;
    private HashMap<String, Action> postActions   = new HashMap<>();
    private RestfulRule[]           rest_post_rules;
    private HashMap<String, Action> putActions    = new HashMap<>();
    private RestfulRule[]           rest_put_rules;
    private HashMap<String, Action> deleteActions = new HashMap<>();
    private RestfulRule[]           rest_delete_rules;
    private Logger                  logger        = ConsoleLogFactory.getLogger();
    
    public ActionCenter(Action[] actions)
    {
        Map<RestfulRule, Action> rest_get_rules = new HashMap<>();
        Map<RestfulRule, Action> rest_post_rules = new HashMap<>();
        Map<RestfulRule, Action> rest_put_rules = new HashMap<>();
        Map<RestfulRule, Action> rest_delete_rules = new HashMap<>();
        for (Action each : actions)
        {
            for (RequestMethod requestMethod : each.getRequestMethods())
            {
                switch (requestMethod)
                {
                    case GET:
                        if (each.isRest())
                        {
                            if (rest_get_rules.containsKey(each.getRestfulRule()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), rest_get_rules.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            rest_get_rules.put(each.getRestfulRule(), each);
                        }
                        else
                        {
                            if (getActions.containsKey(each.getRequestUrl()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), getActions.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            getActions.put(each.getRequestUrl(), each);
                        }
                        break;
                    case POST:
                        if (each.isRest())
                        {
                            if (rest_post_rules.containsKey(each.getRestfulRule()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), rest_post_rules.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            rest_post_rules.put(each.getRestfulRule(), each);
                        }
                        else
                        {
                            if (postActions.containsKey(each.getRequestUrl()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), postActions.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            postActions.put(each.getRequestUrl(), each);
                        }
                        break;
                    case PUT:
                        if (each.isRest())
                        {
                            if (rest_put_rules.containsKey(each.getRestfulRule()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), rest_put_rules.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            rest_put_rules.put(each.getRestfulRule(), each);
                        }
                        else
                        {
                            if (putActions.containsKey(each.getRequestUrl()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), putActions.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            putActions.put(each.getRequestUrl(), each);
                        }
                        break;
                    case DELETE:
                        if (each.isRest())
                        {
                            if (rest_delete_rules.containsKey(each.getRestfulRule()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), rest_delete_rules.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            rest_delete_rules.put(each.getRestfulRule(), each);
                        }
                        else
                        {
                            if (deleteActions.containsKey(each.getRequestUrl()))
                            {
                                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", each.getMethod().toGenericString(), deleteActions.get(each.getRequestUrl()).getMethod().toGenericString()));
                            }
                            deleteActions.put(each.getRequestUrl(), each);
                        }
                        break;
                }
            }
        }
        this.rest_get_rules = rest_get_rules.keySet().toArray(new RestfulRule[0]);
        this.rest_post_rules = rest_post_rules.keySet().toArray(new RestfulRule[0]);
        this.rest_put_rules = rest_put_rules.keySet().toArray(new RestfulRule[0]);
        this.rest_delete_rules = rest_delete_rules.keySet().toArray(new RestfulRule[0]);
        for (Action each : getActions.values())
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (Action each : postActions.values())
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (Action each : putActions.values())
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (Action each : deleteActions.values())
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (RestfulRule each : this.rest_get_rules)
        {
            logger.debug("url:{},调用的方法是{}", each.getUrl(), each.getAction().getMethod().toGenericString());
        }
        for (RestfulRule each : this.rest_post_rules)
        {
            logger.debug("url:{},调用的方法是{}", each.getUrl(), each.getAction().getMethod().toGenericString());
        }
        for (RestfulRule each : this.rest_delete_rules)
        {
            logger.debug("url:{},调用的方法是{}", each.getUrl(), each.getAction().getMethod().toGenericString());
        }
        for (RestfulRule each : this.rest_put_rules)
        {
            logger.debug("url:{},调用的方法是{}", each.getUrl(), each.getAction().getMethod().toGenericString());
        }
    }
    
    public Action getAction(HttpServletRequest request)
    {
        String method = request.getMethod();
        String path = request.getRequestURI();
        Action action = null;
        if (method.equals("GET"))
        {
            action = getActions.get(path);
            if (action == null)
            {
                for (RestfulRule each : rest_get_rules)
                {
                    if (each.match(path))
                    {
                        return each.getAction();
                    }
                }
            }
        }
        else if (method.equals("POST"))
        {
            action = postActions.get(path);
            if (action == null)
            {
                for (RestfulRule each : rest_post_rules)
                {
                    if (each.match(path))
                    {
                        return each.getAction();
                    }
                }
            }
        }
        else if (method.equals("PUT"))
        {
            action = putActions.get(path);
            if (action == null)
            {
                for (RestfulRule each : rest_put_rules)
                {
                    if (each.match(path))
                    {
                        return each.getAction();
                    }
                }
            }
        }
        else if (method.equals("DELETE"))
        {
            action = deleteActions.get(path);
            if (action == null)
            {
                for (RestfulRule each : rest_delete_rules)
                {
                    if (each.match(path))
                    {
                        return each.getAction();
                    }
                }
            }
        }
        return action;
    }
}
