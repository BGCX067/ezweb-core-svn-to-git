package com.ergal.ezweb.context;

import javax.servlet.http.HttpServletResponse;

import com.google.inject.Provider;

/**
 * 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-19
 */
public class HttpResponseProvider implements Provider<HttpServletResponse>{
	public HttpServletResponse get() {
		return EZWebContextManager.getResponse();
	}
}
