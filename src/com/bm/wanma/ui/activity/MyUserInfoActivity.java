package com.bm.wanma.ui.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalDb;

import com.bm.wanma.R;
import com.bm.wanma.broadcast.BroadcastUtil;
import com.bm.wanma.dialog.ActionSheetDialog;
import com.bm.wanma.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.bm.wanma.dialog.ActionSheetDialog.SheetItemColor;
import com.bm.wanma.dialog.MyAlertDialog;
import com.bm.wanma.dialog.TakePhotoDialog;
import com.bm.wanma.entity.AreaBean;
import com.bm.wanma.entity.CityBean;
import com.bm.wanma.entity.ProvinceBean;
import com.bm.wanma.entity.UserInfoBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.NetFile;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.HeadImageUtils;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.RegularExpressionUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.ContainsEmojiEditText;
import com.bm.wanma.view.RoundImageView;
import com.bm.wanma.view.wheelcity.OnWheelChangedListener;
import com.bm.wanma.view.wheelcity.OnWheelScrollListener;
import com.bm.wanma.view.wheelcity.WheelView;
import com.bm.wanma.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.bm.wanma.view.wheelcity.adapters.AreaArrayWheelAdapter;
import com.bm.wanma.view.wheelcity.adapters.CityArrayWheelAdapter;
import com.bm.wanma.view.wheelview.JudgeDate;
import com.bm.wanma.view.wheelview.ScreenInfo;
import com.bm.wanma.view.wheelview.WheelMain;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.ContactsContract.Contacts.Data;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author cm
 * 我的个人资料界面
 */
public class MyUserInfoActivity extends BaseActivity implements OnClickListener,OnFocusChangeListener{
	
	
	private ImageButton ib_back;
	private TextView tv_save,tv_phone,tv_ic,tv_sex,tv_birthday,tv_cartype,tv_code;
	private RoundImageView iv_photo;
	private ContainsEmojiEditText et_nickname,et_realname,et_email,et_address;
	//private EditText et_nickname,et_realname,et_email,et_address;
	private RelativeLayout rl_sex,rl_birthday,rl_cartype,rl_code;
	private TakePhotoDialog takePhotoDialog;
	private File outputImage;
	private Uri captureimageUri;
	private String allMultiFile,carType;
	private static final int CODE_SELECT_CARTYPE_REQUEST = 0xa4;
	//选择时间
	private WheelMain wheelMain;
	private FinalDb finalDb;
	private List<ProvinceBean> provinceList;
	private List<CityBean> cityList;
	private List<AreaBean> areaList;
	private ProvinceBean provinceBean,currentP;
	private CityBean cityBean,currentC;
	private  WheelView view_province;
	private WheelView view_city ;
	private WheelView view_area;
	private String pcode,ccode,acode,cityTxt,usinCarinfoId;
	private String pkUserinfo,usinEmail,usinFacticityname,usinUsername,usinSex;
	private String usinBirthdate,usinUseraddress;
	private UserInfoBean userInfoBean;
	
