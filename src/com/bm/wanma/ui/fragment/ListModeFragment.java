package com.bm.wanma.ui.fragment;


import java.util.Collections;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.adapter.MyListModeListViewAdapter;
import com.bm.wanma.entity.ListModeBean;
import com.bm.wanma.entity.SelectValueBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.ComparatorListMode;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.ProjectApplication;
import com.bm.wanma.widget.RequstDataClipLoading;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * @author cm
 *  首页列表界面
 */
public class ListModeFragment extends BaseFragment implements OnClickListener {

	private RelativeLayout rl_tab_score,rl_tab_price,rl_tab_distance;
	private TextView tv_tab_score,tv_tab_price,tv_tab_distance;
	private View v_tab_score,v_tab_price,v_tab_distance;
	private ListView mListView;
	private List<ListModeBean> mListBean ;
	private MyListModeListViewAdapter listModeAdapter;
	private boolean isDistance,isPrice,isScore;
	private RelativeLayout loading;
	private RequstDataClipLoading ccl;
	private String currentLat,currentLng;
	private String powerInterface,chargingMode,freeStatus,matchMyCar;
	private SelectValueBean currentbean,bean;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View listModeFragment = inflater.inflate(
				R.layout.fragment_home_listmode, container, false);
		initView(listModeFragment);
		initValue();
	
		
		return listModeFragment;
	}

	private void initView (View mView){
		rl_tab_distance = (RelativeLayout)mView.findViewById(R.id.list_mode_rl_distance);
		rl_tab_distance.setOnClickListener(this);
		rl_tab_price = (RelativeLayout)mView.findViewById(R.id.list_mode_rl_price);
		rl_tab_price.setOnClickListener(this);
		rl_tab_score = (RelativeLayout)mView.findViewById(R.id.list_mode_rl_score);
		rl_tab_score.setOnClickListener(this);
		tv_tab_distance = (TextView)mView.findViewById(R.id.list_mode_tv_distance);
		tv_tab_price = (TextView)mView.findViewById(R.id.list_mode_tv_price);
		tv_tab_score = (TextView)mView.findViewById(R.id.list_mode_tv_score);
		v_tab_distance = (View)mView.findViewById(R.id.list_mode_v_distance);
		v_tab_price = (View)mView.findViewById(R.id.list_mode_v_price);
		v_tab_score = (View)mView.findViewById(R.id.list_mode_v_score);
		loading = (RelativeLayout)mView.findViewById(R.id.listmode_fragment_loading);
		ccl = (RequstDataClipLoading)loading.findViewById(R.id.customClipLoading);
		mListView = (ListView)mView.findViewById(R.id.fragment_listmode_listview);
		mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);//设置禁止下拉
		
	}
	private void initValue(){
		isDistance = true;
		isPrice = false;
		isScore = false;
		currentLat = PreferencesUtil.getStringPreferences(getActivity(), "currentlat");
		currentLng = PreferencesUtil.getStringPreferences(getActivity(), "currentlng");
		currentbean = ProjectApplication.getInstance().getSelectValueBean();
		if(currentbean != null){
			//充电模式（5直流，14交流）
			if(currentbean.isFast() && !currentbean.isSlow()){
				chargingMode = "5";
			}else if(!currentbean.isFast() && currentbean.isSlow()){
				chargingMode = "14";
			}
			//接口方式（7国标，19美标，20欧标
			if(currentbean.isGuo() && !currentbean.isOu()){
				powerInterface = "7";
			}else if(!currentbean.isGuo() && currentbean.isOu()){
				powerInterface= "20";
			}
			//空闲充电点（智能、联网、有空闲枪头的桩）1选中
			if(currentbean.isIdle()){
				freeStatus = "1";
			}
			if(isNetConnection()){
				 loading.setVisibility(View.VISIBLE);
				 ccl.start();
				 if(currentbean.isMatch()){
					 matchMyCar = "1";
					 String pkuserid = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
					 GetDataPost.getInstance(getActivity()).getElectricPileList(handler, powerInterface, 
							 chargingMode,freeStatus, matchMyCar, 
							 pkuserid, currentLng, currentLat, null);
				 }else {
					 GetDataPost.getInstance(getActivity()).getElectricPileList(handler, powerInterface, 
							 chargingMode,freeStatus, null, null, currentLng, currentLat, null);
				 }
			 }else {
				 showToast("网络不稳，请稍后再试");
			 }
			
		}else {
			if(isNetConnection()){
				 loading.setVisibility(View.VISIBLE);
				 ccl.start();
			 GetDataPost.getInstance(getActivity()).getElectricPileList(handler, null, 
					 null,null, null, null, currentLng, currentLat, null);
			}else {
				 showToast("网络不稳，请稍后再试");
			 }
		}
		
	}
	//重新调用接口，获取有筛选条件的数据
	public void notifySelectValueChange(boolean isFirst){
		//第一次切换列表时，不调用
		if(!isFirst){
			bean = ProjectApplication.getInstance().getSelectValueBean();
			//if(bean != null && !bean.equals(currentbean)){//筛选条件变化时，才去调接口
			if(bean != null ){
				//充电模式（5直流，14交流）
				if(bean.isFast() && !bean.isSlow()){
					chargingMode = "5";
				}else if(!bean.isFast() && bean.isSlow()){
					chargingMode = "14";
				}else {
					chargingMode = null;
				}
				//接口方式（7国标，19美标，20欧标
				if(bean.isGuo() && !bean.isOu()){
					powerInterface = "7";
				}else if(!bean.isGuo() && bean.isOu()){
					powerInterface= "20";
				}else {
					powerInterface = null;
				}
				//空闲充电点（智能、联网、有空闲枪头的桩）1选中
				if(bean.isIdle()){
					freeStatus = "1";
				}else {
					freeStatus = null;
				}
				if(isNetConnection()){
					 loading.setVisibility(View.VISIBLE);
					 ccl.start();
					 if(bean.isMatch()){
						 matchMyCar = "1";
						 String pkuserid = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
						 GetDataPost.getInstance(getActivity()).getElectricPileList(handler, powerInterface, 
								 chargingMode,freeStatus, matchMyCar, 
								 pkuserid, currentLng, currentLat, null);
					 }else {
						 GetDataPost.getInstance(getActivity()).getElectricPileList(handler, powerInterface, 
								 chargingMode,freeStatus, null, null, currentLng, currentLat, null);
					 }
				 }else {
					 showToast("网络不稳，请稍后再试");
				 }
				
			}
			//currentbean = bean;
			else {
				if(isNetConnection()){
					 loading.setVisibility(View.VISIBLE);
					 ccl.start();
				 GetDataPost.getInstance(getActivity()).getElectricPileList(handler, null, 
						 null,null, null, null, currentLng, currentLat, null);
				}else {
					 showToast("网络不稳，请稍后再试");
				 }
			}
		}
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.list_mode_rl_distance:
			if(isDistance){
				return;
			}
			tv_tab_distance.setTextColor(getActivity().getResources().getColor(R.color.common_orange));
			v_tab_distance.setBackgroundColor(getActivity().getResources().getColor(R.color.common_orange));
			tv_tab_price.setTextColor(getActivity().getResources().getColor(R.color.common_light_black));
			v_tab_price.setBackgroundColor(getActivity().getResources().getColor(R.color.common_middle_gray));
			tv_tab_score.setTextColor(getActivity().getResources().getColor(R.color.common_light_black));
			v_tab_score.setBackgroundColor(getActivity().getResources().getColor(R.color.common_middle_gray));
			isDistance = true;
			isPrice = false;
			isScore = false;
			ComparatorListMode comparator = new ComparatorListMode("distance");
			Collections.sort(mListBean, comparator);
			listModeAdapter = new MyListModeListViewAdapter(getActivity(), mListBean);
			mListView.setAdapter(listModeAdapter);
			
			
			break;
		case R.id.list_mode_rl_price:
			if(isPrice){
				return;
			}
			tv_tab_price.setTextColor(getActivity().getResources().getColor(R.color.common_orange));
			v_tab_price.setBackgroundColor(getActivity().getResources().getColor(R.color.common_orange));
			tv_tab_distance.setTextColor(getActivity().getResources().getColor(R.color.common_light_black));
			v_tab_distance.setBackgroundColor(getActivity().getResources().getColor(R.color.common_middle_gray));
			tv_tab_score.setTextColor(getActivity().getResources().getColor(R.color.common_light_black));
			v_tab_score.setBackgroundColor(getActivity().getResources().getColor(R.color.common_middle_gray));
			isPrice = true;
			isDistance = false;
			isScore = false;
			ComparatorListMode comparator2 = new ComparatorListMode("price");
			Collections.sort(mListBean, comparator2);
			listModeAdapter = new MyListModeListViewAdapter(getActivity(), mListBean);
			mListView.setAdapter(listModeAdapter);
			
			break;
		case R.id.list_mode_rl_score:
			if(isScore){
				return;
			}
			tv_tab_score.setTextColor(getActivity().getResources().getColor(R.color.common_orange));
			v_tab_score.setBackgroundColor(getActivity().getResources().getColor(R.color.common_orange));
			tv_tab_distance.setTextColor(getActivity().getResources().getColor(R.color.common_light_black));
			v_tab_distance.setBackgroundColor(getActivity().getResources().getColor(R.color.common_middle_gray));
			tv_tab_price.setTextColor(getActivity().getResources().getColor(R.color.common_light_black));
			v_tab_price.setBackgroundColor(getActivity().getResources().getColor(R.color.common_middle_gray));
			isScore = true;
			isDistance = false;
			isPrice = false;
			ComparatorListMode comparator3 = new ComparatorListMode("score");
			Collections.sort(mListBean, comparator3);
			listModeAdapter = new MyListModeListViewAdapter(getActivity(), mListBean);
			mListView.setAdapter(listModeAdapter);
			
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// AMapNavi.getInstance(this).removeAMapNaviListener(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		
	} 
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		ccl.stop();
		loading.setVisibility(View.GONE);
		if (bundle != null) {
			mListBean = (List<ListModeBean>) bundle
			.getSerializable(Protocol.DATA);
			//默认按距离排序
			if(mListBean.size()>0){
				ComparatorListMode comparator = new ComparatorListMode("distance");
				Collections.sort(mListBean, comparator);
			}
			listModeAdapter = new MyListModeListViewAdapter(getActivity(), mListBean);
			mListView.setAdapter(listModeAdapter);
			listModeAdapter.notifyDataSetChanged();
			
			
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		showToast(bundle.getString(Protocol.MSG));
		ccl.stop();
		loading.setVisibility(View.GONE);
		
	}



}
