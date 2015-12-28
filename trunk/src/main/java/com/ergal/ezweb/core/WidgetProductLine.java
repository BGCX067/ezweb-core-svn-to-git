package com.ergal.ezweb.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.ergal.ezweb.annotation.Forward;
import com.ergal.ezweb.annotation.P2PScope;
import com.ergal.ezweb.annotation.Redirect;
import com.ergal.ezweb.annotation.SessionScopeLoad;
import com.ergal.ezweb.annotation.SessionScopeSave;
import com.ergal.ezweb.context.EZWebContextManager;
import com.ergal.ezweb.utils.PageObjectFieldUtil;
import com.ergal.ezweb.widgets.Renderable;
import com.google.inject.Injector;

/**
 * ������ͼ����������ʵ�� �����������ҳ����Ⱦ����Ҫ����
 * 
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 * 
 */
public class WidgetProductLine implements EzwebWidgetProductLine {
	// ���������
	private final String serverReqName;

	private final Class pageClass;
	// ��ͼ��url�ļ���
	private Map<String, WidgetProductLine> productlines;
	
	//���ص�����	0Ϊ��ܴ����Ժ��ر���ҳ����ת 	1Ϊ�ض���		2Ϊforward
	private int returnType;	

	// ��Ҫ��ҳ����ͼ�д��ݵ�ʱ��ʹ�õļ���
	private HashSet<String> PageToPageFiledSet;

	// �õ���DesignPaper
	// private final PageWrapper pageWrapper;
	// ��ͼ��װ��
	private final PageDesignPaper pageDesignPaper;
	
	// �ض���ļ���
	//private Map<String, WidgetProductLine> redirectPages;
	
	
	/*public void setRedirectPages(Map<String, WidgetProductLine> redirectPages) {
		this.redirectPages = redirectPages;
	}

	public Map<String, WidgetProductLine> getRedirectPages() {
		return redirectPages;
	}*/

	public HashSet<String> getPageToPageFiledSet() {
		return PageToPageFiledSet;
	}

	public void setPageToPageFiledSet(HashSet<String> pageToPageFiledSet) {
		PageToPageFiledSet = pageToPageFiledSet;
	}

	public Map<String, WidgetProductLine> getProductlines() {
		return productlines;
	}

	public void setProductlines(Map<String, WidgetProductLine> productlines) {
		this.productlines = productlines;
	}

	public Class getPageClass() {
		return pageClass;
	}

	public String getServerReqName() {
		return serverReqName;
	}

	public PageDesignPaper getPageDesignPaper() {
		return pageDesignPaper;
	}

	/**
	 * Ĭ�ϵĹ��췽�� ��ʼ����ʱ����PageDesignPaperBuilder����� �����һ������
	 * 
	 * @param servReqName
	 * @param pageDesignPaper
	 * @param pageClass
	 * @param returnType
	 */
	public WidgetProductLine(String servReqName, PageDesignPaper pageDesignPaper, Class pageClass, int returnType) {
		this.serverReqName = servReqName;
		this.pageClass = pageClass;
		this.pageDesignPaper = pageDesignPaper;
		this.returnType = returnType;
	}

