package com.aliamauri.meat.activity.nearby_activity;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.adapter.DragAdapter;
import com.aliamauri.meat.adapter.OtherAdapter;
import com.aliamauri.meat.db.Dbutils;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.DragGrid;
import com.aliamauri.meat.view.OtherGridView;
import com.umeng.analytics.MobclickAgent;

public class SelectChannelTag extends BaseActivity implements
		OnItemClickListener, OnClickListener {
	/** 用户栏目的GRIDVIEW */
	private DragGrid mUserGridView;
	/** 其它栏目的GRIDVIEW */
	private OtherGridView mOtherGridView;
	/** 用户栏目对应的适配器，可以拖动 */
	DragAdapter userAdapter;
	/** 其它栏目对应的适配器 */ 
	OtherAdapter otherAdapter;
	/** 其它栏目列表 */
	ArrayList<String> mOtherChannelList;
	/** 用户栏目列表 */
	ArrayList<String> mUserChannelList;
	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
	boolean mIsMove;
	/** 当前用户是对标签做改动 */
	boolean mIsChange;
	private TextView mBtn_right;
	private HttpHelp mHttp;
	
	@Override
	protected View getRootView() {
		mIsMove = false;
		mIsChange = false;
		mHttp = new HttpHelp();
		return View.inflate(mActivity, R.layout.subscribe_activity, null);
	}

	@Override
	protected void setListener() {
		// 设置GRIDVIEW的ITEM的点击监听
		mOtherGridView.setOnItemClickListener(this);
		mUserGridView.setOnItemClickListener(this);
		$(R.id.iv_title_backicon).setOnClickListener(this);
		mBtn_right.setOnClickListener(this);

	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	protected String getCurrentTitle() {
		return "设置";
	}

	@Override
	protected void initNet() {
		textDatas();
//		mHttp.sendGet(NetworkConfig.getTitle_tag(),SelectChannelBean.class,new MyRequestCallBack<SelectChannelBean>() {
//
//			@Override
//			public void onSucceed(SelectChannelBean bean) {
//				if(bean == null || bean.cont == null || bean.status ==null){
//					UIUtils.showToast(getApplicationContext(), "网络异常");
//					return ;
//				}
//				if("1".equals(bean.status)){
//					for(int i = 0; i<bean.cont.size() ; i++){
//						mOtherChannelList.add(bean.cont.get(i).typename);
//						otherAdapter = new OtherAdapter(SelectChannelTag.this, mOtherChannelList);
//						mOtherGridView.setAdapter(otherAdapter);
//					}
//				}else{
//					UIUtils.showToast(getApplicationContext(), bean.msg);
//					return ;
//				}
//				
//			}
//		});
	}
	private void textDatas(){
		mOtherChannelList.add("推荐");
		mOtherChannelList.add("热点");
		mOtherChannelList.add( "杭州");
		mOtherChannelList.add("时尚");
		mOtherChannelList.add("科技");
		mOtherChannelList.add("体育");
		otherAdapter = new OtherAdapter(SelectChannelTag.this, mOtherChannelList);
		mOtherGridView.setAdapter(otherAdapter);
	}

	@Override
	protected void initView() {
		mOtherChannelList = new ArrayList<>();
		mOtherChannelList.clear();
		mUserChannelList = (ArrayList<String>) Dbutils.splitFileName(PrefUtils.getString(GlobalConstant.CHILD_FRAGMENT_MOVIES,"精选|美女|新闻|综艺|娱乐|游戏"));
		mBtn_right = $(R.id.tv_title_right);
		mBtn_right.setText("确定");
		mBtn_right.setVisibility(View.VISIBLE);
		mUserGridView = (DragGrid) $(R.id.userGridView);
		mOtherGridView = (OtherGridView) $(R.id.otherGridView);
		userAdapter = new DragAdapter(SelectChannelTag.this, mUserChannelList);
		mUserGridView.setAdapter(userAdapter);
	
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

	/** GRIDVIEW对应的ITEM点击监听接口 */
	@Override
	public void onItemClick(AdapterView<?> parent, final View view,
			final int position, long id) {
		// 如果点击的时候，之前动画还没结束，那么就让点击事件无效
		if (mIsMove) {
			return;
		}
		switch (parent.getId()) {
		case R.id.userGridView:
			// position为 0，1，2 的不可以进行任何操作
			if (position != 0 && position != 1 && position != 2) {
				final ImageView moveImageView = getView(view);
				if (moveImageView != null) {
					mIsChange = true;
					TextView newTextView = (TextView) view
							.findViewById(R.id.text_item);
					final int[] startLocation = new int[2];
					newTextView.getLocationInWindow(startLocation);
					final String channel = ((DragAdapter) parent.getAdapter()).getItem(position);// 获取点击的频道内容
					
					otherAdapter.setVisible(false);
					// 添加到最后一个
					otherAdapter.addItem(channel);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {
								int[] endLocation = new int[2];
								// 获取终点的坐标
								mOtherGridView
										.getChildAt(
												mOtherGridView
														.getLastVisiblePosition())
										.getLocationInWindow(endLocation);
								MoveAnim(moveImageView, startLocation,
										endLocation, channel, mUserGridView);
								userAdapter.setRemove(position);
							} catch (Exception localException) {
							}
						}
					}, 50L);
				}
			}
			break;
		case R.id.otherGridView:
			final ImageView moveImageView = getView(view);
			if (moveImageView != null) {
				mIsChange = true;
				TextView newTextView = (TextView) view
						.findViewById(R.id.text_item);
				final int[] startLocation = new int[2];
				newTextView.getLocationInWindow(startLocation);
				final String channel = ((OtherAdapter) parent.getAdapter()).getItem(position);
				userAdapter.setVisible(false);
				userAdapter.addItem(channel);
				// 添加到最后一个
				new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							int[] endLocation = new int[2];
							// 获取终点的坐标
							mUserGridView.getChildAt(
									mUserGridView.getLastVisiblePosition())
									.getLocationInWindow(endLocation);
							MoveAnim(moveImageView, startLocation, endLocation,
									channel, mOtherGridView);
							otherAdapter.setRemove(position);
						} catch (Exception localException) {
						}
					}
				}, 50L);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 点击ITEM移动动画
	 * 
	 * @param moveView
	 * @param startLocation
	 * @param endLocation
	 * @param moveChannel
	 * @param clickGridView
	 */
	private void MoveAnim(View moveView, int[] startLocation,
			int[] endLocation, final String moveChannel,
			final GridView clickGridView) {
		int[] initLocation = new int[2];
		// 获取传递过来的VIEW的坐标
		moveView.getLocationInWindow(initLocation);
		// 得到要移动的VIEW,并放入对应的容器中
		final ViewGroup moveViewGroup = getMoveViewGroup();
		final View mMoveView = getMoveView(moveViewGroup, moveView,
				initLocation);
		// 创建移动动画
		TranslateAnimation moveAnimation = new TranslateAnimation(
				startLocation[0], endLocation[0], startLocation[1],
				endLocation[1]);
		moveAnimation.setDuration(300L);// 动画时间
		// 动画配置
		AnimationSet moveAnimationSet = new AnimationSet(true);
		moveAnimationSet.setFillAfter(false);// 动画效果执行完毕后，View对象不保留在终止的位置
		moveAnimationSet.addAnimation(moveAnimation);
		mMoveView.startAnimation(moveAnimationSet);
		moveAnimationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				mIsMove = true;
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				moveViewGroup.removeView(mMoveView);
				// instanceof 方法判断2边实例是不是一样，判断点击的是DragGrid还是OtherGridView
				if (clickGridView instanceof DragGrid) {
					otherAdapter.setVisible(true);
					otherAdapter.notifyDataSetChanged();
					userAdapter.remove();
				} else {
					userAdapter.setVisible(true);
					userAdapter.notifyDataSetChanged();
					otherAdapter.remove();
				}
				mIsMove = false;
			}
		});
	}

	/**
	 * 获取移动的VIEW，放入对应ViewGroup布局容器
	 * 
	 * @param viewGroup
	 * @param view
	 * @param initLocation
	 * @return
	 */
	private View getMoveView(ViewGroup viewGroup, View view, int[] initLocation) {
		int x = initLocation[0];
		int y = initLocation[1];
		viewGroup.addView(view);
		LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mLayoutParams.leftMargin = x;
		mLayoutParams.topMargin = y;
		view.setLayoutParams(mLayoutParams);
		return view;
	}

	/**
	 * 创建移动的ITEM对应的ViewGroup布局容器
	 */
	private ViewGroup getMoveViewGroup() {
		ViewGroup moveViewGroup = (ViewGroup) getWindow().getDecorView();
		LinearLayout moveLinearLayout = new LinearLayout(this);
		moveLinearLayout.setLayoutParams(new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		moveViewGroup.addView(moveLinearLayout);
		return moveLinearLayout;
	}

	/**
	 * 获取点击的Item的对应View，
	 * 
	 * @param view
	 * @return
	 */
	private ImageView getView(View view) {
		view.destroyDrawingCache();
		view.setDrawingCacheEnabled(true);
		Bitmap cache = Bitmap.createBitmap(view.getDrawingCache());
		view.setDrawingCacheEnabled(false);
		ImageView iv = new ImageView(this);
		iv.setImageBitmap(cache);
		return iv;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			if (mIsChange) {
				commenData();
			} else {
				UIUtils.showToast(getApplicationContext(), "没有修改标签~");
			}
			break;
		case R.id.iv_title_backicon:
			setExitSwichLayout();
		}
	}
	/**
	 * 提交用户保存的标签到服务器上
	 */
	private void commenData() {
		
//		RequestParams params = new RequestParams();
//		params.addBodyParameter("ucode", value)
//		mHttp.sendPost(NetworkConfig.setUserChannel(), params, claz, requestCallBack)
		
	}
	@Override
	protected void onDestroy() {
		if(mHttp !=null){
			mHttp.stopHttpNET();
		}
		super.onDestroy();
	}
	
}
