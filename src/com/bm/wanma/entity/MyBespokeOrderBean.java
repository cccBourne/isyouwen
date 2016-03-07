package com.bm.wanma.entity;

import java.io.Serializable;
/**
 *  我的预约列表实体类 cm
*/

public class MyBespokeOrderBean implements Serializable {
	
	private String bespCreatedate;//创建时间
	private String pkBespoke;//id
	private String elPi_Longitude;
	private String bespBespoketime;//预约时间长
	private String epName;//电桩名称
	private String bespEndTime;//预约结束时间
	private String elPiElectricPileCode;//电桩编码
	private String pk_ElectricPile;//桩体id
	private String epAddress;//地址
	private String bespFrozenamt;//冻结金额
	private String bespBespokeprice;//费率单价
	private String besp_OrderType;//预约单状态 0未支付 1已支付
	private String bespBespokestatus;//预约状态（只有3和4时可以续约和取消预约，5、6不会返回）  1：取消预约 2：结束预约 3：续预约 4：预约中 5:预约确认中6：预约失败
	private String bespElectricpilehead;//枪头id
	private String bespResepaymentcode;//订单编号
	private String elPi_Latitude;
	private String bespBeginTime;//开始时间
	
	
	public String getBespCreatedate() {
		return bespCreatedate;
	}
	public void setBespCreatedate(String bespCreatedate) {
		this.bespCreatedate = bespCreatedate;
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
	public String getEpName() {
		return epName;
	}
	public void setEpName(String epName) {
		this.epName = epName;
	}
	public String getBespEndTime() {
		return bespEndTime;
	}
	public void setBespEndTime(String bespEndTime) {
		this.bespEndTime = bespEndTime;
	}
	public String getElPiElectricPileCode() {
		return elPiElectricPileCode;
	}
	public void setElPiElectricPileCode(String elPiElectricPileCode) {
		this.elPiElectricPileCode = elPiElectricPileCode;
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
	public String getBespBespokeprice() {
		return bespBespokeprice;
	}
	public void setBespBespokeprice(String bespBespokeprice) {
		this.bespBespokeprice = bespBespokeprice;
	}
	public String getBesp_OrderType() {
		return besp_OrderType;
	}
	public void setBesp_OrderType(String besp_OrderType) {
		this.besp_OrderType = besp_OrderType;
	}
	public String getBespBespokestatus() {
		return bespBespokestatus;
	}
	public void setBespBespokestatus(String bespBespokestatus) {
		this.bespBespokestatus = bespBespokestatus;
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
	public String getElPi_Latitude() {
		return elPi_Latitude;
	}
	public void setElPi_Latitude(String elPi_Latitude) {
		this.elPi_Latitude = elPi_Latitude;
	}
	public String getBespBeginTime() {
		return bespBeginTime;
	}
	public void setBespBeginTime(String bespBeginTime) {
		this.bespBeginTime = bespBeginTime;
	}
	
	
	
	
	

}
