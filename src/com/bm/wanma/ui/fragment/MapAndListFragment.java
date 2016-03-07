package com.bm.wanma.ui.fragment;

import com.bm.wanma.R;
import com.bm.wanma.ui.activity.LoginAndRegisterActivity;
import com.bm.wanma.ui.activity.SearchPointActivity;
import com.bm.wanma.ui.scan.CaptureActivity;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 首页地图列表界面
 * @author cm
 *
 */
public class MapAndListFragment extends BaseFragment implements OnClickListener{
	private BaseFragment currentFragment;
	private MapModeFragment mapModeF;
	private ListModeFragment listModeF;
	private ImageView scanBtn;
	private ImageButton switchBtn;
	private RelativeLayout search_rl;
	private boolean isMapMode;
	private boolean isFirstSwitch;
	private String pkUserinfo;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFragment();
		isMapMode = true;
		isFirstSwitch = true;
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View pileFragment = inflater.inflate(
				R.layout.fragment_map_and_list, container, false);
		
		scanBtn= (ImageView)pileFragment.findViewById(R.id.scanbutton);
		scanBtn.setOnClickListener(this);
		switchBtn = (ImageButton)pileFragment.findViewById(R.id.swith_button);
		switchBtn.setOnClickListener(this);
		search_rl = (RelativeLayout)pileFragment.findViewById(R.id.search_rl);
		search_rl.setOnClickListener(this);
		
		
		return pileFragment;
	}

	

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scanbutton:
			clickScanBtn();
			
			break;
		case R.id.swith_button:
			clickSwitchBtn();
			break;
		case R.id.search_rl:
			//进入搜索界面
			Intent searchIn = new Intent();
			searchIn.setClass(getActivity(),SearchPointActivity.class);
			startActivity(searchIn);
			break;
		default:
			break;
		}
	}
	
	
	
	@SuppressLint("NewApi")
	private void initFragment(){
		
		if(mapModeF == null){
			mapModeF = new MapModeFragment();
			
		}
		if(!mapModeF.isAdded()){
			getChildFragmentManager().beginTransaction().
			add(R.id.map_list_content_layout, mapModeF).commit();
			// 记录当前Fragment
			currentFragment = mapModeF;
		}
		
	}
	
	/**
	 * 地图列表按钮切换事件
	 */
	@SuppressLint("NewApi")
	private void clickSwitchBtn(){
		if(isMapMode){
			if(listModeF == null){
				listModeF = new ListModeFragment();
			}
			isMapMode = false;
			addOrShowFragment(getChildFragmentManager().beginTransaction(),listModeF);
			switchBtn.setImageResource(R.drawable.nav_btn_map);
			//通知列表--有筛选条件改变时，重新获取数据
			listModeF.notifySelectValueChange(isFirstSwitch);
			isFirstSwitch = false;
			
		}else {
			isMapMode = true;
			addOrShowFragment(getChildFragmentManager().beginTransaction(),mapModeF);
			//switchBtn.setBackground(getActivity().getResources().getDrawable(R.drawable.nav_btn_list));
			switchBtn.setImageResource(R.drawable.nav_btn_list);
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
					.add(R.id.map_list_content_layout, fragment).commit();
		} else {
			transaction.hide(currentFragment).show(fragment).commit();
		}

		currentFragment = fragment;
	}

	/**
	 * 点击扫描按钮
	 * 
	 */
	private void clickScanBtn() {
		 
		pkUserinfo = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
		
		if(!Tools.isEmptyString(pkUserinfo)){
			Intent scanIntent = new Intent();
			scanIntent.setClass(getActivity(),
					CaptureActivity.class);
			startActivity(scanIntent);
		}else {
			Intent in = new Intent();  //跳转登录界面
			in.setClass(getActivity(), LoginAndRegisterActivity.class);
			startActivity(in);
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
