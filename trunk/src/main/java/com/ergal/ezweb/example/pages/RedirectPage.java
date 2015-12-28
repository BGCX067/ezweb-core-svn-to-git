package com.ergal.ezweb.example.pages;

import javax.servlet.http.HttpServletRequest;

import com.ergal.ezweb.annotation.Redirect;
import com.ergal.ezweb.annotation.ViewPath;
import com.google.inject.Inject;


/**
 * 重定向测试
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-27
 */
@ViewPath("/redirect")
@Redirect("/example/Redirect.html")
public class RedirectPage {
	
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
