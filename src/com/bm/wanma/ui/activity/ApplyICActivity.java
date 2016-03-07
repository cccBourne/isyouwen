package com.bm.wanma.ui.activity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.tsz.afinal.FinalDb;

import com.bm.wanma.R;
import com.bm.wanma.dialog.ActionSheetDialog;
import com.bm.wanma.dialog.MyAlertDialog;
import com.bm.wanma.dialog.ActionSheetDialog.OnSheetItemClickListener;
import com.bm.wanma.dialog.ActionSheetDialog.SheetItemColor;
import com.bm.wanma.entity.AreaBean;
import com.bm.wanma.entity.CityBean;
import com.bm.wanma.entity.ProvinceBean;
import com.bm.wanma.entity.UserInfoBean;
import com.bm.wanma.net.NetFile;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.RegularExpressionUtil;
import com.bm.wanma.utils.Tools;
import com.bm.wanma.view.ContainsEmojiEditText;
import com.bm.wanma.view.wheelcity.OnWheelScrollListener;
import com.bm.wanma.view.wheelcity.WheelView;
import com.bm.wanma.view.wheelcity.adapters.AbstractWheelTextAdapter;
import com.bm.wanma.view.wheelcity.adapters.AreaArrayWheelAdapter;
import com.bm.wanma.view.wheelcity.adapters.CityArrayWheelAdapter;
import com.bm.wanma.view.wheelview.JudgeDate;
import com.bm.wanma.view.wheelview.ScreenInfo;
import com.bm.wanma.view.wheelview.WheelMain;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
 * 申请充点卡
 */
public class ApplyICActivity extends BaseActivity implements OnClickListener,OnFocusChangeListener{
	
	private ImageButton ib_back;
	private TextView tv_phone,tv_sex,tv_birthday,tv_cartype,tv_code,tv_commit;
	private ContainsEmojiEditText et_realname,et_email,et_address,et_carnum;
	private ContainsEmojiEditText et_icnum,et_chepai; 
	private RelativeLayout rl_sex,rl_birthday,rl_cartype,rl_code;
	
	private UserInfoBean userInfoBean;
	// 选择时间
	private WheelMain wheelMain;
	private FinalDb finalDb;
	private List<ProvinceBean> provinceList;
	private List<CityBean> cityList;
	private List<AreaBean> areaList;
	private ProvinceBean provinceBean,currentP;
	private CityBean cityBean,currentC;
	private WheelView view_province;
	private WheelView view_city ;
	private WheelView view_area;
	private static final int CODE_SELECT_CARTYPE_REQUEST = 0xa4;
	private String pcode, ccode, acode, cityTxt, usinCarinfoId;
	private String pkUserinfo, usinEmail, usinFacticityname,usinSex,carnum;
	private String usinBirthdate, usinUseraddress,carType;
	private String icnum,chepai;
	
