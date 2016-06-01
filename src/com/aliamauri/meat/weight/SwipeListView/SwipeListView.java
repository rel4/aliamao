package com.aliamauri.meat.weight.SwipeListView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.utils.UIUtils;

public class SwipeListView extends ListView implements OnScrollListener {
	private OnRefreshListener listener; // 监听回调
	private View mFooterView; // 脚布局
	private RotateAnimation animation;
	private int mFooterViewHeight; // 脚布局高度
	private ImageView mIv_loading;
	private Boolean mIsHorizontal;
	private View mPreItemView;
	private View mCurrentItemView;
	private float mFirstX;
	private float mFirstY;
	private int mRightViewWidth;
	private final int mDuration = 100;
	private final int mDurationStep = 10;
	private boolean mIsShown;

	public boolean canMove = false;
	private Model model;
	public enum Model {
		PULL_FROM_START,
		PULL_FROM_END,
		/**
		 * 默认不刷新
		 */
		DISABLED,
		BOTH
	}
	/**
	 * 设置刷新模式
	 */
	public void setRefreshModel(Model model){
		this.model = model;
	}
	public void setCanMove(boolean canMove) {
		this.canMove = canMove;
	}

	public SwipeListView(Context context) {
		this(context, null);
		init();
	}

	public SwipeListView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		init();
	}

	public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.swipelistviewstyle);
		// 获取自定义属性和默认值
		mRightViewWidth = (int) mTypedArray.getDimension(
				R.styleable.swipelistviewstyle_right_width, 200);
		mTypedArray.recycle();
		init();
	}

	/**
	 * 初始化 onMeasure -> onLayout -> onDraw
	 * 
	 */
	private void init() {
		model =Model.BOTH;
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
	 * return true, deliver to listView. return false, deliver to child. if
	 * move, return true
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (canMove) {
			float lastX = ev.getX();
			float lastY = ev.getY();
			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mIsHorizontal = null;
				mFirstX = lastX;
				mFirstY = lastY;
				int motionPosition = pointToPosition((int) mFirstX,
						(int) mFirstY);

				if (motionPosition >= 0) {
					View currentItemView = getChildAt(motionPosition
							- getFirstVisiblePosition());
					mPreItemView = mCurrentItemView;
					mCurrentItemView = currentItemView;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				float dx = lastX - mFirstX;
				float dy = lastY - mFirstY;

				if (Math.abs(dx) >= 5 && Math.abs(dy) >= 5) {
					return true;
				}
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				if (mIsShown
						&& (mPreItemView != mCurrentItemView || isHitCurItemLeft(lastX))) {
					/**
					 * 情况一： 一个Item的右边布局已经显示， 这时候点击任意一个item,
					 * 那么那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView);
				}
				break;
			}

		} else {

		}
		return super.onInterceptTouchEvent(ev);
	}

	private boolean isHitCurItemLeft(float x) {
		return x < getWidth() - mRightViewWidth;
	}

	/**
	 * @param dx
	 * @param dy
	 * @return judge if can judge scroll direction
	 */
	private boolean judgeScrollDirection(float dx, float dy) {
		boolean canJudge = true;
		if (Math.abs(dx) > 30 && Math.abs(dx) > 2 * Math.abs(dy)) {
			mIsHorizontal = true;
		} else if (Math.abs(dy) > 30 && Math.abs(dy) > 2 * Math.abs(dx)) {
			mIsHorizontal = false;
		} else {
			canJudge = false;
		}
		return canJudge;
	}

	/**
	 * return false, can't move any direction. return true, cant't move
	 * vertical, can move horizontal. return super.onTouchEvent(ev), can move
	 * both.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (canMove) {
			float lastX = ev.getX();
			float lastY = ev.getY();

			switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;

			case MotionEvent.ACTION_MOVE:
				float dx = lastX - mFirstX;
				float dy = lastY - mFirstY;
				if (mIsHorizontal == null) {
					if (!judgeScrollDirection(dx, dy)) {
						break;
					}
				}

				if (mIsHorizontal) {
					if (mIsShown && mPreItemView != mCurrentItemView) {
						/**
						 * 情况二： 一个Item的右边布局已经显示，
						 * 这时候左右滑动另外一个item,那个右边布局显示的item隐藏其右边布局
						 * 向左滑动只触发该情况，向右滑动还会触发情况五
						 */
						hiddenRight(mPreItemView);
					}

					if (mIsShown && mPreItemView == mCurrentItemView) {
						dx = dx - mRightViewWidth;
					}
					if (dx < 0 && dx > -mRightViewWidth) {
						mCurrentItemView.scrollTo((int) (-dx), 0);
					}
					return true;
				} else {
					if (mIsShown) {
						/**
						 * 情况三： 一个Item的右边布局已经显示，
						 * 这时候上下滚动ListView,那么那个右边布局显示的item隐藏其右边布局
						 */
						hiddenRight(mPreItemView);
					}
				}

				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
				clearPressedState();
				if (mIsShown) {
					/**
					 * 情况四： 一个Item的右边布局已经显示，
					 * 这时候左右滑动当前一个item,那个右边布局显示的item隐藏其右边布局
					 */
					hiddenRight(mPreItemView);
				}

				if (mIsHorizontal != null && mIsHorizontal) {
					if (mFirstX - lastX > mRightViewWidth / 2) {
						showRight(mCurrentItemView);
					} else {
						/**
						 * 情况五：
						 * <p>
						 * 向右滑动一个item,且滑动的距离超过了右边View的宽度的一半，隐藏之。
						 */
						hiddenRight(mCurrentItemView);
					}
					return true;
				}
				break;
			}
		}
		return super.onTouchEvent(ev);
	}

	private void clearPressedState() {
		if (mCurrentItemView!=null) {
			mCurrentItemView.setPressed(false);
		}
		setPressed(false);
		refreshDrawableState();
	}

	private void showRight(View view) {

		Message msg = new MoveHandler().obtainMessage();
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = mRightViewWidth;
		msg.sendToTarget();

		mIsShown = true;
	}
	public void sethiddenRight(){
		hiddenRight(mCurrentItemView);
	}
	public void hiddenRight(View view) {
		if (mCurrentItemView == null) {
			return;
		}
		Message msg = new MoveHandler().obtainMessage();//
		msg.obj = view;
		msg.arg1 = view.getScrollX();
		msg.arg2 = 0;
		msg.sendToTarget();
		mIsShown = false;
	}

	/**
	 * show or hide right layout animation
	 */
	@SuppressLint("HandlerLeak")
	class MoveHandler extends Handler {
		int stepX = 0;
		int fromX;
		int toX;
		View view;
		private boolean mIsInAnimation = false;

		private void animatioOver() {
			mIsInAnimation = false;
			stepX = 0;
		}

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (stepX == 0) {
				if (mIsInAnimation) {
					return;
				}
				mIsInAnimation = true;
				view = (View) msg.obj;
				fromX = msg.arg1;
				toX = msg.arg2;
				stepX = (int) ((toX - fromX) * mDurationStep * 1.0 / mDuration);
				if (stepX < 0 && stepX > -1) {
					stepX = -1;
				} else if (stepX > 0 && stepX < 1) {
					stepX = 1;
				}
				if (Math.abs(toX - fromX) < 10) {
					view.scrollTo(toX, 0);
					animatioOver();
					return;
				}
			}

			fromX += stepX;
			boolean isLastStep = (stepX > 0 && fromX > toX)
					|| (stepX < 0 && fromX < toX);
			if (isLastStep) {
				fromX = toX;
			}
			view.scrollTo(fromX, 0);
			invalidate();
			if (!isLastStep) {
				this.sendEmptyMessageDelayed(0, mDurationStep);
			} else {
				animatioOver();
			}
		}
	}

	public int getRightViewWidth() {
		return mRightViewWidth;
	}

	public void setRightViewWidth(int mRightViewWidth) {
		this.mRightViewWidth = mRightViewWidth;
	}

	public void deleteItem(View v) {
		hiddenRight(v);
	}

	public void setOnRefreshListener(OnRefreshListener listener) {
		this.listener = listener;
	}

	/**
	 * 刷新结束时调用隐藏脚布局
	 */
	public void onRefreashFinish() {

		mFooterView.setPadding(0, -mFooterViewHeight, 0, 0);
		mIv_loading.clearAnimation();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (model==Model.DISABLED) {
			return ;
		}
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

	/**
	 * 刷新接口,监听
	 * 
	 * @author poplar
	 * 
	 */
	public interface OnRefreshListener {
		void onLoadMore();

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}
}
