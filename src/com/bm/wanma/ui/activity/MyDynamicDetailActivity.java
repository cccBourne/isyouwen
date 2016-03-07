package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.entity.MyDynamicListBean;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.Toast;

/**
 * @author cm
 * 动态详情界面
 */
public class MyDynamicDetailActivity extends Activity implements OnClickListener{

	private WebView webView;  
	private ImageButton ib_back;
	private MyDynamicListBean bean;
	private String url;
	private String id,userId;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dynamic_detail);
		bean = (MyDynamicListBean) getIntent().getSerializableExtra("releaseId");
		id = bean.getPk_release();
		userId = PreferencesUtil.getStringPreferences(this, "pkUserinfo");
		ib_back = (ImageButton) findViewById(R.id.activity_dynamic_detail_back);
		ib_back.setOnClickListener(this);
		
		webView = (WebView) findViewById(R.id.activity_dynamic_detail_web);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		url = Protocol.GET_MYDYNAMIC_DETAIL +"?id="+id+"&type=3"+"&userId="+userId;
		webView.loadUrl(url);
		
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_dynamic_detail_back:
			finish();
			break;

		default:
			break;
		}
		
	}

	
}
