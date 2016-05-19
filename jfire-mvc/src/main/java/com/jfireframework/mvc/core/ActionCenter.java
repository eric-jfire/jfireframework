package com.jfireframework.mvc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.mvc.annotation.RequestMethod;

public final class ActionCenter
{
    private final Map<String, Action> getActions    = new HashMap<>();
    private final Action[]            rest_get_actions;
    private final Map<String, Action> postActions   = new HashMap<>();
    private final Action[]            rest_post_actions;
    private final Map<String, Action> putActions    = new HashMap<>();
    private final Action[]            rest_put_actions;
    private final Map<String, Action> deleteActions = new HashMap<>();
    private final Action[]            rest_delete_actions;
    private static final Logger       logger        = ConsoleLogFactory.getLogger();
    
    public ActionCenter(Action[] actions)
    {
        List<Action> rest_get_actions_list = new LinkedList<>();
        List<Action> rest_post_actions_list = new LinkedList<>();
        List<Action> rest_put_actions_list = new LinkedList<>();
        List<Action> rest_delete_actions_list = new LinkedList<>();
        for (Action each : actions)
        {
            for (RequestMethod requestMethod : each.getRequestMethods())
            {
                switch (requestMethod)
                {
                    case GET:
                        if (each.isRest())
                        {
                            rest_get_actions_list.add(each);
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
                            rest_post_actions_list.add(each);
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
                            rest_put_actions_list.add(each);
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
                            rest_delete_actions_list.add(each);
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
        checkRepetition(rest_get_actions_list);
        checkRepetition(rest_post_actions_list);
        checkRepetition(rest_put_actions_list);
        checkRepetition(rest_delete_actions_list);
        rest_get_actions = rest_get_actions_list.toArray(new Action[0]);
        rest_post_actions = rest_post_actions_list.toArray(new Action[0]);
        rest_delete_actions = rest_delete_actions_list.toArray(new Action[0]);
        rest_put_actions = rest_put_actions_list.toArray(new Action[0]);
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
        for (Action each : rest_get_actions)
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (Action each : rest_post_actions)
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (Action each : rest_put_actions)
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
        for (Action each : rest_delete_actions)
        {
            logger.debug("url:{},调用的方法是{}", each.getRequestUrl(), each.getMethod().toGenericString());
        }
    }
    
    /**
     * 检查是否存在重复的url。返回false表示没有重复。
     * 
     * @param restActions
     * @return
     */
    private void checkRepetition(List<Action> restActions)
    {
        Set<String> set = new HashSet<>();
        for (Action each : restActions)
        {
            if (set.add(each.getRequestUrl()) == false)
            {
                throw new UnSupportException(StringUtil.format("url:{}存在重复，请检查{}", each.getRequestUrl(), each.getMethod()));
            }
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
                for (Action each : rest_get_actions)
                {
                    if (each.getRestfulRule().match(path))
                    {
                        return each;
                    }
                }
            }
        }
        else if (method.equals("POST"))
        {
            action = postActions.get(path);
            if (action == null)
            {
                for (Action each : rest_post_actions)
                {
                    if (each.getRestfulRule().match(path))
                    {
                        return each;
                    }
                }
            }
        }
        else if (method.equals("PUT"))
        {
            action = putActions.get(path);
            if (action == null)
            {
                for (Action each : rest_put_actions)
                {
                    if (each.getRestfulRule().match(path))
                    {
                        return each;
                    }
                }
            }
        }
        else if (method.equals("DELETE"))
        {
            action = deleteActions.get(path);
            if (action == null)
            {
                for (Action each : rest_delete_actions)
                {
                    if (each.getRestfulRule().match(path))
                    {
                        return each;
                    }
                }
            }
        }
        return action;
    }
}
