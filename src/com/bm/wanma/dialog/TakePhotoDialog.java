package com.bm.wanma.dialog;

import com.bm.wanma.R;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
/**
 * @Function: 选择拍照或图库
 * @author cm
 */
public class TakePhotoDialog extends Dialog {
   private TextView takephoto,selectphoto,canclephoto;
 
    public TakePhotoDialog(Context context) {
        super(context,R.style.ChargePayDialog);
        setCustomDialog();
    }
 
    private void setCustomDialog() {
        View mView = LayoutInflater.from(getContext()).inflate(R.layout.take_photo_dialog, null);
        super.setContentView(mView);
        takephoto = (TextView) mView.findViewById(R.id.takephoto);
        selectphoto = (TextView) mView.findViewById(R.id.selectphoto);
        canclephoto = (TextView) mView.findViewById(R.id.canclephoto);
        mView.setBackgroundResource(R.drawable.common_shape_dialog);
    }
     
    public View getTakephoto(){
        return takephoto;
    }
    public View getSelectphoto(){
        return selectphoto;
    }
    public View getCanclephoto(){
        return canclephoto;
    }
     
     @Override
    public void setContentView(int layoutResID) {
    }
 
    @Override
    public void setContentView(View view, LayoutParams params) {
    }
 
    @Override
    public void setContentView(View view) {
    }
 
   
}