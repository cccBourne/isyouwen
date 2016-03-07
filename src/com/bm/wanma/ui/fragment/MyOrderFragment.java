package com.bm.wanma.ui.fragment;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;

import com.bm.wanma.R;
import com.bm.wanma.ui.activity.ITcpCallBack;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * 订单界面
 * @author cm
 *
 */
public class MyOrderFragment extends BaseFragment implements OnClickListener{

	private MyChargeOrderFragment chargeFragment;
	private MyBespokeOrderFragment bespokeFragment;
	private BaseFragment currentFragment;
	private TextView tv_bespoke_order,tv_charge_order;
	private boolean isBespoke;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isBespoke = true;
		initFragment();
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View myOrderFragment = inflater.inflate(
				R.layout.fragment_myorder, container, false);
		tv_bespoke_order = (TextView) myOrderFragment.findViewById(R.id.fragment_myorder_bespoke);
		tv_bespoke_order.setOnClickListener(this);
		tv_charge_order = (TextView) myOrderFragment.findViewById(R.id.fragment_myorder_charge);
		tv_charge_order.setOnClickListener(this);
		return myOrderFragment;
	}

	@SuppressLint("NewApi")
	private void initFragment(){
		if(bespokeFragment == null){
			bespokeFragment = new MyBespokeOrderFragment();
		}
		if(!bespokeFragment.isAdded()){
			getChildFragmentManager().beginTransaction().
			add(R.id.myorder_content_layout, bespokeFragment).commit();
			//记录当前Fragment
			currentFragment = bespokeFragment;
		}
		
	}

	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.fragment_myorder_bespoke:
			if(isBespoke){
				return;
			}
			if(bespokeFragment == null){
				bespokeFragment = new MyBespokeOrderFragment();
			}
			addOrShowFragment(getChildFragmentManager().beginTransaction(),bespokeFragment);
			isBespoke = true;
			tv_bespoke_order.setBackground(getActivity().getResources().getDrawable(R.drawable.myorder_shape_left_radius_white));
			tv_bespoke_order.setTextColor(getActivity().getResources().getColor(R.color.common_orange));
			tv_charge_order.setBackground(getActivity().getResources().getDrawable(R.drawable.myorder_shape_right_radius_orange));
			tv_charge_order.setTextColor(getActivity().getResources().getColor(R.color.common_white));
			break;
		case R.id.fragment_myorder_charge:
			if(!isBespoke){
				return;
			}
			if(chargeFragment == null){
				chargeFragment = new MyChargeOrderFragment();
			}
			addOrShowFragment(getChildFragmentManager().beginTransaction(),chargeFragment);
			isBespoke = false;
			tv_bespoke_order.setBackground(getActivity().getResources().getDrawable(R.drawable.myorder_shape_left_radius_orange));
			tv_bespoke_order.setTextColor(getActivity().getResources().getColor(R.color.common_white));
			tv_charge_order.setBackground(getActivity().getResources().getDrawable(R.drawable.myorder_shape_right_radius_white));
			tv_charge_order.setTextColor(getActivity().getResources().getColor(R.color.common_orange));
			break;

		default:
			break;
		}
	}
	/**
	 * 添加或者显示碎片
	 * @param transaction
	 * @param fragment
	 */
	private void addOrShowFragment(FragmentTransaction transaction,
			BaseFragment fragment) {
		if (currentFragment == fragment)
			return;

		if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
			transaction.hide(currentFragment)
					.add(R.id.myorder_content_layout, fragment).commit();
		} else {
			transaction.hide(currentFragment).show(fragment).commit();
		}

		currentFragment = fragment;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		
	}

	@Override
	public void onDetach() {
		super.onDetach();
		 try {
		      Field childFragmentManager = Fragment.class
		          .getDeclaredField("mChildFragmentManager");
		      childFragmentManager.setAccessible(true);
		      childFragmentManager.set(this, null);
		      Log.i("fragment", "Field --childFragmentManager");
		    } catch (NoSuchFieldException e) {
		      throw new RuntimeException(e);
		    } catch (IllegalAccessException e) {
		      throw new RuntimeException(e);
		    }
		
	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// TODO Auto-generated method stub

	}


	

}
