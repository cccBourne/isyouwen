package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.dialog.TipDrawGunDialog;
import com.bm.wanma.utils.Tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author cm
 * 充电完成界面
 */
public class ChargeFinishActivity extends Activity implements OnClickListener{
	
	private TextView tv_total_money,tv_num,tv_time,tv_power;
	private TextView tv_charge_price,tv_server_price,tv_soc;
	private TextView tv_complete,tv_comment;
	private Intent getIn;
	private String pileNum;
	private String serviceprice,chargeprice,totalprice;
	private TipDrawGunDialog mDialog;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_finish);
		getIn = getIntent();
		pileNum = getIn.getStringExtra("pilePK");
		initView();
		
	}
	private void initView(){
		tv_total_money = (TextView) findViewById(R.id.charge_finish_total_money);
		tv_num = (TextView) findViewById(R.id.charge_finish_ordernum);
		tv_time = (TextView) findViewById(R.id.charge_finish_time);
		tv_power = (TextView) findViewById(R.id.charge_finish_power);
		tv_charge_price = (TextView) findViewById(R.id.charge_finish_price);
		tv_server_price = (TextView) findViewById(R.id.charge_finish_serverprice);
		tv_soc = (TextView) findViewById(R.id.charge_finish_soc);
		tv_complete = (TextView) findViewById(R.id.charge_finish_complete);
		tv_complete.setOnClickListener(this);
		tv_comment = (TextView) findViewById(R.id.charge_finish_comment);
		tv_comment.setOnClickListener(this);
		initValue();
		mDialog = new TipDrawGunDialog(this);
		mDialog.setCancelable(false);
		mDialog.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mDialog.dismiss();
			}
		});
		mDialog.show();
	}
	
	private void initValue(){
		chargeprice = getIn.getStringExtra("totalmoney");
		serviceprice = getIn.getStringExtra("servicemoney");
		totalprice = Tools.addStr(serviceprice, chargeprice);
		tv_total_money.setText(totalprice+"元");
		tv_num.setText(""+getIn.getStringExtra("order"));
		tv_time.setText(getIn.getStringExtra("startdate")+"至"+"\n"+getIn.getStringExtra("enddate"));
		tv_power.setText(getIn.getStringExtra("totalpower")+"度");
		tv_charge_price.setText(chargeprice+"元");
		tv_server_price.setText(serviceprice+"元");
		//tv_soc.setText(""+getIn.getStringExtra("totalmoney"));
		tv_soc.setVisibility(View.GONE);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.charge_finish_complete:
			//完成
			finish();
			break;
		case R.id.charge_finish_comment:
			//去评价
			Intent commentIn = new Intent();
			commentIn.putExtra("epId",pileNum);
			commentIn.setClass(ChargeFinishActivity.this, ChargeFinishCommentActivity.class);
			startActivityForResult(commentIn, 0x80); 
			
			break;

		default:
			break;
		}
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(0x80 == requestCode){
			finish();
		}
	}
	
	
	
	
	

}
