package com.aliamauri.meat.fragment;

import java.lang.reflect.Field;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.activity.AddContactActivity;
import com.aliamauri.meat.global.GlobalConstant;

/**
 * 一级fragment------底部标签类
 * 
 * @author limaokeji-windosc
 * 
 */
public abstract class BaseFragment extends Fragment {

	private final String BTN_NAME_DT = "爪印";
	private final String BTN_NAME_HT = "资源库";
	private final String BTN_NAME_XX = "消息";
	private final String BTN_NAME_LXR = "联系人";
	private final String BTN_NAME_FX = "发现";
	private final String BTN_NAME_WD = "我的";

	public Activity mActivity; // 全局的activity
	public LinearLayout mLl_fragment_base_title_btns; // 标题按钮组
	public Button mBtn_fragment_base_title_btn_dt; // 标题按钮 （动态 消息）
	public Button mBtn_fragment_base_title_btn_r; // 标题按钮 (人)
	public Button mBtn_fragment_base_title_btn_ht; // 标题按钮 （话题 联系人）
	public TextView mBtn_fragment_base_title_btn; // 标题右边按钮
	public TextView mTv_fragment_base_title_tag_name; // 中间标题名称
	public FrameLayout mFl_fragment_base_content; // fragment内容区域
	public LinearLayout mLl_title_search; // 搜索的头布局组
	public EditText mEt_title_search_edit; // 输入框区域
	public TextView mBtn_title_search_confirm; // 确认按钮区域
	public ImageView iv_new_contact; // 添加好友
	public FrameLayout mFl_upload_right_layout; // 发布动态正在上传后台数据布局

	public ImageView iv_fbt_gototakevideo;
	public LinearLayout ll_fbt;

	/**
	 * 此方法可解决 noActivity异常
	 * 
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		super.onDetach();
		try {
			Field childFragmentManager = Fragment.class
					.getDeclaredField("mChildFragmentManager");
			childFragmentManager.setAccessible(true);
			childFragmentManager.set(this, null);

		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取每个子类fragment的标记
	 * 
	 * @return
	 */
	public abstract int getchildFragmentTag();

	/**
	 * 子类添加数据的操作
	 */
	public abstract void initDate();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActivity = getActivity();

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initDate();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_base_title, container,
				false);
		init(view);
		setChildTitleStyle(getchildFragmentTag());
		return view;
	}

	/**
	 * 根据子类页面传入的标记来确定各个页面标题的样式
	 * 
	 * @param getchildFragmentTag
	 */
	private void setChildTitleStyle(int Tag) {
		switch (Tag) {
		case GlobalConstant.NEARBY_PAGE: // 附近 标题样式
			iv_fbt_gototakevideo.setVisibility(View.VISIBLE);
			mLl_fragment_base_title_btns.setVisibility(View.VISIBLE);
			mBtn_fragment_base_title_btn_dt.setText("视频");
			mBtn_fragment_base_title_btn_ht.setText("达人");
			// mBtn_fragment_base_title_btn_r.setText("达人");//与资源库换位
			mBtn_fragment_base_title_btn_ht.setVisibility(View.VISIBLE);
			mBtn_fragment_base_title_btn_r.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn.setVisibility(View.VISIBLE);
			mTv_fragment_base_title_tag_name.setVisibility(View.GONE);
			iv_new_contact.setVisibility(View.GONE);
			mLl_title_search.setVisibility(View.GONE);
			mFl_upload_right_layout.setVisibility(View.GONE);
			break;
		case GlobalConstant.ADDR_PAGE: // 通讯标题样式
			iv_fbt_gototakevideo.setVisibility(View.GONE);
			mLl_fragment_base_title_btns.setVisibility(View.VISIBLE);
			iv_new_contact.setVisibility(View.VISIBLE);
			mBtn_fragment_base_title_btn_dt.setText(BTN_NAME_XX);
			mBtn_fragment_base_title_btn_ht.setText(BTN_NAME_LXR);
			mBtn_fragment_base_title_btn_r.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn.setVisibility(View.GONE);
			mTv_fragment_base_title_tag_name.setVisibility(View.GONE);
			mLl_title_search.setVisibility(View.GONE);
			mFl_upload_right_layout.setVisibility(View.GONE);
			break;
		case GlobalConstant.SEARCH_PAGE: // 搜索标题样式
			iv_fbt_gototakevideo.setVisibility(View.GONE);
			mLl_fragment_base_title_btns.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn_r.setVisibility(View.GONE);
			iv_new_contact.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn.setVisibility(View.GONE);
			mTv_fragment_base_title_tag_name.setVisibility(View.GONE);
			mLl_title_search.setVisibility(View.VISIBLE);
			mFl_upload_right_layout.setVisibility(View.GONE);
			break;
		case GlobalConstant.FIND_PAGE: // 发现标题样式
			iv_fbt_gototakevideo.setVisibility(View.GONE);
			mLl_fragment_base_title_btns.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn_r.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn.setVisibility(View.GONE);
			iv_new_contact.setVisibility(View.GONE);
			mTv_fragment_base_title_tag_name.setVisibility(View.VISIBLE);
			mTv_fragment_base_title_tag_name.setText(BTN_NAME_FX);
			mLl_title_search.setVisibility(View.GONE);
			mFl_upload_right_layout.setVisibility(View.GONE);
			break;
		case GlobalConstant.MY_PAGE: // 我的标题样式
			iv_fbt_gototakevideo.setVisibility(View.GONE);
			mLl_fragment_base_title_btns.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn_r.setVisibility(View.GONE);
			mBtn_fragment_base_title_btn.setVisibility(View.GONE);
			mTv_fragment_base_title_tag_name.setVisibility(View.VISIBLE);
			mTv_fragment_base_title_tag_name.setText(BTN_NAME_WD);
			mLl_title_search.setVisibility(View.GONE);
			iv_new_contact.setVisibility(View.GONE);
			mFl_upload_right_layout.setVisibility(View.GONE);
			break;
		}
	}

	/**
	 * 初始化各种控件的查找
	 * 
	 * @param view
	 */
	private void init(View view) {
		mLl_fragment_base_title_btns = $(view, R.id.ll_fragment_base_title_btns);
		mBtn_fragment_base_title_btn_dt = $(view,
				R.id.btn_fragment_base_title_btn_dt);
		iv_new_contact = $(view, R.id.iv_new_contact);
		mBtn_fragment_base_title_btn_r = $(view,
				R.id.btn_fragment_base_title_btn_r);
		mBtn_fragment_base_title_btn_ht = $(view,
				R.id.btn_fragment_base_title_btn_ht);
		mBtn_fragment_base_title_btn = $(view, R.id.btn_fragment_base_title_btn);
		mTv_fragment_base_title_tag_name = $(view,
				R.id.tv_fragment_base_title_tag_name);
		mFl_fragment_base_content = $(view, R.id.fl_fragment_base_content);
		mLl_title_search = $(view, R.id.ll_title_search);
		mEt_title_search_edit = $(view, R.id.et_title_search_edit);
		mBtn_title_search_confirm = $(view, R.id.btn_title_search_confirm);
		mFl_upload_right_layout = $(view, R.id.fl_upload_right_layout);
		iv_fbt_gototakevideo = $(view, R.id.iv_fbt_gototakevideo);
		ll_fbt = $(view, R.id.ll_fbt);

		setAddOnclick();
	}

	/**
	 * 添加好友
	 */
	private void setAddOnclick() {
		// 进入添加好友页
		if (iv_new_contact == null) {
			return;
		}
		iv_new_contact.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(),
						AddContactActivity.class));
			}
		});
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

}