	@SuppressLint("SimpleDateFormat")
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apply_ic);
		finalDb = FinalDb.create(getActivity(),Protocol.DATABASE_NAME,false,Protocol.dbNumer,null);
		userInfoBean = (UserInfoBean) getIntent().getSerializableExtra("userInfo");
		if(userInfoBean != null){
			initView();
		}else {
			finish();
		}
		
	}

	private void initView() {
		ib_back = (ImageButton) findViewById(R.id.activity_apply_ic_back);
		ib_back.setOnClickListener(this);
		tv_phone = (TextView) findViewById(R.id.apply_ic_phone);
		et_realname = (ContainsEmojiEditText) findViewById(R.id.apply_ic_et_realname);
		et_realname.setOnFocusChangeListener(this);
		rl_sex = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_sex);
		rl_sex.setOnClickListener(this);
		tv_sex = (TextView) findViewById(R.id.apply_ic_tv_sex);
		rl_birthday = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_brithday);
		rl_birthday.setOnClickListener(this);
		tv_birthday = (TextView) findViewById(R.id.apply_ic_tv_brithday);
		et_email = (ContainsEmojiEditText) findViewById(R.id.apply_ic_et_email);
		et_email.setOnFocusChangeListener(this);
		rl_cartype = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_cartype);
		rl_cartype.setOnClickListener(this);
		tv_cartype = (TextView) findViewById(R.id.userinfo_detail_tv_cartype);
		rl_code = (RelativeLayout) findViewById(R.id.userinfo_detail_rl_code);
		rl_code.setOnClickListener(this);
		tv_code = (TextView) findViewById(R.id.userinfo_detail_tv_code);
		et_address = (ContainsEmojiEditText) findViewById(R.id.userinfo_detail_et_address);
		et_address.setOnFocusChangeListener(this);
		et_carnum = (ContainsEmojiEditText) findViewById(R.id.apply_ic_et_carnum);
		et_carnum.setOnFocusChangeListener(this);
		et_icnum = (ContainsEmojiEditText) findViewById(R.id.apply_ic_et_icnum);
		et_icnum.setOnFocusChangeListener(this);
		et_chepai = (ContainsEmojiEditText) findViewById(R.id.apply_ic_et_chepai);
		et_chepai.setOnFocusChangeListener(this);
		tv_commit = (TextView) findViewById(R.id.apply_ic_commit);
		tv_commit.setOnClickListener(this);
		initValueToView(userInfoBean);
	}
	private void initValueToView(UserInfoBean bean){
		
		tv_phone.setText(""+bean.getUserTel());
		et_realname.setText(""+bean.getUserRealName());
		//0 男 1 女属性
		if("0".equals(bean.getUserSex())){
			tv_sex.setText("男");
		}else if("1".equals(bean.getUserSex())){
			tv_sex.setText("女");
		}
		if(!Tools.isEmptyString(bean.getUserBrithy())){
			//2015-06-11   
			//String tempbirth = Tools.parseDate(bean.getUserBrithy(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd");
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
		et_email.setText(""+bean.getUserMail());
		tv_cartype.setText(""+bean.getUserCarTypeName());//车的品牌
		et_address.setText(""+bean.getAddress());
		usinCarinfoId = bean.getUserCarType();
		//转换城市区编码对应的名称
		String tempP = "";
		String tempC = "";
		String tempA = "";
		if(!TextUtils.isEmpty(bean.getpCode())){
			 List<ProvinceBean> tempLP = finalDb.findAllByWhere(ProvinceBean.class, "PROVINCE_ID="+bean.getpCode());
			 if(tempLP.size()>0){
				 tempP = tempLP.get(0).getPROVINCE_NAME();
			 }
		}
		if(!TextUtils.isEmpty(bean.getcCode())){
			 List<CityBean> tempLC = finalDb.findAllByWhere(CityBean.class, "CITY_ID="+bean.getcCode());
			 if(tempLC.size()>0){
				 tempC = tempLC.get(0).getCITY_NAME();
			 }
		}
		if(!TextUtils.isEmpty(bean.getaCode())){
			List<AreaBean> tempLA = finalDb.findAllByWhere(AreaBean.class, "AREA_ID="+bean.getaCode());
			 if(tempLA.size()>0){
				 tempA = tempLA.get(0).getAREA_NAME();
			 }
		}
		tv_code.setText(""+tempP+tempC+tempA);
		
		
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.activity_apply_ic_back:
			finish();
			break;
		case R.id.apply_ic_commit:
			//提交申请
			if(isNetConnection()){
				 pkUserinfo = PreferencesUtil.getStringPreferences(ApplyICActivity.this, "pkUserinfo");
				 usinEmail = et_email.getText().toString();
				 usinFacticityname = et_realname.getText().toString();
				 usinSex = "";
				if("男".equals(tv_sex.getText().toString())){
					usinSex = "0";
				}else if("女".equals(tv_sex.getText().toString())){
					usinSex = "1";
				}
				 usinBirthdate = tv_birthday.getText().toString();
				 usinUseraddress = et_address.getText().toString();
				 icnum = et_icnum.getText().toString();//ic卡号
				 carnum =  et_carnum.getText().toString();//车架号
				 chepai = et_chepai.getText().toString();
				
				 if(Tools.isEmptyString(usinFacticityname)){
					 showToast("请填写真实姓名");
					 return;
				 }
				 if(Tools.isEmptyString(usinBirthdate)){
					 showToast("请填写出生日期");
					 return;
				 }
				 if(!Tools.isEmptyString(usinEmail) && !RegularExpressionUtil.checkEmail(usinEmail)){
					 showToast("请输入正确的邮箱地址");
					 return ;
				 }
				 
				 if(Tools.isEmptyString(carnum)){
					 showToast("请填写车架号");
					 return;
				 }
				 if(Tools.isEmptyString(tv_cartype.getText().toString().trim())){
					 showToast("请填写我的爱车");
					 return;
				 }
				 if(Tools.isEmptyString(tv_code.getText().toString()) || Tools.isEmptyString(usinUseraddress)){
					 showToast("请填写邮寄地址");
					 return;
				 }
				showPD("正在提交申请信息...");
				NetFile.getInstance(ApplyICActivity.this).modifyUserInfo(handler, pkUserinfo,
						"","",usinEmail,usinFacticityname,
						"",usinSex,usinBirthdate,"", 
						icnum, "", usinCarinfoId,carnum,chepai,
						usinUseraddress,pcode,ccode,acode,"1");
			}else {
				showToast("网络连接异常，请稍后再试...");
			}
			break;
		case R.id.userinfo_detail_rl_sex:
			//选择性别
			new ActionSheetDialog(ApplyICActivity.this)
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
			LayoutInflater inflater1 = LayoutInflater.from(ApplyICActivity.this);
			final View timepickerview1 = inflater1.inflate(R.layout.myuserinfo_ios_timepicker,
					null);
			ScreenInfo screenInfo1 = new ScreenInfo(ApplyICActivity.this);
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
			final MyAlertDialog dialog = new MyAlertDialog(ApplyICActivity.this).builder()
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
			 Intent carbrandIn = new Intent();
			 carbrandIn.setClass(ApplyICActivity.this, SelectCarBrandActivity.class);
			 startActivityForResult(carbrandIn, CODE_SELECT_CARTYPE_REQUEST);
			 
			break;
		case R.id.userinfo_detail_rl_code:
			// 所在地区
			View view = dialogm();
			final MyAlertDialog dialog1 = new MyAlertDialog(
					ApplyICActivity.this).builder().setTitle("请选择省市区")
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
		
	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
			cancelPD();
			showToast("提交申请成功");
			finish();

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		cancelPD();
		showToast(bundle.getString(Protocol.MSG));

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// TODO Auto-generated method stub
		
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_CANCELED) {
			return;
		}
		switch (requestCode) {
	 case CODE_SELECT_CARTYPE_REQUEST:
     	if(data != null){
     		carType = data.getStringExtra("carType");
     		usinCarinfoId = data.getStringExtra("carTypeId");
         		tv_cartype.setText(carType+"");
     	}
     	break;
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
		
		
		view_province.addScrollingListener(new OnWheelScrollListener() {
			@Override
			public void onScrollingStarted(WheelView wheel) {
				
			}
			
			@Override
			public void onScrollingFinished(WheelView wheel) {
				
				provinceBean = provinceList.get(view_province.getCurrentItem());
				
				if(provinceBean.equals(currentP)){
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
					return ;
				}
				currentC = cityBean;
				areaList = finalDb.findAllByWhere(AreaBean.class, "CITY_ID = "+cityBean.getCITY_ID());
				updatearea(view_area, areaList);
			}
		});
		
		//初始化值
		view_province.setCurrentItem(0);// 设置北京

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
