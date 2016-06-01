package com.aliamauri.meat.fragment.impl_supper;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.TakeVideoActivity;
import com.aliamauri.meat.activity.nearby_activity.BroadCast_DT;
import com.aliamauri.meat.activity.nearby_activity.BroadCast_YS;
import com.aliamauri.meat.eventBus.IsShowRenSelectLayout;
import com.aliamauri.meat.fragment.BaseFragment;
import com.aliamauri.meat.fragment.impl_child.DaRenPage;
import com.aliamauri.meat.fragment.impl_child.HomeTVListFragment;
import com.aliamauri.meat.fragment.impl_child.RenPage;
import com.aliamauri.meat.fragment.impl_child.ResourceLibraryPage;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.utils.FramgentUtils;
import com.aliamauri.meat.view.gif.GifFragment;

import de.greenrobot.event.EventBus;

/**
 * 首页——附近 第一级
 * 
 * 此页面的具体实现可到二级 ，三级fragment
 * 
 * RenPage 人的标签实现 HuaTiPage 话题的标签实现
 * 
 * 
 * mActivity 可直接获取Activity
 * 
 * @author limaokeji-windosc
 * 
 */
public class NearbyPage extends BaseFragment implements OnClickListener {

	private final int DT_TAG = 0; // 顶部标签 ---动态 tag
	private final int R_TAG = 1; // 顶部标签 ---人 tag
	private final int ht_TAG = 2; // 顶部标签 ---话题 tag
	private int CURRENT_TAG = DT_TAG; // 设置当前的标签为： 动态

	private final String BTN_NAME_FB = "发布";
	private final String BTN_NAME_SX = "筛选";
	private final String BTN_NAME_CJ = "发布";

	private FragmentManager cfm; // 子类的fragment管理器
	// private DongTaiPage dt_f; // 附近——动态fragment
	private RenPage r_f; // 附近——人 fragment
	private DaRenPage dr_f; // 达人 fragment
	// private MiaoKePage miaoKePage;
	private ResourceLibraryPage ht_f; // 附近——话题fragment


	/**
	 * 动态----- 发布按钮
	 */
	private void goFunctionPage_dt() {
		startActivity(new Intent(mActivity, BroadCast_DT.class));
	}

	/**
	 * 人----- 筛选按钮
	 * 
	 * 通过eventbus来控制是否显示人中的筛选布局
	 */
	private void goFunctionPage_r() {
		EventBus.getDefault().post(
				new IsShowRenSelectLayout(mBtn_fragment_base_title_btn));

	}

	/**
	 * 资源库----发布按钮
	 */

	private void goFunctionPage_ht() {
		startActivity(new Intent(mActivity, BroadCast_YS.class));
	}

	/**
	 * 一级页面切换时为二级设置默认页面
	 */
	@Override
	public void onHiddenChanged(boolean hidden) {
		if (hidden) {
			setCurrentchilePage();
		}
	}

	@Override
	public void initDate() {
		cfm = getChildFragmentManager();
		initClicks();
		// 初始化进入页面
		setCurrentchilePage();
	}

	/**
	 * 显示当前默认页
	 */
	private void setCurrentchilePage() {
		resBtn();
		CURRENT_TAG = DT_TAG;
		mBtn_fragment_base_title_btn.setText(BTN_NAME_FB);
		mBtn_fragment_base_title_btn_dt.setTextColor(getResources().getColor(
				R.color.bg_white));
		mBtn_fragment_base_title_btn_dt
				.setBackgroundResource(R.drawable.title_name_dt_h);
		setSelectState(DT_TAG);
	}

	/**
	 * 初始化点击事件
	 */
	private void initClicks() {
		mBtn_fragment_base_title_btn_dt.setOnClickListener(this);
		mBtn_fragment_base_title_btn_r.setOnClickListener(this);
		mBtn_fragment_base_title_btn_ht.setOnClickListener(this);
		mBtn_fragment_base_title_btn.setOnClickListener(this);
		iv_fbt_gototakevideo.setOnClickListener(this);
	}

