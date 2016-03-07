package com.bm.wanma.ui.activity;

import java.util.ArrayList;
import java.util.Calendar;

import com.bm.wanma.R;
import com.bm.wanma.adapter.MyWalletAdapter;
import com.bm.wanma.dialog.DoubleDatePickerDialog;
import com.bm.wanma.dialog.RechargeSuccesDialog;
import com.bm.wanma.dialog.WalletWarningDialog;
import com.bm.wanma.entity.WalletBean;
import com.bm.wanma.entity.WalletList;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.popup.MywalletTypePop;
import com.bm.wanma.popup.SearchPop.SearchCallBack;
import com.bm.wanma.utils.PreferencesUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author cm
 * 我的钱包
 *
 */
public class MyWalletActivity extends BaseActivity implements OnClickListener{

	private ImageButton ib_back;
	private TextView tv_balance,tv_charge,tv_type;
	private TextView tv_time_start;
	private TextView tv_time_end;
	private ListView mListView;
	private String pkuserId,type;
	//消费 收益
	private ArrayList<WalletBean> xiaofeiList,shouyiList,tempcustomList;
	private WalletList walletListBean;
	private ArrayList<String> customTypeList = new ArrayList<String>();
	private MywalletTypePop select_type;
	private MyWalletAdapter mMyWalletAdapter;
	private int mYear,mYear1;
	private int mMonth,mMonth1; 
	private int mDay,mDay1;
	private String customeType,startTime,endTime,tempstart,tempend,userBalance;
	private WalletWarningDialog mWarningDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mywallet);
		initView();
		//registerBoradcastReceiver();
	}
	private void initView(){
		pkuserId = PreferencesUtil.getStringPreferences(getActivity(),"pkUserinfo");
	    userBalance = PreferencesUtil.getStringPreferences(getActivity(), "usinAccountbalance");
	    
		ib_back = (ImageButton) findViewById(R.id.mywallet_back);
		ib_back.setOnClickListener(this);
		tv_balance = (TextView) findViewById(R.id.mywallet_current_balance);
		tv_charge = (TextView) findViewById(R.id.mywallet_current_balance_charge);
		tv_charge.setOnClickListener(this);
		tv_type = (TextView) findViewById(R.id.mywallet_select_type);
		tv_type.setOnClickListener(this);
		tv_time_start = (TextView) findViewById(R.id.mywallet_select_time_start);
		tv_time_end = (TextView) findViewById(R.id.mywallet_select_time_end);
		
		mListView = (ListView) findViewById(R.id.mywallet_listview);
		customTypeList.add("全部");
		customTypeList.add("预约");
		customTypeList.add("充电");
		customTypeList.add("充值");
		//customTypeList.add("购物");
		select_type = new MywalletTypePop(this, customTypeList, new SearchCallBack() {
			@Override
			public void callBack(int position) {
				String custome = customTypeList.get(position);
				tv_type.setText(custome);
				//查询类型select_type (1-充电消费 2-预约消费 3-购物消费 4-充值)
				if(custome.equals("充电")){
					customeType = "1";
					GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, customeType);
				}else if(custome.equals("预约")){
					customeType = "2";
					GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, customeType);
				}else if(custome.equals("购物")){
					customeType = "3";
					GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, customeType);
				}else if(custome.equals("充值")){
					customeType = "4";
					GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, customeType);
				}else if(custome.equals("全部")){
					//customeType = null;
					GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, null);
				}
			
			}
		});
		select_type.setList(customTypeList);
		final Calendar c = Calendar.getInstance();
		//日期选择
		tv_time_start.setOnClickListener(new View.OnClickListener() {
			//Calendar c = Calendar.getInstance();
			@Override
			public void onClick(View v) {
				
				// 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
				new DoubleDatePickerDialog(MyWalletActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
							int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
						int endDayOfMonth) {
						/*
						String textStart = String.format("%d-%d-%d", startYear,
								startMonthOfYear + 1, startDayOfMonth);
						String textEnd = String.format("%d-%d-%d",endYear, endMonthOfYear + 1, endDayOfMonth);*/
						Calendar startC = Calendar.getInstance();
						Calendar endC = Calendar.getInstance();
						startC.set(startYear, startMonthOfYear,startDayOfMonth);
						endC.set(endYear, endMonthOfYear,endDayOfMonth);
						//对比开始结束时间
						if(startC.compareTo(endC) != 1){
							mYear = startYear;
							mMonth = startMonthOfYear;
							mDay = startDayOfMonth;
							mYear1 = endYear;
							mMonth1 = endMonthOfYear;
							mDay1 = endDayOfMonth;
							updateDisplay();
							getInitValue();
						}else {
							showErrorDialog();
						}
						
					}
				}, c.get(Calendar.YEAR),c.get(Calendar.MONTH) , c.get(Calendar.DATE), true).show();
			}
		});
		
		tv_time_end.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				new DoubleDatePickerDialog(MyWalletActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
							int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
						int endDayOfMonth) {
						Calendar startC = Calendar.getInstance();
						Calendar endC = Calendar.getInstance();
						startC.set(startYear, startMonthOfYear,startDayOfMonth);
						endC.set(endYear, endMonthOfYear,endDayOfMonth);
						//对比开始结束时间
						if(startC.compareTo(endC) != 1){
							mYear = startYear;
							mMonth = startMonthOfYear;
							mDay = startDayOfMonth;
							mYear1 = endYear;
							mMonth1 = endMonthOfYear;
							mDay1 = endDayOfMonth;
							updateDisplay();
							
							getInitValue();
							/*startTime = tv_time_start.getText().toString();
							endTime = tv_time_end.getText().toString();
							//发送查询数据请求
							if(isNetConnection()){
								 tempstart = startTime;
								 tempend = endTime;
								if(tempstart!= null && !tempstart.isEmpty()){
									tempstart = tempstart +" 00:00:00";
								}
								if(tempend!= null && !tempend.isEmpty()){
									tempend = tempend + " 23:59:59";
								}
								GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, customeType);
							}else {
								showToast("亲，网络不稳，请检查网络连接!");
							}*/
							
						}else {
							showErrorDialog();
						}
					}
				}, c.get(Calendar.YEAR),c.get(Calendar.MONTH) , c.get(Calendar.DATE), true).show();
			}
		});
		
		//日期初始化
		mYear1 = c.get(Calendar.YEAR);
		mMonth1 = c.get(Calendar.MONTH);
		mDay1 = c.get(Calendar.DAY_OF_MONTH);
		Calendar tempC = Calendar.getInstance();
		tempC.add(Calendar.MONDAY, -1);//前1个月
		mYear = tempC.get(Calendar.YEAR);
		mMonth = tempC.get(Calendar.MONTH);
		mDay = tempC.get(Calendar.DAY_OF_MONTH);
		updateDisplay();
		//获取初始值
		getInitValue();
	}

		private void getInitValue(){
			//消费类型(1-充电消费 2-预约消费 3-购物消费 4-充值)
			startTime = tv_time_start.getText().toString();
			endTime = tv_time_end.getText().toString();
			//发送查询数据请求
			if(isNetConnection()){
				 showPD("正在加载数据");
				 tempstart = startTime +" 00:00:00";
				 tempend = endTime+ " 23:59:59" ;
				GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, tempstart, tempend, customeType);
			}else {
				showToast("亲，网络不稳，请检查网络连接!");
			}
		
			
		}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mywallet_back:
			finish();
			break;
		case R.id.mywallet_current_balance_charge:
			//充值
			Intent in = new Intent();
			in.setClass(this, RechargeActivity.class);
			startActivityForResult(in, 0x11);
			break;
		case R.id.mywallet_select_type:
			//类型
			select_type.show(v);
			break;
			
		default:
			break;
		}
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(0x11 == requestCode){
			//GetDataPost.getInstance(this).getMyWalletAll(handler, pkuserId, null, null, null);
			getInitValue();
		}
		
	}

	@Override
	protected void getData() {
		

	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		if (bundle != null) {
			if (sign.equals(Protocol.MY_WALLET)) {
				walletListBean = (WalletList) bundle
						.getSerializable(Protocol.DATA);
				if (walletListBean != null) {
					tv_balance.setText("" + walletListBean.getBalance());
					PreferencesUtil.setPreferences(getActivity(),
							"usinAccountbalance", walletListBean.getBalance());// 更新余额
					xiaofeiList = walletListBean.getConsumeRecord();
					shouyiList = walletListBean.getEarningsRecord();
					if (xiaofeiList != null) {
						mMyWalletAdapter = new MyWalletAdapter(this);
						mMyWalletAdapter.setList(xiaofeiList);
						mListView.setAdapter(mMyWalletAdapter);
					}
				}else {
					tv_balance.setText(userBalance);
				}
			}
		}

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
		finish();

	}
	/**
	 * 更新日期
	 */

	private void updateDisplay() {

		tv_time_start.setText(new StringBuilder().append(mYear + "-")
				.append(
				((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1)) + "-")
				.append(
				(mDay < 10) ? "0" + mDay : mDay));
		tv_time_end.setText(new StringBuilder().append(mYear1 + "-")
				.append(
				((mMonth1 + 1) < 10 ? "0" + (mMonth1 + 1) : (mMonth1 + 1)) + "-")
				.append(
				(mDay1 < 10) ? "0" + mDay1 : mDay1));
	}

	/**
	 * 开始时间大于结束时间对话框警告alert
	 */
	private void showErrorDialog(){
		
		mWarningDialog = new WalletWarningDialog(MyWalletActivity.this,"开始时间不能晚于结束时间！");
		mWarningDialog.setCancelable(false);
		mWarningDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mWarningDialog.dismiss();
			}
		});
		mWarningDialog.show();
	}
	
/*	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction("com.bm.wanma.recharge_wx_ok_refresh");  
        //注册广播        
        registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if(action.equals("com.bm.wanma.recharge_wx_ok_refresh")){  
				 GetDataPost.getInstance(MyWalletActivity.this).getMyWalletAll(handler, pkuserId, null, null, null);
	         }  
		}  
          
    }; */ 

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// unregisterReceiver(mBroadcastReceiver);
	}
	
}
