package com.ergal.ezweb.util;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ergal.ezweb.utils.XMLTools;

public class TestXMLTools {
	public TestXMLTools() {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testReadAnnotation() throws ClassNotFoundException, IOException {
    	String annotation = null;
    	String withAnnotation = "@Iterator(name=asd, var=\"asdfsdf\")";
    	withAnnotation = withAnnotation + "\r\n";
    	Pattern WIDGET_ANNOTATION_REGEX = Pattern.compile("(@\\w\\w*(\\([\\w,=\"'()?:><!\\[\\];{}. ]*\\))?[ \n\r\t]*)\\Z");
    	Matcher matcher = WIDGET_ANNOTATION_REGEX.matcher(withAnnotation);
    	if (matcher.find()) {
            annotation = matcher.group();
        }
    	System.out.println(annotation);
    	assertEquals(withAnnotation, annotation);
    }
}
