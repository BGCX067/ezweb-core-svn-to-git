package com.ergal.ezweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 采用普通html文件做模板 定义其访问的路径
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-08-21
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface ViewHtml {
	String value();
}
