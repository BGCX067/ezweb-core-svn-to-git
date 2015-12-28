package com.ergal.ezweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 定义使用get方法的处理方式
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-08-14
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Get {
	
}
