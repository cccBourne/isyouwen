package com.bm.wanma.ui.activity;

import java.io.StringReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import com.alipay.sdk.app.PayTask;
import com.bm.wanma.R;
import com.bm.wanma.adapter.MyRechargeMoneyGridViewAdapter;
import com.bm.wanma.alipay.PayResult;
import com.bm.wanma.dialog.RechargeSuccesDialog;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.MyDetailGridView;
import com.bm.wanma.weixin.Constants;
import com.bm.wanma.weixin.MD5;
import com.bm.wanma.weixin.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author cm
 * 充值界面
 */
public class RechargeActivity extends BaseActivity implements OnClickListener{
	
	private ImageButton ib_back;
	private TextView tv_commit,tv_current_balance;
	//private EditText et_recharge_money;
	private TextView tv_recharge_money;
	private MyDetailGridView mGridView;
	private RelativeLayout rl_alipay,rl_wxpay;
	private TextView tv_alipay,tv_wxpay;
	private ImageView iv_alipay,iv_wxpay;
	private MyRechargeMoneyGridViewAdapter mAdapter;
	private ArrayList<String> moneTypeList;
	private String pkuserId,userBalance,whichType,rechargemoney;
	private boolean isSelectType;
	//alipay
		private static final int SDK_PAY_FLAG = 1;
		private String aliPayInfo;
		private String WXpayInfo;
		private String userPhone;
		//weixin
		private PayReq req;
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
		private Map<String,String> resultunifiedorder;
		StringBuffer sb;
		private String prepayId;
		private ProgressDialog wxpaydg;
		private RechargeSuccesDialog mSuccesDialog;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_recharge);
		pkuserId = PreferencesUtil.getStringPreferences(this,"pkUserinfo");
		userBalance = PreferencesUtil.getStringPreferences(this, "usinAccountbalance"); 
		userPhone = PreferencesUtil.getStringPreferences(this, "usinPhone"); 
		req = new PayReq();
		sb=new StringBuffer();
		msgApi.registerApp(Constants.APP_ID);
		initView();
		registerBoradcastReceiver();
		
	}

	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.recharge_back);
		ib_back.setOnClickListener(this);
		tv_commit = (TextView) findViewById(R.id.recharge_commit);
		rl_alipay = (RelativeLayout) findViewById(R.id.recharge_alipay_rl);
		rl_alipay.setOnClickListener(this);
		tv_alipay = (TextView) findViewById(R.id.recharge_alipay_tv);
		iv_alipay = (ImageView) findViewById(R.id.recharge_alipay_iv);
		rl_wxpay = (RelativeLayout) findViewById(R.id.recharge_wx_rl);
		rl_wxpay.setOnClickListener(this);
		tv_wxpay = (TextView) findViewById(R.id.recharge_wx_tv);
		iv_wxpay = (ImageView) findViewById(R.id.recharge_wx_iv);
		
		//tv_commit.setOnClickListener(this);
		tv_current_balance = (TextView) findViewById(R.id.recharge_current_balance);
		tv_current_balance.setText(userBalance);
		//et_recharge_money = (EditText) findViewById(R.id.recharge_money_et);
		tv_recharge_money = (TextView) findViewById(R.id.recharge_money_et);
		//et_recharge_money.addTextChangedListener(new MyRegistTextWatch());
		mGridView = (MyDetailGridView) findViewById(R.id.recharge_gridview);
		moneTypeList = new ArrayList<String>();
		moneTypeList.add("10元");
		moneTypeList.add("20元");
		moneTypeList.add("50元");
		moneTypeList.add("100元");
		moneTypeList.add("200元");
		moneTypeList.add("500元");
		mAdapter = new MyRechargeMoneyGridViewAdapter(this, moneTypeList);
		mGridView.setAdapter(mAdapter);
		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				mAdapter.setSelection(position);
				//et_recharge_money.setText(moneTypeList.get(position).substring(0, moneTypeList.get(position).length()-1));
				tv_recharge_money.setText(moneTypeList.get(position).substring(0, moneTypeList.get(position).length()-1));
			}
		});
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		 unregisterReceiver(mBroadcastReceiver);
	}
	
	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);
				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();
				String resultStatus = payResult.getResultStatus();
				LogUtil.i("cm_alipay",resultInfo);
				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
					/*Toast.makeText(RechargeActivity.this, "支付成功",
							Toast.LENGTH_SHORT).show();*/
					mSuccesDialog = new RechargeSuccesDialog(RechargeActivity.this, "充值金额为： "+rechargemoney);
					mSuccesDialog.setCancelable(false);
					mSuccesDialog.setOnPositiveListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							setResult(RESULT_OK);
							mSuccesDialog.dismiss();
							finish();
						}
					});
					mSuccesDialog.show();
					//GetDataPost.getInstance(RechargeActivity.this).getMyWallet(handler, userId);
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(RechargeActivity.this, "支付结果确认中",
								Toast.LENGTH_SHORT).show();
					} else {
						// 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
						Toast.makeText(RechargeActivity.this, "支付失败",
								Toast.LENGTH_SHORT).show();
					}
				}
				break;
			}
			}
		};
	};
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.recharge_back:
			finish();
			
			break;
		case R.id.recharge_commit:
			//去充值
			//rechargemoney = et_recharge_money.getText().toString();
			rechargemoney = tv_recharge_money.getText().toString();
			if("支付宝".equals(whichType)){
				GetDataPost.getInstance(this).getAlipayInfo(handler, "充充币",pkuserId, rechargemoney, userPhone);
			}else if("微信".equals(whichType)){
				Double tempMoney = Double.valueOf(rechargemoney)*100;
				 DecimalFormat df0 = new DecimalFormat("###");
				 //判断是否安装了微信app
				 if(msgApi.isWXAppInstalled()){
					 if(isNetConnection()){
						 	wxpaydg =  ProgressDialog.show(RechargeActivity.this, getString(R.string.wx_tip), getString(R.string.getting_prepayid));
							GetDataPost.getInstance(this).getWXPrepayInfo(handler,pkuserId,"196.168.1.1", "充充币", ""+df0.format(tempMoney), userPhone, "APP");
					 }else {
						 showToast("请，网络不稳，请稍后再试");
					 }
				 } else {showToast("未安装微信客户端,请求失败");}
			}
			break;
		case R.id.recharge_alipay_rl:
			//选择支付宝支付
			whichType = "支付宝";
			isSelectType = true;
			rl_alipay.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_checked));
			tv_alipay.setTextColor(getResources().getColor(R.color.common_orange));
			iv_alipay.setVisibility(View.VISIBLE);
			rl_wxpay.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_uncheck));
			tv_wxpay.setTextColor(getResources().getColor(R.color.common_gray));
			iv_wxpay.setVisibility(View.GONE);
			/*if(!Tools.isEmptyString(et_recharge_money.getText().toString())){
				tv_commit.setOnClickListener(RechargeActivity.this);
				tv_commit.setBackground(getResources().getDrawable(
						R.drawable.popup_select_shape_confirm));
				
			}*/
			if(!Tools.isEmptyString(tv_recharge_money.getText().toString())){
				tv_commit.setOnClickListener(RechargeActivity.this);
				tv_commit.setBackground(getResources().getDrawable(
						R.drawable.popup_select_shape_confirm));
				
			}
			break;
		case R.id.recharge_wx_rl:
			//选择微信支付
			whichType = "微信";
			isSelectType = true;
			rl_wxpay.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_checked));
			tv_wxpay.setTextColor(getResources().getColor(R.color.common_orange));
			iv_wxpay.setVisibility(View.VISIBLE);
			rl_alipay.setBackground(getResources().getDrawable(
					R.drawable.popup_select_shape_uncheck));
			tv_alipay.setTextColor(getResources().getColor(R.color.common_gray));
			iv_alipay.setVisibility(View.GONE);
			/*if(!Tools.isEmptyString(et_recharge_money.getText().toString())){
				tv_commit.setOnClickListener(RechargeActivity.this);
				tv_commit.setBackground(getResources().getDrawable(
						R.drawable.popup_select_shape_confirm));
				
			}*/
			if(!Tools.isEmptyString(tv_recharge_money.getText().toString())){
				tv_commit.setOnClickListener(RechargeActivity.this);
				tv_commit.setBackground(getResources().getDrawable(
						R.drawable.popup_select_shape_confirm));
				
			}
			break;
		default:
			break;
		}
		
	}

	@Override
	protected void getData() {
		
	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		//支付宝获取支付信息alipayInfo
				if (Tools.judgeString(sign, Protocol.AliPayURL)) {
					if(bundle != null){
						aliPayInfo =  (String) bundle.getSerializable(Protocol.DATA);
						LogUtil.i("cm_alipaInfo", aliPayInfo);
						callaliPay();
					}
				}
				//微信获取支付信息WXpayInfo
						if (Tools.judgeString(sign, Protocol.WeiXinPayURL)) {
							if(wxpaydg != null){
								wxpaydg.dismiss();
							}
							if(bundle != null){
								WXpayInfo =  (String) bundle.getSerializable(Protocol.DATA);
								LogUtil.i("cm_WXpaInfo", WXpayInfo);
							Map<String, String> xml = decodeXml(WXpayInfo);
							LogUtil.i("cm_xml",xml+"");
							callWXpay(xml);
							}
						}
		}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		if(LogUtil.isDebug){
			showToast("错误码"+bundle.getString(Protocol.CODE)+"\n"+bundle.getString(Protocol.MSG));
		}else {
			showToast(bundle.getString(Protocol.MSG));
		}
		if(wxpaydg != null){
			wxpaydg.dismiss();
		}

		
	}
	//weixin 调用
		private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String,String>> {
			private ProgressDialog dialog;
			@Override
			protected void onPreExecute() {
				dialog = ProgressDialog.show(RechargeActivity.this, getString(R.string.wx_tip), getString(R.string.getting_prepayid));
			}
			@Override
			protected void onPostExecute(Map<String,String> result) {
				if (dialog != null) {
					dialog.dismiss();
				}
				LogUtil.i("cm_result", result.toString());
				prepayId = result.get("prepay_id");
				if(prepayId != null && !prepayId.isEmpty()){
					LogUtil.i("cm_prepay_id", prepayId);
					resultunifiedorder = result;
					//根据prepayId生成微信支付参数
					genPayReq();
				}
			}

			@Override
			protected void onCancelled() {
				super.onCancelled();
			}

			@Override
			protected Map<String,String>  doInBackground(Void... params) {

				String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
				String entity = genProductArgs();
				LogUtil.i("cm_wx_entity",entity);

				byte[] buf = Util.httpPost(url, entity);

				String content = new String(buf);
				LogUtil.i("cm_wx_content", content);
				Map<String,String> xml=decodeXml(content);
				LogUtil.i("cm_map_xml",xml.toString());
				return xml;
			}
		}
		private void genPayReq() {
			req.appId = Constants.APP_ID;
			req.partnerId = Constants.MCH_ID;
			req.prepayId = resultunifiedorder.get("prepay_id");
			req.packageValue = "Sign=WXPay";
			req.nonceStr = genNonceStr();
			req.timeStamp = String.valueOf(genTimeStamp());
			List<NameValuePair> signParams = new LinkedList<NameValuePair>();
			signParams.add(new BasicNameValuePair("appid", req.appId));
			signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
			signParams.add(new BasicNameValuePair("package", req.packageValue));
			signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
			signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
			signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
			req.sign = genAppSign(signParams);
			LogUtil.i("cm_signParams", signParams.toString());
			//支付发送
			sendPayReq();
		}
		
		private void sendPayReq() {
			msgApi.registerApp(Constants.APP_ID);
			msgApi.sendReq(req);
			
		}
		public Map<String,String> decodeXml(String content) {

			try {
				Map<String, String> xml = new HashMap<String, String>();
				XmlPullParser parser = Xml.newPullParser();
				parser.setInput(new StringReader(content));
				int event = parser.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {

					String nodeName=parser.getName();
					switch (event) {
						case XmlPullParser.START_DOCUMENT:

							break;
						case XmlPullParser.START_TAG:

							if("xml".equals(nodeName)==false){
								//实例化student对象
								xml.put(nodeName,parser.nextText());
							}
							break;
						case XmlPullParser.END_TAG:
							break;
					}
					event = parser.next();
				}

				return xml;
			} catch (Exception e) {
				LogUtil.i("orion",e.toString());
			}
			return null;

		}
		private String genNonceStr() {
			Random random = new Random();
			return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
		}
		
		private long genTimeStamp() {
			return System.currentTimeMillis() / 1000;
		}
		private String genOutTradNo() {
			Random random = new Random();
			return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
		}
		//微信支付调用
		private void callWXpay(Map<String,String> wxpayinfo){
			req.appId = wxpayinfo.get("appid");
			req.partnerId = wxpayinfo.get("partnerid");
			req.prepayId = wxpayinfo.get("prepayid");
			req.packageValue = "Sign=WXPay";
			req.nonceStr = wxpayinfo.get("noncestr");
			req.timeStamp = wxpayinfo.get("timestamp");
			/*req.appId = Constants.APP_ID;
			req.partnerId = Constants.MCH_ID;
			req.prepayId = wxpayinfo.get("prepayid");
			req.packageValue = "Sign=WXPay";
			req.nonceStr = genNonceStr();
			req.timeStamp = String.valueOf(genTimeStamp());*/
			/*LogUtil.i("cm_appid","" + wxpayinfo.get("appid"));
			LogUtil.i("cm_partnerid","" + wxpayinfo.get("partnerid"));
			LogUtil.i("cm_prepayid","" + wxpayinfo.get("prepayid"));
			LogUtil.i("cm_noncestr","" + wxpayinfo.get("noncestr"));
			LogUtil.i("cm_timestamp","" + wxpayinfo.get("timestamp"));*/
			/*List<NameValuePair> signParams = new LinkedList<NameValuePair>();
			signParams.add(new BasicNameValuePair("appid", req.appId));
			signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
			signParams.add(new BasicNameValuePair("package", req.packageValue));
			signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
			signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
			signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
			req.sign = genAppSign(signParams);*/
			req.sign = wxpayinfo.get("sign");
			LogUtil.i("cm_sign","" + wxpayinfo.get("sign"));
			//调用微信支付接口
			msgApi.registerApp(req.appId);
			msgApi.sendReq(req);
			
		} 
		private String genProductArgs() {
			StringBuffer xml = new StringBuffer();

			try {
				String	nonceStr = genNonceStr();
				xml.append("</xml>");
	            List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
				packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
				packageParams.add(new BasicNameValuePair("body", "充充币"));
				packageParams.add(new BasicNameValuePair("mch_id", Constants.MCH_ID));
				packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
				packageParams.add(new BasicNameValuePair("notify_url", "http://www.eichong.com/alipay/notify_url.do"));
				packageParams.add(new BasicNameValuePair("out_trade_no",genOutTradNo()));
				//packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
				packageParams.add(new BasicNameValuePair("spbill_create_ip","196.168.1.1"));
				packageParams.add(new BasicNameValuePair("total_fee", "1"));
				packageParams.add(new BasicNameValuePair("trade_type", "APP"));
				String sign = genPackageSign(packageParams);
				packageParams.add(new BasicNameValuePair("sign", sign));

			    String xmlstring = toXml(packageParams);
				return xmlstring;

			} catch (Exception e) {
				LogUtil.i("cm_weixin", "genProductArgs fail, ex = " + e.getMessage());
				return null;
			}
		}
		@SuppressLint("DefaultLocale")
		private String genAppSign(List<NameValuePair> params) {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < params.size(); i++) {
				sb.append(params.get(i).getName());
				sb.append('=');
				sb.append(params.get(i).getValue());
				sb.append('&');
			}
			sb.append("key=");
			sb.append(Constants.API_KEY);

	       // this.sb.append("sign str\n"+sb.toString()+"\n\n");
			String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
			LogUtil.i("cm_appSign",appSign);
			return appSign;
		}
		/**
		 生成签名
		 */
		@SuppressLint("DefaultLocale")
		private String genPackageSign(List<NameValuePair> params) {
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < params.size(); i++) {
				sb.append(params.get(i).getName());
				sb.append('=');
				sb.append(params.get(i).getValue());
				sb.append('&');
			}
			sb.append("key=");
			sb.append(Constants.API_KEY);

			String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
			LogUtil.i("cm_packageSign",packageSign);
			return packageSign;
		}
		private String toXml(List<NameValuePair> params) {
			StringBuilder sb = new StringBuilder();
			sb.append("<xml>");
			for (int i = 0; i < params.size(); i++) {
				sb.append("<"+params.get(i).getName()+">");
				sb.append(params.get(i).getValue());
				sb.append("</"+params.get(i).getName()+">");
			}
			sb.append("</xml>");
			LogUtil.i("cm_wx_toXml",sb.toString());
			return sb.toString();
		}
		/**
		 * call alipay sdk pay. 调用SDK支付
		 */
		private void callaliPay() {
			
			Runnable payRunnable = new Runnable() {
				@Override
				public void run() {
					// 构造PayTask 对象
					PayTask alipay = new PayTask(RechargeActivity.this);
					// 调用支付接口，获取支付结果
					String result = alipay.pay(aliPayInfo);
					LogUtil.i("cm_result", result);
					Message msg = new Message();
					msg.what = SDK_PAY_FLAG;
					msg.obj = result;
					mHandler.sendMessage(msg);
				}
			};

			// 必须异步调用
			Thread payThread = new Thread(payRunnable);
			payThread.start();
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
				/*String contents = str.toString();
				int length = contents.length();
				if (length == 0) {
					//输入框没值，立即充值置灰
					tv_commit.setOnClickListener(null);
					tv_commit.setBackground(getResources().getDrawable(
							R.drawable.recharge_commit_bg_light_white));
				}else {
					//输入框有值
					tv_commit.setOnClickListener(RechargeActivity.this);
					tv_commit.setBackground(getResources().getDrawable(
							R.drawable.popup_select_shape_confirm));
				}*/
			}
			@SuppressLint("NewApi")
			@Override
			public void afterTextChanged(Editable s) {
				String contents = s.toString();
				int length = contents.length();
				if (length == 0) {
					//输入框没值，立即充值置灰
					tv_commit.setOnClickListener(null);
					tv_commit.setBackground(getResources().getDrawable(
							R.drawable.recharge_commit_bg_light_white));
					mAdapter.setSelection(-1);
				}else {
					//输入框有值
					if(isSelectType){
						tv_commit.setOnClickListener(RechargeActivity.this);
						tv_commit.setBackground(getResources().getDrawable(
								R.drawable.popup_select_shape_confirm));
					}
					if("10".equals(contents)){
						mAdapter.setSelection(0);
					}else if("20".equals(contents)){
						mAdapter.setSelection(1);
					}else if("50".equals(contents)){
						mAdapter.setSelection(2);
					}else if("100".equals(contents)){
						mAdapter.setSelection(3);
					}else if("200".equals(contents)){
						mAdapter.setSelection(4);
					}else if("500".equals(contents)){
						mAdapter.setSelection(5);
					}else {
						mAdapter.setSelection(-1);
					}
				}
			}
		}
	public void registerBoradcastReceiver(){  
	        IntentFilter myIntentFilter = new IntentFilter();  
	        myIntentFilter.addAction("com.bm.wanma.recharge_wx_ok");  
	        //注册广播        
	        registerReceiver(mBroadcastReceiver, myIntentFilter);  
	    }  
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				 if(action.equals("com.bm.wanma.recharge_wx_ok")){  
					 //弹出充值成功对话框
					 mSuccesDialog = new RechargeSuccesDialog(RechargeActivity.this, "充值金额为： "+rechargemoney);
						mSuccesDialog.setCancelable(false);
						mSuccesDialog.setOnPositiveListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								//setResult(RESULT_OK);
								Intent broadIn = new Intent("com.bm.wanma.recharge_wx_ok_refresh");
								sendBroadcast(broadIn);
								mSuccesDialog.dismiss();
								finish();
							}
						});
						mSuccesDialog.show();
		         }  
			}  
	          
	    };  

}
