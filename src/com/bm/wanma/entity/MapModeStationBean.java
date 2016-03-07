package com.bm.wanma.entity;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;
/*地图模式电站实体类 */

@Table(name = "tb_map_station ")
public class MapModeStationBean extends MapModeBean  {
	@Id(column="electricId")
    private String electricId;//电桩或电站id
    private String electricType;//    1：充电桩/充电树     2：电站  3电动自行车
    private String electricState;//上线 离线
    private String longitude;//经度
    private String latitude;//纬度
    private String cityCode;//城市编码
    private String electricName;//电桩名称
    private String electricAddress;//电桩地址
    private String isAppoint;//是否支持预约 0不支持，1支持
    private String del;//0正常1删除，对于删除的数据应该从本地缓存中移除
    
	//必须包含这个默认的构造方法，否则在进行数据查找时，会报错
	public MapModeStationBean() {
		
	}
    
	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
	}

	public String getIsAppoint() {
		return isAppoint;
	}

	public void setIsAppoint(String isAppoint) {
		this.isAppoint = isAppoint;
	}

	public String getElectricName() {
		return electricName;
	}

	public void setElectricName(String electricName) {
		this.electricName = electricName;
	}

	public String getElectricAddress() {
		return electricAddress;
	}

	public void setElectricAddress(String electricAddress) {
		this.electricAddress = electricAddress;
	}

	public String getElectricId() {
		return electricId;
	}
	public void setElectricId(String electricId) {
		this.electricId = electricId;
	}
	public String getElectricType() {
		return electricType;
	}
	public void setElectricType(String electricType) {
		this.electricType = electricType;
	}
	public String getElectricState() {
		return electricState;
	}
	public void setElectricState(String electricState) {
		this.electricState = electricState;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getCityCode() {
		return cityCode;
	}

	public void setCityCode(String cityCode) {
		this.cityCode = cityCode;
	}

  
}
