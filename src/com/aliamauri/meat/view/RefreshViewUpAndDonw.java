package com.aliamauri.meat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.utils.UIUtils;

/**
 * 下拉刷新的ListView
 * 
 * @author Kevin
 * 
 */
public class RefreshViewUpAndDonw extends ListView implements OnScrollListener,
		android.widget.AdapterView.OnItemClickListener {
	private final String TAG = "RefreshViewUpAndDonw";
	private static final int STATE_PULL_REFRESH = 0;// 下拉刷新
	private static final int STATE_RELEASE_REFRESH = 1;// 松开刷新
	private static final int STATE_REFRESHING = 2;// 正在刷新

	private View mHeaderView;
	private int mStartY = -1;// 滑动起点的y坐标
	private int mEndY = 0;
	private int mDy = 0;   //偏移距离
	private int mHeaderViewHeight;

	private int mCurrrentState = STATE_PULL_REFRESH;// 当前状态

	private ImageView mIv_loading_header;
	private ImageView mIv_loading_footer;
	private RotateAnimation animation;

	public RefreshViewUpAndDonw(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);

		initHeaderView();
		initFooterView();
		setOnScrollListener(this);
		animation = new RotateAnimation(359f, 0f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(500);// 设置动画持续时间
		/** 常用方法 */
		animation.setRepeatCount(2000);// 设置重复次数
		animation.setFillAfter(false);// 动画执行完后是否停留在执行完的状态
	}

	public RefreshViewUpAndDonw(Context context, AttributeSet attrs) {
		this(context, attrs, 0);

	}

	public RefreshViewUpAndDonw(Context context) {
		this(context, null);

	}

	/**
	 * 初始化头布局
	 */
	private void initHeaderView() {
		mHeaderView = UIUtils.inflate(R.layout.layout_list_footer);
		mIv_loading_header = (ImageView) mHeaderView
				.findViewById(R.id.iv_loading);
		this.addHeaderView(mHeaderView);
		mHeaderView.setClickable(false);

		mHeaderView.measure(0, 0);
		mHeaderViewHeight = mHeaderView.getMeasuredHeight();
//		mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏头布局
		mHeaderView.setPadding(0, 0, 0, 0);// 显示
	}

	/*
	 * 初始化脚布局
	 */
	private void initFooterView() {

		mFooterView = UIUtils.inflate(R.layout.layout_list_footer);
		mIv_loading_footer = (ImageView) mFooterView
				.findViewById(R.id.iv_loading);
		this.addFooterView(mFooterView);

		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();
		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);// 隐藏
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mStartY = (int) ev.getRawY();
			break;
		case MotionEvent.ACTION_MOVE:
			if (mStartY == -1) {// 确保startY有效
				mStartY = (int) ev.getRawY();
			}

			if (mCurrrentState == STATE_REFRESHING) {// 正在刷新时不做处理
				break;
			}

			int endY = (int) ev.getRawY();
			mDy = (int) ((endY - mStartY) * 0.3);// 移动便宜5量

			if (mDy > 0 && getFirstVisiblePosition() == 0) {// 只有下拉并且当前是第一个item,才允许下拉
				int padding = mDy - mHeaderViewHeight;// 计算padding
				mHeaderView.setPadding(0, padding, 0, 0);// 设置当前padding

				if (padding > 0 && mCurrrentState != STATE_RELEASE_REFRESH) {// 状态改为松开刷新
					mCurrrentState = STATE_RELEASE_REFRESH;
					refreshState();
				} else if (padding < 0 && mCurrrentState != STATE_PULL_REFRESH) {// 改为下拉刷新状态
					mCurrrentState = STATE_PULL_REFRESH;
					refreshState();
				}

				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
			mStartY = -1;// 重置

			if (mCurrrentState == STATE_RELEASE_REFRESH) {
				mCurrrentState = STATE_REFRESHING;// 正在刷新
				mHeaderView.setPadding(0, 0, 0, 0);// 显示
				refreshState();
			} else if (mCurrrentState == STATE_PULL_REFRESH) {
				mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏
			}

			break;

		default:
			break;
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 刷新下拉控件的布局
	 */
	private void refreshState() {
		switch (mCurrrentState) {
		case STATE_PULL_REFRESH:// 下拉刷新

			break;
		case STATE_RELEASE_REFRESH:// 松开刷新

			break;
		case STATE_REFRESHING:// 正在刷新...
			mIv_loading_header.startAnimation(animation);
			isPullRefersh = true;
			if (mListener != null) {
				mListener.onRefresh();
			}
			break;

		default:
			break;
		}
	}



	OnRefreshListener mListener;
	private View mFooterView;
	private int mFooterViewHeight;

	public void setOnRefreshListener(OnRefreshListener listener) {
		mListener = listener;
	}

	public interface OnRefreshListener {
		public void onRefresh();

		public void onLoadMore();// 加载下一页数据
	}

	/**
	 * 收起下拉刷新的控件
	 */
	public void onRefreshComplete() {
		if (isLoadingMore) {// 正在加载更多...
			mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);// 隐藏脚布局
			mIv_loading_footer.clearAnimation();
			isLoadingMore = false;
		} else {
			mCurrrentState = STATE_PULL_REFRESH;
			mHeaderView.setPadding(0, -mHeaderViewHeight, 0, 0);// 隐藏
			mIv_loading_header.clearAnimation();
			isPullRefersh = false;
		}
	}
	/**
	 * 立刻进行请求网路操作
	 */
	public void currentRefersh() {
		mCurrrentState = STATE_REFRESHING;
		mHeaderView.setPadding(0, 0, 0, 0);// 显示
		mIv_loading_header.startAnimation(animation);
		isPullRefersh = true;
		if (mListener != null) {
			mListener.onRefresh();
		}
	}
	private boolean isLoadingMore;
	private boolean isPullRefersh; // 当前是否正在正在下拉刷新状态

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		if (scrollState == SCROLL_STATE_IDLE
				|| scrollState == SCROLL_STATE_FLING) {

			if (getLastVisiblePosition() == getCount() - 1 && !isLoadingMore
					&& !isPullRefersh) {// 滑动到最后
				mFooterView.setPadding(0, 0, 0, 0);// 显示
				setSelection(getCount() - 1);// 改变listview显示位置
				mIv_loading_footer.startAnimation(animation);
				isLoadingMore = true;

				if (mListener != null) {
					mListener.onLoadMore();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	OnItemClickListener mItemClickListener;

	@Override
	public void setOnItemClickListener(android.widget.AdapterView.OnItemClickListener listener) {
		super.setOnItemClickListener(this);
		mItemClickListener = listener;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

		if (mItemClickListener != null && mDy<3) {
			mItemClickListener.onItemClick(parent, view, position- getHeaderViewsCount(), id);
		}
		
	}

}
