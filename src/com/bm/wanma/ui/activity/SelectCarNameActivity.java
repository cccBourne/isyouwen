package com.bm.wanma.ui.activity;


import java.util.ArrayList;

import com.bm.wanma.R;
import com.bm.wanma.entity.CarNameBean;
import com.bm.wanma.entity.CarTypeBean;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.LogUtil;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.ToastUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

/**
 * cm
 * 选择车的类型
 */
public class SelectCarNameActivity extends BaseActivity implements OnClickListener{
	private ImageButton select_brand_ibtn_back;
	private TextView tv_title;
	private ListView mListview;
	private ArrayAdapter<String> madapter;
	private ArrayList<CarNameBean> mCarTypeListBean;
	private ArrayList<String> carTypes;
	private String carBrand,carType,carTypeId;
	private String carbrandname;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_car_type);
		select_brand_ibtn_back = (ImageButton)findViewById(R.id.select_car_name_back);
		select_brand_ibtn_back.setOnClickListener(this);
		tv_title = (TextView) findViewById(R.id.select_car_name_title);
		tv_title.setText(""+carbrandname);
		mListview = (ListView)findViewById(R.id.select_cartype_listview);
		carTypes = new ArrayList<String>();
		
	}

	
	@Override
	protected void getData() {
		carBrand = (String) getIntent().getExtras().get("carbrand");
		carbrandname = (String) getIntent().getExtras().get("carbrandname");
		GetDataPost.getInstance(this).findCar(handler, carBrand);

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if (sign.equals(Protocol.FIND_CAR_INFO)) {
			mCarTypeListBean = (ArrayList<CarNameBean>) bundle.getSerializable(Protocol.DATA);
			for(CarNameBean bean : mCarTypeListBean){
				carTypes.add(bean.getCarinfoStylename());
			}
			madapter = new ArrayAdapter<String>(SelectCarNameActivity.this,R.layout.select_cartype_item, carTypes);
			mListview.setAdapter(madapter);
			mListview.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?>  adapterView, View view,
						int position, long id) {
					 carType = carTypes.get(position);
					 for(CarNameBean bean : mCarTypeListBean){
							if(carType.equals(bean.getCarinfoStylename())){
								carTypeId = bean.getPkCarinfo();
							}
					  }
					 Intent mIntent = new Intent();  
					 mIntent.putExtra("carType", carType);
					 mIntent.putExtra("carTypeId", carTypeId);
					 setResult(RESULT_OK, mIntent);
					 finish();
				}
			});
		}

	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		if(LogUtil.isDebug){
			showToast("错误码"+bundle.getString(Protocol.CODE)+"\n"+bundle.getString(Protocol.MSG));
		}else {
			showToast(bundle.getString(Protocol.MSG));
		}

	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.select_car_name_back:
			finish();
			break;

		}
		
	}

}
