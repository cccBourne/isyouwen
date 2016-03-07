package com.bm.wanma.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.bm.wanma.R;
import com.bm.wanma.entity.WalletBean;

/**
 * 我的钱包 adpter
 */
public class MyWalletAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<WalletBean> list;
	private WalletBean bean;

	public MyWalletAdapter(Context context) {
		this.context = context;
	}

	public void setList(ArrayList<WalletBean> list) {
		this.list = list;
		notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		TextView tv_type = null;
		TextView tv_time = null;
		TextView tv_money = null;
		if (convertView == null) {
			convertView = View.inflate(context, R.layout.listview_item_mywallet, null);
			tv_type = (TextView)convertView.findViewById(R.id.mywallet_listitem_type);// 消费类型
			tv_time =(TextView)convertView.findViewById(R.id.mywallet_listitem_time);// 消费时间   --2014年11月充电消费
			//holder.tv_state = ViewHolder.get(convertView, R.id.mywallet_listitem_state);// 交易状态  --交易成功
			tv_money = (TextView)convertView.findViewById(R.id.mywallet_listitem_money);// 交易金额 --50
			convertView.setTag(new MyHold(tv_type,tv_time,tv_money));
		} else {
			MyHold hold = (MyHold) convertView.getTag();
			tv_type = hold.hold_tv_type;
			tv_time = hold.hold_tv_time;
			tv_money = hold.hold_tv_money;
		}
		bean = list.get(position);
		if(bean != null){//(1-充电消费 2-预约消费 3-购物消费 4-充值
			if("1".equals(bean.getRecordTitle())){
				tv_type.setText("充电");
				tv_time.setText(""+bean.getRecordTime());
				tv_money.setText("-"+bean.getRecordMoney());
			}else if("2".equals(bean.getRecordTitle())){
				tv_type.setText("预约");
				tv_time.setText(""+bean.getRecordTime());
				tv_money.setText("-"+bean.getRecordMoney());
			}else if("3".equals(bean.getRecordTitle())){
				tv_type.setText("购物");
				tv_time.setText(""+bean.getRecordTime());
				tv_money.setText("-"+bean.getRecordMoney());
			}else if("4".equals(bean.getRecordTitle())){
				tv_type.setText("充值");
				tv_time.setText(""+bean.getRecordTime());
				tv_money.setText("+"+bean.getRecordMoney());
			}
		}
		
		return convertView;
	}


	@Override
	public int getCount() {
		return list!=null?list.size():0;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	private final class MyHold {
		TextView hold_tv_type = null;
		TextView hold_tv_time = null;
		TextView hold_tv_money = null;
		public MyHold(
				TextView tvname,TextView tvpark,TextView tvstatus){
			this.hold_tv_type = tvname;
			this.hold_tv_time = tvpark;
			this.hold_tv_money = tvstatus;
		}
	}

}
