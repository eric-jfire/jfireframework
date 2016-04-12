package com.jfireframework.mvc;

import static org.junit.Assert.*;
import org.junit.Test;
import com.jfireframework.mvc.rest.RestfulRule;
import com.jfireframework.mvc.rest.RestfulUrlTool;

public class RestTest
{
    @Test
    public void match()
    {
        RestfulRule restfulRule = RestfulUrlTool.build("/action/{id}/get/{actid}/set/{sid}", null);
        String[] names = restfulRule.getNames();
        assertEquals("id", names[0]);
        assertEquals("actid", names[1]);
        assertEquals("sid", names[2]);
        assertFalse(restfulRule.match("/action/1212/get/2323/set/1212/serer"));
        assertTrue(restfulRule.match("/action/1212/get/2323/set/1212-serer"));
        restfulRule = RestfulUrlTool.build("/action/{id}/get/{bid}/set", null);
        names = restfulRule.getNames();
        assertEquals("id", names[0]);
        assertEquals("bid", names[1]);
        assertTrue(restfulRule.match("/action/1212/get/2323/set"));
        assertFalse(restfulRule.match("/action/1212/get/2323/set/1212-serer"));
    }
    
    @Test
    public void getTest()
    {
        RestfulRule restfulRule = RestfulUrlTool.build("/action/{id}/get/{actid}/set/{sid}", null);
        String[] values = restfulRule.getObtain("/action/1212/get/2323/set/serer");
        assertEquals("1212", values[0]);
        assertEquals("2323", values[1]);
        assertEquals("serer", values[2]);
        restfulRule = RestfulUrlTool.build("/action/*/get/*/set", null);
        values = restfulRule.getObtain("/action/1212/get/2323/set");
        assertEquals("1212", values[0]);
        assertEquals("2323", values[1]);
        
    }
}
