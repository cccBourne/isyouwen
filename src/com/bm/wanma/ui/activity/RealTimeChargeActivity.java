package com.bm.wanma.ui.activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.CancleBespokeDialog;
import com.bm.wanma.dialog.WalletWarningDialog;
import com.bm.wanma.socket.SocketConstant;
import com.bm.wanma.socket.StreamUtil;
import com.bm.wanma.socket.TCPSocketManager;
import com.bm.wanma.ui.scan.ScanSucessActivity;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.TimeUtil;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author cm
 * 充电实时界面
 */
public class RealTimeChargeActivity extends BaseActivity implements OnClickListener,ITcpCallBack{

	private ImageButton ib_back;
	private TextView tv_finish_charge,tv_time,tv_yuchong_money,tv_yichong_power;
	private TextView tv_yichong_money,tv_current_price,tv_soc,tv_soc_tag;
	//private ImageView iv_anim;
	private WebView gifWebView;
	private CancleBespokeDialog mDialog;
	private WalletWarningDialog mFinishChargeD;
	private Intent getIn;
	private String pileNum;
	private TCPSocketManager mTcpSocketManager;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_realtime_charge);
		registerBoradcastReceiver();
		mTcpSocketManager = TCPSocketManager.getInstance(this);
		mTcpSocketManager.setTcpCallback(this);
		//mTcpSocketManager.reopen();
		pileNum = mTcpSocketManager.getPileNum();
		initView();
	} 
	@Override
	protected void onResume() {
		super.onResume();
		mTcpSocketManager.reopen();
		Log.i("cm_socket", "realtime onResume");
	}
	@Override
	protected void onStop() {
		super.onStop();
		Log.i("cm_socket", "realtime onStop");
		mTcpSocketManager.close();
	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView() {
		gifWebView = (WebView) findViewById(R.id.realtime_charge_webview_display);
		WebSettings webSettings = gifWebView.getSettings();
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setJavaScriptEnabled(true);
        
		ib_back = (ImageButton) findViewById(R.id.realtime_charge_back);
		ib_back.setOnClickListener(this);
		tv_finish_charge = (TextView) findViewById(R.id.realtime_charge_finish);
		tv_finish_charge.setOnClickListener(this);
		
		tv_time = (TextView) findViewById(R.id.realtime_charge_time);
		tv_yuchong_money = (TextView) findViewById(R.id.realtime_charge_money);
		tv_current_price = (TextView) findViewById(R.id.realtime_charge_price);
		tv_yichong_power = (TextView) findViewById(R.id.realtime_charge_power);
		tv_yichong_money = (TextView) findViewById(R.id.realtime_charge_consume);
		tv_soc = (TextView) findViewById(R.id.realtime_charge_soc);
		tv_soc_tag = (TextView) findViewById(R.id.realtime_charge_soc_tag_tv);
		//iv_anim = (ImageView) findViewById(R.id.realtime_charge_anim);
		getIn = getIntent();
		if(getIn != null){
			initValue();
		}
		
	}
	private void initValue(){
		//0:空闲3 预约中6 充电中9 故障停用
		int state = getIn.getIntExtra("state", 0);
		if(6 == state){
			//iv_anim.setImageResource(R.drawable.bg_charging_no);
			gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html?t=1");
		}else {
			gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html");
			//iv_anim.setImageResource(R.drawable.bg_charging_ok);
		}
		short tempT = getIn.getShortExtra("chargeTime", (short) 0);
		tv_time.setText(TimeUtil.minConvertDayHourMin(tempT));
		tv_yuchong_money.setText((float)getIn.getIntExtra("yuchong",0)/100+"元");
		short tempfeilv = getIn.getShortExtra("feilv", (short) 0);
		int feilv = tempfeilv & 0xffff ;
		tv_current_price.setText((float)feilv/100+"元/度");
		tv_yichong_power.setText((float)getIn.getIntExtra("diandu", 0)/100+"度");
		tv_yichong_money.setText((float)getIn.getIntExtra("yichong",0)/100+"元");
		//tv_soc.setText(""+getIn.getIntExtra("soc", 0));
		//交流soc隐藏
		tv_soc_tag.setVisibility(View.GONE);
	}
	
	
	//处理返回的tcp报文
	@SuppressLint("SimpleDateFormat")
	@Override
	public void handleTcpPacket(final ByteArrayInputStream result) {
		runOnUiThread(new Runnable() {
			@SuppressLint("NewApi")
			@Override
			public void run() {
				try {
					StreamUtil.readByte(result);//int reason = 
					short cmdtype = StreamUtil.readShort(result);
					switch (cmdtype) {
					case SocketConstant.CMD_TYPE_REAL_DATA:
						//实时信息响应
						int state = StreamUtil.readByte(result);
						short chargeTime = StreamUtil.readShort(result);
						StreamUtil.readShort(result);//short dianya = 
						StreamUtil.readShort(result);//short dianliu = 
						int diandu = StreamUtil.readInt(result);
						short tempfeilv = StreamUtil.readShort(result);
						int feilv = tempfeilv & 0xffff ;
						int yuchong = StreamUtil.readInt(result);
						int yichong = StreamUtil.readInt(result);
						int soc = StreamUtil.readByte(result);
						if(6 == state){
							//充电中
							gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html?t=1");
						}else {
							gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html");
						}
						tv_time.setText(TimeUtil.minConvertDayHourMin(chargeTime));
						tv_yuchong_money.setText((float)yuchong/100+"元");
						tv_current_price.setText((float)feilv/100+"元/度");
						tv_yichong_power.setText((float)diandu/100+"度");
						tv_yichong_money.setText((float)yichong/100+"元");
						//tv_soc.setText(""+soc);
						//交流soc隐藏
						tv_soc_tag.setVisibility(View.GONE);
						break;
					case SocketConstant.CMD_TYPE_STOP_CHARGE:
						cancelPD();
						int stopflag = StreamUtil.readByte(result);
						if(0 == stopflag){
							short error = StreamUtil.readShort(result);
							Log.i("cm_socket", "停止充电失败原因"+ error);
							showToast("停止充电失败,请稍后再试...");
						}else if(1 == stopflag){
							Log.i("cm_socket", "停止充电响应成功");
						}
						break;
					case SocketConstant.CMD_TYPE_YX:
						//遥信数据--桩是否断网
						int isNetWork = StreamUtil.readByte(result);
						switch (isNetWork) {
						case 1:
							//联网
							gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html?t=1");
							tv_finish_charge.setOnClickListener(RealTimeChargeActivity.this);
							tv_finish_charge.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
							
							break;

						case 0 :
							//断网
							showToast("电桩网络通信中断，请稍后再试...");
							gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html");
							tv_finish_charge.setOnClickListener(null);
							tv_finish_charge.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
							break;
						}
						break;
					case SocketConstant.CMD_TYPE_CONNECT:
						int successflag = StreamUtil.readByte(result);
						short errorcode = StreamUtil.readShort(result);
						switch (successflag) {
						case 1:
							int headState = StreamUtil.readByte(result);
							 if(3 == headState || 0 == headState){
								 	gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html");
									tv_finish_charge.setOnClickListener(null);
									tv_finish_charge.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
								 mFinishChargeD = new WalletWarningDialog(RealTimeChargeActivity.this, "充电已结束，请去订单列表查看详情");
								 mFinishChargeD.setCancelable(false);
								 mFinishChargeD.setOnPositiveListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											mFinishChargeD.dismiss();
											finish();
										}
									});
								 mFinishChargeD.show();
								 
								 
								}else if(6 == headState){
									gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html?t=1");
									tv_finish_charge.setOnClickListener(RealTimeChargeActivity.this);
									tv_finish_charge.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
								}  
							
							
							break;
						case 0:
							showErrorCode(errorcode);
							gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html");
							tv_finish_charge.setOnClickListener(null);
							tv_finish_charge.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
							break;
						}
						
						break;
					case SocketConstant.CMD_TYPE_CONSUME_RECORD:
						cancelPD();
						Intent chargefinishIn = new Intent();
						chargefinishIn.setAction(BroadcastUtil.BROADCAST_Charge_CANCLE);
						sendBroadcast(chargefinishIn);
						
						//发送广播，通知充电结束，地图地标消失，
						String order = new String(StreamUtil.readWithLength(result, 21));
						long temps = (long) StreamUtil.readInt(result);
						long tempe = (long) StreamUtil.readInt(result);
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						String startdate = sdf.format(new Date(temps * 1000));
						String enddate = sdf.format(new Date(tempe * 1000));
						String totalpower = String.valueOf((float) StreamUtil
								.readInt(result) / 1000);
						String totalmoney = String.valueOf((float) StreamUtil
								.readInt(result) / 100);
						String servicemoney = String.valueOf((float) StreamUtil
								.readInt(result) / 100);
						String pilePK = String.valueOf(StreamUtil.readInt(result));

						Intent recardIn = new Intent(RealTimeChargeActivity.this,
								ChargeFinishActivity.class);
						recardIn.putExtra("order", order);
						recardIn.putExtra("startdate", startdate);
						recardIn.putExtra("enddate", enddate);
						recardIn.putExtra("totalpower", totalpower);
						recardIn.putExtra("totalmoney", totalmoney);
						recardIn.putExtra("servicemoney", servicemoney);
						recardIn.putExtra("pilePK", pilePK);
						startActivity(recardIn);
						finish();
						break;
						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.realtime_charge_back:
			//跳转到订单列表
			Intent backIn = new Intent();
			backIn.setClass(RealTimeChargeActivity.this, HomeActivity.class);
			backIn.putExtra("tag", "2");
			startActivity(backIn);
			mTcpSocketManager.close();
			setResult(RESULT_OK);
			finish();
			break;
		case R.id.realtime_charge_finish:
			mDialog = new CancleBespokeDialog(RealTimeChargeActivity.this, "是否结束充电？");
			mDialog.setCancelable(false);
			mDialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					//发送结束充电命令
					if(mTcpSocketManager.hasTcpConnection()){
						showPD("正在发送停止充电请求");
						mTcpSocketManager.sendStopChargeCMD();
						mDialog.dismiss();
					}else {
						mTcpSocketManager.reopen();
						showPD("正在发送停止充电请求");
						mTcpSocketManager.sendStopChargeCMD();
						mDialog.dismiss();
					}
					
				}
			});
			mDialog.setOnNegativeListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
					}
				});
			mDialog.show();
			break;

		default:
			break;
		}
		
	}

	@Override
	protected void onStart() {
		super.onStart();
	}
	

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
		
	}

	@Override
	protected void getData() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSuccess(String sign, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Force_Offline); 
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent) {
			  String action = intent.getAction();
			  if(action.equals(BroadcastUtil.BROADCAST_Force_Offline)){ 
				  mTcpSocketManager.close();
				  gifWebView.loadUrl("file:///android_asset/chargedisplay/charge_display_gif.html");
			 }
			
		}
	};
	
	 //网络变化广播接收器
	 public  class NetWorkReceiver extends BroadcastReceiver {  
	        @Override  
	        public void onReceive(Context context, Intent intent) {  
	        	
	        	if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
	        		if(isNetConnection() && isRunningForeground(RealTimeChargeActivity.this)){
	        			Log.i("cm_socket", "网络变化，skcket重连");
	        			mTcpSocketManager.reopen();
	        		}
	        	}
	        }  
	    }  

}
