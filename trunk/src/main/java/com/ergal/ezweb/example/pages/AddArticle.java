package com.ergal.ezweb.example.pages;

import org.apache.commons.fileupload.FileItem;

import com.ergal.ezweb.annotation.ViewHtml;
import com.ergal.ezweb.annotation.ViewPath;


@ViewPath("/article/add")
@ViewHtml("/example/Article.html")
public class AddArticle {
	private String article_title;
	private String article_content;
	private FileItem image_file;
	public String getArticle_title() {
		return article_title;
	}
	public void setArticle_title(String article_title) {
		this.article_title = article_title;
	}
	public String getArticle_content() {
		return article_content;
	}
	public void setArticle_content(String article_content) {
		this.article_content = article_content;
	}
	public FileItem getImage_file() {
		return image_file;
	}
	public void setImage_file(FileItem image_file) {
		this.image_file = image_file;
	}
		
	public void service(String para){
	}
	
	public void service(){
	}
	
}
