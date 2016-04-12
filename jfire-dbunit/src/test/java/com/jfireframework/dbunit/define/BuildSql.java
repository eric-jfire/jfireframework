package com.jfireframework.dbunit.define;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class BuildSql
{
	public final static void createTable(String url)
	{
		try
		{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("stu.sql"))));
			StringBuilder builder = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
			String buildSchemaSql = builder.toString();
			Class.forName(com.mysql.jdbc.Driver.class.getName()).newInstance();
			Connection connection = DriverManager.getConnection(url);
			connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement(buildSchemaSql);
			ps.execute();
			ps.close();
			connection.commit();
			connection.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
