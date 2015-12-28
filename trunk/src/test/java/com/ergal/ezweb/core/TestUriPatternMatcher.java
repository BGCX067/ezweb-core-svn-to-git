package com.ergal.ezweb.core;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * 测试uri路径匹配器的工作
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
public class TestUriPatternMatcher {
	UriPatternMatcher matcher;
	HashMap<String, String> pages = new HashMap<String, String>();
	public TestUriPatternMatcher(){
		
	}
	@Before
	public void setUp() throws Exception {
		String contextPath = "/ezweb";
		this.matcher = new UriPatternMatcher(contextPath);
		
		pages.put("/article/", "Article.class");
		pages.put("/user/", "User.class");
		pages.put("/article/archive/", "ArticleArchive.class");

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetRequestUri() {
		
		String fullUri = "/ezweb/article";
		String expUri = "/article";
		String actUri = matcher.getRequestUri(fullUri);
		assertEquals(actUri, expUri);
		// assertTrue(false);
	}
	
	@Test
	public void testGetReqClassName(){
		String fullUriArticle = "/ezweb/article";
		String fullUriArticleArchive = "/ezweb/article/archive";
		String expClassNameArticle = "Article.class";
		String expClassNameArticleArchive = "ArticleArchive.class";
		String fullUriArticleWithPara = "/ezweb/article/09";
		String fullUriArticleArchiveWithPara = "/ezweb/article/archive/09";
		
		
		String actClassNameArticle = matcher.getReqClassNameByFullUri(pages, fullUriArticle);
		String actClassNameArticleArchive = matcher.getReqClassNameByFullUri(pages, fullUriArticleArchive);
		String actClassNameArticleWithPara = matcher.getReqClassNameByFullUri(pages, fullUriArticleWithPara);
		String actClassNameArticleArchiveWithPara = matcher.getReqClassNameByFullUri(pages, fullUriArticleArchiveWithPara);
		
		
		assertEquals(expClassNameArticle,actClassNameArticle);
		assertEquals(expClassNameArticleArchive,actClassNameArticleArchive);
		assertEquals(actClassNameArticleWithPara,actClassNameArticle);
		assertEquals(actClassNameArticleArchiveWithPara,actClassNameArticleArchive);
		
		
	}
	
	/**
	 * 测试获取请求的uri后面的参数的获取
	 */
	@Test
	public void testGetReqPara(){
		String reqUri = "/ezweb/article/03";
		String expUri = "03";
		String actUri =matcher.getReqPara(pages, reqUri);
		String reqUri2 = "/ezweb/article/archive/02";
		String expUri2 = "02";
		String actUri2 =matcher.getReqPara(pages, reqUri2);
		
		assertEquals(expUri,expUri);
		assertEquals(expUri2,expUri2);
	}
}
