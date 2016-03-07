package com.bm.wanma.entity;

import java.io.Serializable;

/**
 * @author cm
 * 预约详情实体类
 */
public class BespokeDetailBean implements Serializable{

	private String pkBespoke;//预约id
	private String elPi_Longitude;
	private String bespBespoketime;//预约时间
	private String powerInterface;
	private String epName;//电桩名称
	private String ePHeElectricpileHeadState;//枪头状态 0空闲中，3预约中，6充电中，9停用中
	private String elPi_ElectricPileCode;//电桩编码
	private String unitPrice;//费率单价
	private String led_flash;//是否有led灯 1有
	private String pk_ElectricPile;//电桩id
	private String epAddress;
	private String bespFrozenamt;// 预约冻结金额
	private String ePHeElectricpileHeadName;//枪头名称
	private String park_num;
	private String elPi_RelevancePowerStation;//电站id 当有此值时再次预约进站详情，当无此值而有桩id时再次预约进桩详情
	private String bespElectricpilehead;//枪头id
	private String bespResepaymentcode;//预约订单编号
	private String park_lock;
	private String ep_num;
	private String bespBeginTime;
	private String bespEndTime;
	private String chargingMode; 
	private String elPi_Latitude;
	private String eleHeadNum;//枪头在电桩中的编号
	
	
	
	
	public String getBespEndTime() {
		return bespEndTime;
	}
	public void setBespEndTime(String bespEndTime) {
		this.bespEndTime = bespEndTime;
	}
	public String getEleHeadNum() {
		return eleHeadNum;
	}
	public void setEleHeadNum(String eleHeadNum) {
		this.eleHeadNum = eleHeadNum;
	}
	public String getPkBespoke() {
		return pkBespoke;
	}
	public void setPkBespoke(String pkBespoke) {
		this.pkBespoke = pkBespoke;
	}
	public String getElPi_Longitude() {
		return elPi_Longitude;
	}
	public void setElPi_Longitude(String elPi_Longitude) {
		this.elPi_Longitude = elPi_Longitude;
	}
	public String getBespBespoketime() {
		return bespBespoketime;
	}
	public void setBespBespoketime(String bespBespoketime) {
		this.bespBespoketime = bespBespoketime;
	}
	public String getPowerInterface() {
		return powerInterface;
	}
	public void setPowerInterface(String powerInterface) {
		this.powerInterface = powerInterface;
	}
	public String getEpName() {
		return epName;
	}
	public void setEpName(String epName) {
		this.epName = epName;
	}
	public String getePHeElectricpileHeadState() {
		return ePHeElectricpileHeadState;
	}
	public void setePHeElectricpileHeadState(String ePHeElectricpileHeadState) {
		this.ePHeElectricpileHeadState = ePHeElectricpileHeadState;
	}
	public String getElPi_ElectricPileCode() {
		return elPi_ElectricPileCode;
	}
	public void setElPi_ElectricPileCode(String elPi_ElectricPileCode) {
		this.elPi_ElectricPileCode = elPi_ElectricPileCode;
	}
	public String getUnitPrice() {
		return unitPrice;
	}
	public void setUnitPrice(String unitPrice) {
		this.unitPrice = unitPrice;
	}
	public String getLed_flash() {
		return led_flash;
	}
	public void setLed_flash(String led_flash) {
		this.led_flash = led_flash;
	}
	public String getPk_ElectricPile() {
		return pk_ElectricPile;
	}
	public void setPk_ElectricPile(String pk_ElectricPile) {
		this.pk_ElectricPile = pk_ElectricPile;
	}
	public String getEpAddress() {
		return epAddress;
	}
	public void setEpAddress(String epAddress) {
		this.epAddress = epAddress;
	}
	public String getBespFrozenamt() {
		return bespFrozenamt;
	}
	public void setBespFrozenamt(String bespFrozenamt) {
		this.bespFrozenamt = bespFrozenamt;
	}
	public String getePHeElectricpileHeadName() {
		return ePHeElectricpileHeadName;
	}
	public void setePHeElectricpileHeadName(String ePHeElectricpileHeadName) {
		this.ePHeElectricpileHeadName = ePHeElectricpileHeadName;
	}
	public String getPark_num() {
		return park_num;
	}
	public void setPark_num(String park_num) {
		this.park_num = park_num;
	}
	public String getElPi_RelevancePowerStation() {
		return elPi_RelevancePowerStation;
	}
	public void setElPi_RelevancePowerStation(String elPi_RelevancePowerStation) {
		this.elPi_RelevancePowerStation = elPi_RelevancePowerStation;
	}
	public String getBespElectricpilehead() {
		return bespElectricpilehead;
	}
	public void setBespElectricpilehead(String bespElectricpilehead) {
		this.bespElectricpilehead = bespElectricpilehead;
	}
	public String getBespResepaymentcode() {
		return bespResepaymentcode;
	}
	public void setBespResepaymentcode(String bespResepaymentcode) {
		this.bespResepaymentcode = bespResepaymentcode;
	}
	public String getPark_lock() {
		return park_lock;
	}
	public void setPark_lock(String park_lock) {
		this.park_lock = park_lock;
	}
	public String getEp_num() {
		return ep_num;
	}
	public void setEp_num(String ep_num) {
		this.ep_num = ep_num;
	}
	public String getBespBeginTime() {
		return bespBeginTime;
	}
	public void setBespBeginTime(String bespBeginTime) {
		this.bespBeginTime = bespBeginTime;
	}
	public String getChargingMode() {
		return chargingMode;
	}
	public void setChargingMode(String chargingMode) {
		this.chargingMode = chargingMode;
	}
	public String getElPi_Latitude() {
		return elPi_Latitude;
	}
	public void setElPi_Latitude(String elPi_Latitude) {
		this.elPi_Latitude = elPi_Latitude;
	}
	
	
	
	
}