	@Override
	public int getchildFragmentTag() {
		return GlobalConstant.NEARBY_PAGE;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_fragment_base_title_btn: // 标题栏右边的按钮
			goFunctionPage();
			break;
		case R.id.btn_fragment_base_title_btn_dt: // 标题栏的动态按钮
			setSelectState(DT_TAG);
			break;
		case R.id.btn_fragment_base_title_btn_r: // 标题栏的人按钮
			setSelectState(R_TAG);// 与话题换了位置
			break;
		case R.id.btn_fragment_base_title_btn_ht: // 标题栏的话题按钮
			// setSelectState(ht_TAG); // 与人换了位置
			setSelectState(R_TAG);
			break;
		case R.id.iv_fbt_gototakevideo:
			mActivity.startActivity(new Intent(mActivity,
					TakeVideoActivity.class));
			break;
		default:
			break;
		}
	}

	/**
	 * 根据当前状态，改变右侧功能按钮对应的功能
	 */
	private void goFunctionPage() {
		switch (CURRENT_TAG) {
		case DT_TAG:
			goFunctionPage_dt();
			break;
		case R_TAG:
			goFunctionPage_r();
			break;
		case ht_TAG:
			goFunctionPage_ht();
			break;

		default:
			break;
		}
	}

	private Fragment currentFragment;
	private HomeTVListFragment homeTVListFragment;

	/**
	 * 切换标题按钮的时候改变相应的状态
	 * 
	 * @param tag
	 */
	private void setSelectState(int tag) {
		Fragment fragment = null;
		resBtn();
		// FragmentTransaction transaction = cfm.beginTransaction();

		switch (tag) {
		case DT_TAG:
			iv_fbt_gototakevideo.setVisibility(View.VISIBLE);
			mBtn_fragment_base_title_btn.setVisibility(View.GONE);
			CURRENT_TAG = DT_TAG;
			mBtn_fragment_base_title_btn.setText(BTN_NAME_FB);
			mBtn_fragment_base_title_btn_dt.setTextColor(getResources()
					.getColor(R.color.bg_white));
			mBtn_fragment_base_title_btn_dt
					.setBackgroundResource(R.drawable.title_name_dt_h);
			if (homeTVListFragment == null) {
				homeTVListFragment = new HomeTVListFragment();
			}
			fragment = homeTVListFragment;
			// if (!homeTVListFragment.isAdded()) {
			// transaction.replace(R.id.fl_fragment_base_content,
			// homeTVListFragment);
			//
			// }
			mBtn_fragment_base_title_btn_dt.setEnabled(false);
			mBtn_fragment_base_title_btn_r.setEnabled(true);
			mBtn_fragment_base_title_btn_ht.setEnabled(true);

			break;
		case R_TAG:
			iv_fbt_gototakevideo.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn.setVisibility(View.GONE);
			// mBtn_fragment_base_title_btn.setText("筛选");
			CURRENT_TAG = R_TAG;
			// mBtn_fragment_base_title_btn.setText(BTN_NAME_SX);
			// mBtn_fragment_base_title_btn_r.setTextColor(getResources()
			// .getColor(R.color.main_color));
			mBtn_fragment_base_title_btn_r
					.setBackgroundResource(R.drawable.title_name_r_h);// 人与话题换了下
			mBtn_fragment_base_title_btn.setText("筛选");
			mBtn_fragment_base_title_btn_ht.setTextColor(getResources()
					.getColor(R.color.bg_white));
			mBtn_fragment_base_title_btn_ht
					.setBackgroundResource(R.drawable.title_name_ht_h);
			// ******喵客**********
			// if (r_f == null) {
			// r_f = new RenPage();
			// }
			// fragment = r_f;
			// ******达人**********
			if (dr_f == null) {
				dr_f = new DaRenPage();
			}
			fragment = dr_f;
			// ******达人**********

			// if (!r_f.isAdded()) {
			// transaction.replace(R.id.fl_fragment_base_content, r_f);
			// }

			mBtn_fragment_base_title_btn_dt.setEnabled(true);
			mBtn_fragment_base_title_btn_r.setEnabled(false);
			mBtn_fragment_base_title_btn_ht.setEnabled(true);
			break;
		case ht_TAG:
			mBtn_fragment_base_title_btn.setVisibility(View.VISIBLE);
			mBtn_fragment_base_title_btn.setText(BTN_NAME_FB);
			CURRENT_TAG = ht_TAG;
			mBtn_fragment_base_title_btn.setText(BTN_NAME_CJ);
			mBtn_fragment_base_title_btn_ht.setTextColor(getResources()
					.getColor(R.color.bg_white));
			mBtn_fragment_base_title_btn_ht
					.setBackgroundResource(R.drawable.title_name_ht_h);
			if (ht_f == null) {
				ht_f = new ResourceLibraryPage();
			}
			// if (!ht_f.isAdded()) {
			// transaction.replace(R.id.fl_fragment_base_content, ht_f);
			// }
			fragment = ht_f;

			mBtn_fragment_base_title_btn_dt.setEnabled(true);
			mBtn_fragment_base_title_btn_r.setEnabled(true);
			mBtn_fragment_base_title_btn_ht.setEnabled(false);
			break;

		default:
			break;
		}
		if (fragment == null) {
			return;
		}
		if (currentFragment != null) {
			FramgentUtils.switchFragment(cfm, R.id.fl_fragment_base_content,
					currentFragment, fragment);
		} else {
			FragmentTransaction transaction = cfm.beginTransaction();
			transaction.replace(R.id.fl_fragment_base_content, fragment);
			transaction.commitAllowingStateLoss();
		}
		currentFragment = fragment;
	}

	/**
	 * 对外提供获取话题fragment
	 */
	public void setHuaTiPage() {
		setSelectState(ht_TAG);
	}

	/**
	 * 重置按钮的状态 字体颜色和背景图片
	 * 
	 * <Button style="@style/fragment_top_title_text"
	 * android:background="@drawable/title_name_r_q"
	 * android:text="@string/nearby_title_r" android:visibility="gone" />
	 */
	private void resBtn() {
		mBtn_fragment_base_title_btn_dt.setTextColor(getResources().getColor(
				R.color.bg_white));
		mBtn_fragment_base_title_btn_dt
				.setBackgroundResource(R.drawable.title_name_dt_q);

		mBtn_fragment_base_title_btn_r.setTextColor(getResources().getColor(
				R.color.bg_white));
		mBtn_fragment_base_title_btn_r
				.setBackgroundResource(R.drawable.title_name_r_q);

		mBtn_fragment_base_title_btn_ht.setTextColor(getResources().getColor(
				R.color.bg_white));
		mBtn_fragment_base_title_btn_ht
				.setBackgroundResource(R.drawable.title_name_ht_q);
	}

}