	/**
	 * 核心处理渲染流程
	 */
	public void produce(ServletRequest req, ServletResponse rep,
			Injector injector, String uri, String para,
			FileItemFactory fileFactory) {
		//���������ĵ�
		EZWebContextManager.setContext((HttpServletRequest)req, (HttpServletResponse)rep);
		// ��ȡ�Ѿ��������ù�field��page��Object
		Object pageObj = getAndResetPage(req, fileFactory);
		// ����Ҫ�ж��Ƿ�����һ����ͼ��ת
		String returnUrl = getUrlAndService(pageObj, para, injector);
		// ����session���ֵ
		setSessionScope(req, pageObj);
		// ����Ϊ�� ��û����ͼ��ת
		if (returnUrl == null) {
			// ��ȡ��ͼ����İ�װ
			if(returnType == 1){
				//�����redirect
				redirect(req, rep, pageClass);				
			}else if(returnType == 2){
				//�����forward
				forward(req, rep, pageClass);				
			}else{
				//����ǿ����Ⱦ
				// ��ȡ��ͼ����İ�װ
				PageWrapper pageWrapper = getPageWrapper(pageObj);
				renderOriginalPage(pageWrapper, req, rep);
			}
			
			//���������
			EZWebContextManager.clearContext();
		} else {
			// �������ͼ��ת
			WidgetProductLine productlineNext;
			
			if (this.productlines.containsKey(returnUrl.trim())) {
				// ��ȡ��һ����ProductLine
				productlineNext = productlines.get(returnUrl.trim());
				productlineNext.setPageToPageFiledSet(this.getPageToPageFiledSet());
				productlineNext.setProductlines(productlines);
				//productlineNext.setRedirectPages(redirectPages);
				productlineNext.produce(req, rep, injector, returnUrl.trim(), null, fileFactory, pageObj);
			} else {
				// �ó�url�Ĳ����
				String nextUrlPara = UriPatternMatcher.getReqPara(returnUrl
						.trim());
				// url��ǰ��Ĳ���
				String uriPre = returnUrl.split(nextUrlPara)[0];
				if (productlines.containsKey(uriPre)) {
					productlineNext = productlines.get(uriPre);
					productlineNext.setPageToPageFiledSet(this.getPageToPageFiledSet());
					productlineNext.setProductlines(productlines);
					//productlineNext.setRedirectPages(redirectPages);
					productlineNext.produce(req, rep, injector, uriPre, nextUrlPara, fileFactory, pageObj);
				}
				// TODO �����תҳ�����ͼд�����ִ��� ����û���ҵ��Ĵ��?��
			}
		}
	}

