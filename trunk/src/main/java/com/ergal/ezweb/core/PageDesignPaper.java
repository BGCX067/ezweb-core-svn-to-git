package com.ergal.ezweb.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.ergal.ezweb.widgets.RenderChain;
import com.ergal.ezweb.widgets.Renderable;


/**
 * 对渲染链的一个包装
 * 视图页面的设计方案 渲染的流程的定义 需要根据页面的内容来进行定制
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class PageDesignPaper {

	private static final Log log = LogFactory.getLog(PageDesignPaper.class);
	
	private final Renderable renderChain;

	public Renderable getRenderChain() {
		return renderChain;
	}
	public PageDesignPaper(Renderable renderChain){
		this.renderChain = renderChain;
	}
	
}
