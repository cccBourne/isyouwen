package com.bm.wanma.ui.fragment;

import com.bm.wanma.R;
import com.bm.wanma.alipay.Base64;
import com.bm.wanma.entity.LoginBean;
import com.bm.wanma.net.GetDataGet;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.AboutProtolActivity;
import com.bm.wanma.ui.activity.HomeActivity;
import com.bm.wanma.ui.activity.LoginAndRegisterActivity;
import com.bm.wanma.utils.IntentUtil;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.RegularExpressionUtil;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author cm
 *  用户注册fragment
 *
 */
public class RegisterFragment extends BaseFragment implements OnClickListener{
	private EditText et_phone,et_captcha,et_password,et_confirm_pwd;
	private Button btn_chptcha,btn_commit;
	private TextView tv_protocol;
	private CheckBox protocolBox;
	private MyCount mc;
	private String userPhone,captcha,password,confirmPwd;
	private LoginBean loginBean;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRetainInstance(true); 设置后可以在横竖屏切换时不被重新创建
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View registerFragment = inflater.inflate(
				R.layout.fragment_register, container, false);
		et_phone = (EditText)registerFragment.findViewById(R.id.register_userphone);
		et_phone.addTextChangedListener(new MyRegistTextWatch());
		et_captcha = (EditText)registerFragment.findViewById(R.id.register_et_captcha);
		et_password = (EditText)registerFragment.findViewById(R.id.register_set_pwd);
		et_confirm_pwd = (EditText)registerFragment.findViewById(R.id.register_confirm_pwd);
		btn_commit = (Button)registerFragment.findViewById(R.id.register_commit);
		btn_commit.setOnClickListener(this);
		btn_chptcha = (Button)registerFragment.findViewById(R.id.register_btn_captcha);
		btn_chptcha.setOnClickListener(this);
		tv_protocol = (TextView) registerFragment.findViewById(R.id.register_commit_tv_protocol);
		tv_protocol.setOnClickListener(this);
		protocolBox = (CheckBox)registerFragment.findViewById(R.id.register_checkbox_protocol);
		protocolBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){ 
					getActivity().startActivity(new Intent(getActivity(),AboutProtolActivity.class));
		        }
				
			}
		});
		return registerFragment;
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//注册提交
		case R.id.register_commit:
			
			captcha = et_captcha.getText().toString();
			password = et_password.getText().toString();
			confirmPwd = et_confirm_pwd.getText().toString();
			userPhone = et_phone.getText().toString()
					.replaceAll(" ", ""); 
			if(!RegularExpressionUtil.isMobilephone(userPhone)){
				showToast("请输入正确的手机号码！");
				return;
			}
			
			if(captcha.isEmpty()){
				showToast("请输入验证码");
				return;
			}
			
			 if(!protocolBox.isChecked()){
				 showToast("请勾选用户协议！");
				 return;
			 }
			 if(!RegularExpressionUtil.isPasswordLength(password)){
				 showToast("请输入6-8位密码");
				 return;
			 }
			 
			if(!Tools.isEmptyString(password) && password.equals(confirmPwd)){
				showPD("正在提交信息");
				GetDataPost.getInstance(getActivity()).checkCode(handler, userPhone, captcha);
				
			}else {
				showToast("两次输入密码不一致！");
			}
			
			break;
			//获取验证码
		case R.id.register_btn_captcha:
			userPhone = et_phone.getText().toString()
			.replaceAll(" ", ""); 
			
			if (RegularExpressionUtil.isMobilephone(userPhone)) {
				if(isNetConnection()){
					GetDataPost.getInstance(getActivity()).checkPhone(handler, userPhone);
				}else {
					showToast("网络不稳，请稍后再试");
				}
				
			}else{
				showToast("请输入正确的手机号码！");
			}
			
			break;
		case R.id.register_commit_tv_protocol:
			getActivity().startActivity(new Intent(getActivity(),AboutProtolActivity.class));
			
			break;
		default:
			break;
		}
		
	}
	
	
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		if (Tools.judgeString(sign, Protocol.CHECK_PHONE)) {
			// 发送验证码
			getCode();
		} else if (Tools.judgeString(sign, Protocol.CHECK_CODE)) {
			// 验证码通过，发送注册信息
			goRegister();
		} else if (Tools.judgeString(sign, Protocol.GET_AUTH_CODE)) {
			showToast("请查收验证码");
		} else if (Tools.judgeString(sign, Protocol.TO_REGIST)) {
			// 注册成功
			//LoginAndRegisterActivity.tv_title.setText("登录");
			//LoginAndRegisterActivity.tv_switch.setText("注册");
			//LoginAndRegisterActivity.isLogin = true;
			//getFragmentManager().popBackStack();
			String jpushRegistrationid = PreferencesUtil.getStringPreferences(
					getActivity(), "jpushRegistrationid");
			String devicetype = "1";
			String deviceId = getDeviceId();
			PreferencesUtil.setPreferences(getActivity(), "password", password);
			StringBuilder repwd1 = new StringBuilder();
			StringBuilder repwd2 = new StringBuilder();
			repwd1 = repwd1.append(password).append(userPhone);
			password = Tools.encoderByMd5(repwd1.toString());
			String random = Tools.getRandomChar(1);
			password = repwd2.append(password).append(random).toString();
			GetDataPost.getInstance(getActivity()).login(userPhone, password,
					jpushRegistrationid, devicetype, deviceId, handler);
			
		}else if (Tools.judgeString(sign, Protocol.TO_LOGIN)) {//登录成功
			loginBean = (LoginBean) bundle.getSerializable(Protocol.DATA);
			//登录成功之后，获取用户信息，并保存！！
			if (loginBean != null) {
				saveUserInfo();
				go2Home();
			}
		}

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		// 登录失败
		if (LogUtil.isDebug) {
			showToast("错误码" + bundle.getString(Protocol.CODE) + "\n"
					+ bundle.getString(Protocol.MSG));
		} else{
			showToast(bundle.getString(Protocol.MSG));
		}

	}
	
	/**
	 * 注册
	 */
	private void goRegister() {
		password = Tools.encoderByMd5(password);
		if (isNetConnection()) {
			showPD("正在提交注册信息，请稍等...");
			GetDataPost.getInstance(getActivity()).register(handler, userPhone, password);
		} else {
			showToast("亲，网络不稳，请检查网络连接!");
		}
	}
	
	// 获取短信验证码
	public void getCode() {
		if (isNetConnection()) {
			showPD("正在获取信息");
			GetDataGet.getInstance().getCode(handler, userPhone);
			mc = new MyCount(60000, 1000);
			mc.start();
		} else {
			showToast("亲，网络不稳，请稍后再试...");
		}

	}
	
	
	private class MyRegistTextWatch implements TextWatcher{
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			
		}
		@Override
		public void onTextChanged(CharSequence str, int start, int before,
				int count) {
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

		@Override
		public void afterTextChanged(Editable s) {
			
		}
	}
	//定义一个倒计时的内部类
			class MyCount extends CountDownTimer {
				public MyCount(long millisInFuture, long countDownInterval) {
					super(millisInFuture, countDownInterval);
				}
				@Override
				public void onFinish() {
					btn_chptcha.setClickable(true);
					btn_chptcha.setText("获取验证码");
					btn_chptcha.setBackgroundResource(R.drawable.common_shape_corner_orange);
				}
				@Override
				public void onTick(long millisUntilFinished) {
					btn_chptcha.setClickable(false);
					btn_chptcha.setText("等待("+ millisUntilFinished / 1000 + ")秒");
					btn_chptcha.setBackgroundResource(R.drawable.common_shape_corner_gray);
				}
			}
			//获取设备id
			public String getDeviceId(){
				TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
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
			
			/**
			 * 跳转首页
			 */
			private void go2Home() {
				IntentUtil.startIntent(getActivity(), HomeActivity.class);
				getActivity().finish();
			}
			private void saveUserInfo() {
				PreferencesUtil.setPreferences(getActivity(), "pkUserinfo",
						loginBean.getPkUserinfo());
				PreferencesUtil.setPreferences(getActivity(), "usinPhone",
						loginBean.getUsinPhone());
				PreferencesUtil.setPreferences(getActivity(), "usinFacticityname",
						loginBean.getUsinFacticityname());
				PreferencesUtil.setPreferences(getActivity(), "usinSex",
						loginBean.getUsinSex());
				PreferencesUtil.setPreferences(getActivity(), "usinAccountbalance",
						loginBean.getUsinAccountbalance());
				PreferencesUtil.setPreferences(getActivity(), "usinBirthdate",
						loginBean.getUsinBirthdate());
				PreferencesUtil.setPreferences(getActivity(), "usinUserstatus",
						loginBean.getUsinUserstatus());
				PreferencesUtil.setPreferences(getActivity(), "usinHeadimage",
						loginBean.getUsinHeadimage());
				PreferencesUtil.setPreferences(getActivity(), "nickName",
						loginBean.getUsinUsername());
				PreferencesUtil.setPreferences(getActivity(), "carType",
						loginBean.getUsinCarcompanyId());
				PreferencesUtil.setPreferences(getActivity(), "carName",
						loginBean.getUsinCarinfoId());

			}
	
	
}
