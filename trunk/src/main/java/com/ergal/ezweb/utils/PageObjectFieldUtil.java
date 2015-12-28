package com.ergal.ezweb.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.ergal.ezweb.core.PageWrapper;


/**
 * 解析页面对象的工具类 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class PageObjectFieldUtil {
	public static Object getField(Object page, String s){
		String strToParse;
		//如果带有点
		if(s.contains(".")){
			String[] splitStr = s.split("[.]");
			String objStr = splitStr[0];	//对象字符串
			String varStr = splitStr[1];	//field字符串	
			strToParse = objStr;
		}else{
			strToParse = s;
		}
		//获取此field的get的方法
		String getMethodName = getGetMothodStringByFieldName(strToParse);
		Method getMethod;
		try {
			getMethod = page.getClass().getMethod(getMethodName);
			//如果存在此get方法
			if(getMethod!=null){
				//拿到此field的值
				Object fieldValue = getMethod.invoke(page);
				if(s.contains(".")){
					//如果是对象的对象
					String[] splitStr = s.split("[.]");
					String objStr = splitStr[0];	//对象字符串
					String varStr = splitStr[1];	//field字符串
					Object ObjfieldValue = getField(fieldValue, varStr);
					return ObjfieldValue;
				}else{
					//如果直接就取此对象
					return fieldValue;
				}
			}else{
				//针对page包装类
				//如果本对象里没有找到
				PageWrapper pageObj = (PageWrapper)page;
				Map<String, Object> currentFiled = pageObj.getCurrentFiled();
				Object currentObject = currentFiled.get(strToParse);
				if(s.contains(".")){
					//如果是对象的对象
					String[] splitStr = s.split("[.]");
					String objStr = splitStr[0];	//对象字符串
					String varStr = splitStr[1];	//field字符串
					Object ObjfieldValue = getField(currentObject, varStr);
					return ObjfieldValue;
				}else{
					//如果直接就取此对象
					return currentObject;
				}
			}
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
		return null;
	}
	
	
	/**
	 * 获取set方法的字符串
	 * @param fieldName
	 * @return
	 */
	public static String getSetMothodStringByFieldName(String fieldName){
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
	public static String getGetMothodStringByFieldName(String fieldName){
		String fieldHead = fieldName.substring(0, 1);
		String fieldOther = fieldName.substring(1,fieldName.length());			
		//把首字母大写 并添加get
		String methodName = "get" + fieldHead.toUpperCase() + fieldOther;
		return methodName;
	}
}
