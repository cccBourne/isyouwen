package com.bm.wanma.net;

import java.io.Serializable;

import com.bm.wanma.entity.BaseBean;
import com.bm.wanma.ui.activity.BaseActivity;
import com.bm.wanma.utils.LogUtil;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 封装handler
 */
public class NetBase {

	protected void setHandler(Handler handler, BaseBean<?> bean, String sign) {
		String net_msg = "";
		Message msg = new Message();
		msg.what = 0;
		msg.obj = sign;
		Bundle bundle = new Bundle();
		if (bean != null) {
			LogUtil.i("cm_netPost_status", "status==" +bean.getStatus());
			net_msg = bean.getMsg();
			if (bean.getStatus().equals("100")) {
				msg.what = 1;
				if (bean.getData() != null ) {
					bundle.putSerializable(Protocol.DATA, (Serializable) bean.getData());
				}
			}
			handler.sendEmptyMessage(BaseActivity.HIDE_PD);
			bundle.putString(Protocol.MSG, net_msg);
			bundle.putString(Protocol.CODE, bean.getStatus());
			
			msg.setData(bundle);
			handler.sendMessage(msg);
		}else {
			//net_msg = "服务器无响应";
			net_msg = "";
			handler.sendEmptyMessage(BaseActivity.HIDE_PD);
			bundle.putString(Protocol.MSG, net_msg);
			msg.setData(bundle);
			handler.sendMessage(msg);
		}
		
	}
	//请求异常处理
	protected void setErrorHandler(Handler handler) {
		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = "";
		Bundle bundle = new Bundle();
			bundle.putString(Protocol.MSG, "连接异常，请稍后再试!");
		handler.sendEmptyMessage(BaseActivity.HIDE_PD);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}
	
	
	
	protected void setHandler(String context, Handler handler, String sign) {
		Message msg = Message.obtain();
		msg.what = 0;
		msg.obj = sign;
		Bundle bundle = new Bundle();
		if (context != null && context.length() > 0) {
			msg.what = 1;
			bundle.putString(Protocol.DATA, context);
		}
		handler.sendEmptyMessage(BaseActivity.HIDE_PD);
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

}
