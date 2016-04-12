package link.jfire.simplerpc;

import java.util.HashMap;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import com.jfire.test.rule.CustomRule;
import com.jfire.test.rule.MutiThreadTest;
import com.jfireframework.baseutil.simplelog.ConsoleLogFactory;
import com.jfireframework.baseutil.simplelog.Logger;
import com.jfireframework.rpc.client.RpcFactory;
import com.jfireframework.rpc.exception.NoSuchMethodException;
import com.jfireframework.rpc.server.RcConfig;
import com.jfireframework.rpc.server.RcServer;
import link.jfire.simplerpc.data.ComplexOPbject;

public class FunctionTest
{
	private static RcServer	rcServer;
	private Logger			logger	= ConsoleLogFactory.getLogger();
	@Rule
	public CustomRule		rule	= new CustomRule();
	
	@BeforeClass
	public static void before()
	{
		RcConfig rcConfig = new RcConfig();
		rcConfig.setPort(1688);
		Map<String, Object> tmp = new HashMap<>();
		tmp.put("print", new PrintImpl());
		rcConfig.setImplMap(tmp);
		rcServer = new RcServer(rcConfig);
		rcServer.start();
	}
	
	@AfterClass
	public static void after()
	{
		rcServer.stop();
	}
	
	@Test
	public void methodWithoutReturn()
	{
		Print print = RpcFactory.getProxy("print", Print.class, "127.0.0.1", 1688);
		print.methodWithoutReturn("没有方法返回值");
	}
	
	@Test
	public void methodWithReturn()
	{
		Print print = RpcFactory.getProxy("print", Print.class, "127.0.0.1", 1688);
		String result = print.methodWithReturn("有方法返回值");
		Assert.assertEquals("有方法返回值追加的末尾信息", result);
	}
	
	Print print = RpcFactory.buildProxyConfig(Print.class).setProxyName("print").setIp("127.0.0.1").setPort(1688).getProxy();
	
	@Test
	public void returnComplexOPbject()
	{
		ComplexOPbject param = new ComplexOPbject();
		param.setAge(12);
		param.setName("林斌");
		param.setSex(2);
		print.returnComplexOPbject(param);
	}
	
	@Test
	public void wrongMethodName()
	{
		Print2 print2 = RpcFactory.getProxy("print", Print2.class, "127.0.0.1", 1688);
		try
		{
			print2.methodNotExist();
		}
		catch (Exception e)
		{
			e.getCause().printStackTrace();
			Assert.assertTrue(e.getCause().getCause().getCause() instanceof NoSuchMethodException);
		}
	}
	
	// @Ignore
	@Test
	@MutiThreadTest(repeatTimes = 20, threadNums = 80)
	// @RepeatTest(1000000)
	public void testFotTimes() throws InterruptedException
	{
		ComplexOPbject param = new ComplexOPbject();
		param.setAge(12);
		param.setName("林斌");
		param.setSex(2);
		print.returnComplexOPbject(param);
		// ((CloseConnect) print).closeConnect();
		// Thread.sleep(400);
	}
}
