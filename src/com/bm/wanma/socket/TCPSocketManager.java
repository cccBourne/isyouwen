package com.bm.wanma.socket;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.ITcpCallBack;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.Log;

public class TCPSocketManager {
	
	private Context mcContext;
	private static TCPSocketManager mTcpSocketManager = null;
	
	private final int STATE_OPEN = 1;// socket打开
	private final int STATE_CLOSE = 1 << 1;// socket关闭
	private final int STATE_CONNECT_START = 1 << 2;// 开始连接server
	private final int STATE_CONNECT_SUCCESS = 1 << 3;// 连接成功
	private final int STATE_CONNECT_FAILED = 1 << 4;// 连接失败
	private final int STATE_CONNECT_WAIT = 1 << 5;// 等待连接
	private int state = STATE_CONNECT_START;
	private Socket socket = null;
	private OutputStream outStream = null;
	private InputStream inStream = null;
	
	/** 心跳检测时间  */
	private static final long HEART_BEAT_RATE = 10 * 1000;
	private long sendHeartBeatTime = 0L;
	private long lastConnTime=0;
	/** 主机IP地址  */
	//private static final String HOST = "124.42.117.53";//10.9.2.110 10.9.3.114
	/** 端口号  */   
	//private static final int PORT = 8001;
	
	private Thread connThread = null;
	private Thread sendHeartThread = null;
	//private Thread reconnThread = null;
	private Thread readPacketThread = null;
	
	private String pileNum;
	private byte headNum;
	private ITcpCallBack mTcpCallBack;

	public TCPSocketManager(Context c) {
		this.mcContext = c;
	
	}

	  public synchronized static TCPSocketManager getInstance(Context c) {
		  
		  if(mTcpSocketManager == null){
			  mTcpSocketManager = new TCPSocketManager(c);
		  }
		  
		  return mTcpSocketManager;
		  
	  }
	  
	  
	  public void setTcpCallback(ITcpCallBack callBack){
		  this.mTcpCallBack = callBack;
	  }
	  
