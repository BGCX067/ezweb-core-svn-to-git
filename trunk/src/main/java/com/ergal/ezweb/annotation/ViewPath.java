package com.ergal.ezweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 定义使用此标记的类所要处理的url
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-08-13
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface ViewPath {
	String value();
}
