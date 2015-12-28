package com.ergal.ezweb.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.utils.Constant;


/**
 * ezweb框架的必要配置类 定义为ezweb.cfg.properties
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-10-28
 * last modify 2008-10-28
 * @version 1.0
 */
public class EzwebConfiguration {
	private static final Properties GLOBAL_PROPERTIES;
	private static final Properties TEMP_PROPERTIES;	
	private static final Log log = LogFactory.getLog(EzwebConfiguration.class);


	static{
		TEMP_PROPERTIES = new Properties();
		GLOBAL_PROPERTIES =new Properties();
		InputStream inputStram = getResourceAsStream("/ezweb.cfg.properties");
		try {
			TEMP_PROPERTIES.load(inputStram);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//是否有外部配置文件覆写
		String isOutSideStr = TEMP_PROPERTIES.getProperty(Constant.ISOVERWRITE);
		log.info(isOutSideStr);
		
		if(isOutSideStr.equals("yes")){
			//外部配置文件名
			String outSidePropertiesName = TEMP_PROPERTIES.getProperty(Constant.OUTSIDEPROPERTIESNAME);
			InputStream inputStram2 = getResourceAsStream("/"+outSidePropertiesName);
			try {
				GLOBAL_PROPERTIES.load(inputStram2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			GLOBAL_PROPERTIES.putAll(TEMP_PROPERTIES);
		}
	}
	/**
	 * 获取全局的配置变量
	 * @return copy
	 */
	public static Properties getGLOBAL_PROPERTIES() {
		Properties copy = new Properties();
		copy.putAll(GLOBAL_PROPERTIES);
		return copy;
	}
	
	/**
	 * 获取配置的资源文件
	 * @param resource
	 * @return
	 */
	public static InputStream getResourceAsStream(String resource){
		String realSource = resource.startsWith("/")
			? resource.substring(1) 
			: resource;
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(realSource);
	}
}
