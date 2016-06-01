package com.aliamauri.meat.activity.nearby_activity;

import java.util.ArrayList;

import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.umeng.analytics.MobclickAgent;

/**
 * 用于显示用户上传的图片的集合图片w
 */

public class ShowDetailsImages extends BaseActivity implements
		OnGestureListener {

	private ArrayList<String> mImgs;
	private int mPosition;

	private GestureDetector detector;
	private ViewFlipper flipper;
	private TextView mTv_show_text;
	private FrameLayout mFl_progress_layout;
	private TextView mTv_progress_text;

	private HttpHelp mHelp;

	@Override
	protected View getRootView() {
		mImgs = baseIntent.getStringArrayListExtra(GlobalConstant.SHOW_DETAILS_IMAGES);
		mPosition = baseIntent.getIntExtra(GlobalConstant.SHOW_IMAGE_POSITION,0);
		View view = View.inflate(mActivity, R.layout.details_images_layout,null);
		flipper = $(view, R.id.vf_show_all_images);
		mTv_show_text = $(view, R.id.tv_show_text);
		mFl_progress_layout = $(view, R.id.fl_progress_layout);
		mTv_progress_text = $(view, R.id.tv_progress_text);
		return view;
	}
	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	
	@Override
	protected void initView() {
		mHelp = new HttpHelp();
		detector = new GestureDetector(getApplicationContext(), this);
		for(int i=0; i<mImgs.size();i++){
			ImageView iv = new ImageView(this);
			flipper.addView(iv,i);
		}
		setIndicator(mPosition);
		ImageView iv = (ImageView)(flipper.getChildAt(mPosition));
		iv.setTag("yes");
		mHelp.showImage_album(iv, mImgs.get(mPosition),mFl_progress_layout,mTv_progress_text);
		flipper.setDisplayedChild(mPosition);
		
	}

	/**
	 * 根据输入的位置啊显示该位置额view
	 * 
	 * @param mPosition2
	 */
	private void setIndicator(int p) {
		int position = p+1 ;
		mTv_show_text.setText(String.valueOf(position) + "\\"+ String.valueOf(mImgs.size()));
	}

	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return this.detector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {

		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		finish();
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {

	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if (flipper.getChildCount() != 1 && e1.getX() - e2.getX() > 120) {
			mPosition++;
			if (mPosition >= 0 && mPosition < mImgs.size()) {
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_out));
				if("yes".equals(this.flipper.getChildAt(mPosition).getTag())){
					this.flipper.setDisplayedChild(mPosition);
					
				}else{
					ImageView iv = (ImageView)(flipper.getChildAt(mPosition));
					iv.setTag("yes");
					mHelp.showImage_album(iv, mImgs.get(mPosition),mFl_progress_layout,mTv_progress_text);
					this.flipper.setDisplayedChild(mPosition);
				}
				setIndicator(mPosition);
				return true;
			}else{
				mPosition = mImgs.size()-1;
			}

		} else if (flipper.getChildCount() != 1 && e1.getX() - e2.getX() < -120) {
			mPosition--;
			if (mPosition >=0 && mPosition < mImgs.size()) {
				this.flipper.setInAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_right_in));
				this.flipper.setOutAnimation(AnimationUtils.loadAnimation(this,
						R.anim.push_right_out));
				
				if("yes".equals(this.flipper.getChildAt(mPosition).getTag())){
					this.flipper.setDisplayedChild(mPosition);
					
				}else{
					ImageView iv = (ImageView)(flipper.getChildAt(mPosition));
					iv.setTag("yes");
					mHelp.showImage_album(iv, mImgs.get(mPosition),mFl_progress_layout,mTv_progress_text);
					this.flipper.setDisplayedChild(mPosition);
				}
				setIndicator(mPosition);
				return true;
			}else{
				mPosition = 0;
			}
		}
		return false;
	}

	@Override
	protected void setListener() {
		// TODO Auto-generated method stub

	}

}
