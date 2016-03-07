package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.entity.MyBespokeOrderBean;
import com.bm.wanma.entity.MyChargeOrderBean;
import com.bm.wanma.ui.activity.BespokeDetailActivity;
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
 * 充电列表，listview适配器
 *
 */
public class MyChargeOrderAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MyChargeOrderBean> mdata;
	private LayoutInflater inflater;
	private MyChargeOrderBean bean;
	
	public MyChargeOrderAdapter(Context context,ArrayList<MyChargeOrderBean> data) {
		this.mContext = context;
		this.mdata = data;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView tv_status = null;
		TextView tv_name = null;
		TextView tv_address = null;
		TextView tv_time_tag = null;
		TextView tv_time = null;
		View line = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listview_item_mybespoke, null);
			tv_status = (TextView)convertView.findViewById(R.id.listview_mybespoke_status);
			tv_name = (TextView)convertView.findViewById(R.id.listview_mybespoke_name);
			tv_address = (TextView)convertView.findViewById(R.id.listview_mybespoke_address);
			tv_time_tag = (TextView)convertView.findViewById(R.id.listview_mybespoke_time_tag);
			tv_time = (TextView)convertView.findViewById(R.id.listview_mybespoke_time);
			line = convertView.findViewById(R.id.listview_mybespoke_bottom_line);
			
			convertView.setTag(new MyHold(tv_status,tv_name,tv_address,tv_time_tag,tv_time,line));
			
		}else {
			MyHold hold = (MyHold) convertView.getTag();
			tv_status = hold.hold_tv_status;
			tv_name = hold.hold_tv_name;
			tv_address = hold.hold_tv_address;
			tv_time_tag = hold.hold_tv_time_tag;
			tv_time = hold.hold_tv_time;
			line = hold.hold_line;
		}
		
		bean = mdata.get(position);
		tv_name.setText(""+bean.getElPi_ElectricPileName());
		tv_address.setText(""+bean.getElPi_ElectricPileAddress());
		String beginTime = bean.getBegin_charge_time();
		String endTime = bean.getEnd_charge_time();
		String time = beginTime + "\n"+endTime;
		tv_time.setText(time);
		tv_time_tag.setText("充电时间:");
		int section = getSectionForPosition(position);	
		if(position == getPositionForSection(section)){
			tv_status.setVisibility(View.VISIBLE);
			tv_status.setText(bean.getChOr_ChargingStatus());
			if("充电中".equals(bean.getChOr_ChargingStatus())){
				//line.setVisibility(View.GONE);
				line.setVisibility(View.INVISIBLE);
				tv_time.setText(""+bean.getBegin_charge_time());
			}
		}else{
			tv_status.setVisibility(View.GONE);
		}	
		
		return convertView;
	}
	
	public int getSectionForPosition(int position) {
		return mdata.get(position).getChOr_ChargingStatus().charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mdata.get(i).getChOr_ChargingStatus();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		
		return -1;
	}
	
	

	private final class MyHold {
		TextView hold_tv_status = null;
		TextView hold_tv_name = null;
		TextView hold_tv_address = null;
		TextView hold_tv_time_tag = null;
		TextView hold_tv_time = null;
		View hold_line = null ;
		public MyHold(
				TextView tvstatus,TextView tvname,TextView tvaddr,TextView tvtimetag,
				TextView tvtime,View ll){
			this.hold_tv_status = tvstatus;
			this.hold_tv_name = tvname;
			this.hold_tv_address = tvaddr;
			this.hold_tv_time_tag = tvtimetag;
			this.hold_tv_time = tvtime;
			this.hold_line = ll;
		}
	}
	
	
}
