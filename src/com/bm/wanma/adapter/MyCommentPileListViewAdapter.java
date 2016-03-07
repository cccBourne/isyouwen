package com.bm.wanma.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bm.wanma.R;
import com.bm.wanma.entity.PileCommentListBean;
import com.bm.wanma.entity.PileHead;
import com.bm.wanma.utils.Tools;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author cm
 * 桩点评价详情界面，listview适配器
 *
 */
public class MyCommentPileListViewAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<PileCommentListBean> mdata;
	private LayoutInflater inflater;
	private PileCommentListBean bean;
	
	public MyCommentPileListViewAdapter(Context context,ArrayList<PileCommentListBean> data) {
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
		
		ImageView iv_photo = null;
		TextView tv_nick = null;
		RatingBar rb_ratingbar = null;
		TextView tv_content = null;
		TextView tv_time = null;
		View diliver = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.listview_item_comment_detail, null);
			iv_photo = (ImageView)convertView.findViewById(R.id.listview_item_comment_photo);
			tv_nick = (TextView)convertView.findViewById(R.id.listview_item_comment_nick);
			rb_ratingbar = (RatingBar)convertView.findViewById(R.id.listview_item_comment_ratingbar);
			tv_content = (TextView)convertView.findViewById(R.id.listview_item_comment_content);
			tv_time = (TextView)convertView.findViewById(R.id.listview_item_comment_time);
			diliver = (View)convertView.findViewById(R.id.listview_item_comment_line);
			convertView.setTag(new MyHold(iv_photo,tv_nick,rb_ratingbar,tv_content,tv_time,diliver));
			
		}else {
			MyHold hold = (MyHold) convertView.getTag();
			iv_photo = hold.hold_iv_photo;
			tv_nick = hold.hold_tv_nick;
			rb_ratingbar = hold.hold_rb_comment;
			tv_content = hold.hold_tv_content;
			tv_time = hold.hold_tv_time;
			diliver = hold.hold_line;
		}
		bean = mdata.get(position);
		if(bean != null){
			//对控件赋值
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.bg_map_head2)
			.showImageOnFail(R.drawable.bg_map_head2) 
			.cacheInMemory(true)
			.cacheOnDisk(false)
			.bitmapConfig(Config.RGB_565)
			.build(); 
			ImageLoader.getInstance().displayImage(bean.getUserImage(), iv_photo, options);
			if(!Tools.isEmptyString(bean.getEpc_UserName())){
				String tempnick = bean.getEpc_UserName();
				//String nick = tempnick.substring(0,3)+"****"+tempnick.substring(7,tempnick.length());
				tv_nick.setText(tempnick);
			}
			String ratingbar = bean.getEps_CommentStar();
			if(!Tools.isEmptyString(ratingbar)){
				float rate = Float.valueOf(ratingbar);
				rb_ratingbar.setRating(rate);
			}else {
				rb_ratingbar.setRating(0);
				//rb_ratingbar.setVisibility(View.GONE);
			}
			String content = bean.getEpc_Content();
			if(!Tools.isNull(content)){
				tv_content.setText(content);
			}
			String creatTime = bean.getEpc_Createdate();
			if(!Tools.isEmptyString(creatTime)){
				tv_time.setText(creatTime);
			}
			
		}
		
	
		
		return convertView;
	}

	private final class MyHold {
		ImageView hold_iv_photo = null;
		TextView hold_tv_nick = null;
		RatingBar hold_rb_comment = null;
		TextView hold_tv_content = null;
		TextView hold_tv_time = null;
		View hold_line = null;
		public MyHold(
				ImageView tvname,TextView tvpark,RatingBar tvstatus,
				TextView tvbespoke,TextView ivicon,View lll){
			this.hold_iv_photo = tvname;
			this.hold_tv_nick = tvpark;
			this.hold_rb_comment = tvstatus;
			this.hold_tv_content = tvbespoke;
			this.hold_tv_time = ivicon;
			this.hold_line = lll;
		}
	}
	
	
}
