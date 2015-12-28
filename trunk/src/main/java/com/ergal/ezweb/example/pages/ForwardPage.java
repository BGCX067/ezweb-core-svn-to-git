package com.ergal.ezweb.example.pages;

import javax.servlet.http.HttpServletRequest;

import com.ergal.ezweb.annotation.Forward;
import com.ergal.ezweb.annotation.ViewPath;
import com.google.inject.Inject;

/**
 * forward≤‚ ‘
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-28
 */
@ViewPath("/forward")
@Forward("/example/Forward.jsp")
public class ForwardPage {
	@Inject
	private HttpServletRequest request;
	
	private String message;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	public String service(){
		request.setAttribute("message", message);
		return null;
	}
	
	public String service(String para){
		return null;
	}
}
