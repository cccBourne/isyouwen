package com.bm.wanma.wxapi;

import com.bm.wanma.R;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.ToastUtil;
import com.bm.wanma.weixin.Constants;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler{
	
	private static final String TAG = "cm_WXPayEntryActivity";
	
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.weixin_pay_result);
      //  LogUtil.i("cm_callback", "oncreate");
    	api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		//LogUtil.d(TAG, "onPayFinish, errCode = " + resp.errCode);
		//ToastUtil.showToast("onPayFinish"+ resp.errCode);

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.wx_tip);
			builder.setMessage(getString(R.string.pay_result_callback_msg, resp.errStr +";code=" + String.valueOf(resp.errCode)));
			builder.show();
		}
		 switch (resp.errCode) {  
		    case BaseResp.ErrCode.ERR_OK:  
		    	//ToastUtil.TshowToast("支付成功");
		    	//支付成功后发送广播，告知刷新数据
		    	Intent broadIn = new Intent("com.bm.wanma.recharge_wx_ok");
				sendBroadcast(broadIn);
		    	finish();
		        break;  
		    case BaseResp.ErrCode.ERR_USER_CANCEL:  
		    	ToastUtil.TshowToast("支付取消");
		    	finish();
		        break;  
		    case BaseResp.ErrCode.ERR_SENT_FAILED:  
		    	ToastUtil.TshowToast("支付失败");
		    	finish();
		        break; 
		    case BaseResp.ErrCode.ERR_COMM:  
		    	ToastUtil.TshowToast("支付失败");
		    	finish();
		        break; 
		    case BaseResp.ErrCode.ERR_AUTH_DENIED: 
		    	//ToastUtil.showToast("errcode_deny");
		    	finish();
		        break;  
		 }
		
		
		
		
	}
}