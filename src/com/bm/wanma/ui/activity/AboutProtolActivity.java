package com.bm.wanma.ui.activity;

import com.bm.wanma.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutProtolActivity extends Activity {

	private WebView webView;  
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_eichong_protocol);
		
		  	webView = (WebView)findViewById(R.id.eichong_protocel_web);  
	        webView.getSettings().setBuiltInZoomControls(true);  
	        webView.getSettings().setJavaScriptEnabled(true);  
	        webView.getSettings().setBuiltInZoomControls(true);  
	        webView.loadUrl("file:///android_asset/xieyi.html");  
		
		
	}

	
}
