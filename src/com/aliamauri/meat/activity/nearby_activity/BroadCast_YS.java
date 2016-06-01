package com.aliamauri.meat.activity.nearby_activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.eventBus.ResourceUpdateUi;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class BroadCast_YS extends BaseActivity implements OnClickListener {

	private TextView mBtn_r;
	private EditText mEt_ys_title_name;
	private EditText mEt_ys_content;
	private HttpHelp mHttpHelp;

	@Override
	protected View getRootView() {
		return View.inflate(mActivity, R.layout.activity_broadcast_ys, null);
	}

	@Override
	protected void initView() {
		mHttpHelp = new HttpHelp();
		mBtn_r = $(R.id.tv_title_right);
		mEt_ys_title_name = $(R.id.et_ys_title_name);
		mEt_ys_content = $(R.id.et_ys_content);
		mBtn_r.setVisibility(View.VISIBLE);
		mBtn_r.setText("确定");

	}

	@Override
	protected void setListener() {
		mBtn_r.setOnClickListener(this);

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
	
	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	protected String getCurrentTitle() {
		return "发布影视";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			setOkBtn();
			break;

		default:
			break;
		}

	}

	/**
	 * 设置确定按钮操作
	 */
	private void setOkBtn() {
		String title = mEt_ys_title_name.getText().toString().trim();
		String content = mEt_ys_content.getText().toString().trim();
		if (StringUtils.isEmpty(title) && title.length() <= 0) {
			UIUtils.showToast(mActivity, "请输入标题名称……");
			return;
		}
		if (title.length() > 40) {
			UIUtils.showToast(mActivity, "标题名称过长(<40字)……");
			return;
		}
		if (StringUtils.isEmpty(content) && content.length() <= 0) {
			UIUtils.showToast(mActivity, "请输入内容网址……");
			return;
		}
		if (content.length() > 500) {
			UIUtils.showToast(mActivity, "网址内容过长过长(<500字)……");
			return;
		}

		// if (!(content.matches("[a-zA-z]+://[^\\s]*"))) {
		// UIUtils.showToast(mActivity, "请输入正确取网址……");
		// return;
		// }
		sendContent(title, content);

	}

	/**
	 * 发布内容
	 * 
	 * @param content
	 * @param title
	 */
	private void sendContent(String title, String content) {
		setKeyBordClose();
		RequestParams params = new RequestParams();
		params.addBodyParameter("name", title);
		params.addBodyParameter("content", content);
		mHttpHelp.sendPost(NetworkConfig.getUpload_ys(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null) {
							UIUtils.showToast(mActivity, "发布影视失败……");
							return;
						}
						switch (bean.status) {
						case "1":
							UIUtils.showToast(mActivity, "发布影视成功……");
							EventBus.getDefault().post(new ResourceUpdateUi());
							BroadCast_YS.this.finish();
							break;

						default:
							UIUtils.showToast(mActivity, bean.msg);
							break;
						}

					}
				});

	}

	@Override
	protected void onDestroy() {
		if (mHttpHelp != null) {
			mHttpHelp.stopHttpNET();
		}
		super.onDestroy();
	}

	@Override
	protected void setKeyBordClose() {
		if (mEt_ys_content != null) {
			KeyBoardUtils.closeKeybord(mEt_ys_content,
					mActivity.getApplicationContext());
		}
		if (mEt_ys_title_name != null) {
			KeyBoardUtils.closeKeybord(mEt_ys_title_name,
					mActivity.getApplicationContext());
		}
	}

}
