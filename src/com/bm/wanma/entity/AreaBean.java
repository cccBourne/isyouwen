package com.bm.wanma.entity;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
/*区域城市表 */

@Table(name = "tb_m_area")
public class AreaBean implements Serializable {
	@Id(column="AREA_ID")
	private String AREA_ID;//区域编码
	private String CITY_ID;//城市编码
    private String PROVINCE_ID;//省份编码
    private String AREA_NAME;//区域名称
    
	//必须包含这个默认的构造方法，否则在进行数据查找时，会报错
	public AreaBean() {
		
	}

	
	public String getCITY_ID() {
		return CITY_ID;
	}


	public void setCITY_ID(String cITY_ID) {
		CITY_ID = cITY_ID;
	}


	public String getPROVINCE_ID() {
		return PROVINCE_ID;
	}

	public void setPROVINCE_ID(String pROVINCE_ID) {
		PROVINCE_ID = pROVINCE_ID;
	}


	public String getAREA_ID() {
		return AREA_ID;
	}


	public void setAREA_ID(String aREA_ID) {
		AREA_ID = aREA_ID;
	}


	public String getAREA_NAME() {
		return AREA_NAME;
	}


	public void setAREA_NAME(String aREA_NAME) {
		AREA_NAME = aREA_NAME;
	}

    

  
}