	  //处理包数据
	  @SuppressLint("SimpleDateFormat")
		private void handlePacketData(ByteArrayInputStream in) throws IOException {
			 int reason = StreamUtil.readByte(in);
			 short cmdtype = StreamUtil.readShort(in);
			 Log.i("cm_socket","传送原因"+reason+"指令编码"+cmdtype);
			 switch (cmdtype) {
				case SocketConstant.CMD_TYPE_CONNECT:
					//连接充电桩，响应
					int successflag = StreamUtil.readByte(in);
					short errorcode = StreamUtil.readShort(in);
					if(0 == successflag){
						Log.i("cm_socket", "连接充电桩失败原因"+errorcode);
					}else if(1 == successflag){
						 int headState = StreamUtil.readByte(in);
						    Log.i("cm_socket", "连接充电桩成功,充电枪状态"+headState);
						 	sendHeartThread = new Thread(new SendHeartRunnable());
						 	sendHeartThread.start();
						 	//mTcpCallBack.handleTcpPacket(headState);
					}				
					
					break;
				case SocketConstant.CMD_TYPE_HEART:
					//心跳
					Log.i("cm_socket", "接收心跳响应");
					break;
				case SocketConstant.CMD_TYPE_OPEN_LED:
					//闪LED
					
					break;
				case SocketConstant.CMD_TYPE_CLOSE_LED:
					//关LED
					
					break;
				case SocketConstant.CMD_TYPE_CALL_PILL:
					//呼叫充电桩	
					
					break;
				case SocketConstant.CMD_TYPE_STOP_CALL_PILE:
					//停止呼叫充电桩
					
					break;
				case SocketConstant.CMD_TYPE_UNLOCK:
					//降地锁
					
					break;
				case SocketConstant.CMD_TYPE_CANCEL_BESPOKE:
					//取消预约
					Log.i("cm_socket", "取消预约");
					break;
				case SocketConstant.CMD_TYPE_KAI_GAI:
					//开盖
					
					break;
				case SocketConstant.CMD_TYPE_START_CHARGE:
					//开始充电
					
					/*int startchargeres = StreamUtil.readByte(in);
					short starterror = StreamUtil.readShort(in);
					Intent broadIn = new Intent(BroadcastUtil.BROADCAST_TCP_START_CHARGE);
					broadIn.putExtra("startchargeres",startchargeres);
					broadIn.putExtra("starterror",starterror);
					if(0 == startchargeres){
						Log.i("cm_socket", "开始充电失败原因"+ starterror);
						//TcpService.this.sendBroadcast(broadIn);
					}else if(1 == startchargeres){
						Log.i("cm_socket", "开始充电响应成功");
					}
					mcContext.sendBroadcast(broadIn);*/
					break;
				case SocketConstant.CMD_TYPE_STOP_CHARGE:
					//停止充电
					/*int stopchargres = StreamUtil.readByte(in);
					if(0 == stopchargres){
						short error = StreamUtil.readShort(in);
						Log.i("cm_socket", "停止充电失败原因"+error);
					}else if(1 == stopchargres){
						Log.i("cm_socket", "停止充电响应成功");
					}*/
					break;
				case SocketConstant.CMD_TYPE_REAL_DATA:
					
					int state = StreamUtil.readByte(in);
					short chargeTime = StreamUtil.readShort(in);
					short dianya = StreamUtil.readShort(in);
					short dianliu = StreamUtil.readShort(in);
					int diandu = StreamUtil.readInt(in);
					short feilv = StreamUtil.readShort(in);
					int yuchong = StreamUtil.readInt(in);
					int yichongjine = StreamUtil.readInt(in);
					int soc = StreamUtil.readByte(in);
					int fushu = StreamUtil.readInt(in);
					int gaojing = StreamUtil.readInt(in);
					//实时数据包  
					Log.i("cm_socket", "工作状态"+state);
					Log.i("cm_socket", "累计充电时间"+chargeTime);
					Log.i("cm_socket", "充电输出电压"+dianya);
					Log.i("cm_socket", "充电输出电流"+(float)dianliu/100);
					Log.i("cm_socket", "充电电度"+(float)diandu/100);
					Log.i("cm_socket", " 当前费率"+feilv);
					Log.i("cm_socket", "预充金额"+(float)yuchong/100);
					Log.i("cm_socket", "已充金额"+(float)yichongjine/100);
					Log.i("cm_socket", "soc"+soc);
					Log.i("cm_socket", "附属设备状态"+ fushu);
					Log.i("cm_socket", "告警状态"+ gaojing);
					
					break;
				
				case SocketConstant.CMD_TYPE_DC_SELF_CHECK:
					//直流自检
					 
					break;
				case SocketConstant.CMD_TYPE_CONSUME_RECORD:
					//消费记录
					Log.i("cm_socket", "消费记录返回");
					String order = new String(StreamUtil.readWithLength(in, 21));
					Log.i("cm_socket", "订单号"+order);
					long temps = (long)StreamUtil.readInt(in);
					long tempe = (long)StreamUtil.readInt(in);
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String startdate = sdf.format(new Date(temps*1000));
					String enddate = sdf.format(new Date(tempe*1000));
					String totalpower = String.valueOf((float)StreamUtil.readInt(in)/1000);
					String totalmoney = String.valueOf((float)StreamUtil.readInt(in)/100);
					String servicemoney = String.valueOf((float)StreamUtil.readInt(in)/100);
					String pilePK = String.valueOf(StreamUtil.readInt(in));
					Log.i("cm_socket", "开始时间"+startdate);
					Log.i("cm_socket", "结束时间"+ enddate);
					Log.i("cm_socket", "总电量"+totalpower);
					Log.i("cm_socket", "总充电金额"+totalmoney);
					Log.i("cm_socket", "总服务费"+servicemoney);
					Log.i("cm_socket", "电桩id"+pilePK);
					close();
					break;
				default:
					break;
				}
			 
		 }
	  
