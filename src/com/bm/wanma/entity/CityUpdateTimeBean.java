package com.bm.wanma.entity;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
/*地图城市数据更新时间表 */

@Table(name = "city_updateTime")
public class CityUpdateTimeBean implements Serializable {
	@Id(column="cityCode")
    private String cityCode;//城市编码
    private String updateTime;//更新时间
    
	//必须包含这个默认的构造方法，否则在进行数据查找时，会报错
	public CityUpdateTimeBean() {
		
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
    

  
}
