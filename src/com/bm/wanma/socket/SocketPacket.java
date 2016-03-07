package com.bm.wanma.socket;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.util.Log;

public class SocketPacket {
//获得发送数据包
	/**
	 * data信息体 cos 传送原因0-正常，1-确认 cmdtype指令编码*/
public static  byte[] getSendPackage(byte[] data,byte cos,short cmdtype) throws IOException {
		
		PacketHeader Header = new PacketHeader();
		if(null == data){
			Header.setLength(3);
		}else {
			Header.setLength(3 + data.length);
		}

		ByteArrayOutputStream bmsg = new ByteArrayOutputStream( SocketConstant.PHONE_SENDBUFFER);
		
		bmsg.write(Header.toByteArray());
		
		bmsg.write(cos);
		
		byte cmdtypeL = (byte)(cmdtype&0x00ff);		
		bmsg.write(cmdtypeL);
		
		byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
		bmsg.write(cmdtypeH);
		if(null != data){
			bmsg.write(data);
		}
		
		return bmsg.toByteArray();
	}
	//发送连接充电桩data[]
	public static byte[] getConnetPackage(String pileNum,byte headNum,long userId,String yzm) throws IOException {
	
	ByteArrayOutputStream bmsg = new ByteArrayOutputStream( SocketConstant.PHONE_SENDBUFFER);
	bmsg.write(pileNum.getBytes());
	bmsg.write(headNum);
	bmsg.write(SocketParseByteUtil.longToByte(userId));
	bmsg.write(yzm.getBytes());
	/*byte cmdtypeL = (byte)(cmdtype&0x00ff);		
	bmsg.write(cmdtypeL);
	byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
	bmsg.write(cmdtypeH);
	
	bmsg.write(data);*/
	
	return bmsg.toByteArray();
}
		//发送充电指令data[]
		public static byte[] getStartChargePackage(String str,byte chargeMode) throws IOException {
		int money = (int) (Float.valueOf(str) * 100);
		Log.i("cm_socket", "预充金额"+money);
		ByteArrayOutputStream bmsg = new ByteArrayOutputStream( SocketConstant.PHONE_SENDBUFFER);
		bmsg.write(SocketParseByteUtil.int2Bytes(money));
		bmsg.write(chargeMode);
		
		return bmsg.toByteArray();
	}
	
	
	
	
	
	
	

}
