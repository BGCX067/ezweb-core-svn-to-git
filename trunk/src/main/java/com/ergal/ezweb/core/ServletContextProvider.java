package com.ergal.ezweb.core;

import javax.servlet.ServletContext;

import com.google.inject.Provider;

/**
 * 提供ServletContext的Provider
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class ServletContextProvider implements Provider<ServletContext> {
	public ServletContext get() {
        return DispatcherManager.getServletContext();
	}
}
