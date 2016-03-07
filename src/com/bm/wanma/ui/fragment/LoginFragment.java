package com.bm.wanma.ui.fragment;

import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.alipay.Base64;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.entity.LoginBean;
import com.bm.wanma.entity.MyBespokeOrderBean;
import com.bm.wanma.entity.MyChargeOrderBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.ForgetPasswordActivity;
import com.bm.wanma.ui.activity.HomeActivity;
import com.bm.wanma.utils.IntentUtil;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.RegularExpressionUtil;
import com.bm.wanma.utils.Tools;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
   
/**
 * @author cm
 *  登录fragment
 */
public class LoginFragment extends BaseFragment implements OnClickListener{
	
	private EditText et_login_phone;
	private EditText et_login_pwd;
	private Button btn_commit;
	private TextView tv_forgetPwd;
	private String userPhone,userPassword,deviceId;
	private LoginBean loginBean;//登录实体类
	private ArrayList<MyBespokeOrderBean> bespokeBeanList;
	private ArrayList<MyChargeOrderBean> chargeBeanList;
	private MyBespokeOrderBean bespokeOrderBean;
	private MyChargeOrderBean chargeOrderBean;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View loginFragment = inflater.inflate(
				R.layout.fragment_login, container, false);
		et_login_phone = (EditText)loginFragment.findViewById(R.id.login_userphone);
		et_login_phone.addTextChangedListener(new MyLoginTextWatch());
		et_login_pwd = (EditText)loginFragment.findViewById(R.id.login_userpassword);
		
		btn_commit = (Button)loginFragment.findViewById(R.id.login_commit);
		btn_commit.setOnClickListener(this);
		tv_forgetPwd = (TextView)loginFragment.findViewById(R.id.login_reset_password);
		tv_forgetPwd.setOnClickListener(this);
		return loginFragment;
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_commit:
			String usename = et_login_phone.getText().toString().trim();
			userPhone = usename.replaceAll(" ", "");
			userPassword = et_login_pwd.getText().toString().trim();
			check();
			
			break;
		case R.id.login_reset_password:
			
			Intent resetIn = new Intent(getActivity(),ForgetPasswordActivity.class);
			getActivity().startActivity(resetIn);
			
			break;
		default:
			break;
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(sign.equals(Protocol.TO_LOGIN)){
			loginBean = (LoginBean) bundle.getSerializable(Protocol.DATA);
			//登录成功之后，获取用户信息，并保存！！
			if (loginBean != null) {
				//获取预约列表
				GetDataPost.getInstance(getActivity()).getMyBespokeList(handler, loginBean.getPkUserinfo());
				 saveUserInfo();
				 Intent intnet = new Intent(BroadcastUtil.BROADCAST_Login);
				 intnet.putExtra("pkUserId", loginBean.getPkUserinfo());
				 getActivity().sendBroadcast(intnet);
				//go2Home();
			}
		}else if(sign.equals(Protocol.MYBESPOKE_LIST)){
			bespokeBeanList = (ArrayList<MyBespokeOrderBean>) bundle.getSerializable(Protocol.DATA);
			if(bespokeBeanList != null && bespokeBeanList.size()>0){
				for(MyBespokeOrderBean bean : bespokeBeanList){
					if("3".equals(bean.getBespBespokestatus()) || "4".equals(bean.getBespBespokestatus())){
						bespokeOrderBean = bean;
					}
				}
				if(bespokeOrderBean != null){
					 Intent intnet = new Intent(BroadcastUtil.BROADCAST_Bespoke_OK_VISIBLE);
					 intnet.putExtra("bespokePK", bespokeOrderBean.getPkBespoke());
					 getActivity().sendBroadcast(intnet);
				}
			}
			
			GetDataPost.getInstance(getActivity()).getMyChargeOrderList(handler, loginBean.getPkUserinfo(),null);
		}else if(sign.equals(Protocol.MYCHARGE_ORDERLIST)){
			//充电订单列表
			chargeBeanList = (ArrayList<MyChargeOrderBean>) bundle.getSerializable(Protocol.DATA);
			if(chargeBeanList != null && chargeBeanList.size()>0){
				for(MyChargeOrderBean bean : chargeBeanList){
					if("1".equals(bean.getChOr_ChargingStatus())){
						chargeOrderBean = bean;
					}
				}
				if(chargeOrderBean != null){
					 Intent intnet = new Intent(BroadcastUtil.BROADCAST_Charge_Ing);
					 intnet.putExtra("chargepilenum", chargeOrderBean.getElPi_ElectricPileCode());
					 intnet.putExtra("chargeheadnum", chargeOrderBean.getHeadCode());
					 getActivity().sendBroadcast(intnet);
				}
			}
			
			go2Home();
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// 登录失败
		if (LogUtil.isDebug) {
			showToast("错误码" + bundle.getString(Protocol.CODE) + "\n"
					+ bundle.getString(Protocol.MSG));
		}else{
			showToast(bundle.getString(Protocol.MSG));
		}
 
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
				loginBean.getUsinCarinfoId());
		PreferencesUtil.setPreferences(getActivity(), "isPpw",
				loginBean.getIsPpw());

	}

	/**
	 * 跳转首页
	 */
	private void go2Home() {
		IntentUtil.startIntent(getActivity(), HomeActivity.class);
		getActivity().finish();
	}
	
	private void check() {
		if (!RegularExpressionUtil.isMobilephone(userPhone)) {
			showToast("请输入正确的手机号码！");
			return;
		}
		
		if (!RegularExpressionUtil.isPasswordLength(userPassword)) {
			showToast("请重新输入6-8位密码");
			return;
		}

		if (isNetConnection()) {
			loginPost();
		} else {
			showToast("亲，网络不稳，请检查网络连接!");
		}
	}

	// 登录请求
		private void loginPost() {
			
			deviceId = getDeviceId(); 
			String jpushRegistrationid = PreferencesUtil.getStringPreferences(
					getActivity(), "jpushRegistrationid");
			String devicetype = "1";
			userPassword = Tools.encoderByMd5(userPassword);
			PreferencesUtil.setPreferences(getActivity(), "password", userPassword);
			StringBuilder repwd1 = new StringBuilder();
			StringBuilder repwd2 = new StringBuilder();
			repwd1 = repwd1.append(userPassword).append(userPhone);
			userPassword = Tools.encoderByMd5(repwd1.toString());
			String random = Tools.getRandomChar(1);
			userPassword = repwd2.append(userPassword).append(random).toString();

			if (isNetConnection()) {
				showPD("正在登录，请稍等...");
				GetDataPost.getInstance(getActivity()).login(userPhone, userPassword,
						jpushRegistrationid, devicetype, deviceId, handler);
			} else {
				showToast("亲，网络不稳，请检查网络连接!");
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
	
	//手机号码 textwatch
		private class MyLoginTextWatch implements TextWatcher{
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
						et_login_phone.setText(contents);
						et_login_phone.setSelection(contents.length());
					} else { // + 输入时
						contents = contents.substring(0, 3) + " "
								+ contents.substring(3);
						et_login_phone.setText(contents);
						et_login_phone.setSelection(contents.length());
					}
				} else if (length == 9) {
					if (contents.substring(8).equals(new String(" "))) { // -
						contents = contents.substring(0, 8);
						et_login_phone.setText(contents);
						et_login_phone.setSelection(contents.length());
					} else {// +
						contents = contents.substring(0, 8) + " "
								+ contents.substring(8);
						et_login_phone.setText(contents);
						et_login_phone.setSelection(contents.length());
					}
				}
			}
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}

		}
}
