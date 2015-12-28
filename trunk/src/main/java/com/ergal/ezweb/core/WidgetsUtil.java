package com.ergal.ezweb.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ergal.ezweb.context.HttpRequestProvider;
import com.ergal.ezweb.context.HttpResponseProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;


/**
 * 视图组件的配置工具类
 * 组件的绑定:
 * 		系统组件的绑定
 * 		用户页面的绑定
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class WidgetsUtil {
	WidgetsUtil(){
		
	}
	/**
	 * 获取用户的page的包 来产生module
	 */
	public static PagesBuilder configure(){
		//定义个package的集合
		final List<Package> packages = new ArrayList<Package>(); 
		//返回一个PagesBuilder
		return new PagesBuilder(){
			//添加一个page的package并返回PagesBuilder
			public PagesBuilder add(Package pack){
				packages.add(pack);
				return this;
			}
			//构建一个Module 在里面进行一些绑定
			public Module buildModule(){
				return new AbstractModule(){
					protected void configure(){
						//绑定上下文的request提供着
						bind(HttpServletRequest.class).toProvider(HttpRequestProvider.class);
						//绑定上下文的response提供着
						bind(HttpServletResponse.class).toProvider(HttpResponseProvider.class);
						//绑定Set<Package> 为这里定义好的制度的Package集合
						bind(new TypeLiteral<Set<Package>>(){}).toInstance(Collections.unmodifiableSet(new LinkedHashSet<Package>(packages)));
						//绑定ServletContext的Provider
						bind(ServletContext.class).toProvider(ServletContextProvider.class);
						//声明所有Map<String, WidgetProductLine> 这个泛型的是由 PageDesignBuilder来提供的
						bind(new TypeLiteral<Map<String, WidgetProductLine>>(){}).toProvider(PageDesignPaperBuilder.class);
						//重定向页面的提供者绑定
						//bind(new TypeLiteral<Map<String, WidgetProductLine>>(){}).toProvider(RedirectPageProvider.class);
						//早期完成绑定PageDesignPaperBuilder 向里面注入package
						bind(PageDesignPaperBuilder.class).asEagerSingleton();
						
					}
				};
			}
		};
	}
	
	public static interface PagesBuilder {
		PagesBuilder add(Package pack);
		Module buildModule();
	}
}
