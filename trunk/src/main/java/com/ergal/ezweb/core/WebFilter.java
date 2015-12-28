package com.ergal.ezweb.core;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.inject.Injector;



/**
 * 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class WebFilter implements Filter {
	
	private static final Log log = LogFactory.getLog(WebFilter.class);
	
	private DispatcherManager dispatcherManager;
	
	public void destroy() {
		// TODO 销毁 回收
		
	}
	
	public void init(FilterConfig filterConfig) throws ServletException {
		//从FilterConfig里获取ServletContext 并储存到DispatcherManager里
		ServletContext servletContext = filterConfig.getServletContext();
		DispatcherManager.setServletContext(servletContext);
		Injector injector = DispatcherManager.getInjector();
		if (null == injector){
			log.error("系统还没有初始化依赖注入器!!!");
		}
		//初始化页面生成器的一些配置
		injector.getInstance(PageDesignPaperBuilder.class).install();
		log.info("************* 初始化系统结束 !!*************");
	}
	
	public void doFilter(ServletRequest req, ServletResponse rep,
			FilterChain filterChain) throws IOException, ServletException {
		//获取注射器
		final Injector injector = DispatcherManager.getInjector();
		//获取一个WidgetsFactory的实例来处理请求
		injector.getInstance(WidgetsFactory.class).dispatch(req, rep, injector, filterChain);
	}

}
