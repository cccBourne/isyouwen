package com.bm.wanma.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.tsz.afinal.FinalDb;

import com.bm.wanma.R;
import com.bm.wanma.adapter.SearchAutoAdapter;
import com.bm.wanma.adapter.SearchMyCollectAdapter;
import com.bm.wanma.adapter.SearchMyHistoryAdapter;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.TipClearHistoryDialog;
import com.bm.wanma.entity.MapModeBean;
import com.bm.wanma.entity.MapModePileBean;
import com.bm.wanma.entity.MapModeStationBean;
import com.bm.wanma.entity.MyCollectBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.MyDetailListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * @author cm
 * 搜索充电点
 */
public class SearchPointActivity extends BaseActivity implements OnClickListener{
	
	private ImageButton ib_back;
	private EditText et_keyword;
	private TextView tv_search,tv_no_history,tv_history,tv_clear_history;
	private TextView tv_no_collect,tv_collect,tv_collect_more,tv_no_aotu;
	private LinearLayout ll_history_collect;
	private MyDetailListView history_listview,collect_listview,search_listview;
	private ArrayList<MyCollectBean> listBean;
	private ArrayList<String> historyList;
	private SearchMyCollectAdapter searchMyCollectAdapter;
	private SearchMyHistoryAdapter searchMyHistoryAdapter;
	private SearchAutoAdapter searchAutoAdapter;
	private ArrayList<MyCollectBean> mList;
	private List<MapModePileBean> allMapStiltBean;
	private List<MapModeStationBean> allMapStationBean;
	private List<MapModeBean> allMapBean;
	private List<MapModeBean> allKeywordMapBean;
	private FinalDb finalDb;
	private TipClearHistoryDialog mTipClearHistoryDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_search_point);
		
		initView();
		registerBoradcastReceiver();
		initDBValue();
		
	}
	
	private void initDBValue(){
		finalDb = FinalDb.create(this,"eichong.db");
		allMapStiltBean = finalDb.findAll(MapModePileBean.class);
		allMapStationBean = finalDb.findAll(MapModeStationBean.class);
		allKeywordMapBean = new ArrayList<MapModeBean>();
		allMapBean = new ArrayList<MapModeBean>();
		if (allMapStationBean.size() > 0) {
			allMapBean.addAll(allMapStationBean);
		}
		if (allMapStiltBean.size() > 0) {
			allMapBean.addAll(allMapStiltBean);
		}
		
	}
	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.activity_search_point_back);
		ib_back.setOnClickListener(this);
		et_keyword = (EditText) findViewById(R.id.activity_search_point_et);
		et_keyword.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_SEARCH) {  
                    startSearchKeyword();
                    saveHistory();
                }  
				return true;
			}
		});
		et_keyword.addTextChangedListener(new MyWatcher());
		tv_search = (TextView) findViewById(R.id.activity_search_point_search);
		tv_search.setOnClickListener(this);
		ll_history_collect = (LinearLayout) findViewById(R.id.search_point_ll);
		tv_no_history = (TextView) findViewById(R.id.search_point_tv_history_no);
		tv_history = (TextView) findViewById(R.id.search_point_tv_history);
		history_listview = (MyDetailListView) findViewById(R.id.search_point_history_listview);
		search_listview = (MyDetailListView) findViewById(R.id.search_point_search_listview);
		collect_listview = (MyDetailListView) findViewById(R.id.search_point_collect_listview);
		tv_clear_history = (TextView) findViewById(R.id.search_point_tv_history_clear);
		tv_clear_history.setOnClickListener(this);
		tv_no_collect = (TextView) findViewById(R.id.search_point_tv_collect_no);
		tv_collect = (TextView) findViewById(R.id.search_point_tv_collect);
		tv_collect_more = (TextView) findViewById(R.id.search_point_tv_collect_more);
		tv_collect_more.setOnClickListener(this);
		tv_no_aotu = (TextView) findViewById(R.id.search_point_search_listview_no_result);
		historyList = new ArrayList<String>();
		historyList = getHistory();
		if(historyList.size()>0){
			tv_history.setVisibility(View.VISIBLE);
			tv_clear_history.setVisibility(View.VISIBLE);
			searchMyHistoryAdapter = new SearchMyHistoryAdapter(this,historyList);
			history_listview.setAdapter(searchMyHistoryAdapter);
			history_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//去搜索
					et_keyword.setText(historyList.get(position));
					//goToSearch();
					
				}
			});
		}else {
			tv_no_history.setVisibility(View.VISIBLE);
		}
		mList = new ArrayList<MyCollectBean>();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_search_point_back:
			finish();
			break;
		case R.id.activity_search_point_search:
			//搜索
			startSearchKeyword();
			saveHistory();
			break;
		case R.id.search_point_tv_history_clear:
			//清空历史,弹框提示
			mTipClearHistoryDialog = new TipClearHistoryDialog(SearchPointActivity.this);
			mTipClearHistoryDialog.setCancelable(false);
			mTipClearHistoryDialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					clearHistory();
					mTipClearHistoryDialog.dismiss();
				}
			}) ;
			mTipClearHistoryDialog.setOnNegativeListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mTipClearHistoryDialog.dismiss();
				}
			});
			mTipClearHistoryDialog.show();
			break;
		case R.id.search_point_tv_collect_more:
			//更多
			searchMyCollectAdapter = new SearchMyCollectAdapter(SearchPointActivity.this, listBean);
			collect_listview.setAdapter(searchMyCollectAdapter);
			collect_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent,
						View view, int position, long id) {
				//跳转到地图
				Intent in = new Intent(BroadcastUtil.BROADCAST_SEARCH_POINT);
				in.putExtra("searchMode", "collect");
				in.putExtra("searchbean", listBean.get(position));
				SearchPointActivity.this.sendBroadcast(in);
				finish();
				}
			});
			tv_collect_more.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		
	}
	private void startSearchKeyword(){
		  //隐藏软键盘  
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);  
		String key = et_keyword.getText().toString();
		if(Tools.isEmptyString(key)){
			showToast("请输入关键词");
			return;
		}
		allKeywordMapBean.clear();
		for(MapModeBean bean :allMapBean){
			if("15".equals(bean.getElectricState())){
				if(bean.getElectricName().contains(key)
						|| bean.getElectricAddress().contains(key)){
					//包含地址
					allKeywordMapBean.add(bean);
				}
			}
			
		}
		if(allKeywordMapBean.size()==0){
			tv_no_aotu.setVisibility(View.VISIBLE);
			searchAutoAdapter = new SearchAutoAdapter(this, allKeywordMapBean, key);
			search_listview.setAdapter(searchAutoAdapter);
		}else {
			searchAutoAdapter = new SearchAutoAdapter(this, allKeywordMapBean, key);
			search_listview.setAdapter(searchAutoAdapter);
			search_listview.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					//跳转到地图
					Intent in = new Intent(BroadcastUtil.BROADCAST_SEARCH_POINT);
					in.putExtra("searchMode", "auto");
					in.putExtra("allKeywordMapBean", allKeywordMapBean.get(position));
					SearchPointActivity.this.sendBroadcast(in);
					finish();
					
				}
			});
			tv_no_aotu.setVisibility(View.GONE);
		}
	
		//隐藏收藏，搜索
		ll_history_collect.setVisibility(View.GONE);
		
		
		
	}
	
	private class MyWatcher implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			  	/*Pattern pattern = Pattern.compile("[\\u4E00-\\u9FA5]");
		        Matcher matcher = pattern.matcher(s);
		        if(matcher.find()){
					showToast(""+s);
				}*/
		}

		@Override
		public void afterTextChanged(Editable s) {
			String contents = s.toString();
			int length = contents.length();
			/*Pattern  p= Pattern.compile("[\u4e00-\u9fa5]");  
			Matcher m = p.matcher(contents);  
			if(m.matches()){
				showToast(contents);
			}*/
			
			/*if(length == 0){
				ll_history_collect.setVisibility(View.VISIBLE);
				tv_no_aotu.setVisibility(View.GONE);
			}*/
			
			
			
		}
		
	}


	@Override
	protected void getData() {
		if(isNetConnection()){
			String userId = PreferencesUtil.getStringPreferences(this, "pkUserinfo");
			String lat = PreferencesUtil.getStringPreferences(this, "currentlat");
			String lng = PreferencesUtil.getStringPreferences(this, "currentlng");
			GetDataPost.getInstance(this).getMyCollectList(handler, userId, lat,lng);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(bundle != null){
			listBean = (ArrayList<MyCollectBean>) bundle.getSerializable(Protocol.DATA);
			if(listBean!= null &&listBean.size()>0){
				//有收藏数据
				tv_collect.setVisibility(View.VISIBLE);
				if(listBean.size()>3){
					tv_collect_more.setVisibility(View.VISIBLE);
					mList.add(listBean.get(0));
					mList.add(listBean.get(1));
					mList.add(listBean.get(2));
					searchMyCollectAdapter = new SearchMyCollectAdapter(SearchPointActivity.this, mList);
					collect_listview.setAdapter(searchMyCollectAdapter);
					collect_listview.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
						//跳转到地图
						Intent in = new Intent(BroadcastUtil.BROADCAST_SEARCH_POINT);
						in.putExtra("searchMode", "collect");
						in.putExtra("searchbean", mList.get(position));
						SearchPointActivity.this.sendBroadcast(in);
						finish();
						}
					});
				}else {
					searchMyCollectAdapter = new SearchMyCollectAdapter(SearchPointActivity.this, listBean);
					collect_listview.setAdapter(searchMyCollectAdapter);
					collect_listview.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
						//跳转到地图
						Intent in = new Intent(BroadcastUtil.BROADCAST_SEARCH_POINT);
						in.putExtra("searchMode", "collect");
						in.putExtra("searchbean", listBean.get(position));
						SearchPointActivity.this.sendBroadcast(in);
						finish();
						}
					});
					
					
				}
				
			}else {
				//没有收藏数据
				
			}
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// TODO Auto-generated method stub
		
	} 
	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver);
	}
	
	//保存搜索历史，最新5条
	private void saveHistory(){
		String text = et_keyword.getText().toString();
		String savehistory = PreferencesUtil.getStringPreferences(SearchPointActivity.this, "searchHistory");
		if(!savehistory.contains(text + ",")){
			  StringBuilder sb = new StringBuilder(savehistory);
			  sb.insert(0, text + ",");
			  //如果超过5条，更新最早的
			  String[] listH = sb.toString().split(",");
			  if(listH.length>5){
				  String[] newHistories = new String[5];  
				  System.arraycopy(listH, 0, newHistories, 0, 5); 
				  sb = new StringBuilder();
				  sb.append(newHistories[0]+",").append(newHistories[1]+",")
				  .append(newHistories[2]+",").append(newHistories[3]+",")
				  .append(newHistories[4]);
			  }
			  PreferencesUtil.setPreferences(SearchPointActivity.this, "searchHistory",sb.toString());
		}
	}
	//获取搜索历史
	private ArrayList<String> getHistory(){
		String savehistory = PreferencesUtil.getStringPreferences(SearchPointActivity.this, "searchHistory");
		String[] listH = savehistory.split(",");
		ArrayList<String> list = new ArrayList<String>();
		for(int i=0;i<listH.length;i++){
			if(!Tools.isEmptyString(listH[i])){
				list.add(listH[i]);
			}
		}
		return list;
	}
	
	//清空搜索历史
		private void clearHistory(){
			PreferencesUtil.setPreferences(SearchPointActivity.this, "searchHistory", "");
			sendBroadcast(new Intent(BroadcastUtil.BROADCAST_SEARCH_POINT_DELETE_HISTORY));
		}
		
		private  void registerBoradcastReceiver(){  
	        IntentFilter myIntentFilter = new IntentFilter();  
	        myIntentFilter.addAction(BroadcastUtil.BROADCAST_SEARCH_POINT_DELETE_HISTORY); 
	        //注册广播        
	        registerReceiver(mBroadcastReceiver, myIntentFilter);  
	    }  
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if(action.equals(BroadcastUtil.BROADCAST_SEARCH_POINT_DELETE_HISTORY)){ 
				 if(getHistory().size()==0){
					 tv_clear_history.setVisibility(View.GONE);
					 tv_history.setVisibility(View.GONE);
					 tv_no_history.setVisibility(View.VISIBLE);
					 history_listview.setVisibility(View.GONE);
				 }
			 }
			
		}
		
	};	
		

}
