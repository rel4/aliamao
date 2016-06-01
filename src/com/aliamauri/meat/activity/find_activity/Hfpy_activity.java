package com.aliamauri.meat.activity.find_activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.hljy.Yjzq_fjBean.Cont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;
/**
 * 婚恋交友----一见钟情----附近----回应ta
 * @author limaokeji-windosc
 *
 */
public class Hfpy_activity extends Activity implements OnClickListener {
	private CircleImageView mCiv_hfpy_user_icon;  //回应好友的头像
	private TextView mTv_hfpy_username;   //回应好友的姓名
	private TextView mTv_hfpy_time;   //回应好友的时间
	private TextView mTv_hfpy_content;   //回应好友发布的内容
	private EditText mEt_hfpy_impot_content; //我输入的内容
	private TextView mBtn_hfpy_ok;  //确认回应按钮c
	private HttpHelp mHttpHelp;
	private Cont mCont;
	
	protected String mWd; // 伟度
	protected String mJd; // 经度
	
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_frind_hfpy);
		mHttpHelp = new HttpHelp();
		mCont = (Cont)getIntent().getExtras().getSerializable(GlobalConstant.HYPY_TAG);
		initView();
		getLocation();
	}
	/**
	 * 获取当前位置的经纬度
	 */
	private void getLocation() {
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION, "0&&0").split("&&");
		mWd = String.valueOf(location[0]);
		mJd = String.valueOf(location[location.length-1]);
	}


	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_title_backicon:
			KeyBoardUtils.closeKeybord(mEt_hfpy_impot_content,getApplicationContext());
			finish();
			break;
		case R.id.btn_hfpy_ok:
			getImportText();
			break;

		default:
			break;
		}
		
	}
	
	/**
	 * 获取输入框的文字信息内容
	 */
	private void getImportText() {
		String user_text = mEt_hfpy_impot_content.getText().toString().trim();
		if (StringUtils.isEmpty(user_text)) {
			UIUtils.showToast(getApplicationContext(), "输入的内容不能为空~~");
			return;
		}
		if (user_text.length() > 30) {
			UIUtils.showToast(getApplicationContext(), "输入的内容不能超过30个字~~");
			return;
		}

		publishMessage(user_text);
	}
	
	/**
	 * 发布消息功能
	 * 
	 * @param user_text
	 */
	private void publishMessage(final String user_text) {
		RequestParams params = new RequestParams();
		params.addBodyParameter("name", user_text);
		
		params.addBodyParameter("jd", mJd);
		params.addBodyParameter("wd", mWd);
		params.addBodyParameter("id", mCont.id);
		mHttpHelp.sendPost(NetworkConfig.getGgxq_rmgg_url(), params,
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || bean.status == null) {
							return;
						}
						switch (bean.status) {
						case "1":
							UIUtils.showToast(getApplicationContext(),"信息发布成功啦~~");
							KeyBoardUtils.closeKeybord(mEt_hfpy_impot_content,
									getApplicationContext());
							mEt_hfpy_impot_content.setText("");
							Hfpy_activity.this.finish();
							
							break;
						case "2":
							UIUtils.showToast(getApplicationContext(),
									"呀~你还没有登陆~~");
							break;

						default:
							UIUtils.showToast(Hfpy_activity.this,
									"发布失败~~~");
							break;
						}

					}
				});
	}

	/**
	 * 初始化View试图
	 */
	private void initView() {
		 $(R.id.iv_title_backicon).setOnClickListener(this);
		 ((TextView)$(R.id.tv_title_title)).setText("回应好友");;
		 mCiv_hfpy_user_icon = $(R.id.civ_hfpy_user_icon);
		 mTv_hfpy_username = $(R.id.tv_hfpy_username);
		 mTv_hfpy_time = $(R.id.tv_hfpy_time);
		 mTv_hfpy_content = $(R.id.tv_hfpy_content);
		 
		mHttpHelp.showImage(mCiv_hfpy_user_icon, mCont.face);
		mTv_hfpy_username.setText(mCont.nickname);
		mTv_hfpy_time.setText(mCont.time);
		mTv_hfpy_content.setText(mCont.name);
		 
		 
		 mEt_hfpy_impot_content = $(R.id.et_hfpy_impot_content);
		$(R.id.btn_hfpy_ok).setOnClickListener(this);

	}


	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
	}

}
