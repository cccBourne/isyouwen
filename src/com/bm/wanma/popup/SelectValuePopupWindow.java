package com.bm.wanma.popup;


import com.bm.wanma.R;
import com.bm.wanma.dialog.SelectValueWarningDialog;
import com.bm.wanma.entity.SelectValueBean;
import com.bm.wanma.ui.activity.LoginAndRegisterActivity;
import com.bm.wanma.ui.fragment.MapModeFragment.IClickConfirm;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.utils.ProjectApplication;
import com.bm.wanma.utils.Tools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SelectValuePopupWindow extends PopupWindow implements OnClickListener{
    private View mMenuView;
    private TextView popup_select_confirm;
    private IClickConfirm mcallback;
    private Context mContext;
    private RelativeLayout fast_rl,slow_rl,guo_rl,ou_rl;
    private ImageView fast_iv,slow_iv,guo_iv,ou_iv;
    private TextView fast_tv,slow_tv,guo_tv,ou_tv;
    private RelativeLayout idle_rl,fit_rl;
    private ImageView idle_iv,fit_iv;
    private TextView idle_tv,fit_tv;
    private boolean isFast,isSlow,isGuo,isOu,isFit,isIdle;
    private String myCarType,pkUserId;
    private SelectValueBean valueBean;
    private SelectValueWarningDialog mSelectValueWarningDialog;
    
    
	public SelectValuePopupWindow(Context context) {
		super(context);
		/*try {
			mcallback = (IConfirmBack) context;
		} catch (ClassCastException  e) {
			 throw new ClassCastException(context.toString() + " must implement IConfirmBack");
		}
		*/
		this.mContext = context;
		LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popup_select_value, null);
        initView(mMenuView);
        
        WindowManager wm = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        //int height = wm.getDefaultDisplay().getHeight();//获取屏幕高度
        
        //设置按钮监听
        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
      //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
        	@Override
            public boolean onTouch(View v, MotionEvent event) {
                 
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss(); 
                        //mcallback.OnPopDismiss();
                    }
                }               
              return true;
            }
        });
	}
	
	
	private void initView(View mView){
		popup_select_confirm = (TextView)mView.findViewById(R.id.popup_select_confirm);
        popup_select_confirm.setOnClickListener(this);
        fast_rl = (RelativeLayout)mView.findViewById(R.id.select_fast_ll);
        fast_rl.setOnClickListener(this);
		slow_rl = (RelativeLayout)mView.findViewById(R.id.select_slow_ll);
		slow_rl.setOnClickListener(this);
		guo_rl = (RelativeLayout)mView.findViewById(R.id.select_guo_ll);
		guo_rl.setOnClickListener(this);
		ou_rl = (RelativeLayout)mView.findViewById(R.id.select_ou_ll);
		ou_rl.setOnClickListener(this);
		idle_rl = (RelativeLayout)mView.findViewById(R.id.select_idle_ll);
		idle_rl.setOnClickListener(this);
		fit_rl = (RelativeLayout)mView.findViewById(R.id.select_fit_ll);
		fit_rl.setOnClickListener(this);
		
		fast_tv = (TextView)mView.findViewById(R.id.select_fast_tv);
		slow_tv = (TextView)mView.findViewById(R.id.select_slow_tv);
		guo_tv = (TextView)mView.findViewById(R.id.select_guo_tv);
		ou_tv = (TextView)mView.findViewById(R.id.select_ou_tv);
		idle_tv = (TextView)mView.findViewById(R.id.select_idle_tv);
		fit_tv = (TextView)mView.findViewById(R.id.select_fit_tv);
		
		fast_iv = (ImageView)mView.findViewById(R.id.select_fast_iv);
		slow_iv = (ImageView)mView.findViewById(R.id.select_slow_iv);
		guo_iv = (ImageView)mView.findViewById(R.id.select_guo_iv);
		ou_iv = (ImageView)mView.findViewById(R.id.select_ou_iv);
		idle_iv = (ImageView)mView.findViewById(R.id.select_idle_iv);
		fit_iv = (ImageView)mView.findViewById(R.id.select_fit_iv);
		
		isFast = false;
		isSlow = false;
		isGuo = false;
		isOu = false;
		isFit = false;
		isIdle = false;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.popup_select_confirm:
			valueBean = new SelectValueBean();
			valueBean.setFast(isFast);
			valueBean.setSlow(isSlow);
			valueBean.setGuo(isGuo);
			valueBean.setOu(isOu);
			valueBean.setIdle(isIdle);
			valueBean.setMatch(isFit);
			
			mcallback.OnclickConfirm(valueBean);
			Log.i("fragment", ""+isFast+isSlow+isGuo+isOu+isIdle+isFit);
			ProjectApplication.getInstance().setSelectValueBean(valueBean);
			dismiss(); 
			break;
		case R.id.select_fast_ll:
			if(isFast){
				fast_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				fast_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				fast_iv.setVisibility(View.GONE);
				isFast = false;
				
			}else {
				fast_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_checked));
				fast_tv.setTextColor(mContext.getResources().getColor(R.color.common_orange));
				fast_iv.setVisibility(View.VISIBLE);
				isFast = true;
			}
			
			break;
		case R.id.select_slow_ll:
			if(isSlow){
				slow_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				slow_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				slow_iv.setVisibility(View.GONE);
				isSlow = false;
				
			}else {
				slow_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_checked));
				slow_tv.setTextColor(mContext.getResources().getColor(R.color.common_orange));
				slow_iv.setVisibility(View.VISIBLE);
				isSlow = true;
			}
			
			break;
		case R.id.select_guo_ll:
			/*if(isFit){
				return;
			}*/
			if(isGuo){
				guo_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				guo_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				guo_iv.setVisibility(View.GONE);
				isGuo = false;
				
			}else {
				guo_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_checked));
				guo_tv.setTextColor(mContext.getResources().getColor(R.color.common_orange));
				guo_iv.setVisibility(View.VISIBLE);
				isGuo = true;
				fit_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				fit_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				fit_iv.setVisibility(View.GONE);
				isFit = false;
			}
			
			break;
		case R.id.select_ou_ll:
		/*	if(isFit){
				return;
			}*/
			
			if(isOu){
				ou_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				ou_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				ou_iv.setVisibility(View.GONE);
				isOu = false;
				
			}else {
				ou_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_checked));
				ou_tv.setTextColor(mContext.getResources().getColor(R.color.common_orange));
				ou_iv.setVisibility(View.VISIBLE);
				isOu = true;
				fit_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				fit_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				fit_iv.setVisibility(View.GONE);
				isFit = false;
			}
			
			break;
		case R.id.select_idle_ll:
			
			if(isIdle){
				idle_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				idle_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				idle_iv.setVisibility(View.GONE);
				isIdle = false;
				
			}else {
				idle_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_checked));
				idle_tv.setTextColor(mContext.getResources().getColor(R.color.common_orange));
				idle_iv.setVisibility(View.VISIBLE);
				isIdle = true;
			}
			 
			break;
		case R.id.select_fit_ll:
			pkUserId = PreferencesUtil.getStringPreferences(mContext, "pkUserinfo");
			myCarType = PreferencesUtil.getStringPreferences(mContext, "carType");
			if(Tools.isEmptyString(pkUserId)){
				Intent loginIn = new Intent();
				loginIn.setClass(mContext, LoginAndRegisterActivity.class);
				mContext.startActivity(loginIn);
				return;
			}
			//if("0".equals(myCarType) || "0".equals(myCarName) ||
			if("0".equals(myCarType) || Tools.isEmptyString(myCarType)){
				mSelectValueWarningDialog = new SelectValueWarningDialog(mContext);
				mSelectValueWarningDialog.setCancelable(false);
				mSelectValueWarningDialog.setOnPositiveListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mSelectValueWarningDialog.dismiss();
					}
				});
				mSelectValueWarningDialog.show();
				return;
			}
			
			
			if(isFit){
				fit_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				fit_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				fit_iv.setVisibility(View.GONE);
				isFit = false;
				
			}else {
				fit_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_checked));
				fit_tv.setTextColor(mContext.getResources().getColor(R.color.common_orange));
				fit_iv.setVisibility(View.VISIBLE);
				
				/*fast_ll.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				fast_tv.setTextColor(mContext.getResources().getColor(R.color.cell_gray));
				fast_iv.setBackground(mContext.getResources().getDrawable(R.drawable.btn_select));
				slow_ll.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				slow_tv.setTextColor(mContext.getResources().getColor(R.color.cell_gray));
				slow_iv.setBackground(mContext.getResources().getDrawable(R.drawable.btn_select));*/
				guo_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				guo_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				guo_iv.setVisibility(View.GONE);
				ou_rl.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				ou_tv.setTextColor(mContext.getResources().getColor(R.color.common_gray));
				ou_iv.setVisibility(View.GONE);
				/*idle_ll.setBackground(mContext.getResources().getDrawable(R.drawable.popup_select_shape_uncheck));
				idle_tv.setTextColor(mContext.getResources().getColor(R.color.cell_gray));
				idle_iv.setBackground(mContext.getResources().getDrawable(R.drawable.btn_select));*/
				isFit = true;
				isGuo = false ;
				isOu = false ;
				
			}
			
			break;
			
		default:
			break;
		}
		
	}

	  public void setCallBack(IClickConfirm callBack){  
	        this.mcallback = callBack; 
	        
	    }  
	
	
}
