package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.entity.MyBespokeOrderBean;
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
 * 预约列表，listview适配器
 *
 */
public class MyBespokeOrderAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<MyBespokeOrderBean> mdata;
	private LayoutInflater inflater;
	private MyBespokeOrderBean bean;
	
	public MyBespokeOrderAdapter(Context context,ArrayList<MyBespokeOrderBean> data) {
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
		
		
		
		/*convertView = inflater.inflate(R.layout.listview_item_mybespoke, null);
		tv_status = (TextView)convertView.findViewById(R.id.listview_mybespoke_status);
		tv_name = (TextView)convertView.findViewById(R.id.listview_mybespoke_name);
		tv_address = (TextView)convertView.findViewById(R.id.listview_mybespoke_address);
		tv_time_tag = (TextView)convertView.findViewById(R.id.listview_mybespoke_time_tag);
		tv_time = (TextView)convertView.findViewById(R.id.listview_mybespoke_time);
		line = convertView.findViewById(R.id.listview_mybespoke_bottom_line);*/
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
		tv_name.setText(""+bean.getEpName());
		tv_address.setText(""+bean.getEpAddress());
		String beginTime = bean.getBespBeginTime();
		String endTime = bean.getBespEndTime();
		String time = beginTime + "\n"+endTime;
		tv_time.setText(time);
		int section = getSectionForPosition(position);	
		if(position == getPositionForSection(section)){
			tv_status.setVisibility(View.VISIBLE);
			tv_status.setText(bean.getBespBespokestatus());
			if("预约中".equals(bean.getBespBespokestatus())){
				//line.setVisibility(View.GONE);
				line.setVisibility(View.INVISIBLE);
				tv_time_tag.setText("下单时间:");
				tv_time.setText(""+bean.getBespBeginTime());
			}
		}else{
			tv_status.setVisibility(View.GONE);
		}	
		
		return convertView;
	}
	
	public int getSectionForPosition(int position) {
		return mdata.get(position).getBespBespokestatus().charAt(0);
	}

	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = mdata.get(i).getBespBespokestatus();
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
