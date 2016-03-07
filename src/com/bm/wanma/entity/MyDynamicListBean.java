package com.bm.wanma.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 活动列表--动态-实体类
 * @author cm
 */
public class MyDynamicListBean implements Serializable {

	/*新闻id*/
	private String pk_release;
	/*新闻简介*/
	private String brief_introduction;
	/*发布时间*/
	private String rele_createdate;
	/*新闻标题*/
	private String rele_title;
	/*1活动中心2企业动态3行业动态*/
	private String rele_usepk ;
	
	
	public String getPk_release() {
		return pk_release;
	}
	public void setPk_release(String pk_release) {
		this.pk_release = pk_release;
	}
	public String getBrief_introduction() {
		return brief_introduction;
	}
	public void setBrief_introduction(String brief_introduction) {
		this.brief_introduction = brief_introduction;
	}
	public String getRele_createdate() {
		return rele_createdate;
	}
	public void setRele_createdate(String rele_createdate) {
		this.rele_createdate = rele_createdate;
	}
	public String getRele_title() {
		return rele_title;
	}
	public void setRele_title(String rele_title) {
		this.rele_title = rele_title;
	}
	public String getRele_usepk() {
		return rele_usepk;
	}
	public void setRele_usepk(String rele_usepk) {
		this.rele_usepk = rele_usepk;
	}
	
	
	
}
