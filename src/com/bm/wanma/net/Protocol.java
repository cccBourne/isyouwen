package com.bm.wanma.net;

/**
 *  接口地址
 */
public class Protocol {
            
	public static final String MSG = "msg";
	public static final String DATA = "data";
	public static final String CODE = "code";
	public static final String DATABASE_NAME = "eichong.db";
	public static final int dbNumer = 2;
	
	/** 开发测试地址 */          
	//public static final String SERVER_ADDRESS = "http://10.9.3.116:8080/api";//胡飞地址107
	//public static final String SERVER_ADDRESS = "http://10.9.2.109:80/api";//109内网地址
	public static final String SERVER_ADDRESS = "http://115.236.3.66:75/api";//109外网地址
	public static final String SERVER_ADDRESS_HTML = "http://115.236.3.66:75/html";//html外网地址
	public static final String HOST = "115.236.3.66"; 
	public static final int PORT = 8008; 
	
	/** IES开发测试地址 */
	/*public static final String SERVER_ADDRESS = "http://115.236.3.66:76/api";
	public static final String SERVER_ADDRESS_HTML = "http://115.236.3.66:76/html";
	public static final String HOST = "115.236.3.66"; 
	public static final int PORT = 8100;*/
	
	/** 预发布地址 */         
	/*public static final String SERVER_ADDRESS = "http://10.9.2.102/api";
	public static final String SERVER_ADDRESS_HTML = "http://10.9.2.102/html";
	public static final String HOST = "10.9.2.102"; 
	public static final int PORT = 8001; */

	/** 青云演示环境 */ 
	 /*public static final String SERVER_ADDRESS = "http://119.254.209.107/api";
	 public static final String SERVER_ADDRESS_HTML = "http://119.254.209.107/html";//html
	 public static final String HOST = "124.42.117.53";
	 public static final int PORT = 8001;*/
	
