package com.ergal.ezweb.core;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;

/**
 * 定义了视图页面的生产线和生产方案 下面的工作就是 把页面的设计方案交给页面生产线 让其产生定制的页面视图
 * 
 * 能够处理传来的url 找到相应的页面生产线
 * 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
@Singleton
public class WidgetsFactory {
	// 一个产品线的集合
	private final Provider<Map<String, WidgetProductLine>> productlineMapping;
	//文件上传组件
	private final FileItemFactory fileFactory;
	//重定向的集合
	//private final Provider<Map<String, WidgetProductLine>> RedirectPagesProvider;

	@Inject
	public WidgetsFactory(
			Provider<Map<String, WidgetProductLine>> productlineMapping, FileItemFactory fileFactory) {
		this.productlineMapping = productlineMapping;
		this.fileFactory = fileFactory;
	}

	/**
	 * 进行一些必要的初始化
	 */
	public void init() {
		// TODO 初始化工厂
	}

	/**
	 * 所有的处理都是从这里开始 从传来的url 来找到相应的页面生产线EzwebWidgetProductLine的实现
	 */
	public void dispatch(ServletRequest req, ServletResponse rep,
			Injector injector, FilterChain filterChain) {
		// 实现处理请求
		// 将请求进行解析 3中情况 带参数的uri 不带参数的uri 和完全不匹配的请求
		// 解析出uri
		String url = UriPatternMatcher.getRequestUri(req);
		
		//Map<String, WidgetProductLine> redirectPages = RedirectPagesProvider.get();
		/*
		//重定向的分发
		if(redirectPages.containsKey(url)){
			//HttpServletResponse response = (HttpServletResponse)rep;
			HttpServletRequest request = (HttpServletRequest)req;
			try {
				//重定向到新的地方
				//response
				request.getRequestDispatcher(redirectPages.get(url)).forward(req, rep);
				response.sendRedirect(redirectPages.get(url));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ServletException e) {
				e.printStackTrace();
			}
		}else{*/
		Map<String, WidgetProductLine> productlines = productlineMapping.get();
		if (productlines.containsKey(url)) {
			WidgetProductLine productline = productlines.get(url);
			//设置视图对象的集合
			productline.setProductlines(productlines);
			//productline.setRedirectPages(redirectPages);
			productline.produce(req, rep, injector, url, null, fileFactory);
		} else {
			// 如果直接没有找到 就看是否是带参数的uri
			String para = UriPatternMatcher.getReqPara(url);
			String uriPre = url.split(para)[0];
			if (productlines.containsKey(uriPre)) {
				WidgetProductLine productline = productlines.get(uriPre);
				productline.setProductlines(productlines);
				//productline.setRedirectPages(redirectPages);
				productline.produce(req, rep, injector, url, para, fileFactory);
			}
		}
		

		try {
			if (!rep.isCommitted()) {
				filterChain.doFilter(req, rep);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServletException e) {
			e.printStackTrace();
		}
	}
}
