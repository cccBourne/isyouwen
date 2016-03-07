package com.bm.wanma.net;

import java.lang.reflect.Type;

import net.tsz.afinal.http.AjaxParams;
import android.os.Handler;

import com.bm.wanma.entity.BaseBean;
import com.google.gson.reflect.TypeToken;

public class GetDataGet extends NetGet {

	public static GetDataGet instance;

	public Type defaulType = new TypeToken<BaseBean<?>>() {
	}.getType();

	public static GetDataGet getInstance() {
		if (instance == null) {
			instance = new GetDataGet();
		}
		return instance;
	}
	/**
	 * 获取api token
	 * @author cm
	 * @param handler        
	 */
	public void getApiToken(Handler handler) {
		AjaxParams params = new AjaxParams();
		getData(handler, Protocol.GET_API_TOKEN, params, defaulType);
	}
	
	/**
	 * 获得短信验证码
	 * 
	 * @param handler
	 * @param mobileNumber
	 */
	public void getCode(Handler handler, String mobileNumber) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("mobileNumber", mobileNumber);
		getData(handler, Protocol.GET_AUTH_CODE, ajaxParams, defaulType);
	}

	

}
