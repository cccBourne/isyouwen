package com.bm.wanma.ui.fragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.adapter.MyChargeOrderAdapter;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.entity.MyChargeOrderBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.socket.SocketConstant;
import com.bm.wanma.socket.StreamUtil;
import com.bm.wanma.socket.TCPSocketManager;
import com.bm.wanma.ui.activity.ChargeDetailActivity;
import com.bm.wanma.ui.activity.ITcpCallBack;
import com.bm.wanma.ui.activity.RealTimeChargeActivity;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.view.MyDetailListView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 充电订单界面
 * @author cm
 *
 */
public class MyChargeOrderFragment extends BaseFragment implements OnItemClickListener,ITcpCallBack{

	private TextView tv_no_data;
	private ListView chargeListView;
	private String pkUserId;
	private ArrayList<MyChargeOrderBean> allBeanlist,chargeBeanlist;
	private MyChargeOrderBean chargeOrderBean;
	private MyChargeOrderAdapter mAdapter;
	private TCPSocketManager mTcpSocketManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pkUserId = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
		chargeBeanlist = new ArrayList<MyChargeOrderBean>();
		registerBoradcastReceiver();
	}
	 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View chargeOrderFragment = inflater.inflate(
				R.layout.fragment_mycharge_order, container, false);
		init(chargeOrderFragment);
		
		return chargeOrderFragment;
	}

    private void init(View mainView){
    	if(isNetConnection()){
			showPD("正在加载数据...");
			GetDataPost.getInstance(getActivity()).getMyChargeOrderList(handler, pkUserId,null);
		}
    	tv_no_data = (TextView) mainView.findViewById(R.id.mycharge_nodata);
    	chargeListView = (ListView) mainView.findViewById(R.id.mycharge_listview);
    }
    
	
	//处理tcp报文
	@Override
	public void handleTcpPacket(ByteArrayInputStream result) {
		//收到实时数据，进入实时数据界面
		cancelPD();
	    try {
			StreamUtil.readByte(result);//int reason = 
			short cmdtype = StreamUtil.readShort(result);
			switch (cmdtype) {
			case SocketConstant.CMD_TYPE_REAL_DATA:
				int state = StreamUtil.readByte(result);
				short chargeTime = StreamUtil.readShort(result);
				StreamUtil.readShort(result);//short dianya = 
				StreamUtil.readShort(result);//short dianliu = 
				int diandu = StreamUtil.readInt(result);
				short feilv = StreamUtil.readShort(result);
				int yuchong = StreamUtil.readInt(result);
				int yichong = StreamUtil.readInt(result);
				int soc = StreamUtil.readByte(result);
				StreamUtil.readInt(result);//int fushu = 
				StreamUtil.readInt(result);//int gaojing = 
				Intent realIn = new Intent(getActivity(),
						RealTimeChargeActivity.class);
				realIn.putExtra("state", state);
				realIn.putExtra("chargeTime", chargeTime);
				realIn.putExtra("diandu", diandu);
				realIn.putExtra("feilv", feilv);
				realIn.putExtra("yuchong", yuchong);
				realIn.putExtra("yichong", yichong);
				realIn.putExtra("soc", soc);
				//mTcpSocketManager.close();
				startActivity(realIn);
				break;
			case SocketConstant.CMD_TYPE_CONNECT:
				int successflag = StreamUtil.readByte(result);
				short errorcode = StreamUtil.readShort(result);
				switch (successflag) {
				case 1:
					//连接成功
					break;
				case 0:
					showErrorCode(errorcode);
					mTcpSocketManager.close();
					break;
				default:
					break;
				}
				
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	 public void registerBoradcastReceiver(){  
	        IntentFilter myIntentFilter = new IntentFilter();  
	        myIntentFilter.addAction(BroadcastUtil.BROADCAST_Charge_CANCLE); 
	        //注册广播        
	        getActivity().registerReceiver(mBroadcastReceiver, myIntentFilter);  
	    }  
	//取消充电返回列表时重新获取数据
		private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver(){  
			@Override
			public void onReceive(Context context, Intent intent) {
				String action = intent.getAction();
				 if(action.equals(BroadcastUtil.BROADCAST_Charge_CANCLE)){  
					 GetDataPost.getInstance(getActivity()).getMyChargeOrderList(handler, pkUserId,null);
		         }  
			}  
	          
	    };


	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		allBeanlist = (ArrayList<MyChargeOrderBean>) bundle.getSerializable(Protocol.DATA);
		if(allBeanlist!= null && allBeanlist.size()>0){
			tv_no_data.setVisibility(View.GONE);
			chargeBeanlist.clear();
			for(MyChargeOrderBean bean : allBeanlist){
				//订单状态 1：待支付 2：支付成功 3 完成操作 （1状态下需要支付）
				if("1".equals(bean.getChOr_ChargingStatus())){
					bean.setChOr_ChargingStatus("充电中");
				}else {
					bean.setChOr_ChargingStatus("已完成");
				} 
				chargeBeanlist.add(bean);
			}
			
			mAdapter = new MyChargeOrderAdapter(getActivity(), chargeBeanlist);
			chargeListView.setAdapter(mAdapter);
			chargeListView.setOnItemClickListener(this);
			
		}else {
			tv_no_data.setVisibility(View.VISIBLE);
		}
	} 

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));

	}
	@Override
	public void onResume() {
		super.onResume();
		//registerBoradcastReceiver();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(mBroadcastReceiver);
	}


		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			chargeOrderBean = chargeBeanlist.get(position);
			if("已完成".equals(chargeOrderBean.getChOr_ChargingStatus())){
				Intent in = new Intent();
				in.setClass(getActivity(),ChargeDetailActivity.class);
				in.putExtra("chargeKey", chargeOrderBean.getPk_ChargingOrder());
				startActivity(in);
			}else if("充电中".equals(chargeOrderBean.getChOr_ChargingStatus())){
				showPD("正在获取充电信息...");
				mTcpSocketManager = TCPSocketManager.getInstance(getActivity());
				mTcpSocketManager.setTcpCallback(this);
				mTcpSocketManager.conn(chargeOrderBean.getElPi_ElectricPileCode(), 
						Byte.parseByte(chargeOrderBean.getHeadCode()));
			}
	
		}
	
		private void showErrorCode(int error){
			switch (error) {
			case 6000:
				showToast("电桩通讯未连接");
				break;
			case 6001:
				showToast("电桩未响应,超时");
				break;
			case 6100:
				showToast("电桩编码无效");
				break;
			case 6101:
				showToast("电桩枪口编码无效");
				break;
			case 6104:
				showToast("电桩正在升级，不能使用");
				break;
			case 6105:
				showToast("报文错误");
				break;
			case 6200:
				showToast("充电枪被停用,不能使用");
				break;
			case 6300:
				showToast("桩已经被别人使用");
				break;
			case 6301:
				showToast("桩在操作中(设置)，不能预约或充电");
				break;
			case 6401:
				showToast("用户长度无效");
				break;
			case 6402:
				showToast("用户密码错误");
				break;
			case 6403:
				showToast("校验失败，请重新登录");
				break;
			case 6404:
				showToast("用户不存在或者存在多个");
				break;
			case 6405:
				showToast("用户状态无效");
				break;
			case 6406:
				showToast("在使用其他桩");
				break;
			case 6700:
				showToast("充电方式错误");
				break;
			case 6701:
				showToast("有未支付订单,不能充电");
				break;
			case 1002:
				showToast("用户金额不足,不能充电");
				break;
			case 6601:
				showToast("已经有其他人预约");
				break;	
			case 6633:
				showToast("预约中，不能在进行除续约之外的其它操作");
				break;	
			case 6702:
				showToast("充电枪没插好,不能充电");
				break;
			case 6703:
				showToast("充电枪盖没盖好,不能充电");
				break;
			case 6704:
				showToast("车与桩未建立通讯");
				break;
			case 6705:
				showToast("故障，不能充电");
				break; 
			case 6706:
				showToast("枪盖已经打开,不能重复打开");
				break; 
			case 6800:
				showToast("已经在充电,不能重复充电");
				break;
			case 6801:
				showToast("其他人已经在充电,不能重复充电");
				break;
			case 6802:
				showToast("充电枪被预约,不能充电");
				break;
			case 6803:
				showToast("没有充电,不能停止充电");
				break;
			case 6804:
				showToast("充电桩故障,不能预约或充电");
				break;
			default:
				showToast("未知原因");
				break;
			}
			
		}


}
