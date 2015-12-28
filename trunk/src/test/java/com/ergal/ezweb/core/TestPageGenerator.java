package com.ergal.ezweb.core;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * ≤‚ ‘ ”Õº…˙≥…∆˜
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 */
public class TestPageGenerator {
	public TestPageGenerator(){
		
	}
	@Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testMothodStringByFieldName() { 
    	String expMethodName = "getArticle_title";
    	String actMethodName = PageGenerator.getGetMothodStringByFieldName("article_title");
    	
    	String expMethodName2 = "setArticle_title";
    	String actMethodName2 = PageGenerator.getSetMothodStringByFieldName("article_title");
        assertEquals(expMethodName, actMethodName);
        assertEquals(expMethodName2, actMethodName2);
    }
    
    
}