	  public void paraData(byte []msg) throws Exception {
		   //将sock读出的数据放进缓存
		  int writeLen =  MyByteBuf.writeBytes(msg);
		  //分解数据包
		  decode();
		  //sock读出的数据没有全部放入缓存，继续处理
		  if(writeLen< msg.length)
		  {
			  byte [] bb = new byte[msg.length-writeLen];
			  System.arraycopy(msg, writeLen, bb, 0, msg.length-writeLen);
			  paraData(bb);
		  }
	   } 
	  /**
		 * 分包处理
		 */
		 public void decode() {
			 //读处理
			  int readableBytes= MyByteBuf.readableBytes();
			  if(readableBytes<7)//如果长度小于长度,不读
			  {
					return;
			  }
				int pos= MyByteBuf.bytesBefore((byte)0x45);//找到的位置
				int pos1= MyByteBuf.bytesBefore((byte)0x43);//找到的位置
				int discardLen=0;
				if(pos < 0 || pos1<0 || (pos1-pos)!=1)//没找到，全部读掉
				{
					discardLen = readableBytes;
					Log.i("cm_socket", "decode not find flag header 0x4543,pos:"+pos+"readableBytes:"+readableBytes+",discardLen"+discardLen+"\n");
				}
				if(pos>0)
				{
					discardLen = pos;
					Log.i("cm_socket", "decode find flag header 0x68 at pos:"+pos +",discardLen"+discardLen+"\n");
				}
				if(discardLen>0)
				{
					byte[] dicardBytes= new byte[discardLen];
					MyByteBuf.readBytes(dicardBytes);//
					if(discardLen == readableBytes)
					{
						//没有数据可对，还回
						return;
					}
				}
				readableBytes= MyByteBuf.readableBytes();//读取缓存剩余的字节
				if(readableBytes<7)
				{
					return;
				}
				byte protocolhead1 = MyByteBuf.readByte();//读取协议头1
				byte protocolhead2 = MyByteBuf.readByte();//读取协议头2
				int lengL = MyByteBuf.readByte();//读长度1
				int lengH = MyByteBuf.readByte();//读长度2
			    
				int msg_len = lengL+lengH*0x100;
				
				int remain_len = MyByteBuf.readableBytes();

				if(remain_len<msg_len )
				{
					Log.i("cm_socket","remain_len:"+remain_len+"\n");
					MyByteBuf.resetReaderIndex();
					return ; 
				}
				    
				byte datab[]= null;
				datab= new byte[msg_len];
				MyByteBuf.readBytes(datab);
				sendHeartBeatTime = System.currentTimeMillis();
				Log.i("cm_socket", "返回完整的包"+Tools.bytesToHexString(datab));
				//Log.i("cm_socket", "指令编码"+SocketParseByteUtil.getShort(datab, 1));
				//data为完整的数据包，后面自己处理
				try {
					handlePacketData(new ByteArrayInputStream(datab));
					mTcpCallBack.handleTcpPacket(new ByteArrayInputStream(datab));
				} catch (IOException e) {
					e.printStackTrace();
				}
				//递归分包处理
				decode();
			}
	  
	  
		 public void open(String pile,byte head) {
			 if(state == STATE_CONNECT_SUCCESS){
				 return ;
			 }
				conn(pile,head);
			}
		 
		 public String getPileNum (){
			 return pileNum;
		 }
		 public byte getHeadNum (){
			 return headNum;
		 }
		 public synchronized void conn(String pile,byte head) {
			    this.pileNum = pile;
			    this.headNum = head;
		        state = STATE_OPEN;
		        connThread =new Thread(new ConnRunnable());
		        connThread.start();
		    }
		 
		 public synchronized void reopen() {
			 if(state == STATE_CONNECT_SUCCESS){
				 return ;
			 }
		        close();
		        state = STATE_OPEN;
		        connThread =new Thread(new ConnRunnable());
		        connThread.start();
		    }
	  
