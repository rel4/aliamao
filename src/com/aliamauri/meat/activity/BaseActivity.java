package com.aliamauri.meat.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.tandong.swichlayout.BaseEffects;
import com.tandong.swichlayout.SwichLayoutInterFace;
import com.tandong.swichlayout.SwitchLayout;

public abstract class BaseActivity extends Activity implements
		SwichLayoutInterFace {
	protected View rootView;
	protected Activity mActivity;
	protected boolean isKeyDown = false;
	protected Intent baseIntent;
	protected final static String TYPE = "type";

	public final static int INT_LEFT = 0;
	public final static int INT_CENER = 1;
	public final static int INT_RIGHT = 2;

	public RelativeLayout rl_progress;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		baseIntent = getIntent();
		mActivity = this;
		super.onCreate(savedInstanceState);
		rootView = getRootView();
		setContentView(rootView);
		initBaseView();// 初始化标题栏
		initAnima();
		initView();// 初始化布局
		setListener();
		initNet();// 网络请求
	}

	// 开始网络请求
	public void loading() {
		rl_progress.setVisibility(View.VISIBLE);
	}

	// 结束网络请求
	public void loaded() {
		rl_progress.setVisibility(View.GONE);
	}

	/**
	 * 初始布局
	 * 
	 * @return
	 */
	protected abstract View getRootView();

	/**
	 * 初始化数据
	 */
	protected abstract void initView();

	// 设置点击事件
	protected void setListener() {
	};

	/**
	 *	
	 */
	protected void initAnima() {
		this.AnimaMode = setActivityAnimaMode();
		if (AnimaMode > 14 || AnimaMode < -1) {
			AnimaMode = -1;
		}
		if (AnimaMode != -1) {
			setEnterSwichLayout();
		}

	}

	/**
	 * 设置activity切换动画
	 * 
	 * @return
	 */
	protected int setActivityAnimaMode() {
		return -1;
	}

	/**
	 * 父类初始
	 */
	private void initBaseView() {

		rl_progress = (RelativeLayout) rootView.findViewById(R.id.rl_progress);

		TextView tv_title = (TextView) rootView
				.findViewById(R.id.tv_title_title);
		if (tv_title != null) {
			tv_title.setText(getCurrentTitle());
		} else {
			tv_title = (TextView) rootView.findViewById(R.id.tv_title);
			if (tv_title != null) {
				tv_title.setText(getCurrentTitle());
			}
		}
		View ibtn_left_home_back = rootView
				.findViewById(R.id.iv_title_backicon);
		if (ibtn_left_home_back != null) {
			ibtn_left_home_back.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					setKeyBordClose();
					if (AnimaMode == -1) {
						finish();
					} else {
						setExitSwichLayout();
					}
				}
			});
		} else {
			ibtn_left_home_back = rootView
					.findViewById(R.id.ibtn_left_home_back);
			if (ibtn_left_home_back != null) {
				ibtn_left_home_back.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						setKeyBordClose();
						if (AnimaMode == -1) {
							finish();
						} else {
							setExitSwichLayout();
						}
					}
				});
			}
		}

	}

	/**
	 * 点击按钮后关闭当前页面之前关闭软键盘
	 */
	protected void setKeyBordClose() {

	}

	/**
	 * 标题
	 * 
	 * @return
	 */
	protected String getCurrentTitle() {
		return "标题";
	};

	/**
	 * 网络请求
	 */
	protected void initNet() {
	}

	/**
	 * activity 切换动画
	 */
	private int AnimaMode;

	@Override
	public void setEnterSwichLayout() {
		if (AnimaMode == -1) {
			return;
		}
		switch (AnimaMode) {

		case 0:
			SwitchLayout.get3DRotateFromLeft(this, false, null);
			// 三个参数分别为（Activity/View，是否关闭Activity，特效（可为空））
			break;
		case 1:
			SwitchLayout.getSlideFromBottom(this, false,
					BaseEffects.getMoreSlowEffect());
			break;
		case 2:
			SwitchLayout.getSlideFromTop(this, false,
					BaseEffects.getReScrollEffect());
			break;
		case 3:
			SwitchLayout.getSlideFromLeft(this, false,
					BaseEffects.getLinearInterEffect());
			break;
		case 4:
			// SwitchLayout.getSlideFromRight(this, false,
			// BaseEffects.getLinearInterEffect());
			SwitchLayout.getSlideFromRight(this, false, null);
			break;
		case 5:
			SwitchLayout.getFadingIn(this);
			break;
		case 6:
			SwitchLayout.ScaleBig(this, false, null);
			break;
		case 7:
			SwitchLayout.FlipUpDown(this, false,
					BaseEffects.getQuickToSlowEffect());
			break;
		case 8:
			SwitchLayout.ScaleBigLeftTop(this, false, null);
			break;
		case 9:
			SwitchLayout.getShakeMode(this, false, null);
			break;
		case 10:
			SwitchLayout.RotateLeftCenterIn(this, false, null);
			break;
		case 11:
			SwitchLayout.RotateLeftTopIn(this, false, null);
			break;
		case 12:
			SwitchLayout.RotateCenterIn(this, false, null);
			break;
		case 13:
			SwitchLayout.ScaleToBigHorizontalIn(this, false, null);
			break;
		case 14:
			SwitchLayout.ScaleToBigVerticalIn(this, false, null);
			break;
		default:
			break;
		}

	}

	@Override
	public void setExitSwichLayout() {
		if (AnimaMode == -1) {
			return;
		}
		switch (AnimaMode) {

		case 0:
			SwitchLayout.get3DRotateFromRight(this, true, null);
			break;
		case 1:
			SwitchLayout.getSlideToBottom(this, true,
					BaseEffects.getMoreSlowEffect());
			break;
		case 2:
			SwitchLayout.getSlideToTop(this, true,
					BaseEffects.getReScrollEffect());
			break;
		case 3:
			SwitchLayout.getSlideToLeft(this, true,
					BaseEffects.getLinearInterEffect());
			break;
		case 4:
			SwitchLayout.getSlideToRight(this, true, null);
			break;
		case 5:
			SwitchLayout.getFadingOut(this, true);
			break;
		case 6:
			SwitchLayout.ScaleSmall(this, true, null);
			break;
		case 7:
			SwitchLayout.FlipUpDown(this, true,
					BaseEffects.getQuickToSlowEffect());
			break;
		case 8:
			SwitchLayout.ScaleSmallLeftTop(this, true, null);
			break;
		case 9:
			SwitchLayout.getShakeMode(this, true, null);
			break;
		case 10:
			SwitchLayout.RotateLeftCenterOut(this, true, null);
			break;
		case 11:
			SwitchLayout.RotateLeftTopOut(this, true, null);
			break;
		case 12:
			SwitchLayout.RotateCenterOut(this, true, null);
			break;
		case 13:
			SwitchLayout.ScaleToBigHorizontalOut(this, true, null);
			break;
		case 14:
			SwitchLayout.ScaleToBigVerticalOut(this, true, null);
			break;
		default:
			break;
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {// 按返回键时退出Activity的Activity特效动画

		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0
				&& AnimaMode != -1) {
			if (!isKeyDown) {
				setExitSwichLayout();
				isKeyDown = true;
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}
}
