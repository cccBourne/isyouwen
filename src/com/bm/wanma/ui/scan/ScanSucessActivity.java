package com.bm.wanma.ui.scan;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import com.bm.wanma.R;
import com.bm.wanma.entity.ScanInfoBean;
import com.bm.wanma.socket.SocketConstant;
import com.bm.wanma.socket.StreamUtil;
import com.bm.wanma.socket.TCPSocketManager;
import com.bm.wanma.ui.activity.BaseActivity;
import com.bm.wanma.ui.activity.ITcpCallBack;
import com.bm.wanma.ui.activity.RealTimeChargeActivity;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 扫描成功的的页面(扫描后的详情页面)
 */
public class ScanSucessActivity extends BaseActivity implements OnClickListener,ITcpCallBack{
	private ImageButton ib_back;
	private TextView tv_address,tv_charge_mode,tv_charge_interfance;
	private TextView tv_head,tv_power,tv_next;
	private String elpiElectricpilecode,ePHeElectricpileHeadId;//桩体编号,枪头编号
	private ScanInfoBean mInfoBean;
	private TCPSocketManager mTcpSocketManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_success);
		mInfoBean = (ScanInfoBean) getIntent().getSerializableExtra("scanInfo");
		if(null == mInfoBean){
			finish();
		}
		//实例化TCPSocketManager，连接充电桩
		mTcpSocketManager = TCPSocketManager.getInstance(this);
		elpiElectricpilecode = mInfoBean.getElpiElectricpilecode();
		ePHeElectricpileHeadId =  mInfoBean.getePHeElectricpileHeadId();
		try {
			mTcpSocketManager.setTcpCallback(this);
			mTcpSocketManager.open(elpiElectricpilecode,Byte.parseByte(ePHeElectricpileHeadId));
		} catch (NumberFormatException e) {
			e.printStackTrace();
			Log.i("cm_socket", "枪头编号异常，不是数字格式");
		}
		initView();
	}
	
	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.scan_success_back);
		ib_back.setOnClickListener(this);
		tv_address = (TextView) findViewById(R.id.scan_success_addr);
		tv_charge_mode = (TextView) findViewById(R.id.scan_success_mode);
		tv_charge_interfance = (TextView) findViewById(R.id.scan_success_interface);
		tv_head = (TextView) findViewById(R.id.scan_success_head);
		tv_power = (TextView) findViewById(R.id.scan_success_power);
		tv_next = (TextView) findViewById(R.id.scan_success_next);
		initValue();
	}

	@SuppressLint("NewApi")
	private void initValue(){
			tv_address.setText(""+mInfoBean.getElpiElectricpileaddress());
			tv_charge_mode.setText(""+mInfoBean.getElPiChargingMode());
			tv_charge_interfance.setText(""+mInfoBean.getElPiPowerInterface());
			String headnum = mInfoBean.getePHeElectricpileHeadId();
			if(!Tools.isEmptyString(headnum)){
				int i = Integer.valueOf(headnum);
				 char c1=(char) (i+64);
				 tv_head.setText(c1 + "号枪头");
			}
			//tv_head.setText(""+mInfoBean.getePHeElectricpileHeadId()); 
			tv_power.setText(""+mInfoBean.getElPiPowerSize());
		}
	
	//回调，处理tcp返回的报文
	@Override
	public void handleTcpPacket(final ByteArrayInputStream result){
		
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				 try {
						int reason = StreamUtil.readByte(result);
						short cmdtype = StreamUtil.readShort(result);
						switch (cmdtype) {
						case SocketConstant.CMD_TYPE_CONNECT:
							int successflag = StreamUtil.readByte(result);
							short errorcode = StreamUtil.readShort(result);
							int headState = StreamUtil.readByte(result);
							handleConnectEvent(successflag,errorcode,headState);
							break;
						case SocketConstant.CMD_TYPE_REAL_DATA:
							// 收到实时数据，进入实时数据界面
							int state = StreamUtil.readByte(result);
							short chargeTime = StreamUtil.readShort(result);
							StreamUtil.readShort(result);//short dianya = 
							StreamUtil.readShort(result);//short dianliu = 
							int diandu = StreamUtil.readInt(result);
							short feilv = StreamUtil.readShort(result);
							int yuchong = StreamUtil.readInt(result);
							int yichong = StreamUtil.readInt(result);
							int soc = StreamUtil.readByte(result);
							StreamUtil.readInt(result);//int fushu = 
							StreamUtil.readInt(result);//int gaojing = 
							Intent realIn = new Intent(ScanSucessActivity.this,
									RealTimeChargeActivity.class);
							realIn.putExtra("state", state);
							realIn.putExtra("chargeTime", chargeTime);
							realIn.putExtra("diandu", diandu);
							realIn.putExtra("feilv", feilv);
							realIn.putExtra("yuchong", yuchong);
							realIn.putExtra("yichong", yichong);
							realIn.putExtra("soc", soc);
							startActivityForResult(realIn, 0x19);
							break;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
			}
		});
		
	}
	
	@SuppressLint("NewApi")
	private void handleConnectEvent(int successflag,short errorcode,int headState){
		switch (successflag) {
		case 1:
		    if(3 == headState || 0 == headState){
				//下一步变亮
		    	tv_next.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
				tv_next.setOnClickListener(ScanSucessActivity.this);
			}else if(6 == headState){
				/*Intent displayIn = new Intent();
				displayIn.setClass(ScanSucessActivity.this, RealTimeChargeActivity.class);
				//showToast("headstate"+headState);
				startActivityForResult(displayIn, 0x19);*/
			}else if(5 == headState){
				showToast("请插枪");
			}    
			break;

		case 0:
			showErrorCode(errorcode);
			
			break;
		}
		
		
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		mTcpSocketManager.reopen();
	}
	/*@Override
	protected void onStop() {
		super.onStop();
		mTcpSocketManager.close();
	}*/
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTcpSocketManager.close();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_success_back:
			finish();
			break;
		case R.id.scan_success_next:
			//下一步，预充金额
			Intent in = new Intent();
			in.setClass(ScanSucessActivity.this,ScanSucessNextActivity.class);
			startActivityForResult(in, 0x19);
			
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(0x19 == requestCode && resultCode == RESULT_OK){
			setResult(RESULT_OK);
			finish();
		}
	}
	
	@Override
	protected void getData() {
		
		
	}
	
	
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		 
	}
	@Override
	public void onFaile(String sign, Bundle bundle) {
		
	}

		
}
