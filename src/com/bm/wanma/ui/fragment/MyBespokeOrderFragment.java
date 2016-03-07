package com.bm.wanma.ui.fragment;

import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.adapter.MyBespokeOrderAdapter;
import com.bm.wanma.adapter.MyBespokeOrderAdapter;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.entity.MyBespokeOrderBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.BaseActivity;
import com.bm.wanma.ui.activity.BespokeDetailActivity;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.view.MyDetailListView;
import com.bm.wanma.view.PullToRefreshListView;
import com.bm.wanma.view.PullToRefreshListView.OnLoadListener;
import com.bm.wanma.view.PullToRefreshListView.OnRefreshListener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 预约订单界面
 * @author cm
 *
 */
public class MyBespokeOrderFragment extends BaseFragment implements OnItemClickListener,OnRefreshListener,OnLoadListener{

	private MyDetailListView bespokeListView;
	private PullToRefreshListView mListview;
	private final String pageNum = "10";
	private String pkUserId;
	private ArrayList<MyBespokeOrderBean> allBeanlist,bespokeBeanList;
	private MyBespokeOrderBean bespokeBean;
	private BespokeDetailBean detailBean;
	private MyBespokeOrderAdapter mAdapter;
	private String downtime;
	private TextView tv_no_data;
	private Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(mContext == null){
			mContext = getActivity();
		}
		pkUserId = PreferencesUtil.getStringPreferences(mContext, "pkUserinfo");
		bespokeBeanList = new ArrayList<MyBespokeOrderBean>();
		if(isNetConnection()){
			//showPD("正在加载数据...");
			GetDataPost.getInstance(mContext).getMyBespokeList(handler, pkUserId);
		}
	}
	@Override
	public void onResume() {
		super.onResume();
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View bespokeOrderFragment = inflater.inflate(
				R.layout.fragment_mybespoke_order, container, false);
		init(bespokeOrderFragment);
		registerBoradcastReceiver();//注册广播
		return bespokeOrderFragment;
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
		mContext.unregisterReceiver(mBroadcastReceiver);
	}

    private void init(View mainView){
    	bespokeListView = (MyDetailListView) mainView.findViewById(R.id.mybespoke_listview);
    	mListview = (PullToRefreshListView) mainView.findViewById(R.id.mybespoke_refresh_listview);
    	tv_no_data = (TextView) mainView.findViewById(R.id.mybespoke_nodata);
    }

	

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		if(sign.equals(Protocol.MYBESPOKE_LIST)){//获取预约列表
			allBeanlist = (ArrayList<MyBespokeOrderBean>) bundle.getSerializable(Protocol.DATA);
			if(allBeanlist!= null && allBeanlist.size()> 0){
				tv_no_data.setVisibility(View.GONE);
				bespokeBeanList.clear();
				for(MyBespokeOrderBean bean : allBeanlist){
					//预约状态（只有3和4时可以续约和取消预约，5、6不会返回） 
					//1：取消预约 2：结束预约 3：续预约 4：预约中 5:预约确认中6：预约失败
					if("3".equals(bean.getBespBespokestatus()) || "4".equals(bean.getBespBespokestatus())){
						bean.setBespBespokestatus("预约中");
					}else if("2".equals(bean.getBespBespokestatus())){
						bean.setBespBespokestatus("已完成");
					}
					
					bespokeBeanList.add(bean);
				}
				
				//mAdapter = new MyBespokeOrderAdapter(getActivity(), bespokeBeanList);
				mAdapter = new MyBespokeOrderAdapter(mContext, bespokeBeanList);
				bespokeListView.setAdapter(mAdapter);
				bespokeListView.setOnItemClickListener(this);
			}else {
				tv_no_data.setVisibility(View.VISIBLE);
			}
		}else if(sign.equals(Protocol.MYBESPOKE_DETAIL)){//获取单个预约详情
			
			detailBean = (BespokeDetailBean) bundle.getSerializable(Protocol.DATA);
			Intent in = new Intent();
			in.setClass(mContext, BespokeDetailActivity.class);
			in.putExtra("bespokeDetail", detailBean);
			in.putExtra("mode", "list");
			in.putExtra("downtime", downtime);
			mContext.startActivity(in);
			
		}
		
		
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));

	}
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		showPD("正在获取数据,请稍等...");
		//预约中item
	    bespokeBean = bespokeBeanList.get(position);
		if("预约中".equals(bespokeBean.getBespBespokestatus())){
			downtime = "1";
			GetDataPost.getInstance(mContext).getBespokeDetail(handler, bespokeBean.getPkBespoke());
		}else {
			downtime = "2";//区别结束，还是进行中
			GetDataPost.getInstance(mContext).getBespokeDetail(handler, bespokeBean.getPkBespoke());
		}
		
	}
	
	public void registerBoradcastReceiver(){  
        IntentFilter myIntentFilter = new IntentFilter();  
        myIntentFilter.addAction("com.bm.wanma.bespoke.cancle"); 
       // myIntentFilter.addAction("com.bm.wanma.bespoke.ok");
        //注册广播        
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);  
    }  
	//取消预约返回列表时重新获取数据
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			 if(action.equals("com.bm.wanma.bespoke.cancle")||action.equals("com.bm.wanma.bespoke.ok")){  
				//showToast("com.bm.wanma.bespoke.cancle");
				 GetDataPost.getInstance(mContext).getMyBespokeList(handler, pkUserId);
	         }  
		}  
          
    };

	@Override
	public void onLoad() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}  



}