	 /**正式环境--app上线 */ 
	/* public static final String SERVER_ADDRESS = "http://m.api.eichong.com/api";
	 public static final String SERVER_ADDRESS_HTML = "http://html.eichong.com/html";
	 public static final String HOST = "119.254.100.83";
	 public static final int PORT = 8001;*/
	    
	  
	/** 获取短信验证码 */
	public static final String GET_AUTH_CODE = SERVER_ADDRESS + "/app/common/getAuthCode.do";
	/** 电站、电桩查找-列表 */
	public static final String GET_STATION_ELECTRIC_LIST = SERVER_ADDRESS + "/app/electricPileList/getElectricPileListN.do";
	/** 电站、电桩查找-地图 */   
	public static final String GET_STATION_ELECTRIC_MAP = SERVER_ADDRESS + "/app/electricPileMap/getElectricPileMapList.do";
	/** 电站、电桩--锚点 信息 */
	public static final String GET_ANCHOR_SUMMARY = SERVER_ADDRESS + "/app/electricPileMap/getAnchorSummary.do";
	/** 检查手机是否已注册 */
	public static final String CHECK_PHONE = SERVER_ADDRESS + "/app/user/checkphone.do"; 
	/** 登录 */
	public static final String TO_LOGIN = SERVER_ADDRESS + "/app/user/login.do"; 
	/** 判断验证码是否正确 */
	public static final String CHECK_CODE = SERVER_ADDRESS + "/app/user/checkAuthCode.do";
	/** 注册 */
	public static final String TO_REGIST = SERVER_ADDRESS + "/app/user/regist.do";
	/** 找回密码 */
	public static final String RESET_PWD = SERVER_ADDRESS + "/app/user/resetPasswrod.do";
	/** 修改密码 */
	public static final String MODIFY_PWD = SERVER_ADDRESS + "/app/user/modPassword.do";
	/** 检查支付密码 */
	public static final String CHECK_PAY_PWD = SERVER_ADDRESS + "/app/user/checkPayPwd.do";
	/** 设置支付密码 */
	public static final String SET_PAY_PWD = SERVER_ADDRESS + "/app/user/setPayPwd.do";
	/** 修改支付密码 */
	public static final String MODIFY_PAY_PWD = SERVER_ADDRESS + "/app/user/modPayPwd.do";
	/** 获取api通用token */
	public static final String GET_API_TOKEN = SERVER_ADDRESS + "/app/common/getToken.do";
	/** 获取版本信息（版本更新） */
	public static final String GET_APP_VERSION_INFO = SERVER_ADDRESS + "/app/other/getVersionInfo.do";
	/** 注销退出账号 */
	public static final String TO_LOGOUT = SERVER_ADDRESS + "/app/user/logout.do";
	/** 电桩详情 */
	public static final String POWER_Pile_DETAIL = SERVER_ADDRESS + "/app/electricPileDetail/getElectricPileDetail.do";
	/** 电桩评价添加*/
	public static final String COMMIT_PILE_COMMENT = SERVER_ADDRESS + "/app/epComment/insertEpCommnet.do";   
	/** 电桩评分添加 */
	public static final String COMMIT_PILE_STAR = SERVER_ADDRESS+ "/app/epStar/insertEpStar.do";
	/** 电站详情 */
	public static final String POWER_STATION_DETAIL = SERVER_ADDRESS + "/app/powerStationDetail/getPowerStationDetail.do";
	/** 收藏电站，电桩 */
	public static final String COLLECT_STATION_PILE = SERVER_ADDRESS + "/app/usercollect/userFavorites.do";
	/** 删除我的收藏 */
	public static final String REMOVE_COLLECTED = SERVER_ADDRESS + "/app/favorite/removeFavorite.do";
	/**电站评价列表（对所属桩评论的汇总） */
	public static final String GET_STATION_COMMENT = SERVER_ADDRESS + "/app/psComment/findPsComments.do";
	/**获取电桩评价列表 */
	public static final String GET_PILE_COMMENT = SERVER_ADDRESS + "/app/epComment/findEpComments.do";
	/** 获取设备保修类型 */
	public static final String GET_EQUIPMENT_TYPE = SERVER_ADDRESS + "/app/paraconfig/findConfigContentList.do";
	/** 添加设备报修 */
	public static final String COMMIT_EQUIPMENT = SERVER_ADDRESS + "/app/other/addTblEquipmentrepair.do";
	/** 我的预约列表 */
	public static final String MYBESPOKE_LIST = SERVER_ADDRESS + "/app/bespoke/selectBespokes.do";
	/** 我的预约详情 */
	public static final String MYBESPOKE_DETAIL = SERVER_ADDRESS + "/app/bespoke/selectBespokeById.do";
	/** 提交预约、续约 */
	public static final String COMMIT_BESPOKE = SERVER_ADDRESS + "/app/bespoke/insertBespoke.do";
	/** 取消预约 */
	public static final String CANCLE_BESPOKE = SERVER_ADDRESS + "/app/bespoke/updateBespStatus.do";
	/** 充电订单列表 */
	public static final String MYCHARGE_ORDERLIST = SERVER_ADDRESS + "/app/chargeShow/chargeOrderList.do";
	/** 获取用户信息 */
	public static final String GET_USER_INFO = SERVER_ADDRESS + "/app/user/getUserInfo.do";
	/** 修改用户信息 */
	public static final String MODIFY_USER_INFO = SERVER_ADDRESS + "/app/user/modifyUser.do";
	/** 我的钱包 */
	public static final String MY_WALLET = SERVER_ADDRESS + "/app/user/getMyWallet.do";
	/** 降地锁 */
	public static final String DOWN_PARKLOCK = SERVER_ADDRESS + "/app/net/downParkLock.do";
	/** LED开关 */
	public static final String LED = SERVER_ADDRESS + "/app/net/ledControl.do";
	/** 账号余额 */
	public static final String BANLANCE = SERVER_ADDRESS + "/app/user/getUserAB.do";
	/** aliPay */ 
	public static final String AliPayURL = SERVER_ADDRESS+"/app/pay/aliSign.do";
	/** weixin */ 
	public static final String WeiXinPayURL = SERVER_ADDRESS+"/app/pay/wxTempOrder.do";
	/***品牌*/
	public static final String FIND_CAR_INFO = SERVER_ADDRESS + "/app/paraconfig/findCarinfoList.do";
	/**车型号 */
	public static final String FIND_PARACONFIG_LIST = SERVER_ADDRESS + "/app/paraconfig/findParaconfigList.do";
	/**我的收藏 */
	public static final String GET_MYCOLLECT_LIST = SERVER_ADDRESS + "/app/favorite/getFavoriteListN.do";
	/**系统消息*/
	public static final String GET_MYNEWS_SYSTEM_LIST = SERVER_ADDRESS +"/app/usermessge/mylist.do";
	/**系统消息详情*/
	public static final String GET_MYNEWS_SYSTEM_DETAIL = SERVER_ADDRESS +"/app/usemessage/myMessageContent.do";
	/**我的反馈*/
	public static final String GET_MYNEWS_FEEDBACK_LIST = SERVER_ADDRESS +"/app/other/getMyFB.do";
	/**活动列表页--动态*/
	public static final String GET_MYDYNAMIC_LIST = SERVER_ADDRESS +"/app/dynamic/list.do";
	/**意见反馈*/
	public static final String COMMIT_MYFEEDBACK = SERVER_ADDRESS +"/app/other/addTblFeedBack.do";
	/**急救电话*/
	public static final String GET_EMERGENCY_CALL = SERVER_ADDRESS +"/app/rescue/list.do";
	/**车俩维修*/
	public static final String GET_CAR_REPAIR= SERVER_ADDRESS +"/app/carGarage/list.do";
	/** 分享电桩*/
	public static final String SHARE_PILE= SERVER_ADDRESS +"/app/publishEp/add.do";
	/** 获取二维码信息*/
	public static final String GET_SCAN_INFO= SERVER_ADDRESS +"/app/electricPileDetail/selectPileInfo.do";
	
	
	//以下为html 地址
	/**活动详情页*/
	public static final String GET_MYDYNAMIC_DETAIL = SERVER_ADDRESS_HTML +"/html/news/detail.html";
	/**帮助向导页*/
	public static final String INSTRUCTION = SERVER_ADDRESS_HTML +"/html/help/index.html";
	/**费率详情*/
	public static final String ABOUT_PRICE = SERVER_ADDRESS_HTML +"/html/rateinfo/detail.html";
	/**申请建桩*/
	public static final String ApplyBuilder = SERVER_ADDRESS_HTML +"/aichong/applyBuilder.html";
	
	
	
	
}
