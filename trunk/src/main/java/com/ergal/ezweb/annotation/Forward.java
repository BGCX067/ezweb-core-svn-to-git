package com.ergal.ezweb.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 不换url的跳转  用request的forward
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * create date 2008-11-28
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
public @interface Forward {
	String value();
}
