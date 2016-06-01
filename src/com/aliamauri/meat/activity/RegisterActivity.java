package com.aliamauri.meat.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.RegisterBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PhoneInfoUtils;
import com.aliamauri.meat.utils.TimeCountUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.ShowAlertDialog;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

/**
 * 注册
 * 
 * @author ych
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private EditText et_register_pwd;
	private EditText et_register_confirmpwd;

	private EditText et_register_tel;
	private TextView tv_register_usrname;
	private TextView tv_register_getphonecode;// 获取验证码
	private TextView tv_register_register;
	private TextView et_register_phonecode;// 输入验证码

	private Intent registerInent;
	private HttpHelp httpHelp;
	private RegisterBean regBean;

	private LinearLayout rl_register_male;
	private LinearLayout rl_register_female;

	// 注册第一步
	private RelativeLayout rl_register_firststep;
	private RelativeLayout rl_login_bottom;// 跳转到登录页面
	// private TextView tv_register_forshow1;

	// 注册第二步
	private RelativeLayout rl_register_secondstep;
	private TextView tv_register_phoneforshow;

	private RelativeLayout rl_progress;// 加载页面

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
	protected View getRootView() {
		View view = View
				.inflate(RegisterActivity.this, R.layout.register, null);
		registerInent = getIntent();
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		return "注册";
	}

	@Override
	protected void initView() {
		tv_register_usrname = (TextView) findViewById(R.id.tv_register_usrname);
		et_register_pwd = (EditText) findViewById(R.id.et_register_pwd);

		et_register_confirmpwd = (EditText) findViewById(R.id.et_register_confirmpwd);

		et_register_tel = (EditText) findViewById(R.id.et_register_tel);
		tv_register_getphonecode = (TextView) findViewById(R.id.tv_register_getphonecode);
		tv_register_register = (TextView) findViewById(R.id.tv_register_register);
		et_register_phonecode = (TextView) findViewById(R.id.et_register_phonecode);

		rl_login_bottom = (RelativeLayout) findViewById(R.id.rl_login_bottom);
		rl_register_male = (LinearLayout) findViewById(R.id.rl_register_male);
		rl_register_female = (LinearLayout) findViewById(R.id.rl_register_female);
		rl_register_firststep = (RelativeLayout) findViewById(R.id.rl_register_firststep);
		// tv_register_forshow1 = (TextView)
		// findViewById(R.id.tv_register_forshow1);
		tv_register_phoneforshow = (TextView) findViewById(R.id.tv_register_phoneforshow);
		rl_register_secondstep = (RelativeLayout) findViewById(R.id.rl_register_secondstep);
		tv_register_getphonecode.setClickable(false);
		tv_register_getphonecode.setFocusable(false);

		rl_progress = (RelativeLayout) findViewById(R.id.rl_progress);
		// KeyBoardUtils.openKeybord(et_register_pwd, UIUtils.getContext());
		// KeyBoardUtils.openKeybord(et_register_tel, UIUtils.getContext());
	}

	@Override
	protected void setListener() {
		tv_register_getphonecode.setOnClickListener(this);
		tv_register_register.setOnClickListener(this);
		rl_login_bottom.setOnClickListener(this);
		rl_register_male.setOnClickListener(this);
		rl_register_female.setOnClickListener(this);
		rl_progress.setOnClickListener(this);
		et_register_tel.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (CheckUtils.getInstance().isMobile(
						et_register_tel.getText().toString())) {
					tv_register_getphonecode.setSelected(true);
					tv_register_getphonecode.setClickable(true);
					tv_register_getphonecode.setFocusable(true);
					if (timeCount != null && !timeCount.isFinish()) {
						tv_register_getphonecode.setText("重新发送");
						timeCount.onFinish();
						timeCount.cancel();
					} else if (et_register_tel.getText().toString()
							.equals(phoneNumber)) {
						tv_register_getphonecode.setText("重新发送");
					}
				} else {
					tv_register_getphonecode.setText("获取验证码");
					tv_register_getphonecode.setSelected(false);
					tv_register_getphonecode.setClickable(false);
					tv_register_getphonecode.setFocusable(false);
					if (timeCount != null && !timeCount.isFinish()) {
						timeCount.onFinish();
						timeCount.cancel();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void initNet() {
	}

	private int sex = 0;
	private String phoneNumber;

	public void getUserId() {
		rl_progress.setVisibility(View.VISIBLE);
		RequestParams params = new RequestParams();
		params.addBodyParameter("tel", et_register_tel.getText().toString()
				.trim());
		params.addBodyParameter("code", et_register_phonecode.getText()
				.toString().trim());
		params.addBodyParameter("sex", sex + "");
		httpHelp.sendPost(NetworkConfig.getRegisterFirstStep(), params,
				RegisterBean.class, new MyRequestCallBack<RegisterBean>() {

					@Override
					public void onSucceed(RegisterBean bean) {
						rl_progress.setVisibility(View.GONE);
						if (timeCount != null) {
							timeCount.onFinish();
							timeCount.cancel();
						}
						if (bean == null) {
							setSexSelect(3, true);
							return;
						}
						UIUtils.showToast(UIUtils.getContext(), bean.msg);
						if ("1".equals(bean.status)) {
							regBean = bean;
							tv_register_phoneforshow.setText(et_register_tel
									.getText().toString().trim()
									+ "已绑定");
							tv_register_usrname.setText(bean.cont.uid);
							secondStep();
						} else {
							setSexSelect(3, true);
						}

					}
				});

	}

	/**
	 * 设置倒计时启动装置
	 */
	private TimeCountUtils timeCount;

	// class TimeCount extends CountDownTimer {
	// private TextView textView;
	// private boolean isFinish = false;
	//
	// // private RegisterActivity registerActivity;
	//
	// public TimeCount(long millisInFuture, long countDownInterval,
	// TextView textView) {
	// // 参数依次为总时长,和计时的时间间隔
	// super(millisInFuture, countDownInterval);
	// this.textView = textView;
	// // this.textView.setFocusable(false);
	// // this.textView.setOnClickListener(null);
	// // this.registerActivity = registerActivity;
	// this.textView.setSelected(false);
	// this.textView.setClickable(false);
	// this.textView.setFocusable(false);
	//
	// }
	//
	// public boolean isFinish() {
	// return isFinish;
	// }
	//
	// @Override
	// public void onTick(long millisUntilFinished) {
	//
	// textView.setText(String.valueOf((millisUntilFinished / 1000))
	// + "秒后重新发送");
	// }
	//
	// @Override
	// public void onFinish() {
	// isFinish = true;
	// textView.setText("重新发送");
	// if
	// (CheckUtils.getInstance().isMobile(et_register_tel.getText().toString()))
	// {
	// this.textView.setSelected(true);
	// this.textView.setClickable(true);
	// // this.textView.setOnClickListener(registerActivity);
	// }
	// // goHomePage();
	//
	// }
	// }

	private void secondStep() {
		rl_register_firststep.setVisibility(View.GONE);
		rl_login_bottom.setVisibility(View.GONE);
		// tv_register_forshow1.setVisibility(View.GONE);
		tv_register_phoneforshow.setVisibility(View.VISIBLE);
		rl_register_secondstep.setVisibility(View.VISIBLE);
		tv_register_register.setVisibility(View.VISIBLE);
		KeyBoardUtils.openKeybord(et_register_pwd, UIUtils.getContext());
	}

	public void setSexSelect(int i, boolean reset) {
		if (reset) {
			rl_register_male.setSelected(false);
			rl_register_female.setSelected(false);
		} else {
			if (i == INT_LEFT) {
				rl_register_male.setSelected(true);
				sex = 1;
			} else {
				rl_register_male.setSelected(false);
			}
			if (i == INT_RIGHT) {
				rl_register_female.setSelected(true);
				sex = 0;
			} else {
				rl_register_female.setSelected(false);
			}
			ShowAlertDialog s = new ShowAlertDialog(this, "性别注册后不可更改哦");
		}
	}

	@Override
	public void onClick(View v) {
		String phone = et_register_tel.getText().toString().trim();
		switch (v.getId()) {
		case R.id.rl_login_bottom:
			startActivity(new Intent(this, LoginActivity.class));
			finish();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.rl_register_male:
			if (!(et_register_phonecode.getText().toString().length() == 4)) {
				UIUtils.showToast(UIUtils.getContext(), "请填写正确的验证码后再选择性别");
			} else {
				setSexSelect(INT_LEFT, false);
			}
			break;
		case R.id.rl_register_female:
			if (!(et_register_phonecode.getText().toString().length() == 4)) {
				UIUtils.showToast(UIUtils.getContext(), "请填写正确的验证码后再选择性别");
			} else {
				setSexSelect(INT_RIGHT, false);
			}
			break;
		case R.id.tv_register_getphonecode:
			if (CheckUtils.getInstance().isMobile(phone)) {
				tv_register_getphonecode.setSelected(false);
				tv_register_getphonecode.setClickable(false);
				tv_register_getphonecode.setFocusable(false);
				if (timeCount == null) {
					timeCount = new TimeCountUtils(60000, 1000,
							tv_register_getphonecode, et_register_tel);
				}
				timeCount.start();
				phoneNumber = phone;
				// PrefUtils.setString(GlobazlConstant.USER_PHONENUMBER,
				// et_register_tel.getText().toString().trim());
				rl_progress.setVisibility(View.VISIBLE);
				// 手机网络请求 发送验证码
				httpHelp.sendGet(NetworkConfig.getPhoneCode(phone),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								rl_progress.setVisibility(View.GONE);
								if (bean == null) {
									timeCount.onFinish();
									timeCount.cancel();
									return;
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);
								if (!"1".equals(bean.status)) {
									timeCount.onFinish();
									timeCount.cancel();
								}
							}
						});
			} else {
				UIUtils.showToast(this, "请输入正确的手机号 ");
			}
			break;
		case R.id.tv_register_register:
			String pwd1 = et_register_pwd.getText().toString().trim();
			String pwd2 = et_register_confirmpwd.getText().toString().trim();
			if ("".equals(pwd1) || "".equals(pwd2)) {
				UIUtils.showToast(UIUtils.getContext(), "密码不能为空");
				break;
			} else if (!CheckUtils.getInstance().isPassWord(pwd1)
					|| !CheckUtils.getInstance().isPassWord(pwd2)) {
				UIUtils.showToast(UIUtils.getContext(), "请输入6-16位数字、字母");
				break;
			} else if (!pwd1.equals(pwd2)) {
				UIUtils.showToast(UIUtils.getContext(), "两次输入密码不相同");
				break;
			}
			// 手机网络请求 验证验证码
			RequestParams params = new RequestParams();
			params.addBodyParameter("pwd", pwd1);
			params.addBodyParameter("imei",
					PhoneInfoUtils.getPhoneImei(UIUtils.getContext()));
			params.addBodyParameter("mac",
					PhoneInfoUtils.getPhoneMac(UIUtils.getContext()));
			rl_progress.setVisibility(View.VISIBLE);
			httpHelp.sendPost(
					NetworkConfig.getRegisterSecondStep(regBean.cont.ucode),
					params, BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

						@Override
						public void onSucceed(BaseBaen bean) {
							rl_progress.setVisibility(View.GONE);
							if (bean == null) {
								return;
							}
							UIUtils.showToast(UIUtils.getContext(), bean.msg);
							if ("1".equals(bean.status)) {
								if (registerInent.getBooleanExtra("fromLogin",
										false)) {
									Intent i = new Intent();
									i.putExtra("register", regBean.cont.uid);
									setResult(1, i);
								} else {
									Intent i = new Intent(
											RegisterActivity.this,
											LoginActivity.class);
									i.putExtra("splash", regBean.cont.uid);
									startActivity(i);
								}
								finish();
							}

						}
					});
			break;
		case R.id.rl_progress:
			break;
		default:
			break;
		}
	}
}
