package com.jfireframework.mvc.core.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import com.jfireframework.baseutil.StringUtil;
import com.jfireframework.baseutil.exception.UnSupportException;

public final class ActionCenter
{
    private final ActionClassify      getAction;
    private final ActionClassify      postAction;
    private final ActionClassify      delAction;
    private final ActionClassify      putAction;
    private final Map<String, Action> tokenActionMap = new HashMap<String, Action>();
    
    class ActionClassify
    {
        private final Action[]              restActions;
        private final Map<String, Action[]> pathActionMap;
        
        public ActionClassify(Action[] restActions, Map<String, Action[]> pathActionMap)
        {
            this.restActions = restActions;
            this.pathActionMap = pathActionMap;
        }
        
        public Action get(HttpServletRequest request)
        {
            String path = request.getRequestURI();
            Action[] actions = pathActionMap.get(path);
            if (actions != null)
            {
                for (Action action : actions)
                {
                    if (action.getHeaderRule().permit(request))
                    {
                        return action;
                    }
                }
            }
            for (Action action : restActions)
            {
                if (action.getHeaderRule().permit(request) && action.getRestfulRule().match(path))
                {
                    return action;
                }
            }
            return null;
        }
    }
    
    public ActionCenter(Action[] actions)
    {
        
        Map<String, Action[]> getActions = new HashMap<String, Action[]>();
        Map<String, Action[]> postActions = new HashMap<String, Action[]>();
        Map<String, Action[]> putActions = new HashMap<String, Action[]>();
        Map<String, Action[]> deleteActions = new HashMap<String, Action[]>();
        Set<Action> rest_get_actions_set = new HashSet<Action>();
        Set<Action> rest_post_actions_set = new HashSet<Action>();
        Set<Action> rest_put_actions_set = new HashSet<Action>();
        Set<Action> rest_delete_actions_set = new HashSet<Action>();
        for (Action each : actions)
        {
            if (tokenActionMap.containsKey(each.getToken()))
            {
                throw new UnSupportException(StringUtil.format("Action的token值是不能重复的。请检查{}和{}", each.getMethod().toGenericString(), tokenActionMap.get(each.getToken()).getMethod().toGenericString()));
            }
        }
        for (Action each : actions)
        {
            switch (each.getRequestMethod())
            {
                case GET:
                    if (each.isRest())
                    {
                        if (rest_get_actions_set.add(each) == false)
                        {
                            throw new UnSupportException(StringUtil.format("url:{}存在重复，请检查{}", each.getRequestUrl(), each.getMethod()));
                        }
                    }
                    else
                    {
                        dealWithRepet(getActions, each);
                    }
                    break;
                case POST:
                    if (each.isRest())
                    {
                        if (rest_post_actions_set.add(each) == false)
                        {
                            throw new UnSupportException(StringUtil.format("url:{}存在重复，请检查{}", each.getRequestUrl(), each.getMethod()));
                        }
                    }
                    else
                    {
                        dealWithRepet(postActions, each);
                    }
                    break;
                case PUT:
                    if (each.isRest())
                    {
                        if (rest_put_actions_set.add(each) == false)
                        {
                            throw new UnSupportException(StringUtil.format("url:{}存在重复，请检查{}", each.getRequestUrl(), each.getMethod()));
                        }
                    }
                    else
                    {
                        dealWithRepet(putActions, each);
                    }
                    break;
                case DELETE:
                    if (each.isRest())
                    {
                        if (rest_delete_actions_set.add(each) == false)
                        {
                            throw new UnSupportException(StringUtil.format("url:{}存在重复，请检查{}", each.getRequestUrl(), each.getMethod()));
                        }
                    }
                    else
                    {
                        dealWithRepet(deleteActions, each);
                    }
                    break;
            }
        }
        getAction = new ActionClassify(rest_get_actions_set.toArray(new Action[rest_get_actions_set.size()]), getActions);
        postAction = new ActionClassify(rest_post_actions_set.toArray(new Action[rest_post_actions_set.size()]), postActions);
        delAction = new ActionClassify(rest_delete_actions_set.toArray(new Action[rest_delete_actions_set.size()]), deleteActions);
        putAction = new ActionClassify(rest_put_actions_set.toArray(new Action[rest_put_actions_set.size()]), putActions);
    }
    
    private void dealWithRepet(Map<String, Action[]> actionMap, Action target)
    {
        
        if (actionMap.containsKey(target.getRequestUrl()))
        {
            Action[] tmp = actionMap.get(target.getRequestUrl());
            boolean repetition = false;
            Action repetAction = null;
            for (Action action : tmp)
            {
                if (action.getHeaderRule().equals(target.getHeaderRule()))
                {
                    repetAction = action;
                    repetition = true;
                    break;
                }
            }
            if (repetition)
            {
                throw new RuntimeException(StringUtil.format("url存在重复，请检查{}和{}", target.getMethod().toGenericString(), repetAction.getMethod().toGenericString()));
            }
            else
            {
                Action[] result = new Action[tmp.length + 1];
                System.arraycopy(tmp, 0, result, 0, tmp.length);
                result[result.length - 1] = target;
                actionMap.put(target.getRequestUrl(), result);
            }
        }
        else
        {
            actionMap.put(target.getRequestUrl(), new Action[] { target });
        }
    }
    
    public Action getAction(HttpServletRequest request)
    {
        String method = request.getMethod();
        Action action = null;
        if (method.equals("GET"))
        {
            return getAction.get(request);
        }
        else if (method.equals("POST"))
        {
            return postAction.get(request);
        }
        else if (method.equals("PUT"))
        {
            return putAction.get(request);
        }
        else if (method.equals("DELETE"))
        {
            return delAction.get(request);
        }
        return action;
    }
}
