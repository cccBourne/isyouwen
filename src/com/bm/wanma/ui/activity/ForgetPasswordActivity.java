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
 * 忘记密码界面
 * cm
 */

public class ForgetPasswordActivity extends BaseActivity implements OnClickListener{
	
	private EditText et_phone,et_captcha,et_password,et_confirm_pwd;
	private Button btn_chptcha,btn_commit;
	private TextView tv_close;
	private MyCount mc;
	private String userPhone,captcha,password,confirmPwd;

	public ForgetPasswordActivity() {
		// TODO Auto-generated constructor stub
	}
 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forget_pwd);
		et_phone = (EditText)findViewById(R.id.forget_et_userphone);
		et_phone.addTextChangedListener(new MyTextWatch());
		et_captcha = (EditText)findViewById(R.id.forget_et_captcha);
		et_password = (EditText)findViewById(R.id.forget_set_pwd);
		et_confirm_pwd = (EditText)findViewById(R.id.forget_confirm_pwd);
		btn_chptcha = (Button)findViewById(R.id.forget_btn_captcha);
		btn_chptcha.setOnClickListener(this);
		btn_commit = (Button)findViewById(R.id.forget_commit);
		btn_commit.setOnClickListener(this);
		tv_close = (TextView)findViewById(R.id.forget_close);
		tv_close.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.forget_btn_captcha:  //获取验证码
			userPhone = et_phone.getText().toString().trim().replaceAll(" ", "");
			if (!RegularExpressionUtil.isMobilephone(userPhone)) {
				showToast("请输入正确的手机号码");
				return;
			}
			if(isNetConnection()){
				GetDataPost.getInstance(ForgetPasswordActivity.this).checkPhone(handler, userPhone);
			}else {
				showToast("亲，网络不稳，请检查网络连接");
			}
			//getCode();			
			break;
		case R.id.forget_commit:   //完成
			
			check();
			break;
			//返回
		case R.id.forget_close:
			
			finish();
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

		@Override
		public void afterTextChanged(Editable str) {
			String contents = str.toString();
			int length = contents.length();
			if (length == 4) {
				if (contents.substring(3).equals(new String(" "))) { // - 删除
					contents = contents.substring(0, 3);
					et_phone.setText(contents);
					et_phone.setSelection(contents.length());
				} else { // + 输入时
					contents = contents.substring(0, 3) + " "
							+ contents.substring(3);
					et_phone.setText(contents);
					et_phone.setSelection(contents.length());
				}
			} else if (length == 9) {
				if (contents.substring(8).equals(new String(" "))) { // -
					contents = contents.substring(0, 8);
					et_phone.setText(contents);
					et_phone.setSelection(contents.length());
				} else {// +
					contents = contents.substring(0, 8) + " "
							+ contents.substring(8);
					et_phone.setText(contents);
					et_phone.setSelection(contents.length());
				}
			}
		}
		
	}
	

		
	private void check() {
		 userPhone = et_phone.getText().toString().trim().replaceAll(" ", "");
		 captcha = et_captcha.getText().toString().trim();
		 password = et_password.getText().toString().trim();
		 confirmPwd = et_confirm_pwd.getText().toString().trim();
		 
		if (!RegularExpressionUtil.isMobilephone(userPhone)) {
			showToast("请输入正确的手机号码");
			return;
		}
	/*	if (!RegularExpressionUtil.isPassword(ac_reset_newpassword.getText().toString().trim())) {
			ToastUtil.showToast("密码不能为空");
			return;
		}*/
		if(captcha.isEmpty()){
			showToast("请输入验证码!");
			return;
		}
		if (!RegularExpressionUtil.isPasswordLength(password)) {
			showToast("请输入6-8位纯数字密码");
			return;
		}
		if (!password.equals(confirmPwd)) {
			showToast("两次输入的密码不一致");
			return;
		}
		
		forgetPwd();
	}
	
	// 获取短信验证码
		public void getCode() {
			if (isNetConnection()) {
				GetDataGet.getInstance().getCode(handler, userPhone);
				mc = new MyCount(60000, 1000);
				mc.start();
			} else {
				showToast("亲，网络不稳，请检查网络连接！");
			}
		}
	

		// 忘记密码的网络请求
		private void forgetPwd() {
			
			password = Tools.encoderByMd5(password);
			//smsCode = Tools.encoderByMd5(smsCode);
			if(isNetConnection()){
				showPD("正在提交修改信息，请稍等...");
				GetDataPost.getInstance(this).resetPwd(userPhone, password, captcha, handler);
			}else {
				showToast("亲，网络不稳，请检查网络连接!");
			}
		}
	 
	  
		/* 定义一个倒计时的内部类 */
		class MyCount extends CountDownTimer {
			public MyCount(long millisInFuture, long countDownInterval) {
				super(millisInFuture, countDownInterval);
			}

			@Override
			public void onFinish() {
				btn_chptcha.setClickable(true);
				btn_chptcha.setText("发送验证码");
				btn_chptcha.setBackgroundResource(R.drawable.common_shape_corner_orange);
			}

			@Override
			public void onTick(long millisUntilFinished) {
				btn_chptcha.setClickable(false);
				btn_chptcha.setText("等待(" + millisUntilFinished / 1000 + ")秒");
				btn_chptcha.setBackgroundResource(R.drawable.common_shape_corner_gray);
			}
		}

	@Override
	protected void getData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if (Tools.judgeString(sign, Protocol.CHECK_PHONE)) {
			showToast("该手机号未注册...");
		}else if (sign.equals(Protocol.RESET_PWD)) {
			showToast("修改密码成功");
			PreferencesUtil.setPreferences(this, "password",password);
			IntentUtil.startIntent(ForgetPasswordActivity.this, LoginAndRegisterActivity.class);
		}else if (Tools.judgeString(sign, Protocol.GET_AUTH_CODE)) {
			showToast("请查收验证码");
		}

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		
		if (Tools.judgeString(sign, Protocol.CHECK_PHONE)) {
			//发送验证码
			getCode();
		}else if (sign.equals(Protocol.RESET_PWD)) {
			if(LogUtil.isDebug){
				showToast("错误码"+bundle.getString(Protocol.CODE)+"\n"+bundle.getString(Protocol.MSG));
			}else {
				showToast(bundle.getString(Protocol.MSG));
			}
		}else if (Tools.judgeString(sign, Protocol.GET_AUTH_CODE)) {
			if(LogUtil.isDebug){
				showToast("错误码"+bundle.getString(Protocol.CODE)+"\n"+bundle.getString(Protocol.MSG));
			}else {
				showToast(bundle.getString(Protocol.MSG));
			}
		}
		
	}



}
