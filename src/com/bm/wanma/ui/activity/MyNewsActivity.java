package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.ui.fragment.BaseFragment;
import com.bm.wanma.ui.fragment.MyNewsFeedbackFragment;
import com.bm.wanma.ui.fragment.MyNewsSystemFragment;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author cm
 * 我的消息
 *
 */
public class MyNewsActivity extends Activity implements OnClickListener{

	private ImageButton ib_back;
	private RelativeLayout rl_system_news,rl_feedback_news;
	private TextView tv_system_news,tv_feedback_news;
	private View v_system_news,v_feedback_news;
	private boolean isSystemNews,isFeedbackNews;
	private BaseFragment currentFragment;
	private MyNewsSystemFragment newsSystemFragment;
	private MyNewsFeedbackFragment newsFeedbackFragment;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mynews);
		ib_back = (ImageButton) findViewById(R.id.mynews_back);
		ib_back.setOnClickListener(this);
		rl_system_news = (RelativeLayout) findViewById(R.id.mynews_system_rl);
		rl_system_news.setOnClickListener(this);
		tv_system_news = (TextView) findViewById(R.id.mynews_system_tv);
		v_system_news = findViewById(R.id.mynews_system_v);
		rl_feedback_news = (RelativeLayout) findViewById(R.id.mynews_feedback_rl);
		rl_feedback_news.setOnClickListener(this);
		tv_feedback_news = (TextView) findViewById(R.id.mynews_feedback_tv);
		v_feedback_news = findViewById(R.id.mynews_feedback_v);
		isSystemNews = true;
		isFeedbackNews = false;
		if(newsSystemFragment == null){
			newsSystemFragment = new MyNewsSystemFragment();
		}
		if(!newsSystemFragment.isAdded()){
			getFragmentManager().beginTransaction().add(R.id.mynews_content_layout, newsSystemFragment).commit();
			currentFragment = newsSystemFragment;
		}
		
	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mynews_back:
			finish();
			break;
		case R.id.mynews_system_rl:
			if(isSystemNews){
				return;
			}
			if(newsSystemFragment == null){
				newsSystemFragment = new MyNewsSystemFragment();
			}
			addOrShowFragment(getFragmentManager().beginTransaction(), newsSystemFragment);
			tv_system_news.setTextColor(getResources().getColor(R.color.common_orange));
			v_system_news.setBackgroundColor(getResources().getColor(R.color.common_orange));
			tv_feedback_news.setTextColor(getResources().getColor(R.color.common_gray));
			v_feedback_news.setBackgroundColor(getResources().getColor(R.color.common_middle_gray));
			isSystemNews = true;
			isFeedbackNews = false;
			
			break;
		case R.id.mynews_feedback_rl:
			if(isFeedbackNews){
				return;
			}
			if(newsFeedbackFragment == null){
				newsFeedbackFragment = new MyNewsFeedbackFragment();
			}
			addOrShowFragment(getFragmentManager().beginTransaction(), newsFeedbackFragment);
			tv_feedback_news.setTextColor(getResources().getColor(R.color.common_orange));
			v_feedback_news.setBackgroundColor(getResources().getColor(R.color.common_orange));
			tv_system_news.setTextColor(getResources().getColor(R.color.common_gray));
			v_system_news.setBackgroundColor(getResources().getColor(R.color.common_middle_gray));
			isSystemNews = false;
			isFeedbackNews = true;
			
			break;	
			
			
		default:
			break;
		}
		
	}
	/**
	 * 添加或者显示碎片
	 * @param transaction
	 * @param fragment
	 */
	public void addOrShowFragment(FragmentTransaction transaction,
			BaseFragment fragment) {
		if (currentFragment == fragment)
			return;

		if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
			transaction.hide(currentFragment)
					.add(R.id.mynews_content_layout, fragment).commit();
		} else {
			transaction.hide(currentFragment).show(fragment).commit();
		}
		currentFragment = fragment;
	}
	

}
