package com.ergal.ezweb.core;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.annotation.ViewPath;

/**
 * 把所有的控制层的类找出来和其对应的url储存到HashMap里
 * @deprecated
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
public class UrlMappingManager {
	private static final Log log = LogFactory.getLog(UrlMappingManager.class);
	public static HashMap<String, String> getMapping(String[] classAllName,
			String packageName) {
		//获取类的总数
		int classesLength = classAllName.length;
		HashMap<String, String> mapping = new HashMap<String, String>();
		for (int i = 0; i < classesLength; i++) {
			String className = classAllName[i];
			try {
				String classNamePrefix = className.split("[.]")[0];
				String classFullName = packageName + "." + classNamePrefix;
				Class clazz = Class.forName(classFullName);
				ViewPath viewPath = (ViewPath) clazz
						.getAnnotation(ViewPath.class);
				if(viewPath!=null){
					String classViewPath = viewPath.value();
					//如果不是以"/"结尾 自动添加上"/"
					String lastStr = classViewPath.substring(classViewPath.length()-1, classViewPath.length());
					if(!lastStr.equals("/"))
					{
						classViewPath += "/";
					}
					mapping.put(classViewPath, className);
				}
				
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return mapping;
	}
}
