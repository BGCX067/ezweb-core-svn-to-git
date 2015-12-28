package com.ergal.ezweb.widgets;

import com.ergal.ezweb.core.PageWrapper;
import com.ergal.ezweb.core.RespStringWrapper;
import com.ergal.ezweb.utils.PageObjectFieldUtil;


/**
 * 判断的标签渲染器 用法示例 @IfMatch(condition) @IfMatch(x==y) 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
public class IfMatchWidget implements Renderable {
	private final RenderChain renderChain;
	private final TextWidget textWidget;
	private final String type;	//1是表达式 2是变量
	private final String[] vars;
	private final String singleString;
	
	public IfMatchWidget(String type, String[] vars, String singleString, RenderChain renderChain, TextWidget textWidget){
		this.renderChain = renderChain;
		this.textWidget = textWidget;
		this.type = type;
		this.vars = vars;
		this.singleString = singleString;
	}
	
	public void render(PageWrapper pageObj, RespStringWrapper Resp) {
		textWidget.render(pageObj, Resp);
		// 如果是表达式类型的		
		if(type.equals("1")){
			String leftVar = vars[0];
			String rightVar = vars[1];
			//获取等号左右两边的实际对象
			Object leftObj = PageObjectFieldUtil.getField(pageObj.getPage(), leftVar);
			Object rightObj = PageObjectFieldUtil.getField(pageObj.getPage(), rightVar);
			if(leftObj.equals(rightObj)){
				renderChain.render(pageObj, Resp);
			}
		}else{
			String expression = singleString;
			Boolean b = (Boolean)PageObjectFieldUtil.getField(pageObj.getPage(), expression);
			if(b){
				renderChain.render(pageObj, Resp);
			}
		}

	}

}
