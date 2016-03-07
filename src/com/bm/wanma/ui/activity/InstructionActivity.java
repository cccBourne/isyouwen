package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.net.Protocol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.TextView;

/**
 * @author cm
 * 操作说明
 */
public class InstructionActivity extends Activity  implements OnClickListener{

	private WebView webView;  
	//private TextView tv_close;
	private String url;
	//private String priceId;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_instruction);
		//tv_close = (TextView) findViewById(R.id.about_price_close);
		//tv_close.setOnClickListener(this);
		//priceId = getIntent().getStringExtra("priceId");
		webView = (WebView) findViewById(R.id.instruction_webview);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setSupportZoom(true);// 便页面支持缩放
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setUseWideViewPort(true);// 设置此属性，可任意比例缩放
		webView.getSettings().setLoadWithOverviewMode(true);
		url = Protocol.INSTRUCTION;
		//url = "http://html.eichong.com/html/help/index.html";
		webView.addJavascriptInterface(getHtmlObject(), "jsCall");
		webView.setWebChromeClient(new WebChromeClient());//允许alert对话框弹出
		webView.loadUrl(url);
		
	}
	//按返回键时， 不退出程序而是返回上一浏览页面
	public boolean onKeyDown(int keyCode, KeyEvent event) {       
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {       
        	webView.goBack();       
            return true;       
        }       
        return super.onKeyDown(keyCode, event);       
    }
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_price_close:
			finish();
			break;

		default:
			break;
		}
		
	}

	public Object getHtmlObject() {
		Object insertObj = new Object() {
			@JavascriptInterface
			public void close() {
				finish();
			}
		};
		return insertObj;
	}
	
	
	
}
