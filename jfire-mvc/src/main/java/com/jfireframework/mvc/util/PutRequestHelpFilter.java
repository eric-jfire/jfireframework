package com.jfireframework.mvc.util;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import com.jfireframework.mvc.interceptor.impl.DataBinderInterceptor;

/**
 * 针对put请求的辅助类。可以用这个类来过滤这种请求并且手动解析内容体。不过只适合urlencode形式的参数。如果是有上传的就没办法了。
 * 
 * @author 林斌
 *
 */
public class PutRequestHelpFilter implements Filter
{
	private Charset charset = Charset.forName("utf-8");
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest rq = (HttpServletRequest) request;
		if (rq.getMethod().equals("PUT"))
		{
			request.setCharacterEncoding("utf-8");
			byte[] src = new byte[rq.getContentLength()];
			rq.getInputStream().read(src);
			request.setAttribute(DataBinderInterceptor.DATABINDERKEY, URLDecoder.decode(new String(src, charset), "utf-8"));
			chain.doFilter(request, response);
		}
		else
		{
			chain.doFilter(request, response);
		}
	}
	
	@Override
	public void destroy()
	{
		// TODO Auto-generated method stub
		
	}
	
}
