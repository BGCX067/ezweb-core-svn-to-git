package com.ergal.ezweb.core;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ergal.ezweb.example.pages.Iterator;
import com.ergal.ezweb.utils.XMLTools;
import com.ergal.ezweb.widgets.Renderable;


/**
 * ����html������
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class TestHtmlParser {
	public TestHtmlParser() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

/*    @Test
    public void testGetHtml() throws IOException {
    	//�ҵ�html
    	//��ȡ��ǰ���̵�·��
    	String pathThis = this.getClass().getResource("/").getPath();
    	//ȥ��/target/test-classes/
    	String targetDir = "/target/test-classes/";
    	String pathProject = pathThis.substring(0, pathThis.length()-targetDir.length());
    	//����\src\main\webapp\example\Article.html
    	String pathHtml = pathProject+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator+"example"+File.separator+"Article.html";
        File file = new File(pathHtml);
       
        FileReader fr = new FileReader(file);
        BufferedReader br=new BufferedReader(fr); 
        StringBuffer sb = new StringBuffer((int)file.length());
        String line = null;
        while( (line = br.readLine() ) != null ) {
            sb.append(line).append("\n");
        }        

        String actHtmlStr = HtmlParser.getHtml(pathHtml);
    	assertEquals(actHtmlStr, sb.toString());
    }*/
    
    @Test
    public void testAnnotationParse(){
    	String annotation = "@Iterator(name=aaaa, var=\"nams\")";
    	String[] keyAndContent = XMLTools.extractKeyAndContent(annotation);
    	String content = keyAndContent[1];
		String[] contents = content.split(",");
		String item = contents[0].split("=")[1];
		String var = contents[1].split("=")[1];
		if(item.indexOf("\"")!=-1){
			item = item.replaceAll("\"", "");
		}
		if(var.indexOf("\"")!=-1){
			var = var.replaceAll("\"", "");
		}
		
		System.out.println("");
    }
    
   /* @Test
    public void testParse(){
    	//�ҵ�html
    	//��ȡ��ǰ���̵�·��
    	String pathThis = this.getClass().getResource("/").getPath();
    	//ȥ��/target/test-classes/
    	String targetDir = "/target/test-classes/";
    	String pathProject = pathThis.substring(0, pathThis.length()-targetDir.length());
    	//����\src\main\webapp\example\Article.html
    	String pathHtml = pathProject+File.separator+"src"+File.separator+"main"+File.separator+"webapp"+File.separator+"example"+File.separator+"Article.html";
        File file = new File(pathHtml);
    	String actHtmlStr = HtmlParser.getHtml(pathHtml);
    	//ȡ��һ����Ⱦ��
    	Renderable  renderChain = new HtmlParser().parse(actHtmlStr);
    	//��ʼ��һ������
    	String orgStr = "";
    	Iterator myIterator = new Iterator();
    	renderChain.render(myIterator, orgStr);
    	System.out.println(orgStr);
    }*/
    
    
}