	/**
	 * ������ɵ���һ����ͼ
	 * 
	 * @param url
	 * @return
	 */
	private String produce(ServletRequest req, ServletResponse rep,
			Injector injector, String uri, String para,
			FileItemFactory fileFactory, Object prePage) {
		// ��ȡ�Ѿ��������ù�field��page��Object
		Object pageObj = getAndResetPage(req, fileFactory);
		// ���������ҳ����field��ֵ�Ĵ���
		resetNextPage(prePage, pageObj);
		// ����Ҫ�ж��Ƿ�����һ����ͼ��ת
		String returnUrl = getUrlAndService(pageObj, para, injector);
		// ����session���ֵ��session��ȥ
		setSessionScope(req, pageObj);
		// ����Ϊ�� ��û����ͼ��ת
		if (returnUrl == null) {
			
			if(returnType == 1){
				//�����redirect
				redirect(req, rep, pageClass);				
			}else if(returnType == 2){
				//�����forward
				forward(req, rep, pageClass);				
			}else{
				//����ǿ����Ⱦ
				// ��ȡ��ͼ����İ�װ
				PageWrapper pageWrapper = getPageWrapper(pageObj);
				renderOriginalPage(pageWrapper, req, rep);
			}
			//���������
			EZWebContextManager.clearContext();
		} else {
			// �������ͼ��ת
			WidgetProductLine productlineNext;
			if  (this.productlines.containsKey(returnUrl.trim())) {
				// ��ȡ��һ����ProductLine
				productlineNext = productlines.get(returnUrl.trim());
				productlineNext.setPageToPageFiledSet(this.getPageToPageFiledSet());
				productlineNext.setProductlines(productlines);
				//productlineNext.setRedirectPages(redirectPages);
				productlineNext.produce(req, rep, injector, returnUrl.trim(), null, fileFactory, pageObj);
			} else {
				// �ó�url�Ĳ����
				String nextUrlPara = UriPatternMatcher.getReqPara(returnUrl.trim());
				// url��ǰ��Ĳ���
				String uriPre = returnUrl.split(nextUrlPara)[0];
				if (productlines.containsKey(uriPre)) {
					productlineNext = productlines.get(uriPre);
					productlineNext.setPageToPageFiledSet(this.getPageToPageFiledSet());
					productlineNext.setProductlines(productlines);
					//productlineNext.setRedirectPages(redirectPages);
					productlineNext.produce(req, rep, injector, uriPre, nextUrlPara, fileFactory, pageObj);
					// WidgetProductLine productline = productlines.get(uriPre);
					// productline.produce(req, rep, injector, url, para,
					// fileFactory);
				}
				// TODO �����תҳ�����ͼд�����ִ��� ����û���ҵ��Ĵ��?��
			}
		}

		return null;
	}
	
	
	/**
	 * �ض����?��
	 * @param req
	 * @param rep
	 * @param pageClass
	 */
	private void redirect(ServletRequest req, ServletResponse rep, Class pageClass){
		Redirect redirectViewPage = (Redirect)pageClass.getAnnotation(Redirect.class);
		if(redirectViewPage != null){
			//��ȡ��ת��ֵ
			HttpServletRequest httpServletRequest = (HttpServletRequest)req;
			String redirectPage = redirectViewPage.value();
			String baseContextPath = httpServletRequest
			.getScheme()
			+ "://"
			+ httpServletRequest.getServerName()
			+ ":"
			+ httpServletRequest.getServerPort()
			+ httpServletRequest.getContextPath();
			//��ת 
			HttpServletResponse httpServletResponse = (HttpServletResponse)rep;
			try {
				httpServletResponse.sendRedirect(baseContextPath + redirectPage);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * ��ʽ��ת�Ĵ���
	 * @param req
	 * @param rep
	 * @param pageClass
	 */
	private void forward(ServletRequest req, ServletResponse rep, Class pageClass){
		Forward forwardViewPage = (Forward)pageClass.getAnnotation(Forward.class);
		if(forwardViewPage != null){
			String forwardPage = forwardViewPage.value();
			HttpServletRequest request = (HttpServletRequest)req;
			try {
				request.getRequestDispatcher(forwardPage).forward(req, rep);
			} catch (ServletException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ȡһ��page�Ķ��󲢽���field����������
	 * 
	 * @param req
	 * @param fileFactory
	 * @return
	 */
	private Object getAndResetPage(ServletRequest req,
			FileItemFactory fileFactory) {
		HttpServletRequest request = (HttpServletRequest) req;
		try {
			Object page = reSetPage(request, pageClass.newInstance(),
					fileFactory);
			return page;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ����service���� ����ȡ���ص�url
	 * 
	 * @param page
	 * @param para
	 * @return
	 */
	private String getUrlAndService(Object page, String para, Injector injector) {
		String returnUrl = ""; // ���ص���ͼurl
		boolean hasPara = false;
		if (para != null) {
			hasPara = true;
			if (para.equals("")) {
				hasPara = false;
			}
		}
		// �Ƿ��в���� ���ò�ͬ��service����
		if (hasPara) {
			returnUrl = servicePage(page, injector, para);
		} else {
			returnUrl = servicePage(page, injector);
		}
		return returnUrl;
	}

	/**
	 * ��ȡ��װ����ͼ����
	 * 
	 * @param page
	 * @return
	 */
	private PageWrapper getPageWrapper(Object page) {
		PageWrapper pageWrapper = new PageWrapper(page);
		return pageWrapper;
	}

	/**
	 * ����page��service���� �޲���
	 * 
	 * @return
	 */
	private String servicePage(Object page, Injector injector) {
		// ע������
		injector.injectMembers(page);
		String serviceMethodName = "service";
		String returnUrl = ""; // ���ص���ͼurl
		try {
			Method serviceMethod;
			// ��ȡservice����
			// ���ò������ķ���
			serviceMethod = page.getClass().getMethod(serviceMethodName);
			if (serviceMethod != null) {
				// ��ȡ���ص�url ������service����
				returnUrl = (String) serviceMethod.invoke(page);
			}
			return returnUrl;
			// ֻ������service���� ���ô��ݲ��� ���������javabean����������������
			// ���Բ�����service�Ĵ�����ķ���
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ����page��service���� �в���
	 * 
	 * @param para
	 * @return
	 */
	private String servicePage(Object page, Injector injector, String para) {
		// ע������
		injector.injectMembers(page);
		String serviceMethodName = "service";
		String returnUrl = ""; // ���ص���ͼurl
		try {
			Method serviceMethod;
			// ��ȡservice����
			// ���ò������ķ���
			serviceMethod = page.getClass().getMethod(serviceMethodName,
					String.class);
			if (serviceMethod != null) {
				// ��ȡ���ص�url ������service����
				returnUrl = (String) serviceMethod.invoke(page, para);
			}
			return returnUrl;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * ����һ��ҳ��ȥ������һ��ҳ���field
	 * 
	 * @param prePage
	 * @return
	 */
	private Object resetNextPage(Object prePage, Object nextPage) {
		for (Iterator it = this.getPageToPageFiledSet().iterator(); it
				.hasNext();) {
			String fieldName = (String) it.next();
			String getMethodName = PageObjectFieldUtil
					.getGetMothodStringByFieldName(fieldName);
			String setMethodName = PageObjectFieldUtil
					.getSetMothodStringByFieldName(fieldName);
			try {
				Method getMethod = prePage.getClass().getMethod(getMethodName);
				if(getMethod != null){
					Object fieldValue = getMethod.invoke(prePage);
					Method setMethod = nextPage.getClass().getMethod(setMethodName, nextPage.getClass().getDeclaredField(fieldName).getType());
					// Ȼ����ô˷�����ֵ���ý�ȥ
					setMethod.invoke(nextPage, fieldValue);
				}
				
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
			}

		}
		return nextPage;
	}

	/**
	 * ��ɲ����ԭ����ͼ
	 * 
	 * @param page
	 * @param req
	 * @param rep
	 */
	private void renderOriginalPage(PageWrapper page, ServletRequest req,
			ServletResponse rep) {
		Renderable renderChain = this.pageDesignPaper.getRenderChain();
		RespStringWrapper Resp = new RespStringWrapper();
		renderChain.render(page, Resp);
		HttpServletResponse httpServletResponse = (HttpServletResponse) rep;
		httpServletResponse.setCharacterEncoding("utf-8");
		httpServletResponse.setContentType("text/html; charset=utf-8");
		String outStr = Resp.getStr();
		// �滻�����е�@BaseContextPath
		HttpServletRequest httpServletRequest = (HttpServletRequest) req;
		outStr = outStr.replaceAll("@BaseContextPath", httpServletRequest
				.getScheme()
				+ "://"
				+ httpServletRequest.getServerName()
				+ ":"
				+ httpServletRequest.getServerPort()
				+ httpServletRequest.getContextPath() + "/");
		if (!httpServletResponse.isCommitted()) {
			try {
				//httpServletResponse.getWriter().write(outStr);
				//httpServletResponse.flushBuffer();
				PrintWriter out = httpServletResponse.getWriter();
				out.print(outStr);
				out.flush();
			} catch (IOException e) {
				// log.error("����ҳ���ʱ����IO�쳣");
				e.printStackTrace();
			}
		}
	}

	/**
	 * �������ö��� ��session��request���ֵ���ý�ȥ
	 * 
	 * @param req
	 * @param page
	 * @return
	 */
	public Object reSetPage(HttpServletRequest request, Object page,
			FileItemFactory fileFactory) {
		// �����ַ�
		try {
			request.setCharacterEncoding("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		Field[] fileds = page.getClass().getDeclaredFields();
		HttpSession session = request.getSession();
		// ���е�field��name
		HashSet<String> allFieldSet = new HashSet<String>();
		// ��ͨ���ֵ ��request��parameter��ȥȡ
		HashSet<String> filedSet = new HashSet<String>();
		// ����session�����ֵ ��session��attribute��ȥȡ
		// HashSet<String> sessionFiledSet = new HashSet<String>();

		// ������Ҫ��ҳ����ͼ�д��ݵ�field����
		if(this.PageToPageFiledSet==null||this.PageToPageFiledSet.isEmpty()){
			this.PageToPageFiledSet = new HashSet<String>();
		}
		//HashSet<String> nextPageToPageFiledSet = new HashSet<String>();
		for (int i = 0; i < fileds.length; i++) {
			String fieldName = fileds[i].getName();
			allFieldSet.add(fieldName);
			// �����session���ֵ
			if (fileds[i].getAnnotation(SessionScopeLoad.class) != null) {
				Object sessionField = session.getAttribute(fieldName);
				if (sessionField != null) {
					String setSessionName = PageObjectFieldUtil
							.getSetMothodStringByFieldName(fieldName);
					// ��ȡset����
					Method setSessionMethod;
					try {
						setSessionMethod = page.getClass().getMethod(
								setSessionName, fileds[i].getType());
						// Ȼ����ô˷�����ֵ���ý�ȥ
						setSessionMethod.invoke(page, sessionField);
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			} else if (fileds[i].getAnnotation(P2PScope.class) != null) {
				// �����ҳ��֮��Ĵ��ݵ�
				// ������Ҫҳ�洫�ݵ�field�ļ���
				this.PageToPageFiledSet.add(fieldName);
			} else {
				filedSet.add(fieldName);
			}
		}
		// �ж��Ƿ������ļ����͵�ֵ
		boolean isMulti = ServletFileUpload.isMultipartContent(request);
		// ������ļ����͵�
		if (isMulti) {
			page = resetPageField(request, page, fileFactory, allFieldSet);
		} else {
			// û���ļ����͵�ʱ��
			page = resetPageField(request, page, filedSet);
		}
		return page;
	}

	/**
	 * ����ҳ���sessionֵ�� session��
	 * 
	 * @param req
	 * @param page
	 * @return
	 */
	private Object setSessionScope(ServletRequest req, Object page) {

		Field[] fileds = page.getClass().getDeclaredFields();
		HttpServletRequest request = (HttpServletRequest) req;
		HttpSession session = request.getSession();

		for (int i = 0; i < fileds.length; i++) {
			if (fileds[i].getAnnotation(SessionScopeSave.class) != null) {
				// ��ȡget����
				String fieldName = fileds[i].getName();
				String getMethodName = PageObjectFieldUtil
						.getGetMothodStringByFieldName(fieldName);
				try {
					Method getMethod = page.getClass().getMethod(getMethodName);
					Object sessionField = getMethod.invoke(page);
					session.setAttribute(fieldName, sessionField);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				// ���õ�session��
			}
		}
		return null;
	}

	/**
	 * û���ļ��ϴ���ʱ���
	 * 
	 * @param request
	 * @param page
	 * @return
	 */
	public Object resetPageField(HttpServletRequest request, Object page,
			HashSet<String> filedSet) {
		Enumeration enu = request.getParameterNames();
		// ѭ��������
		while (enu.hasMoreElements()) {
			String paraName = (String) enu.nextElement();
			if (filedSet.contains(paraName)) {
				try {
					String valueObj = request.getParameter(paraName);
					if (valueObj != null) {
						// ��ȡ�˶��������field������
						// Class<?> fieldType =
						// page.getClass().getField(paraName).getType();
						// String paraValue = request.getParameter(paraName);
						String setMethodName = PageObjectFieldUtil
								.getSetMothodStringByFieldName(paraName);

						// ��ȡset����
						Method setMethod = page.getClass().getMethod(
								setMethodName, String.class);
						// Ȼ����ô˷�����ֵ���ý�ȥ
						setMethod.invoke(page, valueObj);
					}
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		return page;
	}

	/**
	 * ��������ļ��ϴ�ʱ�ı? ����field�ķ���
	 * 
	 * @param req
	 * @param page
	 * @param fileFactory
	 * @return
	 */
	public Object resetPageField(HttpServletRequest request, Object page,
			FileItemFactory fileFactory, HashSet<String> allFiledSet) {
		// �ļ��ϴ� ���������
		ServletFileUpload fu = new ServletFileUpload(fileFactory);
		// ���е�field
		try {
			List fileItems = fu.parseRequest(request);
			Iterator<FileItem> iter = fileItems.iterator();
			while (iter.hasNext()) {
				FileItem item = (FileItem) iter.next();
				String fieldName = item.getFieldName();
				// �����������ļ�������б?��Ϣ
				if (!item.isFormField()) {
					// �����ļ���field
					if (allFiledSet.contains(fieldName)) {
						try {
							// ��ȡ�˶�������field��set������
							String setMethodName = PageObjectFieldUtil
									.getSetMothodStringByFieldName(fieldName);
							// ��ȡset����
							Method setMethod = page.getClass().getMethod(
									setMethodName, FileItem.class);
							// Ȼ����ô˷�����ֵ���ý�ȥ
							setMethod.invoke(page, item);
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				} else {
					// �����field������
					// ���field��set�ﺬ�д�field
					if (allFiledSet.contains(fieldName)) {
						try {
							String setMethodName = PageObjectFieldUtil
									.getSetMothodStringByFieldName(fieldName);
							// ��ȡset����
							Method setMethod = page.getClass().getMethod(
									setMethodName, String.class);
							// Ȼ����ô˷�����ֵ���ý�ȥ
							Object valueObj = item.getString();
							setMethod.invoke(page, valueObj);
						} catch (SecurityException e) {
							e.printStackTrace();
						} catch (NoSuchMethodException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		} catch (FileUploadException e1) {
			e1.printStackTrace();
		}
		return page;
	}
}
