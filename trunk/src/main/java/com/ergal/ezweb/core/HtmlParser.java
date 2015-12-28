package com.ergal.ezweb.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.ergal.ezweb.utils.XMLTools;
import com.ergal.ezweb.widgets.IfMatchWidget;
import com.ergal.ezweb.widgets.IteratorWidget;
import com.ergal.ezweb.widgets.RenderChain;
import com.ergal.ezweb.widgets.Renderable;
import com.ergal.ezweb.widgets.SingleWidgetChain;
import com.ergal.ezweb.widgets.TextWidget;
import com.ergal.ezweb.widgets.XMLWidget;
import com.google.inject.Singleton;
/**
 * 解析html文档的 也是第一种形式的体现
 * 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
@Singleton
public class HtmlParser {
	private static final Log log = LogFactory.getLog(HtmlParser.class);

	private static final String WIDGET_ITERATOR = "iterator";	//迭代标签
	private static final String WIDGET_IFMATCH = "ifmatch";		//判断匹配标签
	private static final String WIDGET_BASECONTEXTPATH = "basecontextpath";	//web根uri的annotation标签
	
	private Element form;
	/**
	 * 由文件路径读取文件到字符串
	 * 由新的工具类TemplateLoader替换
	 * @deprecated
	 * @param htmlName
	 * @return
	 */
	static String getHtml(String htmlName) {
		try {
			File file = new File(htmlName);
			FileReader fr;
			fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			StringBuffer sb = new StringBuffer((int) file.length());
			String line = null;
			while ((line = br.readLine()) != null) {	
				sb.append(line).append("\n");
			}
			return sb.toString();
		} catch (FileNotFoundException e) {
			log.error("读取html模板文件出错:文件没有找到");
			e.printStackTrace();
		} catch (IOException e) {
			log.error("读取html模板文件出错:读取html文件错误");
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 解析html模板 生成一个渲染链
	 * @param htmlTemplate
	 * @return
	 */
	public Renderable parse(String htmlTemplate){
		//定义一个渲染连
		RenderChain renderChain;
		try {
			//定义解析器
            final SAXReader reader = new SAXReader();
            reader.setEncoding("utf-8");
            reader.setMergeAdjacentText(true);
            //把字符串读进来 并进行xml解析 并获取渲染链
            renderChain = dive(reader.read(new StringReader(htmlTemplate)));
            return renderChain;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
	}
	
	/**
	 * 解析document
	 * @param document
	 * @return
	 */
	private RenderChain dive(Document document){
		//定义一个新的RenderChain
		RenderChain chain = new RenderChain();;
		//直接分析html下面的节点
		final RenderChain docChain = dive(document.getRootElement());
		//添加到渲染链的集合中去
		//在这里得到的是一个第一个根的渲染器 并传给他一个下面的渲染链
        chain.addWidget(getWidget(null, document.getRootElement(), docChain));
        //然后吧整题分析的渲染链返回
        return chain;
	}
	
	/**
	 * 解析Element
	 * @param element
	 * @return
	 */
	private RenderChain dive(Element element){
		//定义一个RenderChain渲染链 要返回给用来做根渲染器链的
		RenderChain widgetChain = new RenderChain();
		for (int i = 0, size = element.nodeCount(); i < size; i++){
			Node node = element.node(i);
			//分析子元素
			//如果子元素的类型是Element 
			//如果它下面的节点是element
			if (XMLTools.isElement(node)) {
				//开始解析下面的节点
				final Element child = (Element) node;
				
				//检查上一个
				String  preNodesAnnotation = checkPreNode(element.node(i-1));
				
				//产生子渲染链 下潜到元素内部再分析
				RenderChain childChain;
				childChain = dive(child);
				//如果是迭代
				if(preNodesAnnotation.equalsIgnoreCase(WIDGET_ITERATOR)){
					//添加一个迭代的渲染器
					widgetChain.addWidget(getWidget(element.node(i-1), child, childChain));
				}else if(preNodesAnnotation.equalsIgnoreCase(WIDGET_IFMATCH)){
					//如果是判断标签
					//添加一个判断渲染器
					widgetChain.addWidget(getWidget(element.node(i-1), child, childChain));
				}else{
					//添加下一个渲染器
					widgetChain.addWidget(getWidget(node, child, childChain));
				}
			}else if (XMLTools.isTextCommentOrCdata(node)) {
                //判断是否是注释 文本 或者CDATA 其中可能包含annotation
				//如果碰到了annotation 检查annotation的类型 并在链上添加一个RenderChain
				Renderable widget = getWidget(node, element);
				if(widget!=null){
					widgetChain.addWidget(widget);
				}
            }
		}
		return widgetChain;
	}
	
	
	
	/**
	 * 查看前一个节点是否annotation
	 * @param node
	 * @return
	 */
	private String checkPreNode(Node node){
		String annotation = XMLTools.readAnnotation(node);
		if(null != annotation){
			String[] keyAndContent = XMLTools.extractKeyAndContent(annotation);
			final String annoName = keyAndContent[0];
			//返回annotation的字段
			return annoName;
		}
		return "";
	}
	
	/**
	 * 文本渲染器的获取 解析出一个渲染器
	 * @param node
	 * @return
	 */
	private Renderable getWidget(Node node, Element element){
		String annotation = XMLTools.readAnnotation(node);
		//如果不是annotation
		if(null == annotation){
			//返回纯文本渲染器
			String text = node.asXML();
			return new TextWidget(text, element.getName());
		}else{
			//如果含有annotation
			String[] keyAndContent = XMLTools.extractKeyAndContent(annotation);
			final String annoName = keyAndContent[0];
			if(annoName.equalsIgnoreCase(WIDGET_BASECONTEXTPATH)){
				String text = node.asXML();
				return new TextWidget(text, element.getName());
            	//return getIteratorWidget(keyAndContent);
            }
		}
		return null;
	}
	
	/**
	 * 其他元素的渲染器的获取 解析出一个渲染器
	 * @param node
	 * @param element
	 * @param rendChain
	 * @return
	 */
	private Renderable getWidget(Node node, Element element, RenderChain rendChain){
		//获取annotation
		String annotation = XMLTools.readAnnotation(node);
		
		String elementName = element.getName();
		String nodeStr = element.asXML();
		String last2 = nodeStr.substring(nodeStr.length()-2, nodeStr.length()-1);
		if(last2.equals("/")){
			rendChain = null;
		}
		//如果没有annotation
		if (null == annotation){
			/*if(nodeStr.substring(beginIndex, endIndex)){				
			}*/
			//获取元素里面的属性键值对 然后得到一个xml的渲染器 并初始化它 把下面的链表传给他,在里面完成链表后面的渲染
			return new XMLWidget(elementName , XMLTools.parseAttribs(element.attributes()), rendChain);
		}else{
			String[] keyAndContent = XMLTools.extractKeyAndContent(annotation);
			final String annoName = keyAndContent[0];
			XMLWidget xmlWidget = new XMLWidget(elementName , XMLTools.parseAttribs(element.attributes()), rendChain);
			SingleWidgetChain singleWidgetChain= new SingleWidgetChain(xmlWidget);
			String text = node.asXML();
        	text = text.replaceAll(annotation, "");
        	TextWidget textWidget = new TextWidget(text, element.getName());
			if(annoName.equalsIgnoreCase(WIDGET_ITERATOR)){
				//获取迭代渲染器并返回
				IteratorWidget iteratorWidget= getIteratorWidget(keyAndContent, singleWidgetChain, textWidget);
            	return iteratorWidget;
            }else if(annoName.equalsIgnoreCase(WIDGET_IFMATCH)){	
				//获取判断渲染器并返回
				IfMatchWidget ifMatchWidget = getIfMatchWidget(keyAndContent, singleWidgetChain, textWidget);
				return ifMatchWidget;
			}
		}
		return null;
	}
	
	/**
	 * 得到一个迭代的渲染器
	 * @param node
	 * @return
	 */
	private IteratorWidget getIteratorWidget(String[] keyAndContent, RenderChain rendChain, TextWidget textWidget){
		//分析后面的两个字段
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
		IteratorWidget iteratorWidget = new IteratorWidget(item, var, rendChain, textWidget);
		return iteratorWidget;
	}
	
	/**
	 * 得到一个判断匹配的渲染器
	 * @param node
	 * @return
	 */
	private IfMatchWidget getIfMatchWidget(String[] keyAndContent, RenderChain rendChain, TextWidget textWidget){
		//分析后面的字段
		IfMatchWidget ifMatchWidget;
		String content = keyAndContent[1];
		//如果里面含有两个等号
		if(content.indexOf("==")!=-1){
			String[] vars = content.split("==");
			ifMatchWidget = new IfMatchWidget(new String("1"), vars, null, rendChain, textWidget);
		}else{
			//如果里面只有一个字段
			ifMatchWidget = new IfMatchWidget(new String("2"), null, content, rendChain, textWidget);
		}
		return ifMatchWidget;
	}
	
}
