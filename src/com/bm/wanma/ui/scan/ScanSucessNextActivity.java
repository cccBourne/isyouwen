package com.bm.wanma.ui.scan;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.PayPasswordDialog;
import com.bm.wanma.dialog.TipInsertGunDialog;
import com.bm.wanma.dialog.WalletWarningDialog;
import com.bm.wanma.entity.BanlanceBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.socket.SocketConstant;
import com.bm.wanma.socket.StreamUtil;
import com.bm.wanma.socket.TCPSocketManager;
import com.bm.wanma.ui.activity.BaseActivity;
import com.bm.wanma.ui.activity.ITcpCallBack;
import com.bm.wanma.ui.activity.RealTimeChargeActivity;
import com.bm.wanma.ui.activity.SetPayPasswordActivity;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 扫描成功下一步的页面(预充金额)
 */
@SuppressLint("NewApi")
public class ScanSucessNextActivity extends BaseActivity implements OnClickListener,ITcpCallBack{
	private ImageButton ib_back;
	private TextView tv_balance,tv_charge_commit,tv_pwdconfirm;
	private EditText et_money,et_paypwd;
	private String pkuserinfo,balancemoney;
	private String pileNum;
	private byte headnum;
	private BanlanceBean mBanlanceBean;
	private PayPasswordDialog mPayPasswordDialog;
	private TipInsertGunDialog mInsertGunDialog;
	private WalletWarningDialog mFinishChargeD;
	private TCPSocketManager mTcpSocketManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_scan_success_next);
		initView();
	}
	
	
	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.scan_success_next_back);
		ib_back.setOnClickListener(this);
		tv_balance = (TextView) findViewById(R.id.scan_success_next_current_balance);
		tv_charge_commit = (TextView) findViewById(R.id.scan_success_next_charge);
		et_money = (EditText) findViewById(R.id.scan_success_next_charge_money_et);
		et_money.addTextChangedListener(new MyRegistTextWatch());
		tv_balance.setText("账户余额： "+PreferencesUtil.getStringPreferences(this,"usinAccountbalance")+"元");
		
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_success_next_back:
			finish();
			break;
		case R.id.scan_success_next_charge:
			//立即充电,弹出支付框
			String ispwd = PreferencesUtil.getStringPreferences(ScanSucessNextActivity.this, "isPpw");
			if("1".equals(ispwd)){
				String paymoney = et_money.getText().toString().trim();
				mPayPasswordDialog = new PayPasswordDialog(ScanSucessNextActivity.this,"支付: "+ paymoney+"元", "账号余额: "+balancemoney+"元");
				et_paypwd = (EditText) mPayPasswordDialog.getEditPwd();
				et_paypwd.addTextChangedListener(new MyTextWatch());
				mPayPasswordDialog.setCancelable(false);
				mPayPasswordDialog.setOnNegativeListener(new OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(View v) {
						mPayPasswordDialog.dismiss();
						tv_charge_commit.setOnClickListener(ScanSucessNextActivity.this);
						tv_charge_commit.setBackground(getResources().getDrawable(
								R.drawable.popup_select_shape_confirm));
					}
				});
				tv_charge_commit.setOnClickListener(null);
				tv_charge_commit.setBackground(getResources().getDrawable(
						R.drawable.recharge_commit_bg_light_white));
				mPayPasswordDialog.show();
			}else {
				//设置支付密码
				Intent setpayIn = new Intent();
				setpayIn.setClass(ScanSucessNextActivity.this, SetPayPasswordActivity.class);
				startActivity(setpayIn);
			}
		
			break;
		default:
			break;
		}
		
	}
	
	@Override
	protected void getData() {
		pkuserinfo = PreferencesUtil.getStringPreferences(this,"pkUserinfo");
		if(isNetConnection()){
			GetDataPost.getInstance(this).getBalance(handler, pkuserinfo);
		}
		
	}
	
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		if(null != bundle){
			if(sign.equals(Protocol.BANLANCE)){
				mBanlanceBean = (BanlanceBean) bundle.getSerializable(Protocol.DATA);
				if(mBanlanceBean != null){
					balancemoney = mBanlanceBean.getUserAB();
					tv_balance.setText("账户余额： "+balancemoney+"元");
					if(balancemoney!= null && Float.valueOf(balancemoney) >= 200){
						et_money.setText("200");
						
					}else if(balancemoney!= null &&Float.valueOf(balancemoney)>1 &&Float.valueOf(balancemoney) < 200){
						//et_money.setText(balancemoney.substring(0, balancemoney.indexOf('.')));
						et_money.setText(balancemoney);
					}else if(balancemoney!= null &&Float.valueOf(balancemoney)<1){
						showToast("余额不足，请去充值");
					} 
				}
			}else if(sign.equals(Protocol.CHECK_PAY_PWD)){
				//支付成功，发送开始充电命令
				mPayPasswordDialog.dismiss();
				mTcpSocketManager = TCPSocketManager.getInstance(ScanSucessNextActivity.this);
				mTcpSocketManager.reopen();
				if(mTcpSocketManager.hasTcpConnection()){
					showPD("正在发送充电请求...");
					mTcpSocketManager.setTcpCallback(this);
					mTcpSocketManager.sendStartChargeCMD(et_money.getText().toString().trim());
				}else {
					showToast("通讯异常，请稍后重试...");
					mTcpSocketManager.reopen();
				}
			}
			 
		}
	}
	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
		if(sign.equals(Protocol.CHECK_PAY_PWD)){
			tv_charge_commit.setOnClickListener(ScanSucessNextActivity.this);
			tv_charge_commit.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_confirm));
		}
	}
	private class MyRegistTextWatch implements TextWatcher{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		@SuppressLint("NewApi")
		@Override
		public void onTextChanged(CharSequence str, int start, int before,
				int count) {
		
		}
		@SuppressLint("NewApi")
		@Override
		public void afterTextChanged(Editable s) {
			 String contents = s.toString().trim();
			 //Pattern pattern = Pattern.compile("^([1-9][0-9]*)$");//开头非零
			 Pattern pattern = Pattern.compile("^[^0].*");
		     Matcher matcher = pattern.matcher(contents);
			int length = contents.length();
			 et_money.setSelection(length);
			if (length == 0 ) {
				//输入框没值，立即充值置灰
				tv_charge_commit.setOnClickListener(null);
				tv_charge_commit.setBackground(getResources().getDrawable(
						R.drawable.recharge_commit_bg_light_white));
			}else if(length > 0 && matcher.matches()){
				 BigDecimal a = new BigDecimal(contents);
			     BigDecimal b = new BigDecimal(balancemoney);
			     BigDecimal c = new BigDecimal(200);
				if(a.compareTo(b)<=0){
					tv_charge_commit.setOnClickListener(ScanSucessNextActivity.this);
					tv_charge_commit.setBackground(getResources().getDrawable(
							R.drawable.popup_select_shape_confirm));
				}else {
					showToast("输入金额不能大于账号余额");
					tv_charge_commit.setOnClickListener(null);
					tv_charge_commit.setBackground(getResources().getDrawable(
							R.drawable.recharge_commit_bg_light_white));
					return ;
				}
				if(a.compareTo(c)<=0){
					tv_charge_commit.setOnClickListener(ScanSucessNextActivity.this);
					tv_charge_commit.setBackground(getResources().getDrawable(
							R.drawable.popup_select_shape_confirm));
				}else {
					showToast("输入金额不能大于200");
					tv_charge_commit.setOnClickListener(null);
					tv_charge_commit.setBackground(getResources().getDrawable(
							R.drawable.recharge_commit_bg_light_white));
				}
				
			}else if(length > 0 && !matcher.matches()){
				tv_charge_commit.setOnClickListener(null);
				tv_charge_commit.setBackground(getResources().getDrawable(
						R.drawable.recharge_commit_bg_light_white));
				//弹出框，提示输入最小金额为1
				showToast("最小输入金额需大于等于1元");
			}
		}
	}
	private class MyTextWatch implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			
		}
				
		@SuppressLint("NewApi")
		@Override
		public void afterTextChanged(Editable str) {
			String contents = str.toString();
			int length = contents.length();
			if(length == 0){
				tv_pwdconfirm = (TextView) mPayPasswordDialog.getPositiveButton();
				int _pL = tv_pwdconfirm.getPaddingLeft();
				int _pT = tv_pwdconfirm.getPaddingTop();
				int _pR = tv_pwdconfirm.getPaddingRight();
				int _pB = tv_pwdconfirm.getPaddingBottom();
				tv_pwdconfirm.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
				tv_pwdconfirm.setPadding(_pL, _pT, _pR, _pB);
				mPayPasswordDialog.setOnPositiveListener(null);
			}else {
				tv_pwdconfirm = (TextView) mPayPasswordDialog.getPositiveButton();
				int _pL = tv_pwdconfirm.getPaddingLeft();
				int _pT = tv_pwdconfirm.getPaddingTop();
				int _pR = tv_pwdconfirm.getPaddingRight();
				int _pB = tv_pwdconfirm.getPaddingBottom();
				tv_pwdconfirm.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
				tv_pwdconfirm.setPadding(_pL, _pT, _pR, _pB);
				mPayPasswordDialog.setOnPositiveListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//检查支付密码
						String uid = PreferencesUtil.getStringPreferences(ScanSucessNextActivity.this, "pkUserinfo");
						String userPhone = PreferencesUtil.getStringPreferences(ScanSucessNextActivity.this, "usinPhone");
						String pwd = et_paypwd.getText().toString().trim();
						pwd = Tools.encoderByMd5(pwd);
						StringBuilder repwd1 = new StringBuilder();
						StringBuilder repwd2 = new StringBuilder();
						repwd1 = repwd1.append(pwd).append(userPhone);
						pwd = Tools.encoderByMd5(repwd1.toString());
						String random = Tools.getRandomChar(1);
						pwd = repwd2.append(pwd).append(random).toString();
						if(isNetConnection()){
							showPD("正在校验支付密码，请稍等...");
							GetDataPost.getInstance(ScanSucessNextActivity.this).checkPayPwd(uid, pwd, handler);
						}else {
							showToast("网络不稳，请稍后再试");
						}
					}
				});
			}
			
		}
		
	}
	//处理连接充电桩事件
	private void handleConnectEvent(int successflag,short errorcode,int headState){
		switch (successflag) {
		case 1:
			if(5 == headState){
				//提示插枪状态
				if(mInsertGunDialog == null){
					mInsertGunDialog = new TipInsertGunDialog(
							ScanSucessNextActivity.this);
				}
				mInsertGunDialog.setInVisible();
				//mInsertGunDialog.setCancelable(false);
				mInsertGunDialog
						.setOnPositiveListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// 取消充电
								mInsertGunDialog.dismiss();
							}
						}); 
				if(!mInsertGunDialog.isShowing()){
					mInsertGunDialog.show();
				}
			}
		
			break;
		case 0:
			showErrorCode(errorcode);
			tv_charge_commit.setOnClickListener(ScanSucessNextActivity.this);
			tv_charge_commit.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_confirm));
			break;
		}
	}
	//处理开始充电时间
	private void handleStartChargeEvent(int startflag,short errorCode){
		
		if(0 == startflag){
			Log.i("cm_socket", "开始充电失败原因"+ errorCode);
			showErrorCode(errorCode);
			tv_charge_commit.setOnClickListener(ScanSucessNextActivity.this);
			tv_charge_commit.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_confirm));
		}else if(1 == startflag){
			Log.i("cm_socket", "开始充电响应成功");
			// 充电成功 , 弹框提示插枪
			if(mInsertGunDialog == null){
				mInsertGunDialog = new TipInsertGunDialog(
						ScanSucessNextActivity.this);
			}
			mInsertGunDialog.setInVisible();
			//mInsertGunDialog.setCancelable(false);
			mInsertGunDialog
					.setOnPositiveListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 取消充电
							mInsertGunDialog.dismiss();
							// mTcpSocketManager.sendStopChargeCMD();
							//mTcpSocketManager.close();
							//finish();
						}
					}); 
			mInsertGunDialog.show();
		}
	}
	
	//处理充电事件--放弃或开始
	private void handleChargeEvent(int eventcode){
		if(0 == eventcode){
			if(mInsertGunDialog !=null){
				mInsertGunDialog.dismiss();
			}
			 mFinishChargeD = new WalletWarningDialog(ScanSucessNextActivity.this, "由于长时间未插枪，本次充电自动放弃！");
			 mFinishChargeD.setCancelable(false);
			 mFinishChargeD.setOnPositiveListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mFinishChargeD.dismiss();
						setResult(RESULT_OK);
						finish();
					}
				});
			 mFinishChargeD.show();
			mTcpSocketManager.close();
		}
	}
	
	//处理返回的tcp报文
	@Override
	public void handleTcpPacket(final ByteArrayInputStream result) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				cancelPD();
				try {
					StreamUtil.readByte(result);//int reason = 
					short cmdtype = StreamUtil.readShort(result);
					switch (cmdtype) {
					case SocketConstant.CMD_TYPE_CONNECT:
						int successflag = StreamUtil.readByte(result);
						short errorcode = StreamUtil.readShort(result);
						int headState = StreamUtil.readByte(result);
						handleConnectEvent(successflag,errorcode,headState);
						
						break;
					case SocketConstant.CMD_TYPE_START_CHARGE:
						//开始充电响应
						int startflag = StreamUtil.readByte(result);
						short errorCode = StreamUtil.readShort(result);
						handleStartChargeEvent(startflag,errorCode);
						break;
					case SocketConstant.CMD_TYPE_CHARGE_EVENT:
						//充电事件 1:充电开始 0:放弃充电
						int eventcode = StreamUtil.readByte(result);
						handleChargeEvent(eventcode);
						
						break;
						
					case SocketConstant.CMD_TYPE_REAL_DATA:
						// 收到实时数据，进入实时数据界面
						if(mInsertGunDialog !=null){
							mInsertGunDialog.dismiss();
						}
					
						Intent chargeStartIn = new Intent();
						chargeStartIn.setAction(BroadcastUtil.BROADCAST_Charge_Ing);
						pileNum = mTcpSocketManager.getPileNum();
						headnum = mTcpSocketManager.getHeadNum();
						
						chargeStartIn.putExtra("chargepilenum",pileNum );
						chargeStartIn.putExtra("chargeheadnum",Byte.toString(headnum));
						sendBroadcast(chargeStartIn);
						
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
						Intent realIn = new Intent(ScanSucessNextActivity.this,
								RealTimeChargeActivity.class);
						realIn.putExtra("state", state);
						realIn.putExtra("chargeTime", chargeTime);
						realIn.putExtra("diandu", diandu);
						realIn.putExtra("feilv", feilv);
						realIn.putExtra("yuchong", yuchong);
						realIn.putExtra("yichong", yichong);
						realIn.putExtra("soc", soc);
						setResult(RESULT_OK);
						startActivity(realIn);
						finish();
						break;
					}
					 
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
}
