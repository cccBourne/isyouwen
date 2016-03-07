package com.bm.wanma.ui.activity;

import com.bm.wanma.R;

import com.bm.wanma.net.GetDataGet;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.IntentUtil;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.RegularExpressionUtil;
import com.bm.wanma.utils.Tools;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 修改密码界面
 * cm
 */

public class ModifyPasswordActivity extends BaseActivity implements OnClickListener{
	
	private EditText et_current_pwd,et_new_pwd,et_confirm_pwd;
	private Button btn_commit;
	private TextView tv_close;
	private String currentpwd,newpassword,confirmPwd;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pwd);
		et_current_pwd = (EditText)findViewById(R.id.modify_et_current_pwd);
		et_new_pwd = (EditText)findViewById(R.id.modify_et_new_pwd);
		et_confirm_pwd = (EditText)findViewById(R.id.modify_et_confirm_pwd);
		
		btn_commit = (Button)findViewById(R.id.modify_commit);
		btn_commit.setOnClickListener(this);
		tv_close = (TextView)findViewById(R.id.modify_close);
		tv_close.setOnClickListener(this);
		
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.modify_commit://确定
			check();
			break;
			//返回
		case R.id.modify_close:
			finish();
		}
		
	}

		
	private void check() {
		currentpwd = et_current_pwd.getText().toString().trim();
		newpassword = et_new_pwd.getText().toString().trim();
		confirmPwd = et_confirm_pwd.getText().toString().trim();
		
		if (!RegularExpressionUtil.isPasswordLength(newpassword)) {
			showToast("请输入6-8位的密码");
			return;
		}
		if (!newpassword.equals(confirmPwd)) {
			showToast("两次输入的密码不一致");
			return;
		}
	/*	String oldpwd = PreferencesUtil.getStringPreferences(this, "password");
		if (Tools.encoderByMd5(currentpwd).equals(oldpwd)) {
			showToast("原始密码不正确");
			return;
		}*/
		
		modifyPwd();
	}
	
		// 修改密码的网络请求
		private void modifyPwd() {
			if(isNetConnection()){
				String uId = PreferencesUtil.getStringPreferences(this, "pkUserinfo");
				showPD("正在提交修改信息，请稍等...");
				GetDataPost.getInstance(this).modifyPwd(uId,  Tools.encoderByMd5(currentpwd),
						Tools.encoderByMd5(newpassword), handler);
			}else {
				showToast("亲，网络不稳，请检查网络连接!");
			}
		}
	 
	  
		
	@Override
	protected void getData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		showToast(bundle.getString(Protocol.MSG));
		PreferencesUtil.setPreferences(this, "password", Tools.encoderByMd5(newpassword));
		finish();

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		
		showToast(bundle.getString(Protocol.MSG));
		
	}



}
