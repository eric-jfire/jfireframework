package com.jfireframework.sql.function.impl;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.sql.DataSource;
import com.jfireframework.baseutil.PackageScan;
import com.jfireframework.baseutil.verify.Verify;
import com.jfireframework.sql.annotation.BatchUpdate;
import com.jfireframework.sql.annotation.Query;
import com.jfireframework.sql.annotation.Update;
import com.jfireframework.sql.dbstructure.MariaDBStructure;
import com.jfireframework.sql.dbstructure.Structure;
import com.jfireframework.sql.function.DAOBean;
import com.jfireframework.sql.function.SessionFactory;
import com.jfireframework.sql.function.SqlSession;
import com.jfireframework.sql.util.DaoFactory;
import com.jfireframework.sql.util.InterfaceMapperFactory;
import com.jfireframework.sql.util.MapBeanFactory;

@Resource
public class SessionFactoryImpl implements SessionFactory
{
	@Resource
	private DataSource				dataSource;
	@Resource
	private ClassLoader				classLoader;
	private ThreadLocal<SqlSession>	sessionLocal	= new ThreadLocal<>();
	private String					scanPackage;
	// 如果值是create，则会创建表。
	private String					tableMode		= "none";
	// 当前支持的类型有mysql,MariaDB
	private String					dbType;
									
	public SessionFactoryImpl()
	{
	
	}
	
	public SessionFactoryImpl(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	@PostConstruct
	public void init()
	{
		if (dataSource == null)
		{
			return;
		}
		Set<String> set = new HashSet<>();
		String[] packageNames = scanPackage.split(";");
		for (String packageName : packageNames)
		{
			for (String each : PackageScan.scan(packageName))
			{
				set.add(each);
			}
		}
		MapBeanFactory.build(set, classLoader);
		DaoFactory.buildDaoBean(set, classLoader);
		try
		{
			next: for (String each : set)
			{
				Class<?> ckass = classLoader == null ? Class.forName(each) : classLoader.loadClass(each);
				if (ckass.isInterface())
				{
					for (Method method : ckass.getMethods())
					{
						if (method.isAnnotationPresent(Query.class) || method.isAnnotationPresent(Update.class) || method.isAnnotationPresent(BatchUpdate.class))
						{
							InterfaceMapperFactory.buildMapper(ckass, classLoader);
							continue next;
						}
					}
				}
			}
		}
		catch (ClassNotFoundException | SecurityException e1)
		{
			throw new RuntimeException(e1);
		}
		if ("create".equals(tableMode) || "update".equals(tableMode))
		{
			int type = "create".equals(tableMode) ? 0 : 1;
			Structure structure;
			Verify.notNull(dbType, "dbType不能为空，必须指定内容。当前支持：mysql,MariaDB");
			if (dbType.equals("mysql"))
			{
				structure = new MariaDBStructure();
			}
			else if (dbType.equals("MariaDB"))
			{
				structure = new MariaDBStructure();
			}
			else
			{
				throw new RuntimeException("暂不支持的数据库结构类型新建或者修改,当前支持：mysql,MariaDB");
			}
			try (Connection connection = dataSource.getConnection())
			{
				connection.setAutoCommit(false);
				for (DAOBean each : DaoFactory.getDaoBeans().values())
				{
					if (type == 0)
					{
						structure.createTable(connection, (DAOBeanImpl) each);
					}
					else
					{
						structure.updateTable(connection, (DAOBeanImpl) each);
					}
				}
				connection.commit();
				connection.setAutoCommit(true);
			}
			catch (Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
	
	@Override
	public SqlSession getCurrentSession()
	{
		return sessionLocal.get();
	}
	
	@Override
	public SqlSession openSession()
	{
		try
		{
			SqlSession session = new SqlSessionImpl(dataSource.getConnection(), this);
			return session;
		}
		catch (SQLException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void removeCurrentSession()
	{
		sessionLocal.remove();
	}
	
	public void setDataSource(DataSource dataSource)
	{
		this.dataSource = dataSource;
	}
	
	@Override
	public void setCurrentSession(SqlSession session)
	{
		sessionLocal.set(session);
	}
	
	public void setScanPackage(String scanPackage)
	{
		this.scanPackage = scanPackage;
	}
	
	public void setTableMode(String mode)
	{
		tableMode = mode;
	}
	
	public void setDbType(String dbType)
	{
		this.dbType = dbType;
	}
}
