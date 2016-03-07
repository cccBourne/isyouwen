package com.bm.wanma.ui.activity;

import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.entity.MyChargeOrderBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.TimeUtil;
import com.bm.wanma.utils.Tools;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChargeDetailActivity extends BaseActivity implements OnClickListener{
	
	private String chargeKey,pkUserId;
	private ImageButton ib_back;
	private TextView tv_ordernum,tv_total_time;
	private TextView tv_total_money,tv_dianliang;
	private TextView tv_dianfei,tv_server_money,tv_time;
	private TextView tv_name,tv_addre;
	private ArrayList<MyChargeOrderBean> beanList;
	private MyChargeOrderBean bean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_charge_order_detail);
		initView();
		
	}
	
	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.activity_charge_detail_back);
		ib_back.setOnClickListener(this);
		tv_ordernum = (TextView) findViewById(R.id.charge_detail_ordernum);
		tv_total_time = (TextView) findViewById(R.id.charge_detail_timelength);
		tv_total_money = (TextView) findViewById(R.id.charge_detail_totalmoney);
		tv_dianliang = (TextView) findViewById(R.id.charge_detail_dianliang);
		tv_dianfei = (TextView) findViewById(R.id.charge_detail_dianfei);
		tv_server_money = (TextView) findViewById(R.id.charge_detail_fuwufei);
		tv_time = (TextView) findViewById(R.id.charge_detail_time);
		tv_name = (TextView) findViewById(R.id.charge_detail_name);
		tv_addre = (TextView) findViewById(R.id.charge_detail_addr);
	}
	private void initValue(MyChargeOrderBean bean){
		
		String tempchargemoney = bean.getChOr_ChargeMoney();
		String tempservermoney = bean.getChOr_ServiceMoney();
		String tempStarttime = bean.getBegin_charge_time();
		String tempEndtime = bean.getEnd_charge_time();
		long start = TimeUtil.getTimestamp(tempStarttime,"yyyy-MM-dd HH:mm:ss");
		long end = TimeUtil.getTimestamp(tempEndtime,"yyyy-MM-dd HH:mm:ss");
		long between = end - start;
		Log.i("cm_socket", "start"+start+"end"+end+"between"+between);
		tv_ordernum.setText(""+bean.getChOr_Code());
		tv_total_time.setText(TimeUtil.getCutDown4(between));
		tv_dianfei.setText(tempchargemoney+"元");
		tv_server_money.setText(tempservermoney+"元");
		tv_total_money.setText(Tools.addStr(tempchargemoney, tempservermoney)+"元");
		tv_dianliang.setText(bean.getChOr_QuantityElectricity()+"kwh");
		tv_time.setText(tempStarttime+"\n"+tempEndtime);
		tv_name.setText(""+bean.getElPi_ElectricPileName());
		tv_addre.setText(""+bean.getElPi_ElectricPileAddress());
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_charge_detail_back:
			finish();
			break;

		default:
			break;
		}
		
	}

	@Override
	protected void getData() {
		pkUserId = PreferencesUtil.getStringPreferences(this, "pkUserinfo");
		chargeKey = getIntent().getStringExtra("chargeKey");
		showPD("正在加载数据...");
		GetDataPost.getInstance(getActivity()).getMyChargeOrderList(handler, pkUserId, chargeKey);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		beanList = (ArrayList<MyChargeOrderBean>) bundle.getSerializable(Protocol.DATA);
		if(beanList != null && beanList.size()>0){
			bean = beanList.get(0);
			initValue(bean);
		}else {
			finish();
		}
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
		finish();

	}


}
