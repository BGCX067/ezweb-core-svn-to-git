package com.ergal.ezweb.widgets;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import com.ergal.ezweb.core.PageWrapper;
import com.ergal.ezweb.core.RespStringWrapper;


/**
 * 循环迭代的渲染器 用法示例@Iterator(item=blogs, var="blog")
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class IteratorWidget implements Renderable {
	private final RenderChain renderChain;
	private final TextWidget textWidget;
	private final String item;
	private final String var;
	
	public IteratorWidget(String item, String var, RenderChain renderChain, TextWidget textWidget){
		this.renderChain = renderChain;
		this.textWidget = textWidget;
		this.item = item;
		this.var = var;
	}
	
	public void render(PageWrapper pageObj, RespStringWrapper Resp) {
		textWidget.render(pageObj, Resp);
		Object page = pageObj.getPage();
		Map<String, Iterable> fieldMapping = pageObj.getScopeFileds();
		String getMethodName = getGetMothodStringByFieldName(item);
		Method getMethod;
		try {
			getMethod = page.getClass().getMethod(getMethodName);
			//如果存在此方法
			if(getMethod!=null){
				Iterable itemObj = (Iterable)getMethod.invoke(page);
				pageObj.addItem(var, itemObj);
				
				for(Iterator it = itemObj.iterator();it.hasNext();){
					pageObj.saveCurrentScope(var, it.next());
					renderChain.render(pageObj, Resp);
				}
				
				return;
			}
			//如果不存在 就到map里找 并继续向map里添加
			
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * 获取set方法的字符串
	 * @param fieldName
	 * @return
	 */
	private String getSetMothodStringByFieldName(String fieldName){
		String fieldHead = fieldName.substring(0, 1);
		String fieldOther = fieldName.substring(1,fieldName.length());			
		//把首字母大写 并添加set
		String methodName = "set" + fieldHead.toUpperCase() + fieldOther;
		return methodName;
	}
	
	/**
	 * 获取get方法的字符串
	 * @param fieldName
	 * @return
	 */
	private String getGetMothodStringByFieldName(String fieldName){
		String fieldHead = fieldName.substring(0, 1);
		String fieldOther = fieldName.substring(1,fieldName.length());			
		//把首字母大写 并添加get
		String methodName = "get" + fieldHead.toUpperCase() + fieldOther;
		return methodName;
	}

}