	@SuppressLint("SimpleDateFormat")
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myuserinfo_detail);
		finalDb = FinalDb.create(getActivity(),Protocol.DATABASE_NAME,true,Protocol.dbNumer,null);
		initView(); 
		userInfoBean = (UserInfoBean) getIntent().getSerializableExtra("userInfo");
		if(userInfoBean == null ){
			String pkUserId = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
			showPD("正在获取用户信息");
			GetDataPost.getInstance(getActivity()).getUserInfo(handler, pkUserId);
		}else {
			initValueToView(userInfoBean);
		}
	}

	private void initView(){
		ib_back = (ImageButton) findViewById(R.id.userinfo_detail_back);
		ib_back.setOnClickListener(this);
		tv_save = (TextView) findViewById(R.id.userinfo_detail_complete);
		tv_save.setOnClickListener(this);
		iv_photo = (RoundImageView) findViewById(R.id.userinfo_detail_photo);
		iv_photo.setOnClickListener(this);
		tv_phone = (TextView) findViewById(R.id.userinfo_detail_phone);
		et_nickname = (ContainsEmojiEditText) findViewById(R.id.userinfo_detail_et_nickname);
		et_nickname.setOnFocusChangeListener(this);
		et_realname = (ContainsEmojiEditText) findViewById(R.id.userinfo_detail_et_realname);
		et_realname.setOnFocusChangeListener(this);
		rl_sex = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_sex);
		rl_sex.setOnClickListener(this);
		tv_sex = (TextView) findViewById(R.id.userinfo_detail_tv_sex);
		rl_birthday = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_brithday);
		rl_birthday.setOnClickListener(this);
		tv_birthday = (TextView) findViewById(R.id.userinfo_detail_tv_brithday);
		tv_ic = (TextView) findViewById(R.id.userinfo_detail_tv_ic);
		et_email = (ContainsEmojiEditText) findViewById(R.id.userinfo_detail_et_email);
		et_email.setOnFocusChangeListener(this);
		rl_cartype = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_cartype);
		rl_cartype.setOnClickListener(this);
		tv_cartype = (TextView) findViewById(R.id.userinfo_detail_tv_cartype);
		rl_code = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_code);
		rl_code.setOnClickListener(this);
		tv_code = (TextView) findViewById(R.id.userinfo_detail_tv_code);
		et_address = (ContainsEmojiEditText) findViewById(R.id.userinfo_detail_et_address);
		et_address.setOnFocusChangeListener(this);
		
		
	}
	private void initValueToView(UserInfoBean bean){
		if(!Tools.isEmptyString(bean.getUserImage())){
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showImageForEmptyUri(R.drawable.img_my_user)
			.showImageOnFail(R.drawable.img_my_user) 
			.cacheInMemory(true)
			.cacheOnDisk(false)
			.bitmapConfig(Config.RGB_565)
			.build();
			ImageLoader.getInstance().displayImage(bean.getUserImage(), iv_photo, options);
		}
		
		//手机号码加****
		String tempphone = bean.getUserTel();
		if(RegularExpressionUtil.isMobilephone(tempphone)){
			String nick = tempphone.substring(0,3)+"****"+tempphone.substring(7,11);
			tv_phone.setText(nick);
		}
		
		et_nickname.setText(""+bean.getUserNickName());
		et_realname.setText(""+bean.getUserRealName());
		//0 男 1 女属性
		if("0".equals(bean.getUserSex())){
			tv_sex.setText("男");
		}else if("1".equals(bean.getUserSex())){
			tv_sex.setText("女");
		}
		if(!Tools.isEmptyString(bean.getUserBrithy())){
			try {
				 SimpleDateFormat tdateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				 Date tempdate = tdateFormat.parse(bean.getUserBrithy());
				 String tempbirth = dateFormat.format(tempdate);
				 tv_birthday.setText(tempbirth);
			} catch (ParseException e) {
				tv_birthday.setText(bean.getUserBrithy());
				e.printStackTrace();
			}
		}
		tv_ic.setText(""+bean.getChargeCard());
		et_email.setText(""+bean.getUserMail());
		tv_cartype.setText(""+bean.getUserCarTypeName());//车的品牌
		
		et_address.setText(""+bean.getAddress());
		//转换城市区编码对应的名称
		String tempP = "";
		String tempC = "";
		String tempA = "";
		if(!TextUtils.isEmpty(bean.getpCode())){
			// tempP = finalDb.findAllByWhere(ProvinceBean.class, "PROVINCE_ID="+bean.getpCode()).get(0).getPROVINCE_NAME();
			 List<ProvinceBean> tempLP = finalDb.findAllByWhere(ProvinceBean.class, "PROVINCE_ID="+bean.getpCode());
			 if(tempLP.size()>0){
				 tempP = tempLP.get(0).getPROVINCE_NAME();
			 }
		}
		if(!TextUtils.isEmpty(bean.getcCode())){
			//tempC = finalDb.findAllByWhere(CityBean.class, "CITY_ID="+bean.getcCode()).get(0).getCITY_NAME();
			 List<CityBean> tempLC = finalDb.findAllByWhere(CityBean.class, "CITY_ID="+bean.getcCode());
			 if(tempLC.size()>0){
				 tempC = tempLC.get(0).getCITY_NAME();
			 }
		}
		if(!TextUtils.isEmpty(bean.getaCode())){
			//tempA = finalDb.findAllByWhere(AreaBean.class, "AREA_ID="+bean.getaCode()).get(0).getAREA_NAME();
			List<AreaBean> tempLA = finalDb.findAllByWhere(AreaBean.class, "AREA_ID="+bean.getaCode());
			 if(tempLA.size()>0){
				 tempA = tempLA.get(0).getAREA_NAME();
			 }
		}
		tv_code.setText(""+tempP+tempC+tempA);
		/*if(!Tools.isEmptyString(bean.getpCode())&&!Tools.isEmptyString(bean.getcCode()) 
				&&!Tools.isEmptyString(bean.getaCode())){
			String tempP = finalDb.findAllByWhere(ProvinceBean.class, "PROVINCE_ID="+bean.getpCode()).get(0).getPROVINCE_NAME();
			String tempC = finalDb.findAllByWhere(CityBean.class, "CITY_ID="+bean.getcCode()).get(0).getCITY_NAME();
			String tempA = finalDb.findAllByWhere(AreaBean.class, "AREA_ID="+bean.getaCode()).get(0).getAREA_NAME();
			tv_code.setText(""+tempP+tempC+tempA);
		}*/
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.userinfo_detail_back:
			finish();
			break;
		case R.id.userinfo_detail_complete:
			//保存
			if(isNetConnection()){
				 pkUserinfo = PreferencesUtil.getStringPreferences(MyUserInfoActivity.this, "pkUserinfo");
				 usinEmail = et_email.getText().toString();
				 usinFacticityname = et_realname.getText().toString();
				 usinUsername = et_nickname.getText().toString();
				 usinSex = "";
				if("男".equals(tv_sex.getText().toString())){
					usinSex = "0";
				}else if("女".equals(tv_sex.getText().toString())){
					usinSex = "1";
				}
				 usinBirthdate = tv_birthday.getText().toString();
				 usinUseraddress = et_address.getText().toString();
				 
				 if(Tools.isEmptyString(usinUsername)){
					 showToast("昵称不能为空");
					 return ;
				 }
				 
				 if(!Tools.isEmptyString(usinEmail) && !RegularExpressionUtil.checkEmail(usinEmail)){
					 showToast("请输入正确的邮箱地址");
					 return ;
				 }
				 
				//showPD("正在提交信息...");
				NetFile.getInstance(MyUserInfoActivity.this).modifyUserInfo(handler, pkUserinfo,
						"",allMultiFile,usinEmail,usinFacticityname,
						usinUsername,usinSex,usinBirthdate,"", 
						"", "", usinCarinfoId,"","",
						usinUseraddress,pcode,ccode,acode,"");
			}else {
				showToast("网络连接异常，请稍后再试...");
			}
			break;
		case R.id.userinfo_detail_photo:
			//选择头像
			tv_save.setVisibility(View.VISIBLE);
			takePhotoDialog = new TakePhotoDialog(this);
			takePhotoDialog.show();
			TextView takePhoto = (TextView) takePhotoDialog.getTakephoto();
			TextView selectPhoto = (TextView) takePhotoDialog.getSelectphoto();
			TextView canclePhoto = (TextView) takePhotoDialog.getCanclephoto();
			takePhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					//HeadImageUtils.openCameraImage(MyDetailInfo.this);
					    outputImage = new File(Environment
							.getExternalStorageDirectory(), System.currentTimeMillis()+".jpg");
							try {
							if (outputImage.exists()) {
							outputImage.delete();
							}
							outputImage.createNewFile();
							} catch (Exception e) {
							e.printStackTrace();
							}
				
							captureimageUri = Uri.fromFile(outputImage);
							Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
							intent.putExtra(MediaStore.EXTRA_OUTPUT, captureimageUri);
							startActivityForResult(intent, 1111);// 启动相机程序
							takePhotoDialog.dismiss();
				}
			}); 
			selectPhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					takePhotoDialog.dismiss();
					HeadImageUtils.openLocalImage(MyUserInfoActivity.this);
				}
			});
			canclePhoto.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					takePhotoDialog.dismiss();
				}
			});
			break;
		case R.id.userinfo_detail_rl_sex:
			tv_save.setVisibility(View.VISIBLE);
			//选择性别
			new ActionSheetDialog(MyUserInfoActivity.this)
			.builder()
			.setCancelable(true)
			.setCanceledOnTouchOutside(true)
			.addSheetItem("男", SheetItemColor.Blue, new OnSheetItemClickListener() {
				public void onClick(int which) {
					tv_sex.setText("男");
				}
			}).addSheetItem("女", SheetItemColor.Blue, new OnSheetItemClickListener() {
				public void onClick(int which) {
					tv_sex.setText("女");
				}
			}).show();
			
			break;
		case R.id.userinfo_detail_rl_brithday:
			//选择生日
			tv_save.setVisibility(View.VISIBLE);
			LayoutInflater inflater1 = LayoutInflater.from(MyUserInfoActivity.this);
			final View timepickerview1 = inflater1.inflate(R.layout.myuserinfo_ios_timepicker,
					null);
			ScreenInfo screenInfo1 = new ScreenInfo(MyUserInfoActivity.this);
			wheelMain = new WheelMain(timepickerview1);
			wheelMain.screenheight = screenInfo1.getHeight();
			Calendar calendar1 = Calendar.getInstance();
			String time1 = tv_birthday.getText().toString();
			//控件中已填写的时间
			if (JudgeDate.isDate(time1, "yyyy-MM-dd")) {
				try {
					calendar1.setTime(dateFormat.parse(time1));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			} 
			int year1 = calendar1.get(Calendar.YEAR);
			int month1 = calendar1.get(Calendar.MONTH);
			int day1 = calendar1.get(Calendar.DAY_OF_MONTH);
			wheelMain.initDateTimePicker(year1, month1, day1);
			final MyAlertDialog dialog = new MyAlertDialog(MyUserInfoActivity.this).builder()
					.setTitle("请选择出生日期").setView(timepickerview1)
					.setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			dialog.setPositiveButton("保存", new OnClickListener() {
				@Override
				public void onClick(View v) {
					String picktime = wheelMain.getTime();
					Date pickD,nowD;
					try {
						pickD = dateFormat.parse(picktime);
						nowD = new Date();
						if(pickD.compareTo(nowD)>0){
							showToast("生日不能选择未来日期");
						}else {
							tv_birthday.setText(picktime);
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			});
			dialog.show();
			
			break;
		case R.id.userinfo_detail_rl_cartype:
			//选择车的品牌
			tv_save.setVisibility(View.VISIBLE);
			 Intent carbrandIn = new Intent();
			 carbrandIn.setClass(MyUserInfoActivity.this, SelectCarBrandActivity.class);
			 startActivityForResult(carbrandIn, CODE_SELECT_CARTYPE_REQUEST);
			 
			break;
		case R.id.userinfo_detail_rl_code:
			// 所在地区
			tv_save.setVisibility(View.VISIBLE);
			
			View view = dialogm();
			final MyAlertDialog dialog1 = new MyAlertDialog(
					MyUserInfoActivity.this).builder().setTitle("请选择省市区")
					.setView(view)
					.setNegativeButton("取消", new OnClickListener() {
						@Override
						public void onClick(View v) {

						}
					});
			dialog1.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(View v) {
					if(cityList.size()>0 && areaList.size()>0){
						cityTxt = provinceList.get(view_province.getCurrentItem()).getPROVINCE_NAME()
								+ cityList.get(view_city.getCurrentItem()).getCITY_NAME()
								+ areaList.get(view_area.getCurrentItem()).getAREA_NAME();
						pcode = provinceList.get(view_province.getCurrentItem()).getPROVINCE_ID();
						ccode = cityList.get(view_city.getCurrentItem()).getCITY_ID();
						acode = areaList.get(view_area.getCurrentItem()).getAREA_ID();
					}else if(cityList.size()>0 &&  areaList.size()==0){
						cityTxt = provinceList.get(view_province.getCurrentItem()).getPROVINCE_NAME()
								+ cityList.get(view_city.getCurrentItem()).getCITY_NAME();
						pcode = provinceList.get(view_province.getCurrentItem()).getPROVINCE_ID();
						ccode = cityList.get(view_city.getCurrentItem()).getCITY_ID();
						acode = "000000";
					}else {
						cityTxt = provinceList.get(view_province.getCurrentItem()).getPROVINCE_NAME();
						pcode = provinceList.get(view_province.getCurrentItem()).getPROVINCE_ID();
						ccode = "000000";
						acode = "000000";
					}
					tv_code.setText(""+cityTxt);
					
				}
			});
			dialog1.show();
			
			break;
			

		default:
			break;
		}
		
	}

	
	@Override
	protected void getData() {
		
		
	/*	pkUserId = PreferencesUtil.getStringPreferences(getActivity(), "pkUserinfo");
		if(isNetConnection()){
			showPD("正在获取信息，请稍等...");
			GetDataPost.getInstance(getActivity()).getUserInfo(handler, pkUserId);
		}
		*/
	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		cancelPD();
		if(sign.equals(Protocol.MODIFY_USER_INFO)){
			 showToast("资料修改成功");
			 Intent intnet = new Intent(BroadcastUtil.BROADCAST_Modify_UserInfo);
			 sendBroadcast(intnet);
			 finish();
		}else if(sign.equals(Protocol.GET_USER_INFO)){
			userInfoBean = (UserInfoBean) bundle.getSerializable(Protocol.DATA);
			initValueToView(userInfoBean);
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));
		finish();
		
	}
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode,
	            Intent intent) {
	       // showToast("resultCode"+resultCode);
		 
	        if (resultCode == RESULT_CANCELED) {
				return;
			}
			switch (requestCode) {
			
			case 1111:
				
			//if (resultCode == RESULT_OK) {
				
				Intent intenttt = new Intent("com.android.camera.action.CROP");
				intenttt.setDataAndType(captureimageUri, "image/*");
				intenttt.putExtra("scale", true);
				intenttt.putExtra("aspectX",1);
				intenttt.putExtra("aspectY", 1);
				intenttt.putExtra("return-data", false);
				intenttt.putExtra(MediaStore.EXTRA_OUTPUT, captureimageUri);
				startActivityForResult(intenttt, 2222);// 启动裁剪程序
			//	}
				break;
			case 2222:
				try {
					//if (resultCode == RESULT_OK) {
						/*Bitmap bitmap = BitmapFactory
								.decodeStream(getContentResolver().openInputStream(
										captureimageUri));*/
						
						Bitmap bitmap = BitmapFactory.decodeFile(outputImage.getAbsolutePath());
						bitmap = resizeBitmap(bitmap, 320, 320);
						iv_photo.setImageURI(captureimageUri);
						allMultiFile = saveBitmap2file(bitmap);
						outputImage.delete();
						//mydetailinfo_iv_photo.setImageBitmap(bitmap);// 将裁剪后的图片显示出来
					//}
				} catch (Exception e) {
					e.printStackTrace();
				}


				break;
			
			// 拍照获取图片
			case HeadImageUtils.GET_IMAGE_BY_CAMERA:
				// uri传入与否影响图片获取方式,以下二选一
				// 方式一,自定义Uri(ImageUtils.imageUriFromCamera),用于保存拍照后图片地址
				if(HeadImageUtils.imageUriFromCamera != null) {
					// 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
					//iv.setImageURI(ImageUtils.imageUriFromCamera);
					// 对图片进行裁剪
					HeadImageUtils.cropImage(this, HeadImageUtils.imageUriFromCamera);
					break;
				}
				
				break;
			// 手机相册获取图片
			case HeadImageUtils.GET_IMAGE_BY_GALLARY:
				if(intent != null && intent.getData() != null) {
					// 可以直接显示图片,或者进行其他处理(如压缩或裁剪等)
					// iv.setImageURI(data.getData());
					// 对图片进行裁剪
					HeadImageUtils.cropImage(this, intent.getData());
				}
				break;
			// 裁剪图片后结果
			case HeadImageUtils.CROP_IMAGE:
				//showToast("uri"+HeadImageUtils.cropImageUri);
				if(HeadImageUtils.cropImageUri != null) {
					// 可以直接显示图片,或者进行其他处理(如压缩等)
					iv_photo.setImageURI(HeadImageUtils.cropImageUri);
					Bitmap bm = convertUri2Bitmap(HeadImageUtils.cropImageUri);
					//mydetailinfo_iv_photo.setImageBitmap(bm);
					allMultiFile = saveBitmap2file(bm);
					/*if(bm != null){
						bm.recycle();
					}*/
					
				}
				break;
				//选择车型处理
			  case CODE_SELECT_CARTYPE_REQUEST:
		        	tv_save.setVisibility(View.VISIBLE);
		        	if(intent != null){
		        		carType = intent.getStringExtra("carType");
		        		usinCarinfoId = intent.getStringExtra("carTypeId");
		            		tv_cartype.setText(carType+"");
		        	}
		        	break;
			}
			super.onActivityResult(requestCode, resultCode, intent);
	        
	    }

	 /**
		 * 缩放bitmap
		 */
		public Bitmap resizeBitmap(Bitmap bitmap,int width,int height){
			  int bitmapWidth = bitmap.getWidth();  
	          int bitmapHeight = bitmap.getHeight();  
	          // 缩放图片的尺寸  
	          float scaleWidth = (float) width / bitmapWidth;  
	          float scaleHeight = (float) height / bitmapHeight;  
	          Matrix matrix = new Matrix();  
	          matrix.postScale(scaleWidth, scaleHeight);  
	          // 产生缩放后的Bitmap对象  
	          Bitmap resizeBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix, false);  
			
			return resizeBitmap;
		}
		 /**
		 * 将bitmap转换为file
		 * 
		 * @param bmp
		 * @param filename
		 * @return
		 */
		public  String saveBitmap2file(Bitmap bmp) {
			//bmp = resizeBitmap(bmp, 320, 320);
			Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
			String localPath = Environment.getExternalStorageDirectory()
					.getAbsolutePath() + "/eichong/";
			String filename = "eichong.jpg";
			//String filename = System.currentTimeMillis()+".jpg";
			File file = new File(localPath);
			if (!file.exists()) {
				file.mkdirs();
			} 
			File saveFile = new File(localPath + filename);
			int quality = 80;
			FileOutputStream stream = null;
			try {
				stream = new FileOutputStream(saveFile);
				bmp.compress(format, quality, stream);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if(!bmp.isRecycled()){
				bmp.recycle();
			}
			return localPath + filename;
		}
		 private Bitmap convertUri2Bitmap(Uri uri) {
		        InputStream is = null;
		        try {
		            is = getContentResolver().openInputStream(uri);
		            Bitmap bitmap = BitmapFactory.decodeStream(is);
		            is.close();
		            return bitmap;
		        } catch (FileNotFoundException e) {
		            e.printStackTrace();
		            return null;
		        } catch (IOException e) {
		            e.printStackTrace();
		            return null;
		        }
		    }
		 @Override
			public void onFocusChange(View v, boolean hasFocus) {
				 if(hasFocus){//获得焦点  
					 tv_save.setVisibility(View.VISIBLE);
			     }
				
			}
		 
		 
			private View dialogm() {
				View contentView = LayoutInflater.from(this).inflate(
						R.layout.wheelcity_cities_layout, null);
				provinceList = finalDb.findAll(ProvinceBean.class);
		
				//初始化值
				cityList = new ArrayList<CityBean>();
				areaList = new ArrayList<AreaBean>();
				
				//省选择
				 view_province = (WheelView) contentView
						.findViewById(R.id.wheelcity_country);
				view_province.setVisibleItems(3);
				view_province.setViewAdapter(new CountryAdapter(this));
				
				//城市选择
				 view_city = (WheelView) contentView
						.findViewById(R.id.wheelcity_city);
				//view_city.setVisibleItems(0);
 
				// 地区选择
			    view_area = (WheelView) contentView
						.findViewById(R.id.wheelcity_ccity);
				//view_area.setVisibleItems(0);// 不限城市
				provinceBean = provinceList.get(0);
				cityList = finalDb.findAllByWhere(CityBean.class,"PROVINCE_ID ="+provinceBean.getPROVINCE_ID());
				updateCities(view_city, cityList, 0);
				if(cityList.size()>0){
					cityBean = cityList.get(0);
					areaList = finalDb.findAllByWhere(AreaBean.class, "CITY_ID = "+cityBean.getCITY_ID());
					updatearea(view_area, areaList);
				}else {
					areaList.clear();
					updatearea(view_area,areaList);
				}
				
				view_province.addChangingListener(new OnWheelChangedListener() {
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
			
						/*if(isFirstProvince){
							provinceBean = provinceList.get(view_province.getCurrentItem());
							cityList = finalDb.findAllByWhere(CityBean.class,"PROVINCE_ID ="+provinceBean.getPROVINCE_ID());
							updateCities(view_city, cityList, 0);
							view_city.setCurrentItem(0);
						}
						isFirstProvince = false;*/
					}
				});
				
				view_province.addScrollingListener(new OnWheelScrollListener() {
					@Override
					public void onScrollingStarted(WheelView wheel) {
						
					}
					
					@Override
					public void onScrollingFinished(WheelView wheel) {
						
						provinceBean = provinceList.get(view_province.getCurrentItem());
						
						if(provinceBean.equals(currentP)){
							Log.i("cm_socket", "相等");
							return ;
						}
						currentP = provinceBean;
						cityList = finalDb.findAllByWhere(CityBean.class,"PROVINCE_ID ="+provinceBean.getPROVINCE_ID());
						updateCities(view_city, cityList, 0);
						if(cityList.size()>0){
							cityBean = cityList.get(0);
							areaList = finalDb.findAllByWhere(AreaBean.class, "CITY_ID = "+cityBean.getCITY_ID());
							updatearea(view_area, areaList);
						}else {
							areaList.clear();
							updatearea(view_area,areaList);
						}
						
					}
				});
				view_city.addScrollingListener(new OnWheelScrollListener() {
					
					@Override
					public void onScrollingStarted(WheelView wheel) {
					}
					
					@Override
					public void onScrollingFinished(WheelView wheel) {
						cityBean = cityList.get(view_city.getCurrentItem());
						if(cityBean.equals(currentC)){
							Log.i("cm_socket", "相等");
							return ;
						}
						currentC = cityBean;
						
						areaList = finalDb.findAllByWhere(AreaBean.class, "CITY_ID = "+cityBean.getCITY_ID());
						updatearea(view_area, areaList);
					}
				});
				view_city.addChangingListener(new OnWheelChangedListener() {
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
						//areaList = areaAllList.get(view_province.getCurrentItem()).get(view_city.getCurrentItem());
						/*updatearea(view_area, areaList);
						cityTxt = provinceList.get(view_province.getCurrentItem()).getPROVINCE_NAME()
								+ cityList.get(view_city.getCurrentItem()).getCITY_NAME()
								+ areaList.get(view_area.getCurrentItem()).getAREA_NAME();
						pcode = provinceList.get(view_province.getCurrentItem()).getPROVINCE_ID();
						ccode = cityList.get(view_city.getCurrentItem()).getCITY_ID();
						acode = areaList.get(view_area.getCurrentItem()).getAREA_ID();*/
					}
				});

				view_area.addChangingListener(new OnWheelChangedListener() {
					public void onChanged(WheelView wheel, int oldValue, int newValue) {
						/*cityTxt = provinceList.get(view_province.getCurrentItem()).getPROVINCE_NAME()
								+ cityList.get(view_city.getCurrentItem()).getCITY_NAME()
								+ areaList.get(view_area.getCurrentItem()).getAREA_NAME();
						pcode = provinceList.get(view_province.getCurrentItem()).getPROVINCE_ID();
						ccode = cityList.get(view_city.getCurrentItem()).getCITY_ID();
						acode = areaList.get(view_area.getCurrentItem()).getAREA_ID();*/
					}
				});
				
				//初始化值
				view_province.setCurrentItem(0);// 设置北京
				//view_city.setCurrentItem(0);
				//view_area.setCurrentItem(0);

				return contentView;
			}
			
			/**
			 * Updates the city wheel
			 */
			private void updateCities(WheelView city, List<CityBean> cities, int index) {
				if(cities.size()>0){
					CityArrayWheelAdapter adapter = new CityArrayWheelAdapter(this,
							cities);
					adapter.setTextSize(18);
					city.setVisibility(View.VISIBLE);
					city.setViewAdapter(adapter);
					city.setCurrentItem(0);
				}else {
					city.setVisibility(View.GONE);
				}
				
			}
			/**
			 * Updates the area wheel
			 */
			private void updatearea(WheelView city, List<AreaBean> areas) {
				if(areas.size()>0){
					AreaArrayWheelAdapter adapter = new AreaArrayWheelAdapter(this,
							areas);
					adapter.setTextSize(18);
					city.setVisibility(View.VISIBLE);
					city.setViewAdapter(adapter);
					city.setCurrentItem(0);
				}else {
					city.setVisibility(View.GONE);
				}
				
			}
			
			/**
			 * Adapter for countries
			 */
			private class CountryAdapter extends AbstractWheelTextAdapter {
				// Countries names
				//private String countries[] = addressData.provinces;//AddressData.PROVINCES;

				/**
				 * Constructor
				 */
				protected CountryAdapter(Context context) {
					super(context, R.layout.wheelcity_country_layout, NO_RESOURCE);
					setItemTextResource(R.id.wheelcity_country_name);
					
				}

				@Override
				public View getItem(int index, View cachedView, ViewGroup parent) {
					View view = super.getItem(index, cachedView, parent);
					return view;
				}

				@Override
				public int getItemsCount() {
					return provinceList.size();
				}

				@Override
				protected CharSequence getItemText(int index) {
					String temp = provinceList.get(index).getPROVINCE_NAME();
					if(temp.length()>3){
						temp = temp.substring(0, 3)+"..";
					}
					return temp;
				}
			}
			
			

}
