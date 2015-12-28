package com.ergal.ezweb.util;

import static org.junit.Assert.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ergal.ezweb.utils.ClassUtil;

/**
 * 测试ClassUtil的方法
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class TestClassUtil {
	
	public TestClassUtil(){
		
	}
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	/**
	 * 测试获取指定包下所有类名的方法
	 */
	@Test
	public void testGetPackageAllClassName() {
		String classLocation = this.getClass().getResource("/").getPath();
		String packageName = this.getClass().getPackage().getName();
		String[] allClassName = ClassUtil.getPackageAllClassName(classLocation, packageName);
		boolean iexsist = false;
		String myClassName = this.getClass().getName().substring(21)+".class";
		for(int i = 0; i < allClassName.length; i++){
			if(allClassName[i].equals(myClassName)){
				iexsist = true;
			}
		}
		
		assertTrue(allClassName.length>=1);
		assertTrue(iexsist);
	}
	
	@Test
	public void testGetClassesByJar(){
		Set<Class<?>> classesExp = new LinkedHashSet<Class<?>>();
		classesExp.add(junit.extensions.ActiveTestSuite.class);
		classesExp.add(junit.extensions.RepeatedTest.class);
		classesExp.add(junit.extensions.TestDecorator.class);
		classesExp.add(junit.extensions.TestSetup.class);
		classesExp.add(junit.extensions.ActiveTestSuite.class);
		Set<Class<?>> classesAct = ClassUtil.getClasses(junit.extensions.ActiveTestSuite.class.getPackage());
		//int asd = classesAct.size();
		//int asd2 = classesExp.size();
		//assertTrue(classesAct.size() == classesExp.size());
		assertTrue(classesAct.contains(junit.extensions.ActiveTestSuite.class));
		assertTrue(classesAct.contains(junit.extensions.RepeatedTest.class));
		assertTrue(classesAct.contains(junit.extensions.TestDecorator.class));
		assertTrue(classesAct.contains(junit.extensions.TestSetup.class));
	}
}
