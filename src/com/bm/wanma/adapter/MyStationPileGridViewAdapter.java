package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.entity.ElectricPileBean;
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
 * 桩点详情界面，gridview适配器
 *
 */
public class MyStationPileGridViewAdapter extends BaseAdapter {
	private Context mContext;
	private String chargingMode;
	private ElectricPileBean mdata;
	private LayoutInflater inflater;

	
	public MyStationPileGridViewAdapter(Context context,ElectricPileBean data) {
		this.mContext = context;
		this.mdata = data;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mdata != null ? 1 :0;
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
		convertView = inflater.inflate(R.layout.gridview_item_station_detail,
				null);
		tv_number = (TextView) convertView
				.findViewById(R.id.gridview_station_detail_number);
		tv_chargeMode = (TextView) convertView
				.findViewById(R.id.gridview_station_detail_mode);
		convertView.setBackground(mContext.getResources().getDrawable(
				R.drawable.station_detail_arc_orange));
		tv_number.setTextColor(mContext.getResources().getColor(
				R.color.common_white));
		tv_chargeMode.setTextColor(mContext.getResources().getColor(
				R.color.common_white));
		chargingMode = mdata.getElectricPileChargingMode();
		tv_number.setVisibility(View.GONE);
		//5 直流桩（快充） 14 交流桩（慢充）
		if ("5".equals(chargingMode)) {
			tv_chargeMode.setText("快充");
		} else if ("14".equals(chargingMode)) {
			tv_chargeMode.setText("慢充");
		}
			
		
		return convertView;
	}

	
}
