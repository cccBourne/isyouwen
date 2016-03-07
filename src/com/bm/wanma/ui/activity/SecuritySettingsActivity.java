package com.bm.wanma.ui.activity;

import com.bm.wanma.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

/**
 * @author cm
 * 安全设置 界面
 */
public class SecuritySettingsActivity extends Activity implements OnClickListener{

	private ImageButton ib_back;
	private RelativeLayout rl_password,rl_pay_password;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_security_settings);
		ib_back = (ImageButton) findViewById(R.id.security_settings_back);
		ib_back.setOnClickListener(this);
		rl_password = (RelativeLayout) findViewById(R.id.security_settings_password);
		rl_password.setOnClickListener(this);
		rl_pay_password = (RelativeLayout) findViewById(R.id.security_settings_pay_password);
		rl_pay_password.setOnClickListener(this);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.security_settings_back:
			finish();
			break;
		case R.id.security_settings_password:
			//修改密码
			Intent modifyIn = new Intent();
			modifyIn.setClass(SecuritySettingsActivity.this, ModifyPasswordActivity.class);
			startActivity(modifyIn);
			break;
		case R.id.security_settings_pay_password:
			//修改支付密码
			Intent modifyPayIn = new Intent();
			modifyPayIn.setClass(SecuritySettingsActivity.this, ModifyPayPasswordActivity.class);
			startActivity(modifyPayIn);
			break;

		default:
			break;
		}
		
	}

	

}
