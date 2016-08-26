package com.jfireframework.mvc.newbinder;

import java.util.HashMap;

public class TreeValueNode extends HashMap<String, ParamTreeNode> implements ParamTreeNode
{
    /**
     * 
     */
    private static final long serialVersionUID = -7949905578641740166L;
    
    // private HashMap<String, ParamTreeNode> content = new HashMap<>();
    public TreeValueNode()
    {
    }
    
    public TreeValueNode(String text, String value)
    {
        put(text, value);
    }
    
    public void put(String text, String value)
    {
        if (text.charAt(0) == '[')
        {
            
            int end = text.indexOf(']');
            String key = text.substring(1, end);
            if (end == text.length() - 1)
            {
                if (key.length() == 0)
                {
                    // 如果key的长度是2，意味着原始内容实际上是[]，那么就是说这是个数组
                    put(String.valueOf(size()), new StringValueNode(value));
                }
                else
                {
                    if (containsKey(key))
                    {
                        ArrayNode arrayNode = new ArrayNode();
                        StringValueNode node = (StringValueNode) get(key);
                        arrayNode.add(node.getValue());
                        arrayNode.add(value);
                        put(key, arrayNode);
                    }
                    else
                    {
                        put(key, new StringValueNode(value));
                    }
                }
            }
            else
            {
                String nestedText = text.substring(end + 1);
                if (nestedText.length() == 0)
                {
                    
                }
                else
                {
                    if (containsKey(key))
                    {
                        TreeValueNode node = (TreeValueNode) get(key);
                        node.put(nestedText, value);
                    }
                    else
                    {
                        put(key, new TreeValueNode(nestedText, value));
                    }
                }
            }
        }
        else
        {
            int index = 0;
            if ((index = text.indexOf('[')) > 0)
            {
                String keyName = text.substring(0, index);
                String nestedText = text.substring(index);
                if (containsKey(keyName) == false)
                {
                    put(keyName, new TreeValueNode(nestedText, value));
                }
                else
                {
                    TreeValueNode node = (TreeValueNode) get(keyName);
                    node.put(nestedText, value);
                }
            }
            else
            {
                if (containsKey(text))
                {
                    ParamTreeNode node = get(text);
                    if (node instanceof StringValueNode)
                    {
                        ArrayNode arrayNode = new ArrayNode();
                        arrayNode.add(((StringValueNode) node).getValue());
                        put(text, arrayNode);
                    }
                    else if (node instanceof ArrayNode)
                    {
                        ((ArrayNode) node).add(value);
                    }
                }
                else
                {
                    put(text, new StringValueNode(value));
                }
            }
        }
        
    }
    
}
