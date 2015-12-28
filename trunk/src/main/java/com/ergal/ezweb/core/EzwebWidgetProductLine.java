package com.ergal.ezweb.core;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.fileupload.FileItemFactory;

import com.google.inject.ImplementedBy;
import com.google.inject.Injector;

/**
 * Ezweb组件的生成器接口 默认的实现WidgetProductLine
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
@ImplementedBy(WidgetProductLine.class)
public interface EzwebWidgetProductLine {
	public void produce(ServletRequest req, ServletResponse rep, Injector injector, String uri, String para, FileItemFactory fileFactory);
}
