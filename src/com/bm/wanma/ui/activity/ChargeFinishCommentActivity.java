package com.bm.wanma.ui.activity;

import com.bm.wanma.R;
import com.bm.wanma.dialog.CommentSuccessDialog;
import com.bm.wanma.net.GetDataPost;
import com.bm.wanma.net.Protocol;
import com.bm.wanma.utils.PreferencesUtil;
import com.bm.wanma.view.ContainsEmojiEditText;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @author cm
 * 充电完成评价
 */
public class ChargeFinishCommentActivity extends BaseActivity implements OnClickListener{
	private ImageButton ib_back;
	private RatingBar ratingBar;
	private ContainsEmojiEditText et_content;
	private TextView tv_commit;
	private String content,epId,nickName;
	private float rate;
	private CommentSuccessDialog mdDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_charge_finish_comment);
		initView();
		epId = getIntent().getStringExtra("epId");
		nickName = PreferencesUtil.getStringPreferences(this,"nickName");
	}
	private void initView(){
		ib_back = (ImageButton)findViewById(R.id.charge_finish_comment_back);
		ib_back.setOnClickListener(this);
		ratingBar = (RatingBar) findViewById(R.id.charge_finish_comment_ratingbar);
		et_content = (ContainsEmojiEditText) findViewById(R.id.charge_finish_comment_content);
		et_content.addTextChangedListener(new myWatcher());
		tv_commit = (TextView) findViewById(R.id.charge_finish_comment_commit);
		//tv_commit.setOnClickListener(this);
		
	}
	
	//setResult(RESULT_OK, mIntent);
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.charge_finish_comment_back:
			finish();
			break;
		case R.id.charge_finish_comment_commit:
			//提交评分
			rate = ratingBar.getRating();
			/*Log.i("cm_socket", "rate"+String.valueOf(rate));
		    DecimalFormat df = new DecimalFormat("0.0");  
		    String newrate = df.format(rate);  
			Log.i("cm_socket", "newrate"+newrate);*/
			//if(rate>0){
			content = et_content.getText().toString();
			String uId = PreferencesUtil.getStringPreferences(ChargeFinishCommentActivity.this, "pkUserinfo");
	
			GetDataPost.getInstance(ChargeFinishCommentActivity.this)
					.commitPileStar(handler, epId, uId, nickName,
							String.valueOf(rate));

			GetDataPost.getInstance(ChargeFinishCommentActivity.this)
					.commitPileComment(handler, epId, uId, "0", nickName,
							content);
			
			break;
		default:
			break;
		}
		
	}
	
	private class myWatcher implements TextWatcher{

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			 
		}

		@SuppressLint("NewApi")
		@Override
		public void afterTextChanged(Editable s) {
			String contents = s.toString().trim();
			if(!TextUtils.isEmpty(contents)){
				tv_commit.setOnClickListener(ChargeFinishCommentActivity.this);
				tv_commit.setBackground(getResources().getDrawable(R.drawable.popup_select_shape_confirm));
			}else {
				tv_commit.setOnClickListener(null);
				tv_commit.setBackground(getResources().getDrawable(R.drawable.bespoke_detail_led_light_gray));
			}
			
		}
	}
	

	@Override
	protected void getData() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(String sign, Bundle bundle) {
		if(mdDialog == null ){
			mdDialog = new CommentSuccessDialog(this);
			mdDialog.setCancelable(false);
			mdDialog.setOnPositiveListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					setResult(RESULT_OK);
					finish();
					mdDialog.dismiss();
				}
			});
			mdDialog.show();
		}
		
	}

	@Override
	public void onFaile(String sign, Bundle bundle) {
		showToast(bundle.getString(Protocol.MSG));

	}
	

}
