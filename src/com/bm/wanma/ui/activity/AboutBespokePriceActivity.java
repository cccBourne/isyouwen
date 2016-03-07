package com.bm.wanma.ui.activity;

import com.bm.wanma.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/**
 * @author cm
 *  预约单价费用说明
 */
public class AboutBespokePriceActivity extends Activity implements OnClickListener{
	
	private TextView tv_close,tv_price;
	private String price;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about_bespoke_price);
		tv_close = (TextView) findViewById(R.id.bespoke_price_cancle);
		tv_close.setOnClickListener(this);
		tv_price = (TextView) findViewById(R.id.bespoke_price);
		price = getIntent().getStringExtra("price");
		tv_price.setText("预约单价:  "+price +"元/分钟");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bespoke_price_cancle:
			finish();
			break;

		default:
			break;
		}
		
	}
	
	
}