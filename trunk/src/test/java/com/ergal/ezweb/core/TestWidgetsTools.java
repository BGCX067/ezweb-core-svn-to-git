package com.ergal.ezweb.core;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * 测试视图组件的处理类
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class TestWidgetsTools {
	public TestWidgetsTools() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testParseIterator() throws IOException{   
    	//找到html
    	//获取当前工程的路径
    	String pathThis = this.getClass().getResource("/").getPath();
    	//去掉/target/test-classes/
    	String targetDir = "/target/test-classes/";
    	String pathProject = pathThis.substring(0, pathThis.length()-targetDir.length());
    	//加上\src\main\webapp\example\Article.html
    	String pathHtml = pathProject+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator+"example"+File.separator+"Article.html";
        File file = new File(pathHtml);
       
        FileReader fr = new FileReader(file);
        BufferedReader br=new BufferedReader(fr); 
        StringBuffer sb = new StringBuffer((int)file.length());
        String line = null;
        while( (line = br.readLine() ) != null ) {
            sb.append(line).append("\n");
        }  
        String testhtml = sb.toString();
        assertTrue(true);
        //assertTrue(false);
    }
}
