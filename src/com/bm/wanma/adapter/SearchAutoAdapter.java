package com.bm.wanma.adapter;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bm.wanma.R;
import com.bm.wanma.entity.MapModeBean;
import com.bm.wanma.utils.Tools;

public class SearchAutoAdapter extends BaseAdapter {
	 private LayoutInflater inflater;
	 private List<MapModeBean> mdata;
	 private MapModeBean listBean;
	 private String keyword,address,name;

	public SearchAutoAdapter(Context context,List<MapModeBean> data,String mword) {
		mdata = data;
		keyword = mword;
		inflater = LayoutInflater.from(context);
	}   

	@Override
	public int getCount() {
		return mdata.size();
	}

	@Override
	public Object getItem(int position) {
		return mdata.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		TextView autoTvName = null;
		TextView autoTvAddr = null;
		if(convertView == null){
			convertView = inflater.inflate(
		            R.layout.listview_item_search_point_auto, null);
			autoTvName = (TextView)convertView.findViewById(R.id.listview_item_search_point_auto_name);
			autoTvAddr = (TextView)convertView.findViewById(R.id.listview_item_search_point_auto_addr);
			convertView.setTag(new MyAutoHold(autoTvName,autoTvAddr));
		}else {
			MyAutoHold hold = (MyAutoHold) convertView.getTag();
			autoTvName = hold.autoname;
			autoTvAddr = hold.autoaddr;
		}
		listBean = mdata.get(position);
		address = listBean.getElectricAddress();
		name = listBean.getElectricName();
		if (!Tools.isEmptyString(address)) {
			//关键词高亮显示
			SpannableStringBuilder s = new SpannableStringBuilder(address);
			Pattern p = Pattern.compile(keyword);
			Matcher m = p.matcher(s);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				s.setSpan(new ForegroundColorSpan(Color.RED), start, end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			autoTvAddr.setText(s);
		}
		if (!Tools.isEmptyString(name)) {
			//关键词高亮显示
			SpannableStringBuilder s = new SpannableStringBuilder(name);
			Pattern p = Pattern.compile(keyword);
			Matcher m = p.matcher(s);
			while (m.find()) {
				int start = m.start();
				int end = m.end();
				s.setSpan(new ForegroundColorSpan(Color.RED), start, end,
						Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			autoTvName.setText(s);
		}
		
		return convertView;
	}
	
	
	
	private final class MyAutoHold {
		TextView autoname = null;
		TextView autoaddr = null;
		public MyAutoHold(
				TextView tv1,TextView tv2){
			this.autoname = tv1;
			this.autoaddr = tv2;
		}
	}
	
	
	
	
	
	

}
