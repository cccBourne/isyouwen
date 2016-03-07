package com.bm.wanma.ui.fragment;

import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.entity.UserInfoBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.ApplyBuildPileActivity;
import com.bm.wanma.ui.activity.ApplyICActivity;
import com.bm.wanma.ui.activity.LoginAndRegisterActivity;
import com.bm.wanma.ui.activity.MyCollectListActivity;
import com.bm.wanma.ui.activity.MyDynamicListActivity;
import com.bm.wanma.ui.activity.MyNewsActivity;
import com.bm.wanma.ui.activity.MyUserInfoActivity;
import com.bm.wanma.ui.activity.MyWalletActivity;
import com.bm.wanma.ui.activity.SettingsActivity;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.RoundImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的个人中心界面
 * @author cm
 *
 */
public class MyPersonFragment extends BaseFragment implements OnClickListener{

	private RoundImageView iv_photo;
	private RelativeLayout rl_has_login,rl_login;
	private TextView tv_phone,tv_balance,tv_message;
	private TextView tv_login,tv_register;
	private TextView tv_mywallet,tv_mycollect,tv_mydynamic,tv_applyIC,tv_settings;
	private String pkUserId;
	private UserInfoBean userInfoBean;
	private boolean isFirst;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isFirst = true;
		registerBoradcastReceiver();//注册调用户信息接口
		
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View MypersonFragment = inflater.inflate(
				R.layout.fragment_myperson, container, false);
		initView(MypersonFragment);
		pkUserId = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
		if(!Tools.isEmptyString(pkUserId)){
			 GetDataPost.getInstance(getActivity()).getUserInfo(handler, pkUserId);
		 }
		return MypersonFragment;
	}
	 @Override
	public void onStart() {
		super.onStart();
		
		
	}

	@Override
	public void onResume() {
		super.onResume();
		pkUserId = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
		if(Tools.isEmptyString(pkUserId)){//未登录
			rl_has_login.setVisibility(View.GONE);
			rl_login.setVisibility(View.VISIBLE);
			iv_photo.setImageResource(R.drawable.img_my_user);
			tv_phone.setText("爱充用户");
			 
		}else {
			String userName = PreferencesUtil.getStringPreferences(getActivity(), "nickName");
			String userPhone = PreferencesUtil.getStringPreferences(getActivity(), "usinPhone");
			String userBalance = PreferencesUtil.getStringPreferences(getActivity(), "usinAccountbalance");
			String imgurl = PreferencesUtil.getStringPreferences(getActivity(), "usinHeadimage");
			rl_has_login.setVisibility(View.VISIBLE);
			rl_login.setVisibility(View.GONE);
			
			if(Tools.isEmptyString(userName)){//昵称为空
				tv_phone.setText(userPhone);
			}else {
				tv_phone.setText(userName);
			}
			tv_balance.setText(""+userBalance);
			
			//设置头像
			if(!Tools.isEmptyString(imgurl)){
				DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.img_my_user)
				.showImageOnFail(R.drawable.img_my_user) 
				.cacheInMemory(true)
				.cacheOnDisk(false)
				.bitmapConfig(Config.RGB_565)
				.build();
				ImageLoader.getInstance().displayImage(imgurl, iv_photo, options);
			}
		}
		
		
	}

	private void updataView() {

		rl_has_login.setVisibility(View.VISIBLE);
		rl_login.setVisibility(View.GONE);

		if (!Tools.isEmptyString(userInfoBean.getUserNickName())) {// 昵称为空
			tv_phone.setText(userInfoBean.getUserNickName());
		} else {
			tv_phone.setText(""+userInfoBean.getUserTel());
		}
		tv_balance.setText("" +userInfoBean.getUserBlance());
		// 设置头像
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.img_my_user)
				.showImageOnFail(R.drawable.img_my_user).cacheInMemory(true)
				.cacheOnDisk(false).bitmapConfig(Config.RGB_565).build();
		ImageLoader.getInstance().displayImage(userInfoBean.getUserImage(), iv_photo, options);
	}


	private void initView(View mainView){
		iv_photo = (RoundImageView) mainView.findViewById(R.id.fragment_myperson_photo);
		iv_photo.setOnClickListener(this);
		rl_has_login = (RelativeLayout) mainView.findViewById(R.id.fragment_myperson_rl_login);
		rl_login = (RelativeLayout) mainView.findViewById(R.id.fragment_myperson_rl_unlogin);
		tv_phone = (TextView) mainView.findViewById(R.id.fragment_myperson_phone);
		tv_balance = (TextView) mainView.findViewById(R.id.fragment_myperson_balance);
		tv_login = (TextView) mainView.findViewById(R.id.fragment_myperson_login);
		tv_login.setOnClickListener(this);
		tv_register = (TextView) mainView.findViewById(R.id.fragment_myperson_register);
		tv_register.setOnClickListener(this);
		tv_message = (TextView) mainView.findViewById(R.id.fragment_myperson_msg);
		tv_message.setOnClickListener(this);
		tv_mywallet = (TextView) mainView.findViewById(R.id.fragment_myperson_wallet);
		tv_mywallet.setOnClickListener(this);
		tv_mycollect = (TextView) mainView.findViewById(R.id.fragment_myperson_collect);
		tv_mycollect.setOnClickListener(this);
		tv_mydynamic = (TextView) mainView.findViewById(R.id.fragment_myperson_dynamic);
		tv_mydynamic.setOnClickListener(this);
		tv_applyIC = (TextView) mainView.findViewById(R.id.fragment_myperson_apply);
		tv_applyIC.setOnClickListener(this);
		tv_settings = (TextView) mainView.findViewById(R.id.fragment_myperson_settings);
		tv_settings.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_myperson_photo:
			//个人资料
			if(Tools.isEmptyString(pkUserId)){
				Intent loginIn = new Intent();
				loginIn.setClass(getActivity(), LoginAndRegisterActivity.class);
				getActivity().startActivity(loginIn);
			}else {
				Intent myuserinfoIn = new Intent();
				myuserinfoIn.setClass(getActivity(), MyUserInfoActivity.class);
				myuserinfoIn.putExtra("userInfo", userInfoBean);
				getActivity().startActivity(myuserinfoIn);
				
			}
			
			
			break;
		case R.id.fragment_myperson_msg:
			//进入我的消息
				Intent personIn = new Intent();
				personIn.setClass(getActivity(), MyNewsActivity.class);
				getActivity().startActivity(personIn);
			
			break;
		case R.id.fragment_myperson_login:
			//未登录
			Intent loginIn = new Intent();
			loginIn.setClass(getActivity(), LoginAndRegisterActivity.class);
			getActivity().startActivity(loginIn);
			break;
		case R.id.fragment_myperson_register:
			//进入注册界面
			Intent registerIn = new Intent();
			registerIn.setClass(getActivity(), LoginAndRegisterActivity.class);
			getActivity().startActivity(registerIn);
			
			break;
		case R.id.fragment_myperson_wallet:
			//我的钱包
			if(Tools.isEmptyString(pkUserId)){
				Intent newsIn = new Intent();
				newsIn.setClass(getActivity(), LoginAndRegisterActivity.class);
				getActivity().startActivity(newsIn);
			}else {
				Intent mywalletIn = new Intent();
				mywalletIn.setClass(getActivity(), MyWalletActivity.class);
				getActivity().startActivity(mywalletIn);
			}
			
			break;
		case R.id.fragment_myperson_collect:
			//我的收藏
			if(Tools.isEmptyString(pkUserId)){
				Intent newsIn = new Intent();
				newsIn.setClass(getActivity(), LoginAndRegisterActivity.class);
				getActivity().startActivity(newsIn);
			}else {
				Intent mycollectIn = new Intent();
				mycollectIn.setClass(getActivity(), MyCollectListActivity.class);
				getActivity().startActivity(mycollectIn);
			}
			break;
		case R.id.fragment_myperson_dynamic:
			//我的动态
			Intent myDynamicIn = new Intent();
			myDynamicIn.setClass(getActivity(), MyDynamicListActivity.class);
			getActivity().startActivity(myDynamicIn);
			
			break;
		case R.id.fragment_myperson_apply:
			//IC卡
			if(Tools.isEmptyString(pkUserId)){
				Intent loginicIn = new Intent();
				loginicIn.setClass(getActivity(), LoginAndRegisterActivity.class);
				getActivity().startActivity(loginicIn);
			}else {
				Intent myuserinfoIn = new Intent();
				myuserinfoIn.setClass(getActivity(), ApplyICActivity.class);
				myuserinfoIn.putExtra("userInfo", userInfoBean);
				getActivity().startActivity(myuserinfoIn);
				
			}
			
			break;
		case R.id.fragment_myperson_settings:
			//设置
			if(Tools.isEmptyString(pkUserId)){
				Intent newsIn = new Intent();
				newsIn.setClass(getActivity(), LoginAndRegisterActivity.class);
				getActivity().startActivity(newsIn);
			}else {
				Intent settingsIn = new Intent();
				settingsIn.setClass(getActivity(),SettingsActivity.class);
				getActivity().startActivity(settingsIn);
			}
			break;
		default:
			break;
		}
		
	}


	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if (sign.equals(Protocol.GET_USER_INFO)) {
			//用户详情信息
			isFirst = false;
			userInfoBean = (UserInfoBean) bundle.getSerializable(Protocol.DATA);
			if(userInfoBean!= null){
				updataView();
				updataUserInfo();
			}
		}/*else if(sign.equals(Protocol.TO_LOGIN)){
			 GetDataPost.getInstance(getActivity()).getUserInfo(handler, pkUserId);
		}*/

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {

	}
	
	
	private void updataUserInfo(){
		PreferencesUtil.setPreferences(getActivity(), "usinHeadimage",
				userInfoBean.getUserImage());
		PreferencesUtil.setPreferences(getActivity(), "usinAccountbalance",
				userInfoBean.getUserBlance());
		PreferencesUtil.setPreferences(getActivity(), "carType",
				userInfoBean.getUserCarType());
		PreferencesUtil.setPreferences(getActivity(), "usinPhone",
				userInfoBean.getUserTel());
		PreferencesUtil.setPreferences(getActivity(), "nickName",
				userInfoBean.getUserNickName());
		PreferencesUtil.setPreferences(getActivity(), "pkUserinfo",
				userInfoBean.getPkUserId());
		PreferencesUtil.setPreferences(getActivity(), "usinFacticityname",
				userInfoBean.getUserRealName());
		PreferencesUtil.setPreferences(getActivity(), "usinSex",
				userInfoBean.getUserSex());
		PreferencesUtil.setPreferences(getActivity(), "usinBirthdate",
				userInfoBean.getUserBrithy());
		
		 
		
		
		/*PreferencesUtil.setPreferences(getActivity(), "usinUserstatus",
				userInfoBean.get);*/
		
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Modify_UserInfo);  
        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Login);  
        
        //注册广播        
        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);  
    } 
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if(action.equals(BroadcastUtil.BROADCAST_Modify_UserInfo)){  
					 GetDataPost.getInstance(getActivity()).getUserInfo(handler, pkUserId);
	         } else if( action.equals(BroadcastUtil.BROADCAST_Login)){
	        	 String pkid = intent.getStringExtra("pkUserId");
	        	 GetDataPost.getInstance(getActivity()).getUserInfo(handler, pkid);
	         }
		}  
          
    };  

}
