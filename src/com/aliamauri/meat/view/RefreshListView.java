package com.aliamauri.meat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.utils.UIUtils;

/**
 * 下拉刷新控件, 上拉加更多
 * 
 * @author poplar
 * 
 */
public class RefreshListView extends ListView implements OnScrollListener {

	private OnRefreshListener listener; // 监听回调
	private View mFooterView; // 脚布局
	private int mFooterViewHeight; // 脚布局高度
	private ImageView mIv_loading;
	private RotateAnimation animation;

	public RefreshListView(Context context) {
		super(context);
		init();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	/**
	 * 初始化 onMeasure -> onLayout -> onDraw
	 * 
	 */
	private void init() {
		initFooterView();
		setOnScrollListener(this);
		animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
				0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(500);// 设置动画持续时间
		/** 常用方法 */
		animation.setRepeatCount(2000);// 设置重复次数
		animation.setFillAfter(false);// 动画执行完后是否停留在执行完的状态

	}

	/**
	 * 初始化脚布局
	 */
	private void initFooterView() {
		// mFooterView = View.inflate(getContext(), R.layout.layout_list_footer,
		// null);

		mFooterView = UIUtils.inflate(R.layout.layout_list_footer);
		mIv_loading = (ImageView) mFooterView.findViewById(R.id.iv_loading);

		// 隐藏脚布局
		mFooterView.measure(0, 0);
		mFooterViewHeight = mFooterView.getMeasuredHeight();

		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);

		this.addFooterView(mFooterView);

	}

	/**
	 * 刷新结束时调用隐藏脚布局
	 */
	public void onRefreashFinish() {

		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		mIv_loading.clearAnimation();
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener = listener;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

		// 当状态改变时候调用
		if (scrollState == SCROLL_STATE_IDLE) {
			// 如果当前状态是 闲置状态
			// 到最后一条
			int lastVisiblePosition = getLastVisiblePosition();

			if (lastVisiblePosition == getCount() - 1) {
				// 显示脚布局
				mFooterView.setPadding(0, 0, 0, 0);
				mIv_loading.startAnimation(animation);

				// 让ListView滚动到最下边
				setSelection(getCount());

				if (listener != null) {

					listener.onLoadMore();
				}
			}

		}

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {

	}

	/**
	 * 刷新接口,监听
	 * 
	 * @author poplar
	 * 
	 */
	public interface OnRefreshListener {
		void onLoadMore();

	}

}
