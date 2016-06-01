package com.aliamauri.meat.top;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.IM.adapter.ChatAllHistoryAdapter;
import com.aliamauri.meat.activity.IM.utils.PreferenceUtils;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.fragment.impl_child.HomeTVListFragment;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.top.adapter.DragAdapter;
import com.aliamauri.meat.top.adapter.OtherAdapter;
import com.aliamauri.meat.top.bean.ChannelItem;
import com.aliamauri.meat.top.bean.ChannelManage;
import com.aliamauri.meat.top.db.SQLHelper;
import com.aliamauri.meat.top.ui.DragGrid;
import com.aliamauri.meat.top.ui.OtherGridView;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.gif.GifFragment;
import com.lidroid.xutils.http.RequestParams;

/**
 * 频道管理
 */
public class ChannelActivity extends BaseActivity implements
		OnItemClickListener {
	public static String TAG = "ChannelActivity";

	/** 用户栏目的GRIDVIEW */
	private DragGrid userGridView;
	/** 其它栏目的GRIDVIEW */
	private OtherGridView otherGridView;
	/** 用户栏目对应的适配器，可以拖动 */
	DragAdapter userAdapter;
	/** 其它栏目对应的适配器 */
	OtherAdapter otherAdapter;
	/** 其它栏目列表 */
	ArrayList<ChannelItem> otherChannelList = new ArrayList<ChannelItem>();
	/** 用户栏目列表 */
	ArrayList<ChannelItem> userChannelList = new ArrayList<ChannelItem>();
	/** 是否在移动，由于这边是动画结束后才进行的数据更替，设置这个限制为了避免操作太频繁造成的数据错乱。 */
	boolean isMove = false;
	private android.app.Fragment newHandFragment;
	private boolean NEWHAND_IN_CHANNEL = false;// 防止没有到第二步，sp数据没有保存而需要重新来。

	private void showView(int type, String text) {
		newHandFragment = new GifFragment(ChannelActivity.this, type,
				R.raw.gif_finger, text);
		getFragmentManager().beginTransaction()
				.add(R.id.root_view, newHandFragment).commit();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.channel);
		initView();
		initData();
	}

	/** 初始化数据 */
	private void initData() {
		userChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(
				getSQLHelper()).getUserChannel());
		otherChannelList = ((ArrayList<ChannelItem>) ChannelManage.getManage(
				getSQLHelper()).getOtherChannel());
		userAdapter = new DragAdapter(this, userChannelList);
		userGridView.setAdapter(userAdapter);
		otherAdapter = new OtherAdapter(this, otherChannelList);
		otherGridView.setAdapter(otherAdapter);
		// 设置GRIDVIEW的ITEM的点击监听
		otherGridView.setOnItemClickListener(this);
		userGridView.setOnItemClickListener(this);
		if (!PrefUtils.getBoolean(GlobalConstant.NOT_NEWHAND_CHANNEL, false)) {
			NEWHAND_IN_CHANNEL = true;
			PrefUtils.setBoolean(GlobalConstant.NOT_NEWHAND_CHANNEL, true);
			if (1 > userChannelList.size() - 1) {
				showView(1, userChannelList.get(0).name);
			} else {
				showView(1, userChannelList.get(1).name);
			}
		}
	}

	private SQLHelper sqlHelper;

	private SQLHelper getSQLHelper() {
		if (sqlHelper == null)
			sqlHelper = new SQLHelper(UIUtils.getContext());
		return sqlHelper;
	}

	/** 初始化布局 */
	private void initView() {
		TextView tv_title_title = (TextView) findViewById(R.id.tv_title_title);
		tv_title_title.setText("设置");
		findViewById(R.id.iv_title_backicon).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onBackPressed();

					}
				});
		userGridView = (DragGrid) findViewById(R.id.userGridView);
		otherGridView = (OtherGridView) findViewById(R.id.otherGridView);
	}

	/** GRIDVIEW对应的ITEM点击监听接口 */
	@Override
	public void onItemClick(AdapterView<?> parent, final View view,
			final int position, long id) {
		// 如果点击的时候，之前动画还没结束，那么就让点击事件无效
		if (isMove) {
			return;
		}
		switch (parent.getId()) {
		case R.id.userGridView:
			// position为 0，1 的不可以进行任何操作
			if (position != 0) {
				final ImageView moveImageView = getView(view);
				if (moveImageView != null) {
					TextView newTextView = (TextView) view
							.findViewById(R.id.text_item);
					final int[] startLocation = new int[2];
					newTextView.getLocationInWindow(startLocation);
					final ChannelItem channel = ((DragAdapter) parent
							.getAdapter()).getItem(position);// 获取点击的频道内容
					otherAdapter.setVisible(false);
					// 添加到最后一个
					otherAdapter.addItem(channel);
					new Handler().postDelayed(new Runnable() {
						public void run() {
							try {
								int[] endLocation = new int[2];
								// 获取终点的坐标
								otherGridView.getChildAt(
										otherGridView.getLastVisiblePosition())
										.getLocationInWindow(endLocation);
								MoveAnim(moveImageView, startLocation,
										endLocation, channel, userGridView);
								userAdapter.setRemove(position);
							} catch (Exception localException) {
							}
						}
					}, 50L);
					if (NEWHAND_IN_CHANNEL) {
						NEWHAND_IN_CHANNEL = false;
						new Handler().postDelayed(new Runnable() {
							public void run() {
								if (2 > otherChannelList.size() - 1) {
									showView(2,
											otherChannelList
													.get(otherChannelList
															.size() - 1).name);
								} else {
									showView(2, otherChannelList.get(2).name);
								}
							}
						}, 1000L);
					}
				}
			}
			break;
		case R.id.otherGridView:
			final ImageView moveImageView = getView(view);
			if (moveImageView != null) {
				TextView newTextView = (TextView) view
						.findViewById(R.id.text_item);
				final int[] startLocation = new int[2];
				newTextView.getLocationInWindow(startLocation);
				final ChannelItem channel = ((OtherAdapter) parent.getAdapter())
						.getItem(position);
				userAdapter.setVisible(false);
				// 添加到最后一个
				userAdapter.addItem(channel);
				new Handler().postDelayed(new Runnable() {
					public void run() {
						try {
							int[] endLocation = new int[2];
							// 获取终点的坐标
							userGridView.getChildAt(
									userGridView.getLastVisiblePosition())
									.getLocationInWindow(endLocation);
							MoveAnim(moveImageView, startLocation, endLocation,
									channel, otherGridView);
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

	public void add(int position) {

		if (position > otherChannelList.size() - 1) {
			position = otherChannelList.size() - 1;
		}
		onItemClick(otherGridView, otherGridView.getChildAt(position),
				position, otherGridView.getItemIdAtPosition(position));
	}

	public void del(int position) {
		if (position == 0) {
			return;
		}
		if (position > userChannelList.size() - 1) {
			position = userChannelList.size() - 1;
		}
		onItemClick(userGridView, userGridView.getChildAt(position), position,
				userGridView.getItemIdAtPosition(position));

	}

	@Override
	protected void onDestroy() {
		if (!PrefUtils.getBoolean(GlobalConstant.NOT_NEWHAND_GOTOTAKEVIDEO,
				false)) {
			PrefUtils
					.setBoolean(GlobalConstant.NOT_NEWHAND_GOTOTAKEVIDEO, true);
			MyApplication.getMainActivity().showView(3);
		}
		super.onDestroy();
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
			int[] endLocation, final ChannelItem moveChannel,
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
				isMove = true;
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
				isMove = false;
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

	/** 退出时候保存选择后数据库的设置 */
	private void saveChannel() {
		ChannelManage.getManage(getSQLHelper()).deleteAllChannel();
		ChannelManage.getManage(getSQLHelper()).saveUserChannel(
				userAdapter.getChannnelLst());
		ChannelManage.getManage(getSQLHelper()).saveOtherChannel(
				otherAdapter.getChannnelLst());
	}

	@Override
	public void onBackPressed() {
		saveChannel();
		if (userAdapter.isListChanged()) {
			HttpHelp httpHelp = new HttpHelp();
			List<ChannelItem> channnelLst = userAdapter.getChannnelLst();
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < channnelLst.size(); i++) {
				int id = channnelLst.get(i).getId();
				if (-1 == id) {
					continue;
				}
				if (i != 1) {
					sb.append("|");

				}
				sb.append(id);

			}
			RequestParams requestParams = new RequestParams();
			requestParams.addBodyParameter("ids", sb.toString());
			httpHelp.sendPost(NetworkConfig.getsetIntrest(), requestParams,
					BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

						@Override
						public void onSucceed(BaseBaen bean) {
							if (bean != null) {
								LogUtil.e(ChannelActivity.this,
										"是否成功： " + "1".equals(bean.status));
							}

						}
					});
			Intent intent = new Intent(getApplicationContext(),
					MainActivity.class);
			setResult(HomeTVListFragment.CHANNELRESULT, intent);
			finish();
			LogUtil.d(TAG, "数据发生改变");
		} else {
			super.onBackPressed();
		}
		// overridePendingTransition(R.anim.slide_in_left,
		// R.anim.slide_out_right);
	}
}
