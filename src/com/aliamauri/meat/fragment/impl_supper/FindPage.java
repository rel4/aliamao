package com.aliamauri.meat.fragment.impl_supper;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.SpecialActivity;
import com.aliamauri.meat.activity.find_activity.HdActivity;
import com.aliamauri.meat.activity.find_activity.HljyActivity;
import com.aliamauri.meat.activity.find_activity.NmqzActivity;
import com.aliamauri.meat.activity.find_activity.TjyyActivity;
import com.aliamauri.meat.activity.search_activity.RecommendMenuDetailActivity;
import com.aliamauri.meat.fragment.BaseFragment;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 首页——发现 第一级 mActivity 可直接获取Activity
 * 
 * @author limaokeji-windosc
 * 
 */
public class FindPage extends BaseFragment implements OnClickListener {
	private RelativeLayout mRl_find_pager_hd; // 活动选项
	private RelativeLayout mRl_find_pager_hljy; // 婚恋交友选项
	private RelativeLayout mRl_find_pager_nmqz; // 匿名圈子选项
	private RelativeLayout mRl_find_pager_tjyy; // 推荐应用选项
	private RelativeLayout rl_find_pager_recommend;

	private RelativeLayout rl_find_special;
	private RelativeLayout rl_find_weirdo;
	private RelativeLayout rl_find_reward;
	private RelativeLayout rl_find_recruit;
	private RelativeLayout rl_find_indiana;
	private RelativeLayout rl_find_redinvelope;
	private RelativeLayout rl_find_task;

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("FindPage");

	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("FindPage");
	}

	@Override
	public void initDate() {
		View view = View.inflate(mActivity, R.layout.fragment_find, null);
		init(view);
		setOnclick();
	}

	/**
	 * 设置点击事件
	 */
	private void setOnclick() {
		mRl_find_pager_hd.setOnClickListener(this);
		mRl_find_pager_hljy.setOnClickListener(this);
		mRl_find_pager_nmqz.setOnClickListener(this);
		mRl_find_pager_tjyy.setOnClickListener(this);
		rl_find_pager_recommend.setOnClickListener(this);
		rl_find_special.setOnClickListener(this);
		rl_find_weirdo.setOnClickListener(this);
		rl_find_reward.setOnClickListener(this);
		rl_find_recruit.setOnClickListener(this);
		rl_find_indiana.setOnClickListener(this);
		rl_find_redinvelope.setOnClickListener(this);
		rl_find_task.setOnClickListener(this);
	}

	/**
	 * 初始化view控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		mRl_find_pager_hd = $(view, R.id.rl_find_pager_hd);
		mRl_find_pager_hljy = $(view, R.id.rl_find_pager_hljy);
		mRl_find_pager_nmqz = $(view, R.id.rl_find_pager_nmqz);
		mRl_find_pager_tjyy = $(view, R.id.rl_find_pager_tjyy);
		rl_find_pager_recommend = $(view, R.id.rl_find_pager_recommend);
		rl_find_special = $(view, R.id.rl_find_special);
		rl_find_weirdo = $(view, R.id.rl_find_weirdo);
		rl_find_reward = $(view, R.id.rl_find_reward);
		rl_find_recruit = $(view, R.id.rl_find_recruit);
		rl_find_indiana = $(view, R.id.rl_find_indiana);
		rl_find_redinvelope = $(view, R.id.rl_find_redinvelope);
		rl_find_task = $(view, R.id.rl_find_task);
		// mRl_find_pager_tjyy.setVisibility(View.GONE);
		// mRl_find_pager_hljy.setVisibility(View.GONE);
		// rl_find_pager_recommend.setVisibility(View.GONE);

		mFl_fragment_base_content.removeAllViews();
		mFl_fragment_base_content.addView(view);
	}

	@Override
	public int getchildFragmentTag() {
		return GlobalConstant.FIND_PAGE;
	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public static <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_find_pager_recommend:
			startActivity(new Intent(mActivity,
					RecommendMenuDetailActivity.class));
			break;
		case R.id.rl_find_pager_hd: // 进入活动界面
			startActivity(new Intent(mActivity, HdActivity.class));
			break;
		case R.id.rl_find_pager_hljy: // 进入婚恋交友界面
			startActivity(new Intent(mActivity, HljyActivity.class));
			break;
		case R.id.rl_find_pager_nmqz: // 进入匿名圈子界面
			startActivity(new Intent(mActivity, NmqzActivity.class));
			break;
		case R.id.rl_find_pager_tjyy: // 进入推荐应用界面
			startActivity(new Intent(mActivity, TjyyActivity.class));
			break;
		case R.id.rl_find_special:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
			// startActivity(new Intent(mActivity, SpecialActivity.class));
			break;
		case R.id.rl_find_weirdo:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
			break;
		case R.id.rl_find_reward:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
			break;
		case R.id.rl_find_recruit:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
		case R.id.rl_find_indiana:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
			break;
		case R.id.rl_find_redinvelope:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
		case R.id.rl_find_task:
			UIUtils.showToast(UIUtils.getContext(), "暂未开通");
		default:
			break;
		}

	}

}
