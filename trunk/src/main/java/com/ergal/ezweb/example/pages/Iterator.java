package com.ergal.ezweb.example.pages;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ergal.ezweb.annotation.ViewHtml;
import com.ergal.ezweb.annotation.ViewPath;

/**
 * 测试循环迭代的组件
 * @author <a href="mailto:ohergal@gmail.com">ohergal</a>
 *
 */
@ViewPath("/iterator")
@ViewHtml("/example/Iterator.html")
public class Iterator {
	private List<String> articles = Arrays.asList("article1","article2","article3","article4");
	private Set<Blog> blogs = new HashSet<Blog>(Arrays.asList(
			new Blog("blob1","blobcontent1"),
			new Blog("blob2","blobcontent2"),
			new Blog("blob3","blobcontent3"),
			new Blog("blob4","blobcontent4")));
	
	public List getArticles() {
		return articles;
	}
	

	public void setArticles(List<String> articles) {
		this.articles = articles;
	}


	public Object service(String para){
		return null;
	}
	
	public Object service(){
		return null;
	}
	
	
	
	public Set<Blog> getBlogs() {
		return blogs;
	}
	
	public void setBlogs(Set<Blog> blogs) {
		this.blogs = blogs;
	}

	public static class Blog{
		private String blogName;
		private String blogContent;
		public Blog(String bname,String bcontent){
			blogName = bname;
			blogContent = bcontent;
		}
		public String getBlogName() {
			return blogName;
		}
		public void setBlogName(String blogName) {
			this.blogName = blogName;
		}
		public String getBlogContent() {
			return blogContent;
		}
		public void setBlogContent(String blogContent) {
			this.blogContent = blogContent;
		}
	}
	
}
