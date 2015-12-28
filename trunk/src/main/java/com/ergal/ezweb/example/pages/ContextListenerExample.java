package com.ergal.ezweb.example.pages;

import java.util.HashSet;
import java.util.Set;

import com.ergal.ezweb.core.WebStartupListener;
import com.ergal.ezweb.core.WidgetsUtil;
import com.google.inject.Module;


/**
 * 所有的依赖关系可以在这里做	也可以自定义一些参数
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class ContextListenerExample extends WebStartupListener{
	/**
	 * 示例:	用户时间自定义的依赖注入的地方
	 */
	@Override
	public Set<Module> getUserModule(){
		Set<Module> modules = new HashSet<Module>();
		Package pagePackage = this.getClass().getPackage();
		Module module = WidgetsUtil.configure()
			.add(pagePackage)//在这里定义视图做在的包
			.buildModule();
		modules.add(module);
		//可以继续绑定其他的依赖
		return modules;
	}
}
