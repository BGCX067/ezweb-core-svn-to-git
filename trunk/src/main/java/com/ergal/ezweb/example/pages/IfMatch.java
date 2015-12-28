package com.ergal.ezweb.example.pages;

import com.ergal.ezweb.annotation.ViewHtml;
import com.ergal.ezweb.annotation.ViewPath;


/**
 * 判断标签的示例
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */

@ViewPath("/ifmatch")
@ViewHtml("/example/IfMatch.html")
public class IfMatch {
	private boolean showit = true;
	private String aaa = "test";
	private String bbb = "test";
	private String ccc = "test2";
	private String message1 = "showit is true";
	private String message2 = "aaa equals bbb";
	private String message3 = "aaa equals ccc";
	public String getMessage3() {
		return message3;
	}
	public void setMessage3(String message3) {
		this.message3 = message3;
	}
	public boolean getShowit() {
		return showit;
	}
	public void setShowit(boolean showit) {
		this.showit = showit;
	}
	public String getAaa() {
		return aaa;
	}
	public String getCcc() {
		return ccc;
	}
	public void setCcc(String ccc) {
		this.ccc = ccc;
	}
	public void setAaa(String aaa) {
		this.aaa = aaa;
	}
	public String getBbb() {
		return bbb;
	}
	public void setBbb(String bbb) {
		this.bbb = bbb;
	}
	public String getMessage1() {
		return message1;
	}
	public void setMessage1(String message1) {
		this.message1 = message1;
	}
	public String getMessage2() {
		return message2;
	}
	public void setMessage2(String message2) {
		this.message2 = message2;
	}
	
	public Object service(String para){
		return null;
	}
	
	public Object service(){
		return null;
	}
}
