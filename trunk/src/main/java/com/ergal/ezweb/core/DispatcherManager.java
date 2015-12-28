package com.ergal.ezweb.core;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.utils.ClassUtil;
import com.google.inject.Injector;

/**
 * 核心的分发管理器 主要负责存储一些系统的参数和变量
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
public class DispatcherManager {
	private static final Log log = LogFactory.getLog(DispatcherManager.class);
	//核心的系统依赖注入器
	private static volatile Injector globalInjector = null;	
	//只能以原子的方式来更新ServletContext
	private static AtomicReference<ServletContext> servletContext = new AtomicReference<ServletContext>();	//储存ServletContext
	//设置web应用的根物理路径 
	private static String webRealPath = "";
	
	//TODO 修改掉  应该不是在这里做映射的事情 把url交给WidgetsFactory, WidgetsFactory里应该包含映射
	

	private static HashMap<String, String> pages; // 储存url和处理控制器对应的mapping集合
	private static String contextPath = ""; // 上下文路径
	
	private static String pagesPackageName;	//处理器所在的包的名字
	private static PageGenerator pageGenerator;
	private static String classLocationPath;	//处理类的物理路径
	
	
	
	/**
	 * 由WebStartupListener提供
	 * @param injector
	 */
	static void setInjector(Injector injector) {
        if (null != injector && null != globalInjector)
            throw new IllegalStateException("设置gucie 注入器失败, 请检查注入器配置");
        globalInjector = injector;
        }
	
        //2009-04-26 修改此方法为public 可以是系统底层进行调用
	public static Injector getInjector() {
	        return globalInjector;
	}
	
	public static ServletContext getServletContext() {
		return servletContext.get();
	}
	
	/**
	 * 设置ServletContext 由Filter来设置
	 * @param context
	 */
	public static void setServletContext(ServletContext context) {
		//设置ServletContext
		servletContext.set(context);
		//设置路径
		setContextPath(context.getContextPath());
		//给路径匹配器设置上下文路径
		UriPatternMatcher.setContextPath(context.getContextPath());
	}
	
	public static String getWebRealPath() {
		return webRealPath;
	}
	/**
	 * 设置web的根物理路径 由listener来初始化
	 * @param webRealPath
	 */
	public static void setWebRealPath(String webRealPath) {
		DispatcherManager.webRealPath = webRealPath;
	}
	
	
	
	public static String getClassLocationPath() {
		return classLocationPath;
	}

	public static void setClassLocationPath(String classLocationPath) {
		DispatcherManager.classLocationPath = classLocationPath;
	}

	public static String getPagesPackageName() {
		return pagesPackageName;
	}

	public static void setPagesPackageName(String packageName) {
		pagesPackageName = packageName;
	}

	public static String getContextPath() {
		return contextPath;
	}

	public static void setContextPath(String contextPathIn) {
		contextPath = contextPathIn;
	}

	

	public static HashMap<String, String> getPages() {
		return pages;
	}

	/*
	 * 初始化
	 */
	static void init() {

		// 找到所有的控制层的类 并将他们定义的url和类储存起来
		//Modules modules = new Modules();
		//String packageName = modules.getClass().getPackage().getName();
		//pagesPackageName = packageName;
		//classLocationPath = modules.getClass().getClassLoader().getResource("/").getPath();
		String[] classNames = ClassUtil.getPackageAllClassName(
				classLocationPath, pagesPackageName);
		// 处理取回来的类名
		pages = UrlMappingManager.getMapping(classNames, pagesPackageName);
		pageGenerator = new PageGenerator(pages);
		pageGenerator.setPagePackageName(pagesPackageName);
		PageGenerator.setPages(pages);
		if(globalInjector!=null){
			PageGenerator.setGlobalInjector(globalInjector);
		}
	}
	
	/**
	 * 处理请求的方法 
	 * @param req
	 * @param rep
	 * @param filterChain
	 */
	static void dispatch(ServletRequest req, ServletResponse rep,
			FilterChain filterChain) {
		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		HttpServletResponse httpServletResponse = (HttpServletResponse) rep;
		if(PageGenerator.getBaseContextPath().equals("")){
			PageGenerator.setBaseContextPath(httpServletRequest.getScheme()
					+"://"+httpServletRequest.getServerName()
					+":"+httpServletRequest.getServerPort()
					+httpServletRequest.getContextPath()+"/");
		}
		pageGenerator.service(httpServletRequest, httpServletResponse);
		try {
			if(!rep.isCommitted()){
				filterChain.doFilter(req, rep);
			}	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}	

	}
}
