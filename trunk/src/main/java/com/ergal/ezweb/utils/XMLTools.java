package com.ergal.ezweb.utils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.Node;

/**
 * 解析xml(html)的一些常用工具和定义
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class XMLTools {
	//form标签定义
	private static final String FORM_TAG = "form";	
	//annotation的正则表达式(@\w\w*(\([\w,=\"'()?:><!\[\];{}. ]*\))?[ \n\r\t]*)\Z
	//以@加上一个或任意个字母开头 加上括号()里面是 
	public static final Pattern WIDGET_ANNOTATION_REGEX = Pattern.compile("(@\\w\\w*(\\([\\w,=\"'()?:><!\\[\\];{}. ]*\\))?[ \n\r\t]*)\\Z");
	static final String XMLNS_ATTRIB_REGEX = " xmlns=\"[a-zA-Z0-9_+%;#/\\-:\\.]*\"";
	
	/**
	 * 去掉annotation的部分的字符串
	 * @param text
	 * @return
	 */
	public static String stripAnnotation(String text) {
	   final Matcher matcher = WIDGET_ANNOTATION_REGEX.matcher(text);
	
		//strip off the ending bit (annotation)
		if (matcher.find())
		    return text.substring(0, matcher.start());
		
		return text;
	}
	/**
	 * 读取annotation的字符串
	 * @param node
	 * @return
	 */
	public static String readAnnotation(Node node) {
        String annotation = null;
        //如果是一个文本类型的
        if (isText(node)) {
        	//定义匹配器
            final Matcher matcher = WIDGET_ANNOTATION_REGEX.matcher(node.asXML());
            //查找annotation标记
            if (matcher.find()) {
                annotation = matcher.group();
            }
        }
        return annotation;
    }
	
	
	public static String asRawXml(Element element) {
        return element.asXML().replaceFirst(XMLNS_ATTRIB_REGEX, "");
    }

	public static boolean skippable(Attribute type) {
        if (null == type)
            return false;

        final String kind = type.getValue();
        return ( "submit".equals(kind) || "button".equals(kind) || "reset".equals(kind) || "file".equals(kind) );
    }
	/**
	 * 读取节点上的属性值 储存为键值对
	 * @param list
	 * @return
	 */
	public static Map<String, String> parseAttribs(List list) {
        Map<String, String> attrs = new LinkedHashMap<String, String>(list.size() + 4);

        for (Object o : list) {
            Attribute attribute = (Attribute)o;

            attrs.put(attribute.getName(), attribute.getValue());
        }

        return attrs;
    }
	/**
	 * 判断是否是form标签
	 * @param node
	 * @return
	 */
	public static boolean isForm(Node node) {
        return FORM_TAG.equals(node.getName());
    }
	
	/**
	 * 判断是否是Element
	 * @param node
	 * @return
	 */
	public static boolean isElement(Node node) {
        return Node.ELEMENT_NODE == node.getNodeType();
    }
	
	/**
	 * 判断是否是文本
	 * @param preceeding
	 * @return
	 */
	public static boolean isText(Node preceeding) {
        return null != preceeding && Node.TEXT_NODE == preceeding.getNodeType();
    }
	
	/**
	 * 判断是否是注释或者CDATA类型
	 * @param node
	 * @return
	 */
	public static boolean isTextCommentOrCdata(Node node) {
        final short nodeType = node.getNodeType();

        return isText(node) || Node.COMMENT_NODE == nodeType || Node.CDATA_SECTION_NODE == nodeType;
    }
	
	/**
	 * 获取annotation的键值对 储存为字符串数组
	 * @param annotation
	 * @return
	 */
	public static String[] extractKeyAndContent(String annotation) {
        final int index = annotation.indexOf('(');

        //there's no content
        if (index < 0)
            return new String[] { annotation.substring(1).toLowerCase(), "" };

        String content = annotation.substring(index + 1, annotation.lastIndexOf(')'));

        //normalize empty string to null
        if ("".equals(content))
            content = null;

        return new String[] { annotation.substring(1, index).toLowerCase(), content };
    }
}
