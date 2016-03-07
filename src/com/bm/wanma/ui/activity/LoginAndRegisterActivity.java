package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.ui.fragment.BaseFragment;
import com.bm.wanma.ui.fragment.LoginFragment;
import com.bm.wanma.ui.fragment.RegisterFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
/**
 * @author cm
 *登录注册宿主 含两个fragment
 */
public class LoginAndRegisterActivity extends Activity implements OnClickListener {
	
	private LoginFragment loginFragment;
	private RegisterFragment registerFragment;
	private  BaseFragment currentFragment;
	public  static TextView tv_close,tv_title,tv_switch;
	public static  boolean isLogin;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_login_register);
		initUI();
		if(loginFragment == null){
			loginFragment = new LoginFragment();
		}
		if(!loginFragment.isAdded()){
			getFragmentManager().beginTransaction().
			add(R.id.login_content_layout,loginFragment).commit();
			currentFragment = loginFragment;
		}
		
		
	}
	
	private void initUI(){
		isLogin = true;
		tv_close = (TextView)findViewById(R.id.login_register_close);
		tv_close.setOnClickListener(this);
		tv_title = (TextView)findViewById(R.id.login_register_title);
		tv_switch = (TextView)findViewById(R.id.login_regist_switch);
		tv_switch.setOnClickListener(this);
	}
	
	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_register_close:
			finish();
			overridePendingTransition(0, R.anim.login_bottom_out);
			break;
		case R.id.login_regist_switch:
			
			if(isLogin){
				registerFragment = new RegisterFragment();
				addOrShowFragment(getFragmentManager().beginTransaction(), registerFragment);
				isLogin = false;
				tv_title.setText("注册");
				tv_switch.setText("登录");
			}else {
				if(loginFragment == null){
					loginFragment = new LoginFragment();
				}
				addOrShowFragment(getFragmentManager().beginTransaction(), loginFragment);
				isLogin = true;
				tv_title.setText("登录");
				tv_switch.setText("注册");
				
			}
			break;
		default:
			break;
		}
		
	}
	// 重写返回键处理事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			finish();
			overridePendingTransition(0, R.anim.login_bottom_out);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 添加或者显示碎片
	 * @param transaction
	 * @param fragment
	 */
	public  void addOrShowFragment(FragmentTransaction transaction,
			BaseFragment fragment) {
		if (currentFragment == fragment)
			return;
		if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
			transaction.remove(currentFragment)
			.add(R.id.login_content_layout, fragment).commit();
		} else {
			transaction.remove(currentFragment).show(fragment).commit();
		}
		
		/*transaction.replace(R.id.login_content_layout, fragment).addToBackStack(null).commit();*/

		currentFragment = fragment;
	}
	

	
}
