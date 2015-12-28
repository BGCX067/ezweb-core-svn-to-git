package com.ergal.ezweb.core;

import java.util.HashMap;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 
 * 处理url的工具类 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * modify date : 2009-3-11
 */
public class UriPatternMatcher {
	private static final Log log = LogFactory.getLog(UriPatternMatcher.class);
	
	private static String contextPath;	//上下问路径 例如:"/ezweb"
	
	public static String getContextPath() {
		return contextPath;
	}
	public static void setContextPath(String contextPath) {
		UriPatternMatcher.contextPath = contextPath;
	}
	public UriPatternMatcher(String contextPath){
		UriPatternMatcher.contextPath = contextPath;
	}
	
	/**
	 * 从请求获得请求的uri 去掉了应用上下文的
	 * @param request
	 * @return
	 */
	public static String getRequestUri(ServletRequest request){
		
		String reqFullUri = ((HttpServletRequest)request).getRequestURI();
		String reqOraUrl = getRequestUri(reqFullUri);
		String lastStr = reqOraUrl.substring(reqOraUrl.length()-1, reqOraUrl.length());
		if(!lastStr.equals("/"))
		{
			reqOraUrl += "/";
		}
		return reqOraUrl;
	}
	
	/**
	 * 取出请求的全部部分
	 * @param fullUri
	 * @return
	 */
	public static String getRequestUri(String fullUri){
		if(contextPath==null||contextPath.equals("")){
			return fullUri;
		}
		String[] subStr = fullUri.split(contextPath);
		String pathUri = null;
		if(subStr.length>=2){
			pathUri = subStr[1];
		}
		return pathUri;
	}
	
	/**
	 * 取出请求的uri参数值
	 * @param fullUri
	 * @return
	 */
	public static String getReqPara(HashMap<String, String> urlMapping, String fullUri){
		
		String paraStr = "";
		String pathUri = null;
		if(contextPath==null||contextPath.equals("")){
			pathUri = fullUri;
		}else{
			String[] subStr = fullUri.split(contextPath);
			
			if(subStr.length>=2){
				pathUri = subStr[1];
			}
		}
		
		//补全url 如果最后没有 "/"
		String lastStr = pathUri.substring(pathUri.length()-1, pathUri.length());
		if(!lastStr.equals("/"))
		{
			pathUri += "/";
		}
		//如果有完整uri匹配 则不需要取参数了
		if(urlMapping.containsKey(pathUri)){
			return null;
		}
		String[] uriSpiit = pathUri.split("/");
		paraStr = uriSpiit[uriSpiit.length-1];
		return paraStr;
	}
	
	/**
	 * 取出请求的uri参数值
	 * @param fullUri
	 * @return
	 */
	public static String getReqPara(String Requri){
		
		String paraStr = "";
		String pathUri = Requri;
		
		//补全url 如果最后没有 "/"
		String lastStr = pathUri.substring(pathUri.length()-1, pathUri.length());
		if(!lastStr.equals("/"))
		{
			pathUri += "/";
		}
		//如果有完整uri匹配 则不需要取参数了
		String[] uriSpiit = pathUri.split("/");
		if(uriSpiit.length>1){
			paraStr = uriSpiit[uriSpiit.length-1];
		}
		return paraStr;
	}
	
	/**
	 * ֻ只通过请求的url来获取相应的处理器
	 * @param urlMapping
	 * @param reqUri
	 * @return
	 */
	public static String getReqClassName(HashMap<String, String> urlMapping, String reqUri){
		//把url补全 最后加上"/"
		String lastStr = reqUri.substring(reqUri.length()-1, reqUri.length());
		if(!lastStr.equals("/"))
		{
			reqUri += "/";
		}
		String className = null;
		//首先查找有没有直接匹配的
		if(urlMapping.containsKey(reqUri)){
			className = urlMapping.get(reqUri);
			return className;
		}
		//看看是否是带参数的uri
		//将url分解 如果可以的话
		String[] subUri = reqUri.split("/");
		//如果可以分解成带参数的uri
		if(subUri.length >= 3){
			//取出最后一个
			String lastUri = subUri[subUri.length-1];
			lastUri += "/";
			//得到剩下的
			reqUri = reqUri.substring(0, reqUri.length()-lastUri.length());
			if(urlMapping.containsKey(reqUri)){
				className = urlMapping.get(reqUri);
				return className;
			}else{
				return null;
			}	
		}else{
			//至少不是带参数的 而且没有匹配的
			return null;
		}
	}
	
	/**
	 * 以请求的完整uri来查找相应的处理器
	 * @param urlMapping
	 * @param fullUri
	 * @return
	 */
	public static String getReqClassNameByFullUri(HashMap<String, String> urlMapping, String fullUri){
		String reqUri = UriPatternMatcher.getRequestUri(fullUri);
		return UriPatternMatcher.getReqClassName(urlMapping, reqUri);
	}
	
	/**
	 * 以请求查找相应的处理器
	 * @param urlMapping
	 * @param fullUri
	 * @return
	 */
	public static String getReqClassNameByRequest(HashMap<String, String> urlMapping, ServletRequest req){
		String reqUri = ((HttpServletRequest)req).getRequestURI();
		return UriPatternMatcher.getReqClassNameByFullUri(urlMapping, reqUri);
	}
	
	/**
	 * 从请求里获取参数
	 * @param request
	 * @return
	 */
	//TODO 这个有问题   现在是直接取最后的参数 如果都完全匹配就不取参数了
	public static String getReqParameterByRequest(HashMap<String, String> urlMapping, ServletRequest request){
		String fullReqUri =((HttpServletRequest)request).getRequestURI();
		return getReqPara( urlMapping, fullReqUri);
	}
}
