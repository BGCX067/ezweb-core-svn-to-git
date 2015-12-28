package com.ergal.ezweb.context;

import javax.servlet.http.HttpServletRequest;

import com.google.inject.Provider;

/**
 * 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-19
 */
public class HttpRequestProvider implements Provider<HttpServletRequest> {

	public HttpServletRequest get() {
		return EZWebContextManager.getRequest();
	}
}