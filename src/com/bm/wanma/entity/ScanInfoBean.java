package com.bm.wanma.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 扫描二维码返回实体类
 * @author cm
 */
public class ScanInfoBean implements Serializable {

	/*桩体编号*/
	private String elpiElectricpilecode;
	/*电桩id*/
	private String pkElectricpile;
	/*参数属性(电桩额定功率)*/
	private String elPiPowerSize ;
	/*电桩名称*/
	private String elpiElectricpilename;
	/*参数属性(电桩接口方式)*/
	private String elPiPowerInterface;
	/*枪头编号*/
	private String ePHeElectricpileHeadId;
	/*充电地址 */
	private String elpiElectricpileaddress;
	/*电桩枪头状态（0空闲中，3预约中，6充电中，9停用中）*/
	private String ePHe_ElectricpileHeadState;
	/*充电方式*/
	private String elPiChargingMode;
	/*连接状态，当为0时，立即充电按钮不可用*/
	private String comm_status;
	
	
	
	
	public String getElpiElectricpilecode() {
		return elpiElectricpilecode;
	}
	public void setElpiElectricpilecode(String elpiElectricpilecode) {
		this.elpiElectricpilecode = elpiElectricpilecode;
	}
	public String getPkElectricpile() {
		return pkElectricpile;
	}
	public void setPkElectricpile(String pkElectricpile) {
		this.pkElectricpile = pkElectricpile;
	}
	public String getElPiPowerSize() {
		return elPiPowerSize;
	}
	public void setElPiPowerSize(String elPiPowerSize) {
		this.elPiPowerSize = elPiPowerSize;
	}
	public String getElpiElectricpilename() {
		return elpiElectricpilename;
	}
	public void setElpiElectricpilename(String elpiElectricpilename) {
		this.elpiElectricpilename = elpiElectricpilename;
	}
	public String getElPiPowerInterface() {
		return elPiPowerInterface;
	}
	public void setElPiPowerInterface(String elPiPowerInterface) {
		this.elPiPowerInterface = elPiPowerInterface;
	}
	public String getePHeElectricpileHeadId() {
		return ePHeElectricpileHeadId;
	}
	public void setePHeElectricpileHeadId(String ePHeElectricpileHeadId) {
		this.ePHeElectricpileHeadId = ePHeElectricpileHeadId;
	}
	public String getElpiElectricpileaddress() {
		return elpiElectricpileaddress;
	}
	public void setElpiElectricpileaddress(String elpiElectricpileaddress) {
		this.elpiElectricpileaddress = elpiElectricpileaddress;
	}
	public String getePHe_ElectricpileHeadState() {
		return ePHe_ElectricpileHeadState;
	}
	public void setePHe_ElectricpileHeadState(String ePHe_ElectricpileHeadState) {
		this.ePHe_ElectricpileHeadState = ePHe_ElectricpileHeadState;
	}
	public String getElPiChargingMode() {
		return elPiChargingMode;
	}
	public void setElPiChargingMode(String elPiChargingMode) {
		this.elPiChargingMode = elPiChargingMode;
	}
	public String getComm_status() {
		return comm_status;
	}
	public void setComm_status(String comm_status) {
		this.comm_status = comm_status;
	}
	
	
	
	
	
	
	
	
	
}
