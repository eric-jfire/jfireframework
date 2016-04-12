package com.jfireframework.rpc.client;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;

@SuppressWarnings("unchecked")
public class RpcFactory
{
	/**
	 * 使用接口方法创建一个代理配置类
	 * 
	 * @param interfaceClass
	 * @return
	 */
	public static <T> RpcProxyConfig<T> buildProxyConfig(Class<T> interfaceClass)
	{
		return new RpcProxyConfig<>(interfaceClass);
	}
	
	/**
	 * 使用代理名称，代理接口，服务器ip和端口生成一个分布式调用实例
	 * 
	 * @param proxyName
	 * @param interfaceClass
	 * @param ip
	 * @param port
	 * @return
	 */
	public static <T> T getProxy(String proxyName, Class<T> interfaceClass, String ip, int port)
	{
		BytecodeInvoker bytecodeInvoker = new BytecodeInvoker();
		bytecodeInvoker.setIp(ip);
		bytecodeInvoker.setPort(port);
		bytecodeInvoker.setProxyName(proxyName);
		return getProxy(interfaceClass, bytecodeInvoker);
	}
	
	protected static <T> T getProxy(Class<T> interfaceClass, BytecodeInvoker bytecodeInvoker)
	{
		try
		{
			bytecodeInvoker.build();
			ClassPool.doPruning = true;
			ClassPool classPool = ClassPool.getDefault();
			classPool.importPackage("com.jfireframework.rpc.client");
			classPool.insertClassPath(new ClassClassPath(interfaceClass));
			classPool.insertClassPath(new ClassClassPath(RpcFactory.class));
			CtClass targetCc = classPool.makeClass(interfaceClass.getSimpleName() + System.nanoTime() + Thread.currentThread().getName());
			CtClass interfacecc = classPool.get(interfaceClass.getName());
			targetCc.addInterface(interfacecc);
			CtClass fieldCtClass = classPool.get(BytecodeInvoker.class.getName());
			CtField ctField = new CtField(fieldCtClass, "invoker", targetCc);
			ctField.setModifiers(Modifier.PUBLIC);
			targetCc.addField(ctField);
			CtClass[] interfaces = new CtClass[interfacecc.getInterfaces().length + 1];
			interfaces[0] = interfacecc;
			System.arraycopy(interfacecc.getInterfaces(), 0, interfaces, 1, interfaces.length - 1);
			for (CtClass eachInterface : interfaces)
			{
				for (CtMethod each : eachInterface.getDeclaredMethods())
				{
					CtMethod targetMethod = new CtMethod(each.getReturnType(), each.getName(), each.getParameterTypes(), targetCc);
					if (each.getReturnType().equals(CtClass.voidType))
					{
						targetMethod.setBody("{Object result = invoker.invoke(\"" + each.getName() + "\",$args);}");
					}
					else
					{
						targetMethod.setBody("{Object result = invoker.invoke(\"" + each.getName() + "\",$args);return ($r)result;}");
					}
					targetCc.addMethod(targetMethod);
				}
			}
			/** 加入关闭链接的接口实现 */
			CtClass closeConnect = classPool.get(CloseConnect.class.getName());
			targetCc.addInterface(closeConnect);
			CtMethod ctMethod = new CtMethod(CtClass.voidType, "closeConnect", null, targetCc);
			ctMethod.setBody("{invoker.close();}");
			targetCc.addMethod(ctMethod);
			/* end */
			Object proxyObject = targetCc.toClass().newInstance();
			Field field = proxyObject.getClass().getDeclaredField("invoker");
			field.set(proxyObject, bytecodeInvoker);
			return (T) proxyObject;
		}
		catch (RuntimeException | NotFoundException | CannotCompileException | InstantiationException | IllegalAccessException | NoSuchFieldException e)
		{
			throw new RuntimeException("根据客户端接口创建实现类发生异常", e);
		}
	}
}
