package com.bm.wanma.ui.activity;

import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.adapter.MyCommentPileListViewAdapter;
import com.bm.wanma.adapter.MyCommentStationListViewAdapter;
import com.bm.wanma.entity.PileCommentListBean;
import com.bm.wanma.entity.StationCommentListBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.view.PullToRefreshListView;
import com.bm.wanma.view.PullToRefreshListView.OnLoadListener;
import com.bm.wanma.view.PullToRefreshListView.OnRefreshListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class CommentDetailActivity extends BaseActivity implements OnClickListener,OnRefreshListener,OnLoadListener{

	private ImageButton ib_back;
	private PullToRefreshListView mListView;
	private MyCommentPileListViewAdapter mPileAdapter;
	private MyCommentStationListViewAdapter mStationAdapter;
	private final String pageNum = "10";
	private int currentPage,currentIndex;
	private ArrayList<StationCommentListBean> mStationCommentListBean,mStationCommentAllListBean;
	private ArrayList<PileCommentListBean> mPileListBean,mPileAllListBean;
	private boolean isRefresh;
	private String type,electricId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment_detail);
		ib_back = (ImageButton) findViewById(R.id.comment_detail_back);
		ib_back.setOnClickListener(this);
		
		mListView = (PullToRefreshListView) findViewById(R.id.comment_detail_listview);
		mListView.setOnRefreshListener(this);
		mListView.setOnLoadListener(this);
		mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		
		mStationCommentListBean = new ArrayList<StationCommentListBean>();
		mStationCommentAllListBean = new ArrayList<StationCommentListBean>();
		mPileListBean = new ArrayList<PileCommentListBean>();
		mPileAllListBean = new ArrayList<PileCommentListBean>();
		
		Intent getDetaIn = getIntent();
		getDetaIn.getSerializableExtra("commentBean");
		 
		
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_detail_back:
			finish();
			break;

		default:
			break;
		}
		
	}

	@Override
	public void onLoad() {
		isRefresh = false;
		//获取下一页数据
		currentPage ++;
		if("1".equals(type)){
			//获取电桩评价列表
			//showPD("正在加载数据...");
			GetDataPost.getInstance(CommentDetailActivity.this).getPileCommentList(handler, electricId, pageNum, String.valueOf(currentPage));
		}else if("2".equals(type)){
			//获取电站列表
			//showPD("正在加载数据...");
			GetDataPost.getInstance(CommentDetailActivity.this).getStationCommentList(handler, electricId, pageNum, String.valueOf(currentPage));
		}
		
	}

	@Override
	public void onRefresh() {
		isRefresh = true;
		currentPage = 1;
		if("1".equals(type)){
			//获取电桩评价列表
			//showPD("正在加载数据...");
			GetDataPost.getInstance(CommentDetailActivity.this).getPileCommentList(handler, electricId, pageNum, String.valueOf(currentPage));
		}else if("2".equals(type)){
			//获取电站列表
			//showPD("正在加载数据...");
			GetDataPost.getInstance(CommentDetailActivity.this).getStationCommentList(handler, electricId, pageNum, String.valueOf(currentPage));
		}
		
	}

	@Override
	protected void getData() {
		// 默认加载第一页数据
		currentPage = 1;
		currentIndex = 0;
		isRefresh = true;
		type = getIntent().getStringExtra("stationType");
		electricId = getIntent().getStringExtra("stationId");
		if("1".equals(type)){
			//获取电桩评价列表
			showPD("正在加载数据...");
			GetDataPost.getInstance(CommentDetailActivity.this).getPileCommentList(handler, electricId, pageNum, String.valueOf(currentPage));
		}else if("2".equals(type)){
			//获取电站列表
			showPD("正在加载数据...");
			GetDataPost.getInstance(CommentDetailActivity.this).getStationCommentList(handler, electricId, pageNum, String.valueOf(currentPage));
		}
		
		
	}
	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
			// 获取站的评论列表
				if (sign.equals(Protocol.GET_STATION_COMMENT)) {
					if (bundle != null) {
							mStationCommentListBean = (ArrayList<StationCommentListBean>)
									  bundle.getSerializable(Protocol.DATA);
						  if(isRefresh){
							  currentIndex = 0;
							  mStationCommentAllListBean.clear();
							  mStationCommentAllListBean.addAll(mStationCommentListBean);
							  mStationAdapter = new MyCommentStationListViewAdapter(CommentDetailActivity.this, mStationCommentAllListBean);
							  mListView.setAdapter(mStationAdapter);
							  mListView.onRefreshComplete();
							  mListView.setResultSize(mStationCommentListBean.size());
							  LogUtil.i("cm_refresh", "是刷新项" + mStationCommentListBean.size());
						  }else {
							  if(mStationCommentListBean!=null && mStationCommentListBean.size()>0){
								  mStationCommentAllListBean.addAll(mStationCommentListBean);
								  currentIndex = mStationCommentAllListBean.size()- mStationCommentListBean.size();
								  mStationAdapter = new MyCommentStationListViewAdapter(CommentDetailActivity.this, mStationCommentAllListBean);
								  mListView.setAdapter(mStationAdapter);
								  mListView.setSelection(currentIndex);
								  mListView.onLoadComplete();
								  mListView.setResultSize(mStationCommentListBean.size());
								  LogUtil.i("cm_refresh", "是加载项" + mStationCommentAllListBean.size());
							  }else {
								  mListView.onLoadComplete();
								  mListView.setResultSize(1);
								  LogUtil.i("cm_refresh", "是加载项" + "已无数据");
							  }
							 
						  }
						 // mStationAdapter.notifyDataSetChanged();
					}
				}
				// 获取桩的评论列表  mStiltListBean,mStiltAllListBean
				if (sign.equals(Protocol.GET_PILE_COMMENT)) {
					if (bundle != null) {
						mPileListBean = (ArrayList<PileCommentListBean>) bundle.getSerializable(Protocol.DATA); 
						  if(isRefresh){
							  mPileAllListBean.clear();
							  mPileAllListBean.addAll(mPileListBean);
							  mPileAdapter = new MyCommentPileListViewAdapter(CommentDetailActivity.this, mPileAllListBean);
							  mListView.setAdapter(mPileAdapter);
							  mListView.onRefreshComplete();
						  }else {
							  mPileAllListBean.addAll(mPileListBean);
							  mPileAdapter = new MyCommentPileListViewAdapter(CommentDetailActivity.this, mPileAllListBean);
							  mListView.setAdapter(mPileAdapter);
							  mListView.onLoadComplete();
						  }
						  mListView.setResultSize(mPileListBean.size());
						  mPileAdapter.notifyDataSetChanged();
					}
				}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		showToast(bundle.getString(Protocol.MSG));
		
	}

	
}
