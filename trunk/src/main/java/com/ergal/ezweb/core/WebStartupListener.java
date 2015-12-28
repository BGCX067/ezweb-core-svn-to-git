package com.ergal.ezweb.core;

import java.io.File;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.cfg.EzwebPropertyFactory;
import com.ergal.ezweb.utils.Constant;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;


/**
 * 
 * 启动的初始化类 包含核心的渲染器初始化
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public abstract class WebStartupListener implements ServletContextListener {
	private static final Log log = LogFactory.getLog(WebStartupListener.class);
	
	public void contextDestroyed(ServletContextEvent arg0) {
		//TODO 销毁方法
	}
	
	/**
	 * 初始化servlet上下文
	 */
	public void contextInitialized(ServletContextEvent event) {
		log.info("************* web服务器启动 ezweb开始初始化 *************");
		this.initEnvironment(event);
	}
	
	/**
	 * 初始化web应用框架的所需环境
	 * @param event
	 */
	public void initEnvironment(ServletContextEvent event){
		// 得到servlet的上下文的根路径
		String root = event.getServletContext().getRealPath("/");
		// 把根路径储存起来
		DispatcherManager.setWebRealPath(root);
		//PageGenerator.setBaseWebFilePath(root);
		// ��ʼ���ַ��� �Ĵ�����İ��������·��
		DispatcherManager.setPagesPackageName(this.getClass().getPackage().getName());
		DispatcherManager.setClassLocationPath(this.getClass().getClassLoader().getResource("/").getPath());
		// 设置google的Injector 提供给程序使用
		DispatcherManager.setInjector(getInjector());		
	}
	
	/**
	 * ��ȡ���ϵͳ������ע����û��Զ��������ע��
	 * @return
	 */
	public Injector getInjector(){
		
		//����ϵͳ�����еİ�
		Module ezwebModule = new AbstractModule() {
		 	protected void configure() {
		 		//��ȡftp�����ò���
		 		Properties globalProp = EzwebPropertyFactory.getGlobalProperties();
				int ftpcachesize = Integer.parseInt(globalProp.getProperty(Constant.EZWEB_FTP_CACHE_SIZE).trim());
				String ftpCacheDir = globalProp.getProperty(Constant.EZWEB_FTP_CACHE_DIR).trim();
		 		//��һ���ļ��ϴ���� 
				bind(FileItemFactory.class).toInstance(new DiskFileItemFactory(ftpcachesize, new File(ftpCacheDir)));
		 	}
		};
		//��ȡ�û���
		Set<Module> moduleSet = this.getUserModule();
		//�󶨵ķ��� �ɸ����Ҫ��ѡ����ע���ģʽStage.PRODUCTION ���� Stage.DEVELOPMENT
		Stage bindStage = Stage.PRODUCTION;
		if(moduleSet==null||moduleSet.size()==0){
			//���û���û��� ��ֻ�ǽ���ϵͳ������ע������
			return Guice.createInjector(bindStage, ezwebModule);
		}else{
			//���ϵͳ����ע������
			moduleSet.add(ezwebModule);
			return Guice.createInjector(bindStage, moduleSet);
		}
		
	}
	
	/**
	 * ��ȡ�û���module
	 * @return
	 */
	public abstract Set<Module> getUserModule();
	
}
