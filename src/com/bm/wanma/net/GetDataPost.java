package com.bm.wanma.net;

import java.lang.reflect.Type;
import java.util.ArrayList;

import net.tsz.afinal.http.AjaxParams;
import android.content.Context;
import android.os.Handler;
import com.bm.wanma.alipay.Base64;
import com.bm.wanma.entity.AnchorSummary;
import com.bm.wanma.entity.BanlanceBean;
import com.bm.wanma.entity.BaseBean;
import com.bm.wanma.entity.BespokeDetailBean;
import com.bm.wanma.entity.CancleBespokeBean;
import com.bm.wanma.entity.CarNameBean;
import com.bm.wanma.entity.CarRepairBean;
import com.bm.wanma.entity.CarTypeBean;
import com.bm.wanma.entity.EmergencyCallBean;
import com.bm.wanma.entity.MyChargeOrderBean;
import com.bm.wanma.entity.ElectricPileBean;
import com.bm.wanma.entity.EquipmentBean;
import com.bm.wanma.entity.ListModeBean;
import com.bm.wanma.entity.LoginBean;
import com.bm.wanma.entity.MapModeBean;
import com.bm.wanma.entity.MyBespokeOrderBean;
import com.bm.wanma.entity.MyCollectBean;
import com.bm.wanma.entity.MyDynamicListBean;
import com.bm.wanma.entity.MyNewsFeedbackBean;
import com.bm.wanma.entity.MyNewsSystemBean;
import com.bm.wanma.entity.PileCommentListBean;
import com.bm.wanma.entity.PowerStationBean;
import com.bm.wanma.entity.ScanInfoBean;
import com.bm.wanma.entity.StationCommentListBean;
import com.bm.wanma.entity.UserInfoBean;
import com.bm.wanma.entity.VersionInfoBean;
import com.bm.wanma.entity.WalletList;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;
import com.google.gson.reflect.TypeToken;

public class GetDataPost extends NetPost {

	public static GetDataPost instance;
	public static String replaceToken = "";
	/** 默认type */
	private Type defaulType = new TypeToken<BaseBean<?>>() {
	}.getType();
	/** 获取版本信息 */
	private Type VersionInfoType = new TypeToken<BaseBean<VersionInfoBean>>() {
	}.getType();
	/** 获取地图锚点信息 */
	private Type AnchorSummaryType = new TypeToken<BaseBean<AnchorSummary>>() {
	}.getType();
	/** 电桩type */
	private Type eleType = new TypeToken<BaseBean<ArrayList<ElectricPileBean>>>() {
	}.getType();
	/** 电站type */
	private Type staType = new TypeToken<BaseBean<ArrayList<PowerStationBean>>>() {
	}.getType();
	/** 电站评价列表 */
	private Type StationCommentListType = new TypeToken<BaseBean<ArrayList<StationCommentListBean>>>() {
	}.getType();
	/** 电桩评价列表 */
	private Type StiltCommentListType = new TypeToken<BaseBean<ArrayList<PileCommentListBean>>>() {
	}.getType();
	/** 我的预约 */
	private Type myBespokeType = new TypeToken<BaseBean<ArrayList<MyBespokeOrderBean>>>() {
	}.getType();
	/** 预约详情 */
	private Type bespokeDetailType = new TypeToken<BaseBean<BespokeDetailBean>>() {
	}.getType();
	/** 充电订单type */
	private Type chargeOrderType = new TypeToken<BaseBean<ArrayList<MyChargeOrderBean>>>() {
	}.getType();
	/** 用户信息 */
	private Type userType = new TypeToken<BaseBean<UserInfoBean>>() {
	}.getType();
	
	
	
	
	
    /**
     * @param mc
     * @return Instance
     * 获得实例
     */
	public static GetDataPost getInstance(Context mc) {
		String apiToken = PreferencesUtil.getStringPreferences(mc, "apiToken");
		Long timeStamp = System.currentTimeMillis();
		String toToken = apiToken + timeStamp;
		replaceToken = "";
		char[] chars = toToken.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			replaceToken += Tools.replace((byte) chars[i]);
		}
		replaceToken = Base64.encode(replaceToken.getBytes());
		if (instance == null) {
			instance = new GetDataPost();
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
	 * 获取版本信息
	 * @author cm
	 * @param handler        
	 */
	public void getAppVersion(Handler handler,String versNumber) {
		AjaxParams params = new AjaxParams();
		params.put("versNumber", versNumber);
		params.put("versType", "1");
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_APP_VERSION_INFO, params, VersionInfoType);
	}
	
