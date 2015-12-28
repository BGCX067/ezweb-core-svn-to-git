package com.ergal.ezweb.core;


/**
 * 响应的字符串包装 
 * 如果直接用字符串 字符串因为引用的问题 不会改变
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class RespStringWrapper {
	private final StringBuilder out = new StringBuilder();
	public void write(String text) {
        out.append(text);
    }
	public void write(char c) {
        out.append(c);
    }
	
	public String getStr(){
		return out.toString();
	}
}
