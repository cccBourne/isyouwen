package com.bm.wanma.ui.activity;



import java.util.List;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bm.wanma.R;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.TimeUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class TTT extends BaseActivity implements OnGeocodeSearchListener{
	private GeocodeSearch geocoderSearch ;
	private String lat,lng;
	private LatLonPoint latLonPoint;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_select_value_warnig);
		geocoderSearch = new GeocodeSearch(this); 
		geocoderSearch.setOnGeocodeSearchListener(this); 
		
		Log.i("cm_socket", TimeUtil.minConvertDayHourMin(3342));
		
		lat = PreferencesUtil.getStringPreferences(this, "currentlat");
		lng = PreferencesUtil.getStringPreferences(this, "currentlng");
		latLonPoint = new LatLonPoint(Double.valueOf(lat), Double.valueOf(lng));
		
		//latLonPoint参数表示一个Latlng，第二参数表示范围多少米，GeocodeSearch.AMAP表示是国测局坐标系还是GPS原生坐标系   
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200,GeocodeSearch.AMAP); 
		geocoderSearch.getFromLocationAsyn(query);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void getData() {
		// TODO Auto-generated method stub
		 
	}
 
	@Override
	public void onSuccess(String sign, Bundle bundle) {
		
		
	} 

	@Override
	public void onFaile(String sign, Bundle bundle) {
	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		List<PoiItem> poiItems  = result.getRegeocodeAddress().getPois();
		for (PoiItem poiItem : poiItems) {
			Log.i("cm_socket", poiItem.getTitle());
			
			
		}
	}

	

}
