package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.entity.PowerElectricpileListBean;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author cm
 * 站点详情界面，gridview适配器
 *
 */
public class MyStationGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private String chargingMode;
	private String epnum;
	private ArrayList<PowerElectricpileListBean> mdata;
	private PowerElectricpileListBean bean;
	private LayoutInflater inflater;
	 //标识选择的Item
	private int mselection = -1;
	
	 public void setSelection(int selection){
		 this.mselection = selection;
		 super.notifyDataSetChanged();
		 }
	
	
	public MyStationGridViewAdapter(Context context,ArrayList<PowerElectricpileListBean> data) {
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

	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView tv_number = null;
		TextView tv_chargeMode = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.gridview_item_station_detail, null);
			tv_number = (TextView)convertView.findViewById(R.id.gridview_station_detail_number);
			tv_chargeMode = (TextView)convertView.findViewById(R.id.gridview_station_detail_mode);
			convertView.setTag(new MyHold(tv_number, tv_chargeMode));
		}else {
			MyHold hold = (MyHold) convertView.getTag();
			tv_number = hold.tv_number;
			tv_chargeMode = hold.tv_mode;
			
		}
		bean = mdata.get(position);
		if(bean != null){
			chargingMode = bean.getElpiChargingmode();
			epnum = bean.getEp_num();
		}
		tv_number.setText(epnum+"号桩");
		if("5".equals(chargingMode)){
			tv_chargeMode.setText("快");
		}else if("14".equals(chargingMode)){
			tv_chargeMode.setText("慢");
		}
		
		if (position == mselection) {
				convertView.setBackground(mContext.getResources().getDrawable(
						R.drawable.station_detail_arc_orange));
				tv_number.setTextColor(mContext.getResources().getColor(R.color.common_white));
				tv_chargeMode.setTextColor(mContext.getResources().getColor(R.color.common_white));
				
		} else {
			convertView.setBackground(mContext.getResources().getDrawable(
					R.drawable.station_detail_arc_white));
			tv_number.setTextColor(mContext.getResources().getColor(R.color.common_black));
			tv_chargeMode.setTextColor(mContext.getResources().getColor(R.color.common_black));
			
		}
		
		return convertView;
	}

	private final class MyHold {
		TextView tv_number = null;
		TextView tv_mode = null;
		public MyHold(
				TextView tvnum,TextView tvmode){
			this.tv_number = tvnum;
			this.tv_mode = tvmode;
		}
	}
	
	
}
