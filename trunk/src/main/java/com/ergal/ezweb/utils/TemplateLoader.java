package com.ergal.ezweb.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.annotation.ViewHtml;
import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * 获取模板文件的主要类
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class TemplateLoader {
	private static final Log log = LogFactory.getLog(TemplateLoader.class);
	private final Provider<ServletContext> context;

    @Inject
    public TemplateLoader(Provider<ServletContext> context) {
        this.context = context;
    }
	/**
	 * 获取模板类的字符流
	 * @param pageClass
	 * @return
	 */
	public String load(Class<?> pageClass){
		if(pageClass.getAnnotation(ViewHtml.class)!=null){
			//获取html页面的名字
			String htmlPageName = pageClass.getAnnotation(ViewHtml.class).value();
			String htmlFilePath = getHtmlFilePath(htmlPageName);
			String htmpTemplate = getHtml(htmlFilePath);
			return htmpTemplate;
		}
		return null;
		
	}
	
	/**
	 * 获取真实的html物理地址
	 * @param htmlPageName
	 * @return
	 */
	public String getHtmlFilePath(String htmlPageName){
		//获取web的根物理路径
		String webBaseRealPath = context.get().getRealPath("/");
		String[] tempStrs = htmlPageName.split("/");
		String htmlFilePath = webBaseRealPath;
		for(int i = 0; i < tempStrs.length; i++){
			if(!tempStrs[i].equals("")){
				htmlFilePath = htmlFilePath + File.separator + tempStrs[i];
			}
		}
		return htmlFilePath;
	}
	
	/**
	 * 读取html的页面内容
	 * 这里是没有处理编码格式的
	 * @param htmlName
	 * @return
	 */
	public String getHtml(String htmlName) {
		try {
			File file = new File(htmlName);
			FileInputStream fis = new FileInputStream(file); 		
			InputStreamReader isr = new InputStreamReader(fis, "utf-8");
			String ss = isr.getEncoding();
			BufferedReader br = new BufferedReader(isr);
			StringBuffer sb = new StringBuffer((int) file.length());
			String line = null;
			while ((line = br.readLine()) != null) {		
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			log.error("查找解析html模板文件出错:文件没有找到 :" + htmlName);
			e.printStackTrace();
		} catch (IOException e) {
			log.error("查找解析html模板文件出错:读取html文件错误" + htmlName);
			e.printStackTrace();
		}
		return null;
	}
}
