package com.jfireframework.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.jfireframework.mvc.rule.RestfulRule;

public class RestTest
{
    @Test
    public void match()
    {
        RestfulRule restfulRule = new RestfulRule("/action/{id}/get/{actid}/set/{sid}");
        String[] names = restfulRule.getNames();
        assertEquals("id", names[0]);
        assertEquals("actid", names[1]);
        assertEquals("sid", names[2]);
        assertFalse(restfulRule.match("/action/1212/get/2323/set/1212/serer"));
        assertTrue(restfulRule.match("/action/1212/get/2323/set/1212-serer"));
        restfulRule = new RestfulRule("/action/{id}/get/{bid}/set");
        names = restfulRule.getNames();
        assertEquals("id", names[0]);
        assertEquals("bid", names[1]);
        assertTrue(restfulRule.match("/action/1212/get/2323/set"));
        assertFalse(restfulRule.match("/action/1212/get/2323/set/1212-serer"));
        restfulRule = new RestfulRule("/user/{name}/{password}");
        assertTrue(restfulRule.match("/user/121/212"));
        assertFalse(restfulRule.match("//user/121/212"));
    }
    
    @Test
    public void getTest()
    {
        RestfulRule restfulRule = new RestfulRule("/action/{id}/get/{actid}/set/{sid}");
        String[] values = restfulRule.getObtain("/action/1212/get/2323/set/serer");
        assertEquals("1212", values[0]);
        assertEquals("2323", values[1]);
        assertEquals("serer", values[2]);
        restfulRule = new RestfulRule("/action/*/get/*/set");
        values = restfulRule.getObtain("/action/1212/get/2323/set");
        assertEquals("1212", values[0]);
        assertEquals("2323", values[1]);
        restfulRule = new RestfulRule("/user/{name}/{password}");
        values = restfulRule.getObtain("//user/121/212");
        assertEquals("121", values[0]);
        assertEquals("212", values[1]);
        
    }
}
