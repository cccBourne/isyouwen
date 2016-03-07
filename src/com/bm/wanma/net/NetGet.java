package com.bm.wanma.net;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;

import net.tsz.afinal.http.AjaxCallBack;
import net.tsz.afinal.http.AjaxParams;
import android.os.Handler;
import android.util.Log;

import com.bm.wanma.entity.BaseBean;
import com.bm.wanma.model.net.FinalHttpFactory;
import com.bm.wanma.utils.Tools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * 网络访问
 * 
 */
public class NetGet extends NetBase {

	/**
	 * 异步get获取
	 */
	protected void getData(final Handler handler, final String url, final AjaxParams params, final Type type) {
		Log.i("info", "url=="+url);
		Log.i("info", "params="+params);
		FinalHttpFactory.getFinalHttp().get(url, params, new AjaxCallBack<Object>() {
			@Override
			public void onSuccess(Object t) {
				Gson gson = new Gson();
				String context = t.toString();
				if (context != null) {
					context = context.trim();
				}
				Log.i("info", "context=====" + context);
				BaseBean<?> bean = null;
				if (context != null && !Tools.judgeString(context, "")) {
					try {
						bean = gson.fromJson(context, type);
					} catch (Exception e) {
						Log.i("info", "Exception");
						e.printStackTrace();
					}
				}
				setHandler(handler, bean, url);
			}
		});

	}
}
