package com.bm.wanma.ui.scan;

import java.io.IOException;

import com.bm.wanma.R;
import com.bm.wanma.entity.ScanInfoBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.ui.activity.BaseActivity;
import com.bm.wanma.utils.ToastUtil;
import com.bm.wanma.utils.Tools;
import com.zbar.lib.camera.CameraManager;
import com.zbar.lib.decode.CaptureActivityHandler;
import com.zbar.lib.decode.InactivityTimer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 描述: 扫描界面
 */
public class CaptureActivity extends BaseActivity implements Callback,OnClickListener {

	private CaptureActivityHandler captureHandler;
	private boolean hasSurface;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	private int x = 0;
	private int y = 0;
	private int cropWidth = 0;
	private int cropHeight = 0;
	private RelativeLayout mContainer = null;
	private RelativeLayout mCropLayout = null;
	private boolean isNeedCapture = false;
	private ImageButton scan_ibtn_back;
	private String mcode,mq;
	private LinearLayout ll_scan_light;
	private TextView tv_scan_light;
	private ImageView iv_scan_light;
	private boolean isOpenLight;
	private ScanInfoBean mScanInfoBean;
	
	public boolean isNeedCapture() {
		return isNeedCapture;
	}

	public void setNeedCapture(boolean isNeedCapture) {
		this.isNeedCapture = isNeedCapture;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getCropWidth() {
		return cropWidth;
	}

	public void setCropWidth(int cropWidth) {
		this.cropWidth = cropWidth;
	}

	public int getCropHeight() {
		return cropHeight;
	}

	public void setCropHeight(int cropHeight) {
		this.cropHeight = cropHeight;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_qr_scan);
		ll_scan_light = (LinearLayout) findViewById(R.id.scan_light_ll);
		ll_scan_light.setOnClickListener(this);
		tv_scan_light = (TextView) findViewById(R.id.scan_light_tv);
		iv_scan_light = (ImageView) findViewById(R.id.scan_light_iv);
		
		scan_ibtn_back = (ImageButton)findViewById(R.id.activity_scan_back);
		scan_ibtn_back.setOnClickListener(this);
		//是否开启手电筒
		isOpenLight = false;
		
		// 初始化 CameraManager
		CameraManager.init(getApplication());
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

		mContainer = (RelativeLayout) findViewById(R.id.capture_containter);
		mCropLayout = (RelativeLayout) findViewById(R.id.capture_crop_layout);

		ImageView mQrLineView = (ImageView) findViewById(R.id.capture_scan_line);
		TranslateAnimation mAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f,
				TranslateAnimation.RELATIVE_TO_PARENT, 0f, TranslateAnimation.RELATIVE_TO_PARENT, 0.9f);
		mAnimation.setDuration(1500);
		mAnimation.setRepeatCount(-1);
		mAnimation.setRepeatMode(Animation.REVERSE);
		mAnimation.setInterpolator(new LinearInterpolator());
		mQrLineView.setAnimation(mAnimation);
	}
	
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.scan_light_ll:
			//手电筒开关
			if(isOpenLight){
				CameraManager.get().offLight();//关闭手电筒
				isOpenLight = false;
				ll_scan_light.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
				tv_scan_light.setText("打开手电筒");
				tv_scan_light.setTextColor(getResources().getColor(R.color.common_white));
				iv_scan_light.setImageResource(R.drawable.btn_lamp_on);
			}else {
				CameraManager.get().openLight(); //开启手电筒
				isOpenLight = true ;
				ll_scan_light.setBackground(getResources().getDrawable(R.drawable.scan_light_white_bg));
				tv_scan_light.setText("关闭手电筒");
				tv_scan_light.setTextColor(getResources().getColor(R.color.common_orange));
				iv_scan_light.setImageResource(R.drawable.btn_lamp_off);
			}
			
			break;
		case R.id.activity_scan_back:
			CaptureActivity.this.finish();
			break;
		default:
			break;
		}	
	}

/*	boolean flag = true;

	protected void light() {
		if (flag == true) {
			flag = false;
			// 开闪光灯
			CameraManager.get().openLight();
		} else {
			flag = true;
			// 关闪光灯
			CameraManager.get().offLight();
		}

	}*/

	@SuppressWarnings("deprecation")
	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.capture_preview);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (captureHandler != null) {
			captureHandler.quitSynchronously();
			captureHandler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	protected void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	public void handleDecode(String result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		//扫描成功，解析二维码
		if (result != null && !result.isEmpty()) {
			if(result.contains("type=") && result.contains("&code") && result.contains("&q")){
				//mtype = result.substring(result.indexOf("type=")+5, result.indexOf("&code"));
				mcode = result.substring(result.indexOf("code=")+5, result.indexOf("&q"));
				mq = result.substring(result.indexOf("&q=")+3);
				if(isNetConnection()){
					showPD(getString(R.string.request_data));
					GetDataPost.getInstance(this).getScanInfo(handler, mcode, mq);
					//showToast(result);
				}else {
					showToast("亲，网络不稳，请检查网络连接");
				}
				
			}else {
				ToastUtil.TshowToast("无法识别的二维码！");
				finish();
			}
			
		} else {
			Toast.makeText(CaptureActivity.this, "扫描失败", Toast.LENGTH_SHORT)
			.show();
			finish();
			/*Intent intent = new Intent(this, ScanSucessActivity.class);
			Bundle bundle = new Bundle();
			bundle.putString("result", result);
			intent.putExtras(bundle);
			//startActivity(intent);
			//finish();
		*/
		}
		// 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
		 //captureHandler.sendEmptyMessage(R.id.restart_preview);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
	
		try {
			CameraManager.get().openDriver(surfaceHolder);
			//CameraManager.get().requestAutoFocus(captureHandler, 1);
			Point point = CameraManager.get().getCameraResolution();
			int width = point.y;
			int height = point.x;
			Log.i("cm_camera", "width"+width);
			int x = mCropLayout.getLeft() * width / mContainer.getWidth();
			int y = mCropLayout.getTop() * height / mContainer.getHeight();

			int cropWidth = mCropLayout.getWidth() * width / mContainer.getWidth();
			int cropHeight = mCropLayout.getHeight() * height / mContainer.getHeight();

			setX(x);
			setY(y);
			setCropWidth(cropWidth);
			setCropHeight(cropHeight);
			// 设置是否需要截图
			//setNeedCapture(false);
			setNeedCapture(true);

		} catch (IOException ioe) {
		
			return;
		} catch (RuntimeException e) {
			//应用权限被禁在这异常 提示
			Toast.makeText(getApplicationContext(), "请开启摄像头权限", 1).show();
			
			return;
		}
		if (captureHandler == null) {
			captureHandler = new CaptureActivityHandler(CaptureActivity.this);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;

	}

	public Handler getHandler() {
		return captureHandler;
	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	protected void getData() {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(0x18 == requestCode){
			finish();
		}
		
	}
 
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		//扫描结果处理
		cancelPD();
		if(bundle != null){
			mScanInfoBean = (ScanInfoBean) bundle.getSerializable(Protocol.DATA);
			if(mScanInfoBean == null){
				showToast("获取数据异常");
				finish();
			}else {
				Intent in = new Intent();
				in.putExtra("scanInfo", mScanInfoBean);
				in.setClass(this, ScanSucessActivity.class);
				startActivityForResult(in, 0x18);
			}
			
		}else {
			showToast("解析异常");
			finish();
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
		finish();
	}

	
}