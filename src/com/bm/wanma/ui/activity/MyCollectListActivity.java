package com.bm.wanma.ui.activity;

import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.adapter.MyCollectListViewAdapter;
import com.bm.wanma.entity.MyCollectBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.view.MyDetailListView;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

/**
 * @author cm
 * 我的收藏
 */
public class MyCollectListActivity extends BaseActivity implements OnClickListener{

	private ImageButton ib_back;
	private MyDetailListView listview;
	private MyCollectListViewAdapter mAdapter;
	private String userId,lat,lng;
	private ArrayList<MyCollectBean> listBean;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mycollect_list);
		ib_back = (ImageButton) findViewById(R.id.collect_list_back);
		ib_back.setOnClickListener(this);
		listview = (MyDetailListView) findViewById(R.id.collect_list_listview);
		userId = PreferencesUtil.getStringPreferences(this,"pkUserinfo");
		lat = PreferencesUtil.getStringPreferences(this,"currentlat");
		lng = PreferencesUtil.getStringPreferences(this,"currentlng");
		GetDataPost.getInstance(this).getMyCollectList(handler, userId, lat, lng);
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.collect_list_back:
			finish();
			
			break;

		default:
			break;
		}
		
	}
	@Override
	protected void getData() {

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(bundle != null){
			listBean = (ArrayList<MyCollectBean>) bundle.getSerializable(Protocol.DATA);
			mAdapter = new MyCollectListViewAdapter(this, listBean);
			listview.setAdapter(mAdapter);
		}

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		showToast(bundle.getString(Protocol.MSG));
		finish();

	}




}
