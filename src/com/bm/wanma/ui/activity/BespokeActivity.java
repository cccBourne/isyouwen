package com.bm.wanma.ui.activity;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.bm.wanma.R;
import com.bm.wanma.alipay.Base64;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.PayPasswordDialog;
import com.bm.wanma.entity.BanlanceBean;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.CustomSeekBar;
import com.bm.wanma.view.ProgressItem;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author cm
 * 预约界面
 */
@SuppressLint("NewApi")
public class BespokeActivity extends BaseActivity implements OnClickListener,SeekBar.OnSeekBarChangeListener{

	private String bespokePrice,bespokeElectricId,bespokeHeadId,pkBespoke;
	private String pkUserId,deviceId,bespokeHeadName;
	//private String bespFrozenamt;
	private String bespokeTime,bespBeginTime,bespEndTime,bespBespoketimes;
	private ImageButton ib_back;
	private TextView tv_title,tv_time,tv_price,tv_commit;
	private ImageView iv_price_question;
	private CustomSeekBar seekbar;
	private float totalSpan = 12;
	private int hasBespoke = 0;
	private ArrayList<ProgressItem> progressItemList;
	private ProgressItem mProgressItem;
	private float tempTim;
	private BespokeDetailBean bespokeDetailBean;
	
	private PayPasswordDialog mPayPasswordDialog;
	private String balancemoney,paymoney;
	private BanlanceBean mBanlanceBean;
	private EditText et_paypwd;
	private TextView tv_pwdconfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// No Titlebar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		/*getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
				);*/
		setContentView(R.layout.activity_bespoke);
		bespokePrice = getIntent().getStringExtra("bespBespokeprice");
		if(!Tools.isEmptyString(bespokePrice)){
			bespokePrice = Tools.getSub2Value(bespokePrice);//四舍五入后两位
		}else {
			bespokePrice = "0";
		}
		bespokeElectricId = getIntent().getStringExtra("bespElectricpileid");
		bespokeHeadId = getIntent().getStringExtra("bespElectricpilehead");
		bespokeHeadName = getIntent().getStringExtra("bespElectricpileheadName");
		hasBespoke = getIntent().getIntExtra("bespoke", 0);
		pkBespoke = getIntent().getStringExtra("pkBespoke");
		//showToast("pkBespoke"+pkBespoke);
		deviceId = getDeviceId();
		pkUserId = PreferencesUtil.getStringPreferences(BespokeActivity.this, "pkUserinfo");
		GetDataPost.getInstance(this).getBalance(handler, pkUserId);
		init();
		
	}
	private void init(){
		ib_back = (ImageButton) findViewById(R.id.bespoke_back);
		ib_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.bespoke_title);
		if(!Tools.isEmptyString(bespokeHeadName)){
			 try {
				int i = Integer.valueOf(bespokeHeadName);
				 char c1=(char) (i+64);
				 tv_title.setText(c1 + "号枪头");
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		//tv_title.setText(""+bespokeHeadName);
		tv_time = (TextView) findViewById(R.id.bespoke_time);
		tv_price = (TextView) findViewById(R.id.bespoke_price);
		tv_commit = (TextView) findViewById(R.id.bespoke_commit);
		if(Tools.isEmptyString(pkBespoke)){
			tv_time.setText("请选择预约时间");
			tv_price.setText("预约费用: 0.00 元");
		}else {
			tv_time.setText("请选择续约时间");
			tv_price.setText("续约费用: 0.00 元");
			tv_commit.setText("续约");
		}
		
		//tv_commit.setOnClickListener(this);
		seekbar = (CustomSeekBar) findViewById(R.id.bespoke_seekbar);
		seekbar.setOnSeekBarChangeListener(this);
		iv_price_question = (ImageView) findViewById(R.id.bespoke_price_question);
		iv_price_question.setOnClickListener(this);
		initDataToSeekbar();
		
	}
	private void initDataToSeekbar() {
		progressItemList = new ArrayList<ProgressItem>();
		mProgressItem = new ProgressItem();
		mProgressItem.progressItemPercentage = (hasBespoke / totalSpan) * 100;
		mProgressItem.color = R.color.common_black;
		if (hasBespoke > 0) {
			progressItemList.add(mProgressItem);
		}
		mProgressItem = new ProgressItem();
		mProgressItem.progressItemPercentage = ((12-hasBespoke) / totalSpan) * 100;
		mProgressItem.color = R.color.common_light_gray;
		progressItemList.add(mProgressItem);
		seekbar.setProgress(hasBespoke);
		seekbar.initData(progressItemList);
		seekbar.invalidate();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bespoke_back:
			finish();
			break;
		case R.id.bespoke_price_question:
			Intent priceIn = new Intent();
			priceIn.setClass(this, AboutBespokePriceActivity.class);
			priceIn.putExtra("price", bespokePrice);
			startActivity(priceIn);
			
			break;
		case R.id.bespoke_commit:
			//提交预约请求
			//增加输入支付密码弹出框
			String ispwd = PreferencesUtil.getStringPreferences(BespokeActivity.this, "isPpw");
			if("1".equals(ispwd)){
				
				mPayPasswordDialog = new PayPasswordDialog(BespokeActivity.this,"支付: "+ paymoney+"元", "账号余额: "+balancemoney+"元");
				et_paypwd = (EditText) mPayPasswordDialog.getEditPwd();
				et_paypwd.addTextChangedListener(new MyTextWatch());
				mPayPasswordDialog.setCancelable(false);
				mPayPasswordDialog.setOnNegativeListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mPayPasswordDialog.dismiss();
					}
				});
				mPayPasswordDialog.show();
			}else {
				//设置支付密码
				Intent setpayIn = new Intent();
				setpayIn.setClass(BespokeActivity.this, SetPayPasswordActivity.class);
				startActivity(setpayIn);
			}
			/*if(isNetConnection()){
				SimpleDateFormat formatter = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				Long tsbespoke = Long.parseLong(bespokeTime) * 60 * 1000;
				Date curDate = new Date(System.currentTimeMillis());
				Date endDate = new Date(System.currentTimeMillis() + tsbespoke);
				bespBeginTime = formatter.format(curDate);
				bespEndTime = formatter.format(endDate);
				// 预约时间段 2015-05-07 11:54:34
				bespBespoketimes = bespBeginTime.substring(10,16) + " 至"
						+ bespEndTime.substring(10,16);
				
				showPD("正在提交请求，请稍等...");
				GetDataPost.getInstance(BespokeActivity.this).commitBespoke(handler, pkUserId, pkBespoke, bespokeElectricId,
				bespokeTime, bespBespoketimes, bespokeHeadId, bespFrozenamt, bespokePrice, bespBeginTime, bespEndTime, deviceId);
				
			}else {
				showToast("网络不稳，请稍后再试...");
			}*/
			
			break;
		default:
			break;
		}
		
	} 

	@Override
	protected void getData() {
		
		 
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(bundle != null){
			//预约成功
			if(sign.equals(Protocol.COMMIT_BESPOKE)){
				pkBespoke = bundle.getString(Protocol.DATA);
				if(pkBespoke==null || "0".equals(pkBespoke)){
					//跳转预约列表
					Intent mainIn = new Intent();
					mainIn.setClass(this, HomeActivity.class);
					mainIn.putExtra("tag", "2");
					sendCloseActivity();
					startActivity(mainIn);
					finish();
					
				}else {
					//请求预约详情，跳转到详情界面
					GetDataPost.getInstance(this).getBespokeDetail(handler, pkBespoke);
					sendBespokeActivity();
				}
				 
			}else if(sign.equals(Protocol.MYBESPOKE_DETAIL)){
				//预约详情实体类
				bespokeDetailBean = (BespokeDetailBean) bundle.getSerializable(Protocol.DATA);
				if(bespokeDetailBean != null){
					Intent bespokeDetailIn = new Intent();
					bespokeDetailIn.setClass(this, BespokeDetailActivity.class);
					bespokeDetailIn.putExtra("bespokeDetail", bespokeDetailBean);
					bespokeDetailIn.putExtra("mode", "bespoke");
					bespokeDetailIn.putExtra("downtime", "1");
					sendCloseActivity();
					startActivity(bespokeDetailIn);
					finish();
				}
			}else if(sign.equals(Protocol.BANLANCE)){
				//获取余额
				mBanlanceBean = (BanlanceBean) bundle.getSerializable(Protocol.DATA);
				if(mBanlanceBean != null){
					balancemoney = mBanlanceBean.getUserAB();
					PreferencesUtil.setPreferences(this, "usinAccountbalance", balancemoney);
				}
			}else if(sign.equals(Protocol.CHECK_PAY_PWD)){
				//支付成功，去预约
				mPayPasswordDialog.dismiss();
				if(isNetConnection()){
					SimpleDateFormat formatter = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					Long tsbespoke = Long.parseLong(bespokeTime) * 60 * 1000;
					Date curDate = new Date(System.currentTimeMillis());
					Date endDate = new Date(System.currentTimeMillis() + tsbespoke);
					bespBeginTime = formatter.format(curDate);
					bespEndTime = formatter.format(endDate);
					// 预约时间段 2015-05-07 11:54:34
					bespBespoketimes = bespBeginTime.substring(10,16) + " 至"
							+ bespEndTime.substring(10,16);
					
					showPD("正在提交请求，请稍等...");
					GetDataPost.getInstance(BespokeActivity.this).commitBespoke(handler, pkUserId, pkBespoke, bespokeElectricId,
					bespokeTime, bespBespoketimes, bespokeHeadId, paymoney, bespokePrice, bespBeginTime, bespEndTime, deviceId);
					
				}else {
					showToast("网络不稳，请稍后再试...");
				}
				
			}
			
		}
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));

	}
	//seekbar 滑动变化时，在这处理
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		if(progress<hasBespoke){
			seekBar.setProgress(hasBespoke);
		}else {
			progressItemList.clear();
			mProgressItem = new ProgressItem();
			float tee = mProgressItem.progressItemPercentage = ((hasBespoke / totalSpan) * 100);
			mProgressItem.color = R.color.common_black;
			if (hasBespoke > 0) {
				progressItemList.add(mProgressItem);
			}
			mProgressItem = new ProgressItem();
			float te = mProgressItem.progressItemPercentage = ((Math.abs(progress-hasBespoke) / totalSpan) * 100);
			mProgressItem.color = R.color.common_orange;
			if(te>0){
				progressItemList.add(mProgressItem);
			}
			mProgressItem = new ProgressItem();
			mProgressItem.progressItemPercentage = 100-te-tee;
			mProgressItem.color = R.color.common_light_gray;
			if(mProgressItem.progressItemPercentage > 0){
				progressItemList.add(mProgressItem);
			}
			seekbar.initData(progressItemList);
			seekbar.invalidate();
			if(hasBespoke == 0){//预约
				if(progress == 0){
					tv_time.setText("请选择预约时间");
					tv_price.setText("预约费用： 0.00 元");
					tv_commit.setOnClickListener(null);
					tv_commit.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
				}else {
					tempTim = (float)progress / 2;
					tv_time.setText("预约时间: "+tempTim+" 小时");
					//float tempFroz = progress * 30 * Float.valueOf(bespokePrice);
					float tempFroz = tempTim * 60 * Float.valueOf(bespokePrice);
					DecimalFormat decimalFormat= new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
					paymoney = decimalFormat.format(tempFroz);//format 返回的是字符串
					tv_price.setText("预约费用： "+ paymoney + " 元");
					bespokeTime = String.valueOf(progress * 30);
					tv_commit.setOnClickListener(this);
					tv_commit.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
				}
				
			}else {//续约
				if(Math.abs(progress-hasBespoke) == 0){
					tv_time.setText("请选择续约时间");
					tv_price.setText("续约费用： 0.00 元");
					tv_commit.setOnClickListener(null);
					tv_commit.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
					tv_commit.setText("续约");
				}else {
					tempTim = (float)Math.abs(progress-hasBespoke) / 2;
					tv_time.setText("续约时间: "+tempTim+" 小时");
					float tempFroz = tempTim * 60 * Float.valueOf(bespokePrice);
					DecimalFormat decimalFormat= new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
					paymoney = decimalFormat.format(tempFroz);//format 返回的是字符串
					tv_price.setText("续约费用： "+paymoney+ " 元");
					bespokeTime = String.valueOf(Math.abs(progress-hasBespoke) * 30);
					tv_commit.setOnClickListener(this);
					tv_commit.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
					tv_commit.setText("续约");
				}
			}
			
		}
	}
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		
	}
	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		
	}
	private void sendCloseActivity(){
		
		 Intent intnet = new Intent(BroadcastUtil.BROADCAST_Bespoke_OK);
		 intnet.putExtra("bespokePK", pkBespoke);
		 sendBroadcast(intnet);
	}
	private void sendBespokeActivity(){
		
		 Intent intnet = new Intent(BroadcastUtil.BROADCAST_Bespoke_OK_VISIBLE);
		 intnet.putExtra("bespokePK", pkBespoke);
		 sendBroadcast(intnet);
	}
	private class MyTextWatch implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
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
						String uid = PreferencesUtil.getStringPreferences(BespokeActivity.this, "pkUserinfo");
						String userPhone = PreferencesUtil.getStringPreferences(BespokeActivity.this, "usinPhone");
						String pwd = et_paypwd.getText().toString().trim();
						pwd = Tools.encoderByMd5(pwd);
						StringBuilder repwd1 = new StringBuilder();
						StringBuilder repwd2 = new StringBuilder();
						repwd1 = repwd1.append(pwd).append(userPhone);
						pwd = Tools.encoderByMd5(repwd1.toString());
						String random = Tools.getRandomChar(1);
						pwd = repwd2.append(pwd).append(random).toString();
						
						if(isNetConnection()){
							GetDataPost.getInstance(BespokeActivity.this).checkPayPwd(uid, pwd, handler);
						}else {
							showToast("网络不稳，请稍后再试");
						}
					}
				});
			}
			
		}
		
	}
	
	
	
	
	//获取设备id
			public String getDeviceId(){
				TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				String deviceId = tm.getDeviceId(); 
				deviceId = Tools.encoderByMd5(deviceId);
		   		char[] chars = deviceId.toCharArray();
		   		String encodeID = "";
		   		for (int i = 0; i < chars.length; i++) {
		   			encodeID += Tools.replace((byte) chars[i]);
		   		}
		   		encodeID = Base64.encode(encodeID.getBytes()); 
		   		return encodeID;
		   	}

}
