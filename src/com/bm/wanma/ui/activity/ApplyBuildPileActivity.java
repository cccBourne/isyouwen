package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageButton;

/**
 * @author cm
 * 申请充点卡
 */
public class ApplyBuildPileActivity extends Activity implements OnClickListener{
	private WebView webView; 
	private String url,userId;
	private ImageButton ib_back;
	
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_build_pile);
		userId = PreferencesUtil.getStringPreferences(this, "pkUserinfo");
		webView = (WebView) findViewById(R.id.activity_apply_build_pile_web);
		ib_back = (ImageButton) findViewById(R.id.activity_apply_build_pile_back);
		ib_back.setOnClickListener(this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setBuiltInZoomControls(true);
		//webView.setWebChromeClient(new WebChromeClient());//允许alert对话框弹出
		webView.setWebChromeClient(new WebChromeClient(){
			@Override
			public boolean onJsAlert(WebView view, String url, String message,
				final JsResult result) {
				AlertDialog.Builder b2 = new AlertDialog.Builder(ApplyBuildPileActivity.this).setTitle("").setMessage(message).
						setPositiveButton("ok",new AlertDialog.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								result.confirm();
							}
						}); 
				b2.setCancelable(false);
				b2.create(); 
				b2.show();  
				return true;
			}
		});
		url = Protocol.ApplyBuilder +"?userId=" + userId + "&aepOrigin=99";
		//url = Protocol.SERVER_ADDRESS_HTML + "/aichong/applyBuilder.html?userId=" + userId + "&aepOrigin=99";
		webView.loadUrl(url);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_apply_build_pile_back:
			finish();
			break;

		default:
			break;
		}
		
	}

}