	/**
	 * 电桩 电站 --地图查找
	 * @author cm 2015年4月15日
	 * @param handler
	 * @param powerInterface 接口方式（7国标，19美标，20欧标）   
	 * @param chargingMode 充电模式（5直流，14交流）
	 * @param freeStatus 空闲充电点（智能、联网、有空闲枪头的桩）1选中
	 * @param matchMyCar 适合爱车 1选中
	 * @param userId （必须与适合爱车一起传入）
	 * @param cityCode 城市编码（需与更新时间一起传入）
	 * @param reqTime 更新时间 2000-01-01 00:00:00（必须与城市编码一起传入）
	 */
	public void getElectricPileMapList(Handler handler, String powerInterface,String chargingMode,
			String freeStatus,String matchMyCar,String userId,String cityCode,String reqTime) {
		AjaxParams params = new AjaxParams();
		params.put("powerInterface", powerInterface);
		params.put("chargingMode", chargingMode);
		params.put("freeStatus", freeStatus);
		params.put("matchMyCar", matchMyCar);
		params.put("userId", userId);
		params.put("cityCode", cityCode);
		params.put("reqTime", reqTime);
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_STATION_ELECTRIC_MAP, params, new TypeToken<BaseBean<ArrayList<MapModeBean>>>() {
		}.getType());
	}
	
	/**
	 * 获取地图锚点 简介信息
	 * @author cm
	 * @param handler  
	 * @param lng  经度（人所在地的经纬)
	 * @param lat 
	 * @param eid 桩、站id
	 * @param type  1桩2站
	 */
	public void getAnchorSummary(Handler handler,String lng,String lat,String eid,String type) {
		AjaxParams params = new AjaxParams();
		params.put("lng", lng);
		params.put("lat", lat);
		params.put("eid", eid);
		params.put("type", type);
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_ANCHOR_SUMMARY, params, AnchorSummaryType);
	}
	/**
	 * 电站、电桩-列表查找
	 * @param cm
	 * @param handler
	 * @param powerInterface 接口方式（7国标，19美标，20欧标）
	 * @param chargingMode 充电模式（5直流，14交流）
	 * @param freeStatus 空闲充电点（智能、联网、有空闲枪头的桩）1选中
	 * @param matchMyCar 适合爱车 1选中
	 * @param userId  （必须与适合爱车一起传入）
	 * @param searchKey 搜索关键字
	 * @param Longitude 经度
	 * @param Latitude 维度
	 */
	public void getElectricPileList(Handler handler, String powerInterface, String chargingMode, String freeStatus, String matchMyCar,
			String userId, String Longitude,String Latitude,String searchKey) {
		AjaxParams params = new AjaxParams();
		params.put("powerInterface", powerInterface);
		params.put("chargingMode", chargingMode);
		params.put("freeStatus", freeStatus);
		params.put("matchMyCar", matchMyCar);
		params.put("userId", userId);
		params.put("Longitude", Longitude);
		params.put("Latitude", Latitude);
		params.put("searchKey", searchKey);
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_STATION_ELECTRIC_LIST, params, new TypeToken<BaseBean<ArrayList<ListModeBean>>>() {
		}.getType());
	}
	
	/**
	 * 登录
	 * cm
	 * @param usinPhone   登录手机号
	 * @param usInPassword 密码（md5加密
	 * @param jpushRegistrationid 极光推送手机唯一标示
	 * @param jpushDevicetype  设备类型（1安卓2ios）
	 * @param handler
	 */
	public void login(String usinPhone, String usInPassword,String jpushRegistrationid,String jpushDevicetype,String did, Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("usinPhone", usinPhone);
		ajaxParams.put("usInPassword", usInPassword);
		ajaxParams.put("jpushRegistrationid", jpushRegistrationid);
		ajaxParams.put("jpushDevicetype", jpushDevicetype);
		ajaxParams.put("did", did);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.TO_LOGIN, ajaxParams, new TypeToken<BaseBean<LoginBean>>() {
		}.getType());
	}
	/**
	 * 用户注销
	 * @param userId  用户ID  是
	 */
	public void logout(Handler handler, String userId) {
		AjaxParams params = new AjaxParams();
		params.put("userId", userId);
		params.put("t", replaceToken);
		getData(handler, Protocol.TO_LOGOUT, params, defaulType);
	}
	
	/**
	 * 检查手机号码是否已注册
	 * @author cm
	 * @param handler
	 * @param phone
	 */
	public void checkPhone(Handler handler, String phone) {
		AjaxParams params = new AjaxParams();
		params.put("phone", phone);
		params.put("t", replaceToken);
		getData(handler, Protocol.CHECK_PHONE, params, defaulType);
	}

	/**
	 * 检查验证码是否正确
	 * @author cm
	 * @param handler
	 * @param usinPhone
	 * @param authCode
	 */
	public void checkCode(Handler handler, String usinPhone, String authCode) {
		AjaxParams params = new AjaxParams();
		params.put("usinPhone", usinPhone);
		params.put("authCode", authCode);
		params.put("t", replaceToken);
		getData(handler, Protocol.CHECK_CODE, params, defaulType);
	}
	/**
	 * 找回密码  cm
	 * @param usinPhone
	 * @param usInPassword
	 * @param authCode
	 * @param handler
	 */

	public void resetPwd(String userPhone, String pwd, String smsCode, Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("usinPhone", userPhone);
		ajaxParams.put("usinPassword", pwd);
		ajaxParams.put("authCode", smsCode);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.RESET_PWD, ajaxParams, new TypeToken<BaseBean<?>>() {
		}.getType());

	}
	
	/**
	 *  修改密码 cm
	 * @param uId 用户id
	 * @param opw 原密码（md5）
	 * @param npw 新密码（md5加密）
	 * @param handler
	 */

	public void modifyPwd(String uId, String opw, String npw, Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uId", uId);
		ajaxParams.put("opw", opw);
		ajaxParams.put("npw", npw);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.MODIFY_PWD, ajaxParams, new TypeToken<BaseBean<?>>() {
		}.getType());

	}
	/**
	 * 检查支付密码 cm
	 * @param uid 用户id
	 * @param pwd （md5加密）
	 * @param handler
	 * 输错3次，当天锁定
	 */

	public void checkPayPwd(String uid, String pwd,Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", uid);
		ajaxParams.put("pwd", pwd);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.CHECK_PAY_PWD, ajaxParams, new TypeToken<BaseBean<?>>() {
		}.getType());

	}
	/**
	 *  修改支付密码 cm
	 * @param uid 用户id
	 * @param oppw 原密码（md5）
	 * @param nppw 新密码（md5加密）
	 * @param handler
	 */

	public void modifyPayPwd(String uid, String oppw, String nppw, Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", uid);
		ajaxParams.put("oppw", oppw);
		ajaxParams.put("nppw", nppw);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.MODIFY_PAY_PWD, ajaxParams, new TypeToken<BaseBean<?>>() {
		}.getType());

	}
	/**
	 *  设置支付密码 cm
	 * @param uid 用户id
	 * @param pwd 密码（md5）
	 * @param handler
	 */

	public void setPayPwd(String uid, String pwd,Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("uid", uid);
		ajaxParams.put("pwd", pwd);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.SET_PAY_PWD, ajaxParams, new TypeToken<BaseBean<?>>() {
		}.getType());

	}
	/**
	 * 账号注册--只有手机号码 和 密码
	 * @author cm
	 * @param handler
	 * @param usinPhone
	 * @param usinPassword 密码（md5加密）
	 * @param platform 3android 4ios(固定)	
	 */
	public void register(Handler handler, String usinPhone, String usinPassword) {
		AjaxParams params = new AjaxParams();
		params.put("usinPhone", usinPhone);
		params.put("usinPassword", usinPassword);
		params.put("platform", "3");
		params.put("t", replaceToken);
		getData(handler, Protocol.TO_REGIST, params, defaulType);
	}
	/**
	 * 获取电站详情
	 * @author cm 
	 * @param handler
	 * @param powerStationId   电站id
	 * @param  pkUserinfo	用户id
	 * @param longitude 经度
	 * @param latitude 维度
	 */
	public void getStationDetail(Handler handler, String powerStationId,String pkUserinfo,String longitude,String latitude) {
		AjaxParams params = new AjaxParams();
		params.put("powerStationId", powerStationId);
		params.put("pkUserinfo", pkUserinfo);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("t", replaceToken);
		getData(handler, Protocol.POWER_STATION_DETAIL, params, staType);
	}

	/**
	 * 获取电桩详情
	 * cm
	 * @param handler
	 * @param electricPileId
	 * @param pkUserinfo
	 * @param longitude
	 * @param latitude
	 */
	public void getPileDetail(Handler handler, String electricPileId,String pkUserinfo,String longitude,String latitude) {
		AjaxParams params = new AjaxParams();
		params.put("electricPileId", electricPileId);
		params.put("pkUserinfo", pkUserinfo);
		params.put("longitude", longitude);
		params.put("latitude", latitude);
		params.put("t", replaceToken);
		getData(handler, Protocol.POWER_Pile_DETAIL, params, eleType);
	}
	/**
	 * 收藏电站，电桩
	 * @author cm
	 * @param handler
	 * @param userId 用户id
	 * @param favoriteType 类型（1电桩，2电站）
	 * @param favoriteTypeId 电桩ID/电桩ID
	 */
	public void collectStationPile(Handler handler, String userId, String favoriteType, String favoriteTypeId) {
		AjaxParams params = new AjaxParams();
		params.put("favoriteType", favoriteType);
		params.put("userId", userId);
		params.put("favoriteTypeId", favoriteTypeId);
		params.put("t", replaceToken);
		getData(handler, Protocol.COLLECT_STATION_PILE, params, defaulType);
	}
	/**
	 * 删除我的收藏
	 * 
	 * @author cm
	 * @param handler
	 * @param favoriteType 收藏类型  3-商城收藏  1-电桩 2-电站
	 * @param userCollectId
	 */
	public void removeMyCollect(Handler handler, String userCollectId,String favoriteType) {
		AjaxParams params = new AjaxParams();
		params.put("userCollectId", userCollectId);
		params.put("favoriteType", favoriteType);
		params.put("t", replaceToken);
		getData(handler, Protocol.REMOVE_COLLECTED, params, defaulType);
	}
	/**
	 * 获取电站评价列表   
	 * @author cm 
	 * @param handler
	 * @param pageNum 每页多少条
	 * @param pageNumber 当前第几页
	 * @param prCoProductId 电站ID
	 */
	public void getStationCommentList(Handler handler,String prCoProductId ,String pageNum,String pageNumber) {
		AjaxParams params = new AjaxParams();
		params.put("prCoProductId", prCoProductId);
		params.put("pageNum", pageNum);
		params.put("pageNumber", pageNumber);
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_STATION_COMMENT, params,StationCommentListType);
	}
	/**
	 * 获取电桩评论列表  
	 * @author cm 
	 * @param handler
	 * @param pageNum 每页多少条
	 * @param pageNumber 当前第几页
	 * @param prCoProductId 电站ID
	 * @param type 1评价2留言
	 */
	public void getPileCommentList(Handler handler,String prCoProductId ,String pageNum,String pageNumber) {
		AjaxParams params = new AjaxParams();
		params.put("prCoProductId", prCoProductId);
		params.put("pageNum", pageNum);
		params.put("pageNumber", pageNumber);
		params.put("type", "1");
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_PILE_COMMENT, params,StiltCommentListType);
	}
	/**
	 * 获得设备保修类型
	 * cm
	 * @param handler
	 * @param cpId 配置类型 （固定值29）
	 */
	public void getEquipment(Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("cpId", "29");
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_EQUIPMENT_TYPE, ajaxParams, new TypeToken<BaseBean<ArrayList<EquipmentBean>>>() {
		}.getType());
	}

	/**
	 * 提交设备保修
	 * cm
	 * @param handler
	 * @param eqreUserid 用户id
	 * @param eqreWarrantytypeid 设备报修类型（保修项id）
	 * @param epId 电桩、电站id
	 * @param deviceType 设备类型 1电桩2电站
	 * @param eqreContent 报修内容   (非必须)
	 */
	public void commitEquipment(Handler handler, String eqreUserid, String eqreWarrantytypeid, String epId,String deviceType,String eqreContent) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("eqreUserid",eqreUserid );
		ajaxParams.put("eqreWarrantytypeid", eqreWarrantytypeid);
		ajaxParams.put("epId", epId);
		ajaxParams.put("deviceType", deviceType);
		ajaxParams.put("eqreContent", eqreContent);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.COMMIT_EQUIPMENT, ajaxParams,defaulType);
	}
	/**
	 * 获取我的预约列表 
	 * @author cm 
	 * @param handler
	 * @param  bespUserInfo 用户id
	 */
	public void getMyBespokeList(Handler handler,String bespUserInfo) {
		AjaxParams params = new AjaxParams();
		params.put("bespUserInfo", bespUserInfo);
		params.put("t", replaceToken);
		getData(handler, Protocol.MYBESPOKE_LIST, params,myBespokeType);
	}
	/**
	 * 获取我的充电订单列表 /详情（只列10条）
	 * @author cm 
	 * @param handler
	 * @param  userId 用户id
	 * @param coId 订单id（不传查列表，传查详情） 	
	 */
	public void getMyChargeOrderList(Handler handler,String userId,String coId) {
		AjaxParams params = new AjaxParams();
		params.put("userId", userId);
		params.put("coId", coId);
		params.put("t", replaceToken);
		getData(handler, Protocol.MYCHARGE_ORDERLIST, params,chargeOrderType);
	}

	/**
	 * 提交预约、续约请求 
	 * @author cm 
	 * @param handler
	 * @param  bespUserinfo 用户id
	 * @param  pkBespoke 预约id（为空时是预约，不为空为续约
	 * @param  bespElectricpileid 电桩ID
	 * @param  bespBespoketime 预约时间（总共预约了多长时间，单位分钟，如31
	 * @param  bespBespokeremark 预约描述
	 * @param  bespBespoketimes 预约时间段（14:40:13至17:10:13
	 * @param  bespElectricpilehead 预约枪口id
	 * @param  bespResepaymentcode 预约订单编号（用户账户+时间戳（精确到秒）
	 * @param  bespFrozenamt 预约冻结资金（保留小数点后2位
	 * @param  bespBespokeprice 预约单价（保留小数点后2位）
	 * @param  bespBeginTime 预约开始时间(yyyy-MM-dd HH:mm:ss)
	 * @param  bespEndTime 预约结束时间(yyyy-MM-dd HH:mm:ss)
	 */
	public void commitBespoke(Handler handler,String bespUserinfo,String pkBespoke,String bespElectricpileid ,String bespBespoketime,
			String bespBespoketimes,String bespElectricpilehead,
			String bespFrozenamt,String bespBespokeprice,String bespBeginTime,String bespEndTime,String did) {
		AjaxParams params = new AjaxParams();
		params.put("bespUserinfo", bespUserinfo);
		params.put("pkBespoke", pkBespoke);
		params.put("bespElectricpileid", bespElectricpileid);
		params.put("bespBespoketime", bespBespoketime);
		//params.put("bespBespokeremark", bespBespokeremark);
		params.put("bespBespoketimes", bespBespoketimes);
		params.put("bespElectricpilehead", bespElectricpilehead);
		//params.put("bespResepaymentcode", bespResepaymentcode);
		params.put("bespFrozenamt", bespFrozenamt);
		params.put("bespBespokeprice", bespBespokeprice);
		params.put("bespBeginTime", bespBeginTime);
		params.put("bespEndTime", bespEndTime);
		params.put("t", replaceToken);
		params.put("did", did);
		getData(handler, Protocol.COMMIT_BESPOKE, params,defaulType);
		
	}
	/**
	 * 取消预约
	 * @author cm
	 * @param handler
	 * @param uId 用户id  是
	 * @param pkBespoke 预约ID  是
	 * @param bespBeginTime  预约开始时间  否
	 * @param bespElectricpilehead  枪头ID  是
	 */
	public void cancelBespoke(Handler handler, String uId, String pkBespoke, String bespElectricpilehead,String did) {
		AjaxParams params = new AjaxParams();
		params.put("uId", uId);
		params.put("pkBespoke", pkBespoke);
		//params.put("bespBeginTime", bespBeginTime);
		params.put("bespElectricpilehead", bespElectricpilehead);
		params.put("t", replaceToken);
		params.put("did", did);
		getData(handler, Protocol.CANCLE_BESPOKE, params,new TypeToken<BaseBean<CancleBespokeBean>>() {
		}.getType());
	}
	/**
	 * 获取预约详情
	 * @author cm
	 * @param handler
	 * @param pkBespoke
	 */
	public void getBespokeDetail(Handler handler, String pkBespoke) {
		AjaxParams params = new AjaxParams();
		params.put("pkBespoke", pkBespoke);
		params.put("t", replaceToken);
		getData(handler, Protocol.MYBESPOKE_DETAIL, params, bespokeDetailType);
	}
	
	/**
	 * 获取用户信息
	 * cm
	 * @param handler
	 * @param userId
	 */
	public void getUserInfo(Handler handler, String userId ) {
		AjaxParams params = new AjaxParams();
		params.put("userId", userId);
		params.put("t", replaceToken);
		getData(handler, Protocol.GET_USER_INFO, params, userType);
	}
	/**
	 * 我的钱包
	 * 筛选条件
	 * cm
	 * @param handler
	 * @param userId
	 * @param starttime  endtime（ 2015-04-09 00:00:00）两个时间都有或都无
	 * @param type 消费类型(1-充电消费 2-预约消费 3-购物消费 4-充值)
	 */
	public void getMyWalletAll(Handler handler, String userId,String startTime,String endTime,String type) {
		AjaxParams params = new AjaxParams();
		params.put("userId", userId);
		params.put("starttime", startTime);
		params.put("endtime", endTime);
		params.put("type", type);
		params.put("t", replaceToken);
		getData(handler, Protocol.MY_WALLET, params, new TypeToken<BaseBean<WalletList>>() {
		}.getType());
	}
	/**
	 * 降地锁 
	 * cm
	 * @param handler
	 * @param epCode 电桩编号
	 * @param headNum 枪口号，不是枪口id
	 * @param parkNum 枪口对应的车位号
	 * @param uid 用户id
	 * @param lat 维度（手机经纬度）
	 * @param lng 经度
	 * @param eplat 维度（桩的经纬度）
	 * @param eplng 经度
	 */
	public void downParkLock(Handler handler, String epCode,String headNum,
			String parkNum,String uid,String lat,String lng,String eplat,String eplng){
		AjaxParams params = new AjaxParams();
		params.put("epCode", epCode);
		params.put("headNum", headNum);
		params.put("parkNum", parkNum);
		params.put("uid", uid);
		params.put("lat", lat);
		params.put("lng", lng);
		params.put("eplat", eplat);
		params.put("eplng", eplng);
		params.put("t", replaceToken);
		getData(handler, Protocol.DOWN_PARKLOCK, params, defaulType);
	}
	/**
	 * LED开关
	 * cm
	 * @param handler
	 * @param epCode 电桩编号
	 * @param type 1开，2关
	 * @param remainTime 持续闪烁时间，分钟为单位  否
	 * @param uid 用户id
	 * @param lat 维度（手机经纬度）
	 * @param lng 经度
	 * @param eplat 维度（桩的经纬度）
	 * @param eplng 经度
	 */
	public void ledSwitch(Handler handler, String epCode,String type,
			String remainTime,String uid,String lat,String lng,String eplat,String eplng) {
		AjaxParams params = new AjaxParams();
		params.put("epCode", epCode);
		params.put("type", type);
		params.put("remainTime", remainTime);
		params.put("uid", uid);
		params.put("lat", lat);
		params.put("lng", lng);
		params.put("eplat", eplat);
		params.put("eplng", eplng);
		params.put("t", replaceToken);
		getData(handler, Protocol.LED, params, defaulType);
	}

	/**
	 * 获得车型
	 * cm
	 * @param handler
	 * @param carcompanyId 品牌厂家ID
	 */
	public void findCar(Handler handler, String carcompanyId) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("carcompanyId", carcompanyId);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.FIND_CAR_INFO, ajaxParams, new TypeToken<BaseBean<ArrayList<CarNameBean>>>() {
		}.getType());

	}

	/**
	 * 获得品牌
	 * @param paraType
	 * @param handler
	 * cm
	 */
	public void getCarType(Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("paraType", "1");
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.FIND_PARACONFIG_LIST, ajaxParams, new TypeToken<BaseBean<ArrayList<CarTypeBean>>>() {
		}.getType());

	}
	
	/**
	 * 我的收藏
	 * @param userId
	 * @param handler
	 * @param lat lng
	 * cm
	 */
	public void getMyCollectList(Handler handler,String userId,String lat,String lng) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("userId", userId);
		ajaxParams.put("lat", lat);
		ajaxParams.put("lng", lng);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_MYCOLLECT_LIST, ajaxParams, new TypeToken<BaseBean<ArrayList<MyCollectBean>>>() {
		}.getType());

	}
	
	/**
	 * 系统消息
	 * @param userId 用户id
	 * @param handler
	 * cm
	 */
	public void getMyNewsSystemList(Handler handler,String userId) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("userId", userId);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_MYNEWS_SYSTEM_LIST, ajaxParams, new TypeToken<BaseBean<ArrayList<MyNewsSystemBean>>>() {
		}.getType());

	}
	/**
	 * 系统消息详情
	 * @param mid 消息id
	 * @param handler
	 * cm
	 */
	public void getMyNewsSystemDetail(Handler handler,String mid) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("mid", mid);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_MYNEWS_SYSTEM_DETAIL, ajaxParams, new TypeToken<BaseBean<ArrayList<MyNewsSystemBean>>>() {
		}.getType());

	}	
	/**
	 * 提交我的反馈
	 * @param febaUserid 用户id 
	 * @param handler
	 * @param febaContent 反馈内容	
	 * cm
	 */
	public void commitMyFeedback(Handler handler,String febaUserid,String febaContent) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("febaUserid", febaUserid);
		ajaxParams.put("febaContent", febaContent);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.COMMIT_MYFEEDBACK, ajaxParams,defaulType);

	}	
	/**
	 * 获取我的反馈
	 * @param userId 
	 * @param handler
	 * cm
	 */
	public void getMyNewsFeedback(Handler handler,String userId) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("userId", userId);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_MYNEWS_FEEDBACK_LIST, ajaxParams, new TypeToken<BaseBean<ArrayList<MyNewsFeedbackBean>>>() {
		}.getType());

	}	
	/**
	 * 活动列表页--动态
	 * @param pageNumber 当前页码从1开始
	 * @param handler
	 * @param pageNum 每页数据量
	 * cm
	 */
	public void getMyDynamicList(Handler handler,String pageNumber,String pageNum) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("pageNumber", pageNumber);
		ajaxParams.put("pageNum", pageNum);
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_MYDYNAMIC_LIST, ajaxParams, new TypeToken<BaseBean<ArrayList<MyDynamicListBean>>>() {
		}.getType());

	}	
	/**
	 *急救电话
	 * @param handler
	 * cm
	 */
	public void getEmergencyCall(Handler handler) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("t", replaceToken);
		getData(handler, Protocol.GET_EMERGENCY_CALL, ajaxParams, new TypeToken<BaseBean<ArrayList<EmergencyCallBean>>>() {
		}.getType());

	}	
	/**
	 *车俩维修
	 * @param handler
	 * @param latitude
	 * @param longitude
	 * @param pageNumber 当前页码从1开始（默认列表的话为必须项）
	 * @param pageNum 每页数据量（默认列表的话为必须项）
	 * @param kw 查询关键字（搜索的话为必须项）
	 * @param type 查询类型 1名称 2地址（搜索的话为必须项）
	 * cm
	 */
	public void getCarRepair(Handler handler,String latitude,String longitude,String pageNumber,String pageNum,String kw,String type) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("t", replaceToken);
		ajaxParams.put("latitude", latitude);
		ajaxParams.put("longitude", longitude);
		ajaxParams.put("pageNumber", pageNumber);
		ajaxParams.put("pageNum", pageNum);
		ajaxParams.put("kw", kw);
		ajaxParams.put("type", type);
		getData(handler, Protocol.GET_CAR_REPAIR, ajaxParams, new TypeToken<BaseBean<ArrayList<CarRepairBean>>>() {
		}.getType());

	}	
	/**
	 * 获取二维码信息
	 * @param handler
	 * @param elpiElectricpilecode 桩体编号，不是桩体id
	 * @param ePHeElectricpileHeadId 枪头编号，不是枪头id
	 * cm
	 */
	public void getScanInfo(Handler handler,String elpiElectricpilecode,String ePHeElectricpileHeadId) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("t", replaceToken);
		ajaxParams.put("elpiElectricpilecode", elpiElectricpilecode);
		ajaxParams.put("ePHeElectricpileHeadId", ePHeElectricpileHeadId);
		getData(handler, Protocol.GET_SCAN_INFO, ajaxParams, new TypeToken<BaseBean<ScanInfoBean>>() {
		}.getType());

	}	
	/**
	 * 获取账号余额
	 * @param handler
	 * @param uid 用户id
	 * cm
	 */
	public void getBalance(Handler handler,String uid) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("t", replaceToken);
		ajaxParams.put("uid", uid);
		getData(handler, Protocol.BANLANCE, ajaxParams, new TypeToken<BaseBean<BanlanceBean>>() {
		}.getType());

	}	
	/**
	 * 电桩评价添加
	 * @param handler
	 * @param epId 电桩ID
	 * @param uId 用户id
	 * @param pcId 评论id（对桩的评论为0，对评论的回复为被回复评论id）
	 * @param uName 用户名称
	 * @param epContent 评论内容
	 * @param type 1评价2留言
	 * 
	 * cm
	 */
	public void commitPileComment(Handler handler,String epId,String uId,String pcId,String uName,String epContent) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("t", replaceToken);
		ajaxParams.put("epId", epId);
		ajaxParams.put("uId", uId);
		ajaxParams.put("pcId", pcId);
		ajaxParams.put("uName", uName);
		ajaxParams.put("epContent", epContent);
		ajaxParams.put("type", "1");
		getData(handler, Protocol.COMMIT_PILE_COMMENT, ajaxParams,defaulType);

	}	
	
	/**
	 * 电桩评分添加
	 * @param handler
	 * @param epId 电桩ID
	 * @param uId 用户id
	 * @param pcId 评论id（对桩的评论为0，对评论的回复为被回复评论id）
	 * @param uName 用户名称
	 * @param epStar 评论内容
	 * cm
	 */
	public void commitPileStar(Handler handler,String epId,String uId,String uName,String epStar) {
		AjaxParams ajaxParams = new AjaxParams();
		ajaxParams.put("t", replaceToken);
		ajaxParams.put("epId", epId);
		ajaxParams.put("uId", uId);
		ajaxParams.put("uName", uName);
		ajaxParams.put("epStar", epStar);
		getData(handler, Protocol.COMMIT_PILE_STAR, ajaxParams,defaulType);

	}	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * 获取alipay支付信息
	 * @author cm
	 * @param handler        
	 * @param subject 标题     是
	 * @param body  内容（使用用户id）   是
	 * @param price 订单价格   是
	 * @param userMobel  用户手机号  是
	 */
	public void getAlipayInfo(Handler handler, String subject, String body, String price, String userMobel) {
		AjaxParams params = new AjaxParams();
		params.put("subject", subject);
		params.put("body", body);
		params.put("price", price);
		params.put("userMobel", userMobel);
		params.put("t", replaceToken);
		getData(handler, Protocol.AliPayURL, params, defaulType);
	}
	/**
	 * 获取WX支付信息
	 * @author cm
	 * @param handler  
	 * @param userId  用户id  
	 * @param ipAddr 手机ip   是
	 * @param body  内容   是
	 * @param price 订单价格   是
	 * @param userMobel  用户手机号  是
	 * @param tradeType  请求类型（APP或WAP)
	 */
	public void getWXPrepayInfo(Handler handler, String userId,String ipAddr, String body, String price, String userMobel,String tradeType) {
		AjaxParams params = new AjaxParams();
		params.put("userId", userId);
		params.put("ipAddr", ipAddr);
		params.put("body", body);
		params.put("price", price);
		params.put("userMobel", userMobel);
		params.put("tradeType", tradeType);
		params.put("t", replaceToken);
		getData(handler, Protocol.WeiXinPayURL, params, defaulType);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
