package com.ergal.ezweb.cfg;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * 获取所有的配置 properties
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-10-28
 * last modify 2008-10-28
 * @version 1.0
 */
public class EzwebPropertyFactory {
	private static final Log log = LogFactory.getLog(EzwebPropertyFactory.class);
	private static Properties GLOBAL_PROPERTIES;
	
	public static Properties getGlobalProperties(){
		if(GLOBAL_PROPERTIES==null){
			log.info("The first time get global configuration properties !!");
			GLOBAL_PROPERTIES = EzwebConfiguration.getGLOBAL_PROPERTIES(); 
		}
		return GLOBAL_PROPERTIES;
	}	
}
