package com.bm.wanma.jpush;

import org.json.JSONException;
import org.json.JSONObject;

import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.ui.activity.ForceOffline;
import com.bm.wanma.ui.activity.HomeActivity;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.SoundManager;
import com.bm.wanma.utils.Tools;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则： 1) 默认用户会打开主界面 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "cm_test_Jpush";
	private Vibrator vibrator;
	private SoundManager soundManager;
	private Context mcontext;
	private String pkuserId;

	private void notifyUser(Context context) {
		soundManager = new SoundManager(context);
		vibrator = (Vibrator) context
				.getSystemService(context.VIBRATOR_SERVICE);
		vibrator.vibrate(200);
		soundManager.addSound("notify", R.raw.shake_match);
		soundManager.playSound("notify");
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle bundle = intent.getExtras();
		mcontext = context;
		// LogUtil.i(TAG, "[Jpush] onReceive - " + intent.getAction() +
		// ", extras: " + printBundle(bundle));

		if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
			String regId = bundle
					.getString(JPushInterface.EXTRA_REGISTRATION_ID);
			// 保存极光推送id
			PreferencesUtil.setPreferences(context, "jpushRegistrationid",
					regId);
			LogUtil.i(TAG, "regId" + regId);
			// send the Registration Id to your server...

		} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent
				.getAction())) {
			LogUtil.i(
					TAG,
					"[MyReceiver] 接收到推送下来的自定义消息cm: "
							+ bundle.getString(JPushInterface.EXTRA_MESSAGE));
			String type = bundle.getString(JPushInterface.EXTRA_EXTRA);
			JSONObject jsonObject;
			pkuserId = PreferencesUtil.getStringPreferences(mcontext, "pkUserinfo");
			try {
				jsonObject = new JSONObject(type);
				String str = jsonObject.getString("type");
				LogUtil.i(TAG, "自定义消息type:" + str);
				if ("9".equals(str)) {
					// 根据type跳转不同界面 消息类型 1 充电结束推送 2 余额不足推送 3充电开始推送 4消费记录 5预约完成
					// 6取消预约
					// 类型为1时，进入 充电订单列表界面；3.充电展示界面 5和6.预约列表 7.推送消息通知8.强制下线
					// 定义NotificationManager
				/*	NotificationManager mNotificationManager = (NotificationManager) mcontext
							.getSystemService(Context.NOTIFICATION_SERVICE);
					NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
							mcontext);

					mBuilder.setContentTitle("测试标题")// 设置通知栏标题
							.setContentText("测试内容")// 设置通知栏显示内容
							// .setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL))//设置通知栏点击意图
							// .setNumber(number);
							.setTicker("测试通知来啦")// 通知栏首次出现在通知栏，带上动画效果
							.setWhen(System.currentTimeMillis())// 通知栏时间，一般是直接用系统的
							.setPriority(Notification.PRIORITY_MAX)// 设置通知栏优先级
							.setAutoCancel(true)// 用户单击面板后消失
							.setOngoing(false)// true,设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此
							// 占用设备(如一个文件下载，同步操作，主动网络连接)
							.setDefaults(Notification.DEFAULT_VIBRATE)// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，
							// 使用default属性，可以组合
							// Notification.DEFAULT_ALL
							// Notification.DEFAULT_SOUND 添加声音 // requires
							// VIBRATE permission
							.setSmallIcon(R.drawable.bg_map_head2);
					Notification notification = mBuilder.build();
					notification.flags = Notification.FLAG_ONGOING_EVENT;
					// notification.flags =
					// Notification.FLAG_NO_CLEAR;//点击清除的时候不清除
					Intent goToIntent = new Intent(mcontext, HomeActivity.class);
					// goToIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					goToIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
					PendingIntent pendingIntent = PendingIntent.getActivity(
							mcontext, 0, goToIntent, 0);
					mBuilder.setContentIntent(pendingIntent);
					mNotificationManager.notify(1, notification);*/
				} else if ("8".equals(str) && !Tools.isEmptyString(pkuserId)) {
					Intent forceofflineIn = new Intent(context,
							ForceOffline.class);
					forceofflineIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(forceofflineIn);
				}else if("6".equals(str)){
					// 5预约完成 6取消预约--通知地图预约按钮出现，或结束
					Log.i("cm_socket", "取消预约");
					Intent bespokefinishIn = new Intent();
					bespokefinishIn.setAction(BroadcastUtil.BROADCAST_Bespoke_Finish);
					mcontext.sendBroadcast(bespokefinishIn);
				}else if("5".equals(str)){
					// 5预约完成 6取消预约
					Log.i("cm_socket", "预约完成");
				}else if("1".equals(str)){
					Log.i("cm_socket", "充电结束");
					Intent chargefinishIn = new Intent();
					chargefinishIn.setAction(BroadcastUtil.BROADCAST_Charge_CANCLE);
					mcontext.sendBroadcast(chargefinishIn);
				}

			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent
				.getAction())) {
			LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知");
			// bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
			// LogUtil.i(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
		} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent
				.getAction())) {
			LogUtil.i(TAG, "[MyReceiver] 用户点击打开了通知");
			String type = bundle.getString(JPushInterface.EXTRA_EXTRA);
			// LogUtil.i(TAG, "type:" + type);
			/*try {
				JSONObject jsonObject = new JSONObject(type);
				String str = jsonObject.getString("type");
				// 根据type跳转不同界面 消息类型 1 充电结束推送 2 余额不足推送 3充电开始推送 4消费记录 5预约完成 6取消预约
				// 类型为1时，进入 充电订单列表界面；3.充电展示界面 5和6.预约列表 7.推送消息通知8.强制下线
				if (str.equals("1")) {
				} else if (str.equals("3")) {
					Intent i = new Intent(context,
							ChargingDisplayActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				} else if (str.equals("5")) {
					Intent i = new Intent(context, MyBespokeListActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				} else if (str.equals("6")) {
					Intent i = new Intent(context, MyBespokeListActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				} else if (str.equals("7")) {
					Intent i = new Intent(context, MyNewsActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
*/
			
		} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent
				.getAction())) {
			LogUtil.i(
					TAG,
					"[MyReceiver] 用户收到到RICH PUSH CALLBACK: "
							+ bundle.getString(JPushInterface.EXTRA_EXTRA));
			// 在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity，
			// 打开一个网页等..

		} else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent
				.getAction())) {
			boolean connected = intent.getBooleanExtra(
					JPushInterface.EXTRA_CONNECTION_CHANGE, false);
			LogUtil.i(TAG, "[MyReceiver]" + intent.getAction()
					+ " connected state change to " + connected);
		} else {
			LogUtil.i(TAG,
					"[MyReceiver] Unhandled intent - " + intent.getAction());
		}
	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}

	// 注销用户信息
	private void logoutInfo() {
		PreferencesUtil.setPreferences(mcontext, "pkUserinfo", "");
		PreferencesUtil.setPreferences(mcontext, "usinPhone", "");
		PreferencesUtil.setPreferences(mcontext, "usinFacticityname", "");
		PreferencesUtil.setPreferences(mcontext, "usinSex", "");
		PreferencesUtil.setPreferences(mcontext, "usinAccountbalance", "");
		PreferencesUtil.setPreferences(mcontext, "usinBirthdate", "");
		PreferencesUtil.setPreferences(mcontext, "usinUserstatus", "");
		PreferencesUtil.setPreferences(mcontext, "usinHeadimage", "");
		PreferencesUtil.setPreferences(mcontext, "nickName", "");

	}

}
