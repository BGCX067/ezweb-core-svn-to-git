package com.ergal.ezweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 直接跳转到某个物理页面
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-27
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Redirect {
	String value();
}
