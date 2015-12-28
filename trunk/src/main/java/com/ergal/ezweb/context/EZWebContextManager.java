package com.ergal.ezweb.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * 上下文相关的统一设置和获取
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-19
 */
public class EZWebContextManager {
	static final ThreadLocal<HttpContext> httpContext = new ThreadLocal<HttpContext>();
	
	private EZWebContextManager(){
		
	}
	/**
	 * 设置Context
	 * @param request
	 * @param response
	 */
	public static void setContext(HttpServletRequest request,
			HttpServletResponse response) {
		httpContext.set(new HttpContext(request, response));
	}
	
	/**
	 * 清理
	 */
	public static void clearContext() {
		httpContext.remove();
		/*currentRestUri.remove();
		velocityContext.remove();
		freemarkderContext.remove();
		GuiceContext.getInstance().closePersistenceContext();*/
	}
	
	/**
	 * 获取request
	 * @return
	 */
	public static HttpServletRequest getRequest() {
		HttpContext context = httpContext.get();
		if (null == context) {
			throw new RuntimeException(
					"Cannot access scoped object. It appears we"
							+ " are not currently inside an HTTP Servlet request");
		}

		return context.getRequest();
	}
	
	/**
	 * 获取response
	 * @return
	 */
	public static HttpServletResponse getResponse() {
		HttpContext context = httpContext.get();
		if (null == context) {
			throw new RuntimeException(
					"Cannot access scoped object. It appears we"
							+ " are not currently inside an HTTP Servlet request");
		}

		return context.getResponse();
	}

	/**
	 * 内部类 存放会话和请求 响应等等
	 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
	 *
	 */
	static class HttpContext {
		final HttpServletRequest request;
		final HttpServletResponse response;
		//final ModelMap modelMap;

		HttpContext(HttpServletRequest request, HttpServletResponse response) {//ModelMap param) {
			this.request = request;
			this.response = response;
			//this.modelMap = param;
		}

		HttpServletRequest getRequest() {
			return request;
		}

		HttpServletResponse getResponse() {
			return response;
		}

		/*public ModelMap getModelMap() {
			return modelMap;
		}*/
	}
}
