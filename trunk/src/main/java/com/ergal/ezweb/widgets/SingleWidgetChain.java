package com.ergal.ezweb.widgets;

import com.ergal.ezweb.core.PageWrapper;
import com.ergal.ezweb.core.RespStringWrapper;


/**
 * 单独的特殊渲染链
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class SingleWidgetChain extends RenderChain {
	private final Renderable widget;
	
	public SingleWidgetChain(Renderable widget){
		this.widget = widget;
	}
	public void render(PageWrapper pageObj, RespStringWrapper Resp) {
		widget.render(pageObj, Resp);
	}
	
}
