package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.entity.PileHead;
import com.bm.wanma.ui.activity.BespokeActivity;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author cm
 * 站桩点详情 枪口列表界面，listview适配器
 *
 */
public class MyStationListViewAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<PileHead> mdata;
	private LayoutInflater inflater;
	private PileHead bean;
	private String price,electricId,comm_status;
	
	public MyStationListViewAdapter(Context context,ArrayList<PileHead> data,String id,String prices,String status) {
		this.mContext = context;
		this.mdata = data;
		this.price = prices;
		this.electricId = id;
		this.comm_status = status;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView tv_name = null;
		TextView tv_parkNum = null;
		TextView tv_status = null;
		TextView tv_bespoke = null;
		ImageView iv_icon = null;
		LinearLayout ll_convert = null;
		
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listview_item_station_detail, null);
			tv_name = (TextView)convertView.findViewById(R.id.listview_station_detail_name);
			tv_parkNum = (TextView)convertView.findViewById(R.id.listview_station_detail_parknum);
			tv_status = (TextView)convertView.findViewById(R.id.listview_station_detail_status);
			tv_bespoke = (TextView)convertView.findViewById(R.id.listview_station_detail_bespoke);
			iv_icon = (ImageView)convertView.findViewById(R.id.listview_station_detail_icon);
			ll_convert = (LinearLayout)convertView.findViewById(R.id.listview_station_detail_ll);
			convertView.setTag(new MyHold(tv_name, tv_parkNum,tv_status,tv_bespoke,iv_icon,ll_convert));
			
		}else {
			MyHold hold = (MyHold) convertView.getTag();
			tv_name = hold.hold_tv_name;
			tv_parkNum = hold.hold_tv_parkNum;
			tv_status = hold.hold_tv_status;
			tv_bespoke = hold.hold_tv_bespoke;
			iv_icon = hold.hold_iv_icon;
			ll_convert = hold.hold_ll_view;
		}
		bean = mdata.get(position);
		if(bean != null){
			//tv_name.setText(""+bean.getPileHeadName());
			//1号枪头转换成A
			String headnum = bean.getHeadNum();
			if(!Tools.isEmptyString(headnum)){
				int i = Integer.valueOf(headnum);
				 char c1=(char) (i+64);
				 tv_name.setText(c1 + "号枪头");
			}
			tv_parkNum.setText(""+bean.getParkNum());
			String status = bean.getPileHeadState();
			//（0空闲中，3预约中，6充电中，9停用中）
			ll_convert.setTag(position);
			if("1".equals(comm_status)){//电桩连接状态，0断开，1连接
				if("0".equals(status)){
					tv_status.setText("空闲中");
					tv_status.setTextColor(mContext.getResources().getColor(R.color.common_green));
					tv_bespoke.setTextColor(mContext.getResources().getColor(R.color.common_orange));
					iv_icon.setImageResource(R.drawable.pop_anchor_bespoke);
					ll_convert.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							int pos = Integer.parseInt(v.getTag().toString());
							PileHead headbean = mdata.get(pos);
							//去预约
							//Toast.makeText(mContext, "预约"+headlist.getPileHeadId(), 1).show();
							Intent bespokeIn = new Intent();
							bespokeIn.setClass(mContext, BespokeActivity.class);
							bespokeIn.putExtra("bespElectricpileid",electricId);//电桩id 
							bespokeIn.putExtra("bespElectricpilehead", headbean.getPileHeadId());//预约枪口id
							bespokeIn.putExtra("bespBespokeprice", price);//预约单价（保留小数点后2位）
							//bespokeIn.putExtra("bespElectricpileheadName",headbean.getPileHeadName());//枪口名称
							bespokeIn.putExtra("bespElectricpileheadName",headbean.getHeadNum());//枪口名称
							mContext.startActivity(bespokeIn);
							
						}
					});
					
					
				}else if("3".equals(status)){
					tv_status.setText("预约中");
				}
				else if("6".equals(status)){
					tv_status.setText("充电中");
				}
				else if("9".equals(status)){
					tv_status.setText("停用中");
				}
			}else {
				tv_status.setText("不在线");
			}
		}
		//偶数行  奇数行背景间隔改变
		if(position % 2 == 0){
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.common_light_white));
		}else {
			convertView.setBackgroundColor(mContext.getResources().getColor(R.color.common_white));
		}
		
		return convertView;
	}

	private final class MyHold {
		TextView hold_tv_name = null;
		TextView hold_tv_parkNum = null;
		TextView hold_tv_status = null;
		TextView hold_tv_bespoke = null;
		ImageView hold_iv_icon = null;
		LinearLayout hold_ll_view = null;
		public MyHold(
				TextView tvname,TextView tvpark,TextView tvstatus,
				TextView tvbespoke,ImageView ivicon,LinearLayout ll){
			this.hold_tv_name = tvname;
			this.hold_tv_parkNum = tvpark;
			this.hold_tv_status = tvstatus;
			this.hold_tv_bespoke = tvbespoke;
			this.hold_iv_icon = ivicon;
			this.hold_ll_view = ll;
		}
	}
	
	
}
