package com.ergal.ezweb.example.pages;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.ergal.ezweb.annotation.P2PScope;
import com.ergal.ezweb.annotation.SessionScopeLoad;
import com.ergal.ezweb.annotation.SessionScopeSave;
import com.ergal.ezweb.annotation.ViewHtml;
import com.ergal.ezweb.annotation.ViewPath;
import com.google.inject.Inject;

@ViewPath("/article")
@ViewHtml("/example/Article.html")
public class Article {
	private String article_title = "文章标题";
	private String article_content = "文章内容";
	
	@P2PScope
	private String message;
	
	@Inject
	HttpServletRequest request;
	
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	@SessionScopeSave
	@SessionScopeLoad
	private int count;
	
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getArticle_title() {
		return article_title;
	}
	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}
	public String getArticle_content() {
		return article_content;
	}
	public void setArticle_content(String article_content) {
		this.article_content = article_content;
	}

	public String service(String para){
		if(para.equals("error")){
			this.setArticle_title("错误的题目");
			this.setArticle_content("错误的内容");
			return null;
		}else if(para.equals("blog")){
			this.message = "return to new page----> blog page";
			return "/blog/";
		}
		else if(para.equals("redirect")){
			this.message = "the message is redirect";
			return "/redirect/";
		}else if(para.equals("forward")){
			this.message = "the message is forward";
			return "/forward/";
		}else if(para.equals("cookie")){
			Cookie[] cookies = request.getCookies();
			System.out.println(request.getHeader("User-Agent"));
			return null;
		}else{
			this.count += 1;
			this.setArticle_title(this.getArticle_title() + para);
			this.setArticle_content(this.getArticle_content() + para);
			return null;
		}
	}
	
	public String service(){
		return null;
	}
}
