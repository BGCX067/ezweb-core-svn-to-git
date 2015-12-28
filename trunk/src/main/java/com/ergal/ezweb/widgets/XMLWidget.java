package com.ergal.ezweb.widgets;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import com.ergal.ezweb.core.PageWrapper;
import com.ergal.ezweb.core.RespStringWrapper;


/**
 * xml的渲染器
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class XMLWidget implements Renderable {
	
	private final String elementName;	//元素的名字
	private final Map<String, String> attributes;	//元素里面的属性键值对
	private final RenderChain renderChain;
	public XMLWidget(String elementName, Map<String, String> attributes, RenderChain renderChain){
		this.elementName = elementName;
		this.attributes = attributes;
		this.renderChain = renderChain;
	}
	
	public void render(PageWrapper pageObj, RespStringWrapper Resp) {
		// TODO Auto-generated method stub
		Resp.write("<"+elementName);
		if(attributes.size()!=0){
			Resp.write(" ");
			for(Map.Entry<String, String> attribute : attributes.entrySet()){
				String aName = attribute.getKey();
				String aValue = attribute.getValue();
				aValue = replaceFiled(aValue, pageObj);
				Resp.write(aName);
				Resp.write("=\"");
				Resp.write(aValue);
				Resp.write("\"");
				Resp.write(" ");
			}
		}
		if(renderChain==null){
			Resp.write(" />");
		}else{
			Resp.write(">");
			if(!renderChain.hasNoChild()){
				renderChain.render(pageObj, Resp);
			}
			Resp.write("</"+elementName+">");
		}		
	}
	
	
	private String replaceFiled(String orgString, PageWrapper pageObj){
		//如果有annotation 就擦除
		
		
		
		String handleString = "";
		if(orgString!=null){
			handleString = orgString;
		}
		
		int indexOfField = handleString.indexOf("${");
		Object page = pageObj.getPage();
		//Map<String, Iterable> fieldMapping = pageObj.getScopeFileds();
		
		//如果有要替换的
		if(indexOfField!=-1){
			String field = handleString.substring(indexOfField+2, handleString.indexOf("}"));
			//field的前缀 部分
			String fieldPrefix = "";
			String fieldPostfix = "";
			//如果field里有"." 说明此field是对象 并取的是对象的field
			if(field.contains(".")){
				String[] asdasd = field.split("[.]");
				fieldPrefix = asdasd[0];
				fieldPostfix = asdasd[1];
			}else{
				fieldPrefix = field;
			}			
			String getMethodName = getGetMothodStringByFieldName(fieldPrefix);
			Method getMethod;
			String head = "\\u0024\\u007B";
			String end = "\\u007D";
			String expression = head + field + end;
			//如果是直接取对象
			try {
				Field[] fields = page.getClass().getDeclaredFields();
				boolean hasField = false;
				for(int i = 0; i < fields.length; i++){
					if(fields[i].getName().equals(fieldPrefix)){
						hasField = true;
					}
				}
				//找到此field
				if(hasField){
					getMethod = page.getClass().getMethod(getMethodName);
					//如果在视图对象里找到
					if(getMethod!=null){
						Object fieldValue = getMethod.invoke(page);
						
						//如果拿出来的值是null 就变成""
						if(fieldValue==null){
							String outputStr = this.replaceStr(orgString, field, "");			
							if(outputStr.indexOf("${")!=-1){
								outputStr = replaceFiled(outputStr, pageObj);
							}
							return outputStr;
						}
						//如果field不是object
						if(fieldPostfix.equals("")){
							String outputStr = this.replaceStr(orgString, field, fieldValue.toString());			
							if(outputStr.indexOf("${")!=-1){
								outputStr = replaceFiled(outputStr, pageObj);
							}
							return outputStr;
						}else{
							//如果是对象
							Object fieldObj = getObjectField(fieldValue, fieldPostfix);
							String outputStr = this.replaceStr(orgString, field, fieldObj.toString());			
							if(outputStr.indexOf("${")!=-1){
								outputStr = replaceFiled(outputStr, pageObj);
							}
							return outputStr;		
						}	
					}
				}else{
					Map<String, Object> currentFiled = pageObj.getCurrentFiled();
					Object currentObject = currentFiled.get(fieldPrefix);
					if(fieldPostfix.equals("")){
						String fieldValue = currentObject.toString();
						String outputStr = orgString.replaceAll(expression, fieldValue);
						if(outputStr.indexOf("${")!=-1){
							outputStr = replaceFiled(outputStr, pageObj);
						}
						return outputStr;
					}else{
						Object fieldObj = getObjectField(currentObject, fieldPostfix);
						String fieldValue = fieldObj.toString();
						String outputStr = orgString.replaceAll(expression, fieldValue);
						if(outputStr.indexOf("${")!=-1){
							outputStr = replaceFiled(outputStr, pageObj);
						}
						return outputStr;
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
		}
		return orgString;
	}
	
	/**
	 * 替换字符串的方法
	 * @param orgStr
	 * @param key
	 * @param toReplace
	 * @return
	 */
	private String replaceStr(String orgStr, String key, String toReplace){
		//表达式
		String baseExpression = "${"+key +"}";
		//表达式长度
		int expressionLength = baseExpression.length();
		//表达式的开始位置
		int headInt = orgStr.indexOf("${");
		//进行替换
		String resultStr = orgStr.substring(0, headInt)+toReplace+ orgStr.substring(headInt+expressionLength, orgStr.length());
		return resultStr;
	}
	
	/**
	 * 获取对象里的field
	 * @param field
	 * @param objecFieldName
	 * @return
	 */
	private Object getObjectField(Object field, String objecFieldName){
		Method getMethod;
		String getMethodName = getGetMothodStringByFieldName(objecFieldName);
		try {
			getMethod = field.getClass().getMethod(getMethodName);
			if(getMethod!=null){
				Object fieldValue = getMethod.invoke(field);
				return fieldValue;
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
