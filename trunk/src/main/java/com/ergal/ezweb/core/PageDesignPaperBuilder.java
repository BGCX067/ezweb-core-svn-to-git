package com.ergal.ezweb.core;

import static com.google.inject.matcher.Matchers.annotatedWith;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.annotation.Forward;
import com.ergal.ezweb.annotation.Redirect;
import com.ergal.ezweb.annotation.ViewHtml;
import com.ergal.ezweb.annotation.ViewPath;
import com.ergal.ezweb.utils.ClassUtil;
import com.ergal.ezweb.utils.TemplateLoader;
import com.ergal.ezweb.widgets.Renderable;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.matcher.Matcher;


/**
 * 提供pagedesign的Provider
 * 单例模式
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
@Singleton
public class PageDesignPaperBuilder implements Provider<Map<String, WidgetProductLine>>{
	private static final Log log = LogFactory.getLog(PageDesignPaperBuilder.class);
	
	private final HtmlParser  parse;
	//符合视图annotation的匹配器定义
	private final Matcher<? super Class<?>> matcher = annotatedWith(ViewHtml.class).or(annotatedWith(ViewPath.class));
	//只取视图类 所以重新定义一个集合
	private final Set<Class<?>> pageClasses = new LinkedHashSet<Class<?>>();
	//视图和url的映射集合
	private Map<String, Class<?>> pageMapping = new HashMap<String, Class<?>>();
	//url和生产线的集合
	private Map<String, WidgetProductLine> productLines = new HashMap<String, WidgetProductLine>();
	//重定向集合
	//private static Map<String, WidgetProductLine> redirectPages = new HashMap<String, WidgetProductLine>();
	//隐式跳转集合集合
	//private static Map<String, WidgetProductLine> forwardPages = new HashMap<String, WidgetProductLine>();
	
	//定义一个读取模板的读取类
	private final TemplateLoader loader;
	
	private Set<Package> packages;	//需要注入的包含所有视图的包
	
	@Inject
	public PageDesignPaperBuilder(Set<Package> packages, TemplateLoader loader, HtmlParser parse){
		this.packages = packages;
		this.loader = loader;
		this.parse = parse;
	}
	
	public Set<Package> getPackages() {
		return packages;
	}
	public void setPackages(Set<Package> packages) {
		this.packages = packages;
	}
	
	/**
	 * 实现Guice的provider的get方法
	 */
	public Map<String,WidgetProductLine> get(){
		return this.productLines;
	}
	
	/**
	 * 获取所有重定向的页面
	 * @return
	 */
	/*public static Map<String, WidgetProductLine> getRedriectPages(){
		return redirectPages;
	}*/
	
	/**
	 * 获取所有隐式跳转的页面
	 * @return
	 */
	/*public static Map<String, WidgetProductLine> getForwardPages(){
		return forwardPages;
	}*/
	
	/*public Map<String, Class<?>> get(){
		//实现返回一个EzwebWidgetProductLine的集合
		return this.pageMapping;
	}*/
	
	/**
	 * 取出所有的类 如果是视图page类 就组装一个PageDesignPaper的集合 
	 */
	public PageDesignPaperBuilder install(){
		log.info("******************** 开始扫描用户定义的视图类 ********************");
		this.scanViewPackages();
		this.initViewClasses();
		log.info("******************** 扫描用户定义的视图类结束 ********************");
		return this;
	}
	
	/**
	 * 循环这个package的集合
	 */
	public void scanViewPackages(){
		for(Package pack : packages){
			//获取到所有的视图类
			Set<Class<?>> classes = ClassUtil.getClasses(pack);
			//循环所有类
			for(Class<?> clazz : classes){
				if(matcher.matches(clazz)){
					//如果有视图定义的一些annotation 就添加到视图集合中
					pageClasses.add(clazz);
					log.info("添加自定义视图类: "+clazz.getName());
				}
			}
		}
	}
	
	/**
	 * 初始化渲染的流程类
	 */
	public void initViewClasses(){
		for(Class<?> pageClass : pageClasses){
			//获取ViewPath的参数
			ViewPath viewPath = (ViewPath) pageClass.getAnnotation(ViewPath.class);
			//获取重定向的路径
			Redirect redirectPath = (Redirect) pageClass.getAnnotation(Redirect.class);
			//获取隐式跳转的路径
			Forward forwardPath = (Forward)pageClass.getAnnotation(Forward.class);
			if(viewPath!=null){
				String classViewPath = viewPath.value();
				//如果不是以"/"结尾 自动添加上"/"
				String lastStr = classViewPath.substring(classViewPath.length()-1, classViewPath.length());
				if(!lastStr.equals("/"))
				{
					classViewPath += "/";
				}
				//分别交给三种方式去处理
				if(redirectPath!=null){
					//获取重定向的页面的路径 并存到hashmap中
					//String redirectPathString = redirectPath.value();
					productLines.put(classViewPath, new WidgetProductLine(classViewPath, null, pageClass, 1));
				}else if(forwardPath != null){
					productLines.put(classViewPath, new WidgetProductLine(classViewPath, null, pageClass, 2));
				}else{
					//扫描视图类产生渲染设计链 并获取一个集合
					final Renderable widgetChain = parse.parse(loader.load(pageClass));
					PageDesignPaper pageDesignPaper = new PageDesignPaper(widgetChain);
					productLines.put(classViewPath, new WidgetProductLine(classViewPath, pageDesignPaper, pageClass, 0));
				}
				
			}
			
		}
	}
	
}
