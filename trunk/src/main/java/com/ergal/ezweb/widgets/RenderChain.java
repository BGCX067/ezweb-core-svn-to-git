package com.ergal.ezweb.widgets;

import java.util.ArrayList;
import java.util.List;

import com.ergal.ezweb.core.PageWrapper;
import com.ergal.ezweb.core.RespStringWrapper;
/**
 * 渲染链 核心渲染类 里面是可以嵌套RenderChain的
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class RenderChain implements Renderable {
	//渲染组件的集合
	private final List<Renderable> widgets = new ArrayList<Renderable>();
	
	/**
	 * 循环链式渲染
	 */
	public void render(PageWrapper pageObj, RespStringWrapper Resp) {
		for(Renderable widget : widgets){
			widget.render(pageObj, Resp);
		}
	}
	
	/**
	 * 添加一个组件
	 * @param widget
	 * @return
	 */
	public RenderChain addWidget(Renderable widget){
		widgets.add(widget);
		return this;
	}
	
	public boolean hasNoChild(){
		if(widgets.size()==0){
			return true;
		}
		return false;
	}

}
