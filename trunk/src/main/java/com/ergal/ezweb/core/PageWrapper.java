package com.ergal.ezweb.core;

import java.util.HashMap;
import java.util.Map;


/**
 * 用户视图的包装类
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 */
public class PageWrapper {
	private final Object page;
	private final Map<String, Iterable> scopeFileds = new HashMap<String, Iterable>();
	private final Map<String, Object> currentFiled = new HashMap<String, Object>();
	
	public PageWrapper(Object page){
		this.page = page;
	}
	public Object getPage() {
		return page;
	}
	public Map<String, Iterable> getScopeFileds() {
		return scopeFileds;
	}
	public void addItem(String key, Iterable value){
		scopeFileds.put(key, value);
	}
	
	/**
	 * 获取当前的field对象
	 * @return
	 */
	public Map<String, Object> getCurrentFiled(){
		return currentFiled;
	}
	
	/**
	 * 把当前要处理的写进去
	 * @param key
	 * @param obj
	 */
	public void saveCurrentScope(String key, Object obj){
		if(currentFiled.containsKey(key)){
			currentFiled.remove(key);
			currentFiled.put(key, obj);
		}else{
			currentFiled.put(key, obj);
		}
	}
}
