package com.jfireframework.sql.test;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import com.jfireframework.sql.util.DynamicSqlTool;
import com.jfireframework.sql.util.MapBeanFactory;

public class MapSqlTest
{
	@Test
	public void test()
	{
		Set<String> set = new HashSet<>();
		set.add("link.jfire.orm.entity.User");
		set.add("link.jfire.orm.entity.Home");
		MapBeanFactory.build(set, null);
		// String sql = "select * from User where name='sdada'";
		// String result = DynamicSqlTool.transMapSql(sql);
		// System.out.println(result);
		// sql="update User set name=name+'dsas'";
		// System.out.println(DynamicSqlTool.transMapSql(sql));
		String sql = "select a.name as username ,b.name as name from User as a,Home as b on a.id=b.id";
		System.out.println(DynamicSqlTool.transMapSql(sql));
	}
	
	@Test
	public void test2() throws NoSuchFieldException, SecurityException
	{
		String sql = "select * from com [$searchStr!=null || $state!=null] where 1=1 [$searchStr] and name like $%searchStr% # [$state] and state=$state # # [$order] order by {order} # ";
		String result = DynamicSqlTool.analyseDynamicSql(sql, new String[] { "searchStr", "state", "order" }, new Class[] { String.class, String.class, String.class }, false, null);
		System.out.println(result);
	}
}
