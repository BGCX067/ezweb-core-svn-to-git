package com.ergal.ezweb.core;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.annotation.ViewHtml;
import com.google.inject.Injector;



/**
 * 视图 生成器
 * @deprecated
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
public class PageGenerator {
	private static final Log log = LogFactory.getLog(PageGenerator.class);
	private static HashMap<String, String> pages; // 储存url和处理控制器对应的mapping集合
	private static String baseWebFilePath = "";	//web的根物理路径
	private static String pagePackageName = "";	//存放page类的包名
	private static Injector globalInjector = null;
	private static String baseContextPath = ""; //应用的上下文path
	
	public static String getBaseContextPath() {
		return baseContextPath;
	}

	public static void setBaseContextPath(String baseContextPath) {
		PageGenerator.baseContextPath = baseContextPath;
	}

	static Injector getGlobalInjector() {
		return globalInjector;
	}

	static void setGlobalInjector(Injector globalInjector) {
		PageGenerator.globalInjector = globalInjector;
	}

	public static String getPagePackageName() {
		return pagePackageName;
	}

	public static void setPagePackageName(String pagePackageName) {
		PageGenerator.pagePackageName = pagePackageName;
	}

	public static String getBaseWebFilePath() {
		return baseWebFilePath;
	}

	public static void setBaseWebFilePath(String baseWebFilePath) {
		PageGenerator.baseWebFilePath = baseWebFilePath;
	}

	public static HashMap<String, String> getPages() {
		return pages;
	}

	public static void setPages(HashMap<String, String> pages) {
		PageGenerator.pages = pages;
	}
	
	public PageGenerator(HashMap<String, String> pages){
		this.pages = pages;
	}
	
	/**
	 * 处理url 找到合适的Page去处理
	 * @param uri
	 */
	public static void service(ServletRequest req, ServletResponse rep){
		//从mapping里查找相关的类名
		String className = UriPatternMatcher.getReqClassNameByRequest(pages, req);
		HashSet<String> filedSet = new HashSet<String>();
		if(className==null){
			return;
		}
		String[] classNames = className.split("[.]");
		//得到完整类名
		String fullClassName = pagePackageName +"." + classNames[0];
		try {
			//获取实例
			Object pageObj = Class.forName(fullClassName).newInstance();
			if(globalInjector!=null){
				globalInjector.injectMembers(pageObj);
			}
			Method[] methods = pageObj.getClass().getMethods();
			//找到所有的成员变量 如果请求里的任何范围里有此变量 就set进去 如果
			HttpServletRequest httpServletRequest = (HttpServletRequest)req;
			httpServletRequest.setCharacterEncoding("UTF-8");
			HttpSession session = httpServletRequest.getSession();
			//拿到page里的field集合
			Field[] fileds = pageObj.getClass().getDeclaredFields();
			//存到hashset里
			for(int i = 0; i < fileds.length; i++){
				String fieldName = fileds[i].getName();
				filedSet.add(fieldName);
			}
			//获取request的所有parameter参数值 并储存起来
			HashSet<String> reqParameters = getAllReqParameter(httpServletRequest);
			//将参数储存到对象里
			setPageBeanPara(pageObj, filedSet, reqParameters, httpServletRequest);
			String htmlFileString = null;
			//取出原始的模板页面
			if(pageObj.getClass().getAnnotation(ViewHtml.class)!=null){
				String htmlPageName = pageObj.getClass().getAnnotation(ViewHtml.class).value();
				String htmlFileName = getHtmlFullFileName(htmlPageName);
				//拿到html的文本内容
				htmlFileString = HtmlParser.getHtml(htmlFileName);
			}
			
			//取出request的后缀参数
			String reqPara = UriPatternMatcher.getReqParameterByRequest(pages, req);
			
			//根据情况调用service方法
			Object returnPage = null;
			if(reqPara==null){
				//调用基本service方法
				String serviceMethodName = "service";
				try {
					Method serviceMethod = pageObj.getClass().getMethod(serviceMethodName);
					if(serviceMethod != null){
						//调用service方法 并查看返回的对象
						returnPage = serviceMethod.invoke(pageObj);
						
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}else{
				//调用带参数的service方法
				String serviceMethodName = "service";
				try {
					Method serviceMethod = pageObj.getClass().getMethod(serviceMethodName, String.class);
					if(serviceMethod != null){
						returnPage = serviceMethod.invoke(pageObj, reqPara);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
			//如果对象为空,将结束处理page的链, 直接输出视图
			if(returnPage == null){
			//根据page对象里的field 替换模板字符串里的变量值
				if(htmlFileString!=null){
					String outputHtmlStr = getOutputHtmlStr(htmlFileString, fileds, pageObj);
					writeOut(outputHtmlStr, rep);
				}
			}else{
				//如果对象不为空 则表示有另一个链上的视图需要处理 继续跳转处理下一个页面
				String outputHtmlStr = doChainPage(returnPage);
				writeOut(outputHtmlStr, rep);
			}
			//TODO 然后输出到客户端
			
			
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			log.error("处理page类是出错,加载相关类错误");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取html文件的全路径
	 * @param nameFromAnnotation
	 * @return
	 */
	static String getHtmlFullFileName(String nameFromAnnotation){
		String[] tempStrs = nameFromAnnotation.split("/");
		String htmlFileName = baseWebFilePath;
		for(int i = 0; i < tempStrs.length; i++){
			if(!tempStrs[i].equals("")){
				htmlFileName = htmlFileName + File.separator + tempStrs[i];
			}
		}
		return htmlFileName;
	}
	
	/**
	 * 获取request的parameter的参数名的集合
	 * @param request
	 * @return
	 * @throws IOException 
	 */
	public static HashSet<String> getAllReqParameter(HttpServletRequest request) throws IOException{
		HashSet<String> reqparas = new HashSet<String>();
		Enumeration enu = request.getParameterNames();  
        while(enu.hasMoreElements()){  
            String paraName = (String)enu.nextElement();  
            String paraValue = request.getParameter(paraName);
            reqparas.add(paraName);
        } 
		return reqparas;
	}
	
	/**
	 * 获取set方法的字符串
	 * @param fieldName
	 * @return
	 */
	public static String getSetMothodStringByFieldName(String fieldName){
		String fieldHead = fieldName.substring(0, 1);
		String fieldOther = fieldName.substring(1,fieldName.length());			
		//把首字母大写 并添加set
		String methodName = "set" + fieldHead.toUpperCase() + fieldOther;
		return methodName;
	}
	
	/**
	 * 获取get方法的字符串
	 * @param fieldName
	 * @return
	 */
	public static String getGetMothodStringByFieldName(String fieldName){
		String fieldHead = fieldName.substring(0, 1);
		String fieldOther = fieldName.substring(1,fieldName.length());			
		//把首字母大写 并添加get
		String methodName = "get" + fieldHead.toUpperCase() + fieldOther;
		return methodName;
	}
	
	/**
	 * 根据获取到的request的参数的值 去查找field 如果存在 就调用set方法去初始化page的javabean
	 * @param page
	 * @param fields
	 * @param reqParas
	 * @param request
	 */
	public static void setPageBeanPara(Object page, HashSet<String> fields, HashSet<String> reqParas, HttpServletRequest request){
		//从request的参数里取参数名称
		for(Iterator it = reqParas.iterator(); it.hasNext();){
			String paraName = (String)it.next();
			//看看对象的field里有没有
			if(fields.contains(paraName)){
				//如果存在此参数名 set进去
				String methodName = getSetMothodStringByFieldName(paraName);
				try {
					Method setMethod = page.getClass().getMethod(methodName, String.class);
					setMethod.invoke(page, request.getParameter(paraName));
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					log.error("找不到此参数相关的 set 方法");
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 替换输入的html里的表达式的内容
	 * @param orgHtmlStr
	 * @param fields
	 * @param page
	 * @return
	 */
	public static String getOutputHtmlStr(String orgHtmlStr, Field[] fields, Object page){
		String outputHtmlStr = orgHtmlStr;
		outputHtmlStr = outputHtmlStr.replaceAll("@BaseContextPath", baseContextPath);
		for(int i = 0; i < fields.length; i++){
			Field field = fields[i];
			String fieldName = field.getName();
			//将field拼写成表达式
			String head = "\\u0024\\u007B";
			String end = "\\u007D";
			String expression = head + fieldName + end;
			//如果包含此field
			int indexOfField = orgHtmlStr.indexOf("${"+fieldName+"}");
			if(indexOfField != -1){
				//拿到get方法的字符串
				String getMethodName = getGetMothodStringByFieldName(fieldName);
				try {
					Method getMethod = page.getClass().getMethod(getMethodName);
					String fieldValue = (String)getMethod.invoke(page);
					//将此值替换进去
					outputHtmlStr = outputHtmlStr.replaceAll(expression, fieldValue);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return outputHtmlStr;
	}
	
	/**
	 * 输出页面流
	 * @param outStr
	 * @param response
	 */
	public static void  writeOut(String outStr, ServletResponse response){
		HttpServletResponse httpServletResponse = (HttpServletResponse)response;
		response.setCharacterEncoding("utf-8");   
		response.setContentType("text/html; charset=utf-8"); 
		if(!httpServletResponse.isCommitted()){
			try {
				PrintWriter out = httpServletResponse.getWriter();
				out.print(outStr);
				out.flush();
			} catch (IOException e) {
				log.error("输入页面的时候发生IO异常");
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * 在处理页面的链里 处理第二个开始的链上的视图对象
	 * 由于从第二个开始的 不需要去运用第一步的机制去设置参数 所以可以直接调用此对象的service方法 来判断返回类型 
	 * 如果返回值不为空 则继续处理下一个 如果返回值为空 则可以刷新视图进行输出了
	 * @param pageObj
	 * @param req
	 * @param rep
	 * @return
	 */
	public static String doChainPage(Object pageObj){
		if(globalInjector!=null){
			globalInjector.injectMembers(pageObj);
		}
		Object returnPage = null;
		String outputHtmlStr = null;
		try {
			//获取service方法
			String serviceMethodName = "service";
			Method serviceMethod = pageObj.getClass().getMethod(serviceMethodName);
			//只调用其service方法 不用传递参数 参数可以在javabean里自行设置来传递 所以不调用service的带参数表的方法
			if(serviceMethod != null){
				//调用service方法 并查看返回的对象
				returnPage = serviceMethod.invoke(pageObj);		
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		if(returnPage == null){
			//输出视图
			//取出原始的模板页面
			String htmlPageName = pageObj.getClass().getAnnotation(ViewHtml.class).value();
			String htmlFileName = getHtmlFullFileName(htmlPageName);
			//拿到html的文本内容
			String htmlFileString = HtmlParser.getHtml(htmlFileName);
			Field[] fileds = pageObj.getClass().getDeclaredFields();
			outputHtmlStr = getOutputHtmlStr(htmlFileString, fileds, pageObj);
			}else{
				//继续传递到chain的下一个页面视图对象来处理
				doChainPage(returnPage);
			}
		return outputHtmlStr;
	}
	
}
