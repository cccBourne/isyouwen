package com.bm.wanma.ui.activity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bm.wanma.R;
import com.bm.wanma.adapter.CommonViewPagerAdapter;
import com.bm.wanma.entity.CityUpdateTimeBean;
import com.bm.wanma.entity.MapModePileBean;
import com.bm.wanma.entity.MapModeStationBean;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.DatabaseHelper;
import com.bm.wanma.utils.GetResourceUtil;
import com.bm.wanma.utils.IntentUtil;
import com.viewpagerindicator.CirclePageIndicator;


import java.util.ArrayList;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DbUpdateListener;
public class GuideActivity extends BaseActivity implements DbUpdateListener {
   private  CommonViewPagerAdapter mAdapter;
   private  ViewPager mPager;
   private  CirclePageIndicator mIndicator;
   private FinalDb finalDb;
   private boolean isUpgrate = false;
    protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_guide);
		mPager = (ViewPager)findViewById(R.id.activity_guide_vp);
		mIndicator = (CirclePageIndicator)findViewById(R.id.activity_guide_indicator);
		finalDb = FinalDb.create(this,Protocol.DATABASE_NAME,false,Protocol.dbNumer,this);
		initView();
		
		if(!isUpgrate){
			new Thread(new Runnable() {
				@Override
				public void run() {
					finalDb.creatTable(GuideActivity.this,"t_m_area");
				}
			}).start();
		}else {
			new Thread(new Runnable() {
				@Override
				public void run() {
					/*finalDb.dropTable(CityUpdateTimeBean.class);
					finalDb.dropTable(MapModePileBean.class);
					finalDb.dropTable(MapModeStationBean.class);*/
					finalDb.dropDb();
					finalDb.creatTable(GuideActivity.this,"t_m_area");
				}
			}).start();
			
		}
    }

    @Override
    protected void getData() {

    }

    @SuppressWarnings("deprecation")
	protected void initView() {
        ArrayList<View> list = new ArrayList<View>();
        for(int i=0 ; i<4 ; i++){
            View view = getLayoutInflater().inflate(R.layout.activity_guide_item, null);
            ImageView tempIv = (ImageView) view.findViewById(R.id.guide_item_iv);
            switch (i){
                case 0:
                    tempIv.setBackgroundDrawable(GetResourceUtil.getDrawable(R.drawable.guide_one));
                    break;
                case 1:
                    tempIv.setBackgroundDrawable(GetResourceUtil.getDrawable(R.drawable.guide_two));
                    break;
                case 2:
                    tempIv.setBackgroundDrawable(GetResourceUtil.getDrawable(R.drawable.guide_three));
                    break;
                case 3:
                    tempIv.setBackgroundDrawable(GetResourceUtil.getDrawable(R.drawable.guide_four));
                    break;
            }
            if(i == 3){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        IntentUtil.startIntent(GuideActivity.this, HomeActivity.class);//方便调试，先放开点击
                        finish();
                    }
                });
            }
       /*     tempTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentUtil.startIntent(GuideActivity.this, HomeActivity.class);
                    finish();
                }
            });*/ 
            
            list.add(view);
        }
        mAdapter = new CommonViewPagerAdapter(list);
        mPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mPager);
    }

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		isUpgrate = true;
		
	}
}
