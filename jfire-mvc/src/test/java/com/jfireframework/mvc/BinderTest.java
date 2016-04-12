package com.jfireframework.mvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Test;
import com.jfireframework.baseutil.time.ThreadTimewatch;
import com.jfireframework.mvc.binder.DataBinder;
import com.jfireframework.mvc.binder.DataBinderFactory;
import com.jfireframework.mvc.binder.ParamInfo;
import com.jfireframework.mvc.data.MockRequest;
import com.jfireframework.mvc.vo.Desk;
import com.jfireframework.mvc.vo.Home;
import com.jfireframework.mvc.vo.Person;

public class BinderTest
{
	@Test
	public void simple()
	{
		Map<String, String> map = new HashMap<>();
		map.put("name", "林斌");
		map.put("age", "25");
		map.put("weight", "75.26");
		map.put("ids[0]", "1");
		map.put("ids[1]", "10");
		map.put("ids[3]", "100");
		MockRequest request = new MockRequest(map);
		ParamInfo info = new ParamInfo();
		info.setPrefix("");
		info.setEntityClass(Person.class);
		DataBinder dataBinder = DataBinderFactory.build(info, new HashSet<Class<?>>());
		Person person = (Person) dataBinder.binder(request, map, null);
		assertEquals(person.getName(), "林斌");
		assertEquals(person.getAge(), 25);
		assertEquals(person.getWeight(), 75.26, 0.001);
		Integer[] ids = person.getIds();
		assertEquals(ids[0].intValue(), 1);
		assertEquals(ids[1].intValue(), 10);
		assertEquals(ids[3].intValue(), 100);
	}
	
	@Test
	public void test()
	{
		
		Map<String, String> map = new HashMap<>();
		map.put("host.name", "林斌");
		map.put("host.age", "25");
		map.put("host.weight", "75.26");
		map.put("host.ids[0]", "1");
		map.put("host.ids[1]", "10");
		map.put("host.ids[3]", "100");
		map.put("length", "100");
		map.put("width", "50");
		map.put("desks[0].name", "desk1");
		map.put("desks[0].width", "11");
		map.put("desks[1].name", "desk2");
		map.put("desks[1].width", "12");
		MockRequest request = new MockRequest(map);
		ParamInfo info = new ParamInfo();
		info.setPrefix("");
		info.setEntityClass(Home.class);
		ThreadTimewatch.start();
		DataBinder dataBinder = DataBinderFactory.build(info, new HashSet<Class<?>>());
		ThreadTimewatch.end();
		Home home = (Home) dataBinder.binder(request, map, null);
		assertEquals(home.getHost().getName(), "林斌");
		assertEquals(home.getHost().getAge(), 25);
		assertEquals(home.getHost().getWeight(), 75.26, 0.001);
		Integer[] ids = home.getHost().getIds();
		assertEquals(ids[0].intValue(), 1);
		assertEquals(ids[1].intValue(), 10);
		assertEquals(ids[3].intValue(), 100);
		Desk[] desks = home.getDesks();
		assertEquals("desk1", desks[0].getName());
		assertEquals("desk2", desks[1].getName());
		assertEquals(11, desks[0].getWidth());
		assertEquals(12, desks[1].getWidth());
	}
	
	@Test
	public void emptyTest()
	{
		Map<String, String> map = new HashMap<>();
		
		MockRequest request = new MockRequest(map);
		ParamInfo info = new ParamInfo();
		info.setPrefix("");
		info.setEntityClass(Home.class);
		ThreadTimewatch.start();
		DataBinder dataBinder = DataBinderFactory.build(info, new HashSet<Class<?>>());
		ThreadTimewatch.end();
		Home home = (Home) dataBinder.binder(request, map, null);
		assertNull(home);
	}
}
