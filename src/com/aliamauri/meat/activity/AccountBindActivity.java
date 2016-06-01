package com.aliamauri.meat.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.ListContBean;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 绑号绑定
 * 
 * @author ych
 * 
 */
public class AccountBindActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private TextView tv_title_title;

	private EditText ed_accountbind_bindphone;// 输入手机号
	private EditText ed_accountbind_bindmail;// 输入邮箱
	private TextView tv_accountbind_phonesend;// 发送手机验证码
	private TextView tv_accountbind_mailsend;// 发送邮箱验证吗
	private EditText et_accountbind_phonecheck;
	private EditText ed_accountbind_mailcheck;
	private TextView tv_accountbind_bindphone;
	private TextView tv_accountbind_bindmail;

	private TextView tv_bind_phone;
	private TextView tv_bind_mail;
	private Intent myDataIntent;

	@Override
	protected View getRootView() {
		View view = View.inflate(AccountBindActivity.this,
				R.layout.account_bind, null);
		httpHelp = new HttpHelp();
		myDataIntent = getIntent();
		return view;
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
	protected void initView() {

		tv_title_title = (TextView) findViewById(R.id.tv_title_title);
		tv_title_title.setText("帐号绑定");
		tv_title_title.setFocusable(true);
		tv_title_title.setFocusableInTouchMode(true);
		tv_accountbind_bindphone = (TextView) findViewById(R.id.tv_accountbind_bindphone);
		tv_accountbind_bindmail = (TextView) findViewById(R.id.tv_accountbind_bindmail);

		ed_accountbind_bindphone = (EditText) findViewById(R.id.ed_accountbind_bindphone);

		tv_accountbind_phonesend = (TextView) findViewById(R.id.tv_accountbind_phonesend);

		ed_accountbind_bindmail = (EditText) findViewById(R.id.ed_accountbind_bindmail);

		tv_accountbind_mailsend = (TextView) findViewById(R.id.tv_accountbind_mailsend);
		et_accountbind_phonecheck = (EditText) findViewById(R.id.et_accountbind_phonecheck);
		ed_accountbind_mailcheck = (EditText) findViewById(R.id.ed_accountbind_mailcheck);
		tv_bind_phone = (TextView) findViewById(R.id.tv_bind_phone);
		tv_bind_mail = (TextView) findViewById(R.id.tv_bind_mail);

		if ("1".equals(PrefUtils
				.getString(GlobalConstant.USER_EMAIL_VERIFY, ""))) {
			tv_accountbind_bindmail.setText("已绑定邮箱");
			ed_accountbind_bindmail.setText(PrefUtils.getString(
					GlobalConstant.USER_EMAIL, ""));
			tv_bind_mail.setText("修改绑定邮箱");
			tv_accountbind_mailsend.setText("已绑定");
		} else {
			tv_bind_mail.setText("绑定邮箱");
		}

		if ("1".equals(PrefUtils.getString(GlobalConstant.USER_TEL_VERIFY, ""))) {
			ed_accountbind_bindphone.setText(PrefUtils.getString(
					GlobalConstant.USER_TEL, ""));
			tv_accountbind_bindphone.setText("已绑定手机号");
			tv_bind_phone.setText("修改绑定手机");
			tv_accountbind_phonesend.setText("已绑定");
		} else {
			tv_bind_phone.setText("绑定手机");
		}

		setOnTextChange();
	}

	private String phoneNumber;
	private String email;

	private void setOnTextChange() {
		ed_accountbind_bindphone.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (CheckUtils.getInstance().isMobile(
						ed_accountbind_bindphone.getText().toString())) {
					tv_accountbind_phonesend.setSelected(true);
					if (phoneTime != null && !phoneTime.isFinish()) {
						tv_accountbind_phonesend.setText("重新发送");
						phoneTime.onFinish();
						phoneTime.cancel();
					} else if (ed_accountbind_bindphone.getText().toString()
							.equals(phoneNumber)) {
						tv_accountbind_phonesend.setText("重新发送");
					}
					// 判断是否和已绑定的手机号码是否一样
					if (!"".equals(PrefUtils.getString(GlobalConstant.USER_TEL,
							""))
							&& PrefUtils.getString(GlobalConstant.USER_TEL, "")
									.equals(ed_accountbind_bindphone.getText()
											.toString())) {
						tv_accountbind_phonesend.setText("已绑定");
					}
				} else {
					if (phoneTime != null && !phoneTime.isFinish()) {
						phoneTime.onFinish();
						phoneTime.cancel();
					}
					tv_accountbind_phonesend.setText("获取验证码");
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
		ed_accountbind_bindmail.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (CheckUtils.getInstance().isEmail(
						ed_accountbind_bindmail.getText().toString())) {
					tv_accountbind_mailsend.setSelected(true);
					if (mailTime != null && !mailTime.isFinish()) {
						tv_accountbind_mailsend.setText("重新发送");
						mailTime.onFinish();
						mailTime.cancel();
					} else if (ed_accountbind_bindmail.getText().toString()
							.equals(email)) {
						tv_accountbind_mailsend.setText("重新发送");
					}

					// 判断是否和已绑定的邮箱是否一样
					if (!"".equals(PrefUtils.getString(
							GlobalConstant.USER_EMAIL, ""))
							&& PrefUtils.getString(GlobalConstant.USER_EMAIL,
									"").equals(
									ed_accountbind_bindmail.getText()
											.toString())) {
						tv_accountbind_mailsend.setText("已绑定");
					}
				} else {
					tv_accountbind_mailsend.setText("获取验证码");
					if (mailTime != null && !mailTime.isFinish()) {
						mailTime.onFinish();
						mailTime.cancel();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	@Override
	protected void setListener() {
		tv_accountbind_phonesend.setOnClickListener(this);
		tv_accountbind_mailsend.setOnClickListener(this);
		tv_bind_phone.setOnClickListener(this);
		tv_bind_mail.setOnClickListener(this);
	}

	/**
	 * 设置倒计时启动装置
	 */
	class TimeCount extends CountDownTimer {
		private TextView textView;
		private boolean isFinish = false;

		public TimeCount(long millisInFuture, long countDownInterval,
				TextView textView) {
			// 参数依次为总时长,和计时的时间间隔
			super(millisInFuture, countDownInterval);
			this.textView = textView;
			this.textView.setFocusable(false);
			this.textView.setClickable(false);
		}

		public boolean isFinish() {
			return isFinish;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			textView.setText(String.valueOf((millisUntilFinished / 1000))
					+ "秒后重新发送");
		}

		@Override
		public void onFinish() {
			isFinish = true;
			textView.setText("重新发送");
			this.textView.setSelected(true);
			this.textView.setClickable(true);

		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Intent intent = new Intent();
		intent.setAction("myDataUpdate");
		AccountBindActivity.this.sendBroadcast(intent);
	}

	private TimeCount phoneTime;
	private TimeCount mailTime;

	@Override
	public void onClick(View v) {
		final String email = ed_accountbind_bindmail.getText().toString()
				.trim();
		final String phone = ed_accountbind_bindphone.getText().toString()
				.trim();

		switch (v.getId()) {
		case R.id.tv_title_right:
			startActivity(new Intent(this, LoginActivity.class));
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.tv_accountbind_phonesend:

			if (CheckUtils.getInstance().isMobile(phone)) {
				phoneTime = new TimeCount(60000, 1000, tv_accountbind_phonesend);
				phoneTime.start();
				phoneNumber = phone;
				// 手机网络请求 发送验证码
				httpHelp.sendGet(NetworkConfig.getPhoneCodeAccount(phone),
						ListContBean.class,
						new MyRequestCallBack<ListContBean>() {

							@Override
							public void onSucceed(ListContBean bean) {
								if (bean == null) {
									phoneTime.onFinish();
									phoneTime.cancel();
									KeyBoardUtils.openKeybord(
											ed_accountbind_bindphone,
											UIUtils.getContext());
									return;
								}
								if ("1".equals(bean.status)) {

								} else {
									phoneTime.onFinish();
									phoneTime.cancel();
									KeyBoardUtils.openKeybord(
											ed_accountbind_bindphone,
											UIUtils.getContext());
								}
								UIUtils.showToast(AccountBindActivity.this,
										bean.msg);

							}
						});
			} else {
				UIUtils.showToast(this, "请输入正确的手机号 ");
			}
			break;

		case R.id.tv_bind_phone:
			String phonecode = et_accountbind_phonecheck.getText().toString()
					.trim();
			if (CheckUtils.getInstance().isMobile(phone)) {
				// 手机网络请求 验证验证码
				httpHelp.sendGet(NetworkConfig.getPhoneCheck(phone, phonecode),
						ListContBean.class,
						new MyRequestCallBack<ListContBean>() {

							@Override
							public void onSucceed(ListContBean bean) {
								if (bean == null) {
									UIUtils.showToast(AccountBindActivity.this,
											"网络请求失败，请再试一试");
									return;
								}
								UIUtils.showToast(AccountBindActivity.this,
										bean.msg);
								if ("1".equals(bean.status)) {
									et_accountbind_phonecheck.setText("");
									tv_accountbind_bindphone.setText("已绑定手机号");
									tv_accountbind_phonesend.setText("已绑定");
									tv_bind_phone.setText("修改绑定手机");
									phoneTime.onFinish();
									phoneTime.cancel();
									KeyBoardUtils.closeKeybord(
											et_accountbind_phonecheck,
											UIUtils.getContext());
									tv_title_title.requestFocus();
									PrefUtils.setString(
											GlobalConstant.USER_TEL, phone);
									PrefUtils.setString(
											GlobalConstant.USER_TEL_VERIFY,
											bean.status);
								}
							}
						});

			} else {
				UIUtils.showToast(this, "请输入正确的手机号 ");
			}
			break;
		case R.id.tv_accountbind_mailsend:
			if (CheckUtils.getInstance().isEmail(email)) {
				mailTime = new TimeCount(60000, 1000, tv_accountbind_mailsend);
				mailTime.start();
				// 邮箱网络请求 发送验证码
				httpHelp.sendGet(NetworkConfig.getMailCode(email),
						BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

							@Override
							public void onSucceed(BaseBaen bean) {
								if (bean == null) {
									mailTime.onFinish();
									mailTime.cancel();
									KeyBoardUtils.openKeybord(
											ed_accountbind_bindmail,
											UIUtils.getContext());
									return;
								}
								if ("1".equals(bean.status)) {

								} else {
									mailTime.onFinish();
									mailTime.cancel();
									KeyBoardUtils.openKeybord(
											ed_accountbind_bindmail,
											UIUtils.getContext());
								}
								UIUtils.showToast(AccountBindActivity.this,
										bean.msg);
							}
						});
			} else {
				UIUtils.showToast(this, "请输入邮箱号");
			}
			break;
		case R.id.ibtn_left_home_back:
			finish();
			break;
		case R.id.tv_bind_mail:
			String mailcode = ed_accountbind_mailcheck.getText().toString()
					.trim();
			if (CheckUtils.getInstance().isEmail(email)) {
				// 邮箱网络请求 验证验证码
				httpHelp.sendGet(NetworkConfig.getMailCheck(email, mailcode),
						ListContBean.class,
						new MyRequestCallBack<ListContBean>() {

							@Override
							public void onSucceed(ListContBean bean) {
								if (bean == null) {
									return;
								}
								UIUtils.showToast(AccountBindActivity.this,
										bean.msg);
								if (Integer.parseInt(bean.status) == 1) {
									ed_accountbind_mailcheck.setText("");
									tv_accountbind_bindmail.setText("已绑定邮箱");
									tv_accountbind_mailsend.setText("已绑定");
									tv_bind_mail.setText("修改绑定邮箱");
									mailTime.onFinish();
									mailTime.cancel();
									KeyBoardUtils.closeKeybord(
											ed_accountbind_mailcheck,
											UIUtils.getContext());
									tv_title_title.requestFocus();

									PrefUtils.setString(
											GlobalConstant.USER_EMAIL, email);
									PrefUtils.setString(
											GlobalConstant.USER_EMAIL_VERIFY,
											bean.status);
								}

							}
						});
			} else {
				UIUtils.showToast(this, "请输入邮箱号");
			}
			break;
		default:
			break;
		}
	}

}