	   /* public synchronized void reconn() {
	        if(System.currentTimeMillis()-lastConnTime < 5000)
	        {
	            return;
	        }
	        //cm test
	        if(state == STATE_CONNECT_SUCCESS){
	        	return;
	        }
	        lastConnTime=System.currentTimeMillis();
	        close();
	        state = STATE_OPEN;
	        Log.i("cm_socket", "发送重连reconn");
	        connThread =new Thread(new ConnRunnable());
	        connThread.start();
	    }*/
	    /**
		 * 判断是否存在tcp连接
		 */
	    public boolean hasTcpConnection(){
	    	if(STATE_CONNECT_SUCCESS == state){
	    		return true;
	    	}else 
	    		return false ;
	    }
	    
	/**
	 * 发送开始充电
	 */
	public void sendStartChargeCMD(String money) {
		try {
			byte[] sendStartChargePacket = SocketPacket.getSendPackage(
					SocketPacket.getStartChargePackage(money, (byte) 1),
					(byte) 0, (short) 10);
			Log.i("cm_socket", "发送开始充电"+Tools.bytesToHexString(sendStartChargePacket));
			synchronized (outStream) {
				outStream.write(sendStartChargePacket);
				outStream.flush();
			}
			
		} catch (Exception e) {
			state = STATE_CLOSE;
			//开始充电异常处理
			try {
				PacketHeader Header = new PacketHeader();
				Header.setLength(6);
				ByteArrayOutputStream bmsg = new ByteArrayOutputStream( SocketConstant.PHONE_SENDBUFFER);
				bmsg.write(Header.toByteArray());
				short cmdtype = 10;
				short error = 1111;
				bmsg.write((byte)0);
				byte cmdtypeL = (byte)(cmdtype&0x00ff);		
				bmsg.write(cmdtypeL);
				byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
				bmsg.write(cmdtypeH);
				bmsg.write((byte)0);
				byte errorL = (byte)(error&0x00ff);		
				bmsg.write(errorL);
				byte errorH = (byte)((error>>8)&0x00ff);
				bmsg.write(errorH);
				byte[] errorD = bmsg.toByteArray();
				handlePacketData(new ByteArrayInputStream(errorD));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			reopen();
			e.printStackTrace();
		}

	}

	/**
	 * 发送停止充电
	 */
	public void sendStopChargeCMD() {
		try {
			byte[] sendStopChargePacket = SocketPacket.getSendPackage(null,
					(byte) 0, (short) 11);
			Log.i("cm_socket", "发送结束充电"+Tools.bytesToHexString(sendStopChargePacket));
			synchronized (outStream){
			outStream.write(sendStopChargePacket);
			outStream.flush();
		 }
		} catch (Exception e) {
			//停止充电异常处理，发送广播
			try {
				PacketHeader Header = new PacketHeader();
				Header.setLength(6);
				ByteArrayOutputStream bmsg = new ByteArrayOutputStream( SocketConstant.PHONE_SENDBUFFER);
				bmsg.write(Header.toByteArray());
				short cmdtype = 11;
				short error = 1111;
				bmsg.write((byte)0);
				byte cmdtypeL = (byte)(cmdtype&0x00ff);		
				bmsg.write(cmdtypeL);
				byte cmdtypeH = (byte)((cmdtype>>8)&0x00ff);
				bmsg.write(cmdtypeH);
				bmsg.write((byte)0);
				byte errorL = (byte)(error&0x00ff);		
				bmsg.write(errorL);
				byte errorH = (byte)((error>>8)&0x00ff);
				bmsg.write(errorH);
				byte[] errorD = bmsg.toByteArray();
				handlePacketData(new ByteArrayInputStream(errorD));
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			reopen();
			//reconn();
			/*Intent stopbroadIn = new Intent(BroadcastUtil.BROADCAST_TCP_STOP_CHARGE);
			stopbroadIn.putExtra("stopres", 2);
			mcContext.sendBroadcast(stopbroadIn);*/
			e.printStackTrace();
		}
	}
	
	// 连接socket
	private class ConnRunnable implements Runnable {
		public void run() {
			while (state != STATE_CLOSE) {
				try {
					state = STATE_CONNECT_START;
					//System.setProperty("http.keepAlive", "false");
					socket = new Socket();
					socket.connect(new InetSocketAddress(Protocol.HOST,
							Protocol.PORT), 30 * 1000);
					state = STATE_CONNECT_SUCCESS;
				} catch (Exception e) {
					e.printStackTrace();
					state = STATE_CONNECT_FAILED;
					Log.i("cm_socket", "STATE_CONNECT_FAILED");
				}

				if (state == STATE_CONNECT_SUCCESS) {
					try {
						outStream = socket.getOutputStream();
						inStream = socket.getInputStream();
						byte[] data = getConnetPackage();
						byte[] sendPacket = SocketPacket.getSendPackage(data,
								(byte) 0, (short) 1);
						Log.i("cm_socket",
								"连接充电桩发送报文"
										+ Tools.bytesToHexString(sendPacket));
						synchronized (outStream) {
							outStream.write(sendPacket);
							outStream.flush();
						}
						// 开启线程获取数据报文
						readPacketThread = new Thread(new ReadPacketRunnable());
						readPacketThread.start();
					} catch (Exception e) {
						e.printStackTrace();
					}
					break;
				} else {
					state = STATE_CONNECT_WAIT;
					// 如果有网络没有连接上，则定时取连接，没有网络则直接退出
					// if(NetworkUtil.isNetworkAvailable(context))
					// {
					try {
						Thread.sleep(10 * 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
						break;
					}
					// }
					/*
					 * else { break; }
					 */
				}
			}
		}
	}
	    
	    //接收报文
	    private class ReadPacketRunnable implements Runnable{
			@Override
			public void run() {
				// if(state!=STATE_CLOSE&&state==STATE_CONNECT_SUCCESS&&null!=inStream){
					   
	                 byte[] b = new byte[1024];  
	                 try {
						//int n = inStream.read(b);
	                	 int n = inStream.read(b);
	                	 while(n != -1){
	                		 byte[] readByte = new byte[n]; 
	                		 System.arraycopy(b, 0, readByte, 0, n);
	                		 paraData(readByte);
	                		 n = inStream.read(b);
	                	 }
					} catch (Exception e) {
						e.printStackTrace();
					}
			} 
	    }
	    //发心跳
	    private class SendHeartRunnable implements Runnable
	    {
	    	
	        public void run() {
	            try { 
	            		byte[] buffer =  SocketPacket.getSendPackage(null, (byte)0,(short)2);
	                    while(state==STATE_CONNECT_SUCCESS&&null!=outStream)
	                    {
	                    	//Thread.sleep(10 *1000);
	                    	if((System.currentTimeMillis() - sendHeartBeatTime)> HEART_BEAT_RATE){
		                    	//byte[] buffer =  SocketPacket.getSendPackage(null, (byte)0,(short)2);
		                    	Log.i("cm_socket", "发送心跳包"+Tools.bytesToHexString(buffer));
		                    	sendHeartBeatTime = System.currentTimeMillis(); 	
		                    	synchronized (outStream) {
		                         outStream.write(buffer);
		                         outStream.flush();
		                    	}
		                                   
	                    	}
	                    }  
	            }catch(SocketException e1) 
	            {
	            	state = STATE_CLOSE;
	                e1.printStackTrace();//发送的时候出现异常，说明socket被关闭了(服务器关闭)java.net.SocketException: sendto failed: EPIPE (Broken pipe)
	                reopen();
	            }  
	            catch (Exception e) {
	            	state = STATE_CLOSE;
	            	reopen();
	                e.printStackTrace();
	            }
	        }
	    } 
	    //重连socket
	    private class ReconnRunnable implements Runnable
	    {
	        public void run() {
	             
	            try {
	                    while(state!=STATE_CLOSE&&state==STATE_CONNECT_SUCCESS&&null!=inStream)
	                    {
	                          //  reconn();//走到这一步，说明服务器socket断了
	                            break;
	                    }
	            }
	           /* catch(SocketException e1) 
	            {
	                e1.printStackTrace();//客户端主动socket.close()会调用这里 java.net.SocketException: Socket closed
	            } */
	            catch (Exception e2) {
	                e2.printStackTrace();
	            }
	             
	        }
	    }
	  //获取连接充电桩数据包
	  	private byte[] getConnetPackage() throws IOException{
	         String pkuser = PreferencesUtil.getStringPreferences(mcContext, "pkUserinfo");
	         String pwd = PreferencesUtil.getStringPreferences(mcContext, "password");
	         long userId = Long.valueOf(pkuser);
	        /*  long userId = 109;
	        String pwd = "e10adc3949ba59abbe56e057f20f883e";
	        String pkuser = "109";*/
	  		// 获取设备id
	  		TelephonyManager tm = (TelephonyManager) mcContext
	  				.getSystemService(Context.TELEPHONY_SERVICE);
	  		String deviceId = tm.getDeviceId();
	  		deviceId = Tools.encoderByMd5(deviceId);
	  		String yzm = deviceId + pwd + pkuser;
	  		yzm = Tools.encoderByMd5(yzm);
	  		
	  		ByteArrayOutputStream bmsg = new ByteArrayOutputStream( SocketConstant.PHONE_SENDBUFFER);
	  		bmsg.write(pileNum.getBytes());
	  		bmsg.write(headNum);
	  		
	  		bmsg.write(SocketParseByteUtil.longToByte(userId));
	  		bmsg.write(yzm.getBytes());
	  
	  		return bmsg.toByteArray();
	  	} 
	    public synchronized void close()
	    {
	        if(state!=STATE_CLOSE)
	        {
	            try {
	                if(null!=socket)
	                {
	                    socket.close();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	                socket=null;
	            }
	             
	            try {
	                if(null!=outStream)
	                {
	                	synchronized (outStream){
	                    outStream.close();
	                	}
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	                outStream=null;
	            }
	             
	            try {
	                if(null!=inStream)
	                {
	                    inStream.close();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	                inStream=null;
	            }
	             
	            try {
	                if(null!=connThread&&connThread.isAlive())
	                {
	                	connThread.interrupt();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	            	connThread=null;
	            }
	             
	            try {
	                if(null!=sendHeartThread&&sendHeartThread.isAlive())
	                {
	                	sendHeartThread.interrupt();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	            	sendHeartThread=null;
	            }
	            try {
	                if(null!=readPacketThread&&readPacketThread.isAlive())
	                {
	                	readPacketThread.interrupt();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	            	readPacketThread=null;
	            }
	          /*  try {
	                if(null!=recThread&&recThread.isAlive())
	                {
	                	recThread.interrupt();
	                }
	            } catch (Exception e) {
	                e.printStackTrace();
	            }finally{
	            	recThread=null;
	            }*/
	             
	            state=STATE_CLOSE;
	        }
	    }
	    
	  //网络变化广播接收器
	/*	 public  class NetWorkReceiver extends BroadcastReceiver {  
		        @Override  
		        public void onReceive(Context context, Intent intent) {  
		        	
		        	if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")){
		        		LogUtil.i("cm_network","网络变化");
		        		if(isNetConnection() && !hasTcpConnection()){
		        			Log.i("cm_socket", "网络变化，skcket重连");
		        			//reopen();
		        		}
		        	}
		        }  
		    } */ 
		 /* 判断是否有网络 */
			public boolean isNetConnection() {
				ConnectivityManager cwjManager = (ConnectivityManager)mcContext.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = cwjManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					return true;
				} else {
					return false;
				}
			}   
	    
	    
}
