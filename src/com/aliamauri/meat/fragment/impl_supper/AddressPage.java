package com.aliamauri.meat.fragment.impl_supper;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.fragment.BaseFragment;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.utils.FramgentUtils;

/**
 * 首页——通讯录 第一级
 * 
 * 此页面的具体实现可到二级fragment
 * 
 * ChatAllHistoryFragment 消息类 ContactlistFragment 联系人类
 * 
 * mActivity 可直接获取Activity
 * 
 * @author limaokeji-windosc
 * 
 */
public class AddressPage extends BaseFragment implements OnClickListener {

	private final int XX_TAG = 0; // 顶部标签 --- 消息 tag
	private final int LXR_TAG = 1; // 顶部标签 ---联系人 tag

	private FragmentManager mFm; // 子类的fragment管理器
	// private ChatAllHistoryFragment xx_f; // 通讯录—— 消息 fragment
	// private ContactlistFragment lxr_f; // 通讯录—— 联系人 fragment

	private final String CHILE_XX_TAG = "xiaoxi"; // 通讯录——消息fragment
	private final String CHILE_LXR_TAG = "lianxiren"; // 通讯录——联系人fragment

	@Override
	public void initDate() {
		mFm = getChildFragmentManager();
		initClicks();
		// 初始化进入页面
		setCurrentchilePage();
	}

	/**
	 * 初始化点击事件
	 */
	private void initClicks() {
		mBtn_fragment_base_title_btn_dt.setOnClickListener(this);
		mBtn_fragment_base_title_btn_ht.setOnClickListener(this);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			setCurrentchilePage();
		}
	}

	/**
	 * 显示当前默认页
	 */
	private void setCurrentchilePage() {
		resBtn();
		mBtn_fragment_base_title_btn_dt.setTextColor(getResources().getColor(
				R.color.main_color));
		mBtn_fragment_base_title_btn_dt
				.setBackgroundResource(R.drawable.title_name_dt_h);
		setSelectState(LXR_TAG);
		setSelectState(XX_TAG);

	}

	@Override
	public int getchildFragmentTag() {

		return GlobalConstant.ADDR_PAGE;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.btn_fragment_base_title_btn_dt: // 标题栏的消息按钮
			setSelectState(XX_TAG);
			break;

		case R.id.btn_fragment_base_title_btn_ht: // 标题栏的联系人按钮
			setSelectState(LXR_TAG);
			break;

		default:
			break;
		}
	}

	private Fragment currentFragment;

	/**
	 * 切换标题按钮的时候改变相应的状态
	 * 
	 * @param tag
	 */
	private void setSelectState(int tag) {
		Fragment fragment = null;
		resBtn();
		setCurrentState(tag);
		MainActivity mainActivity = (MainActivity) MyApplication
				.getMainActivity();
		if (XX_TAG == tag) {
			if (iv_new_contact != null && iv_new_contact.isShown()) {
				iv_new_contact.setVisibility(View.GONE);
				iv_new_contact.setClickable(false);
				iv_new_contact.setFocusable(false);
			}
			fragment = mainActivity.getChatAllHistoryFragment();
		} else if (LXR_TAG == tag) {
			if (iv_new_contact != null && !iv_new_contact.isShown()) {
				iv_new_contact.setVisibility(View.VISIBLE);
				iv_new_contact.setClickable(true);
				iv_new_contact.setFocusable(true);
			}
			fragment = mainActivity.getContactlistFragment();
		}
		if (fragment == null) {
			return;
		}

		if (currentFragment != null) {
			FramgentUtils.switchFragment(mFm, R.id.fl_fragment_base_content,
					currentFragment, fragment);
		} else {
			FragmentTransaction transaction = mFm.beginTransaction();
			transaction.replace(R.id.fl_fragment_base_content, fragment);
			transaction.commitAllowingStateLoss();
		}
		currentFragment = fragment;

	}

	/**
	 * 设置当前图标的状态
	 * 
	 * @param xX_TAG2
	 */
	private void setCurrentState(int key) {
		switch (key) {
		case XX_TAG:
			mBtn_fragment_base_title_btn_dt.setTextColor(getResources()
					.getColor(R.color.bg_white));
			mBtn_fragment_base_title_btn_dt
					.setBackgroundResource(R.drawable.title_name_dt_h);
			mBtn_fragment_base_title_btn_dt.setEnabled(false);
			mBtn_fragment_base_title_btn_ht.setEnabled(true);
			break;
		case LXR_TAG:
			mBtn_fragment_base_title_btn_ht.setTextColor(getResources()
					.getColor(R.color.bg_white));
			mBtn_fragment_base_title_btn_ht
					.setBackgroundResource(R.drawable.title_name_ht_h);
			mBtn_fragment_base_title_btn_dt.setEnabled(true);
			mBtn_fragment_base_title_btn_ht.setEnabled(false);
			break;

		default:
			break;
		}
	}

	/**
	 * 重置按钮的状态 字体颜色和背景图片
	 */
	private void resBtn() {
		mBtn_fragment_base_title_btn_dt.setTextColor(getResources().getColor(
				R.color.bg_white));
		mBtn_fragment_base_title_btn_dt
				.setBackgroundResource(R.drawable.title_name_dt_q);

		mBtn_fragment_base_title_btn_ht.setTextColor(getResources().getColor(
				R.color.bg_white));
		mBtn_fragment_base_title_btn_ht
				.setBackgroundResource(R.drawable.title_name_ht_q);
	}

}
