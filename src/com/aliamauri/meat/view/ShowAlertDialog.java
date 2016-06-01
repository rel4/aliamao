package com.aliamauri.meat.view;

/**
 * 鏄剧ず鍑虹敓骞存湀绐楀彛閮ㄤ欢
 */

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.DataCleanManager;
import com.aliamauri.meat.activity.LoginActivity;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.RegisterActivity;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.TimeCountUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.easemob.EMCallBack;
import com.lidroid.xutils.http.RequestParams;

@SuppressLint("NewApi")
public class ShowAlertDialog {
	private View dialogView;
	private TextView tv_sd_message;
	private TextView tv_sd_yes;
	private TextView tv_sd_cancel;
	private Activity activity;
	private AlertDialog.Builder builder;
	AlertDialog create;
	private HttpHelp httpHelp;

	public ShowAlertDialog(Activity activity, String message, String type) {
		this.activity = activity;
		builder = new AlertDialog.Builder(activity);
		dialogView = View.inflate(activity, R.layout.show_dialog, null);
		tv_sd_message = (TextView) dialogView.findViewById(R.id.tv_sd_message);
		tv_sd_yes = (TextView) dialogView.findViewById(R.id.tv_sd_yes);
		tv_sd_cancel = (TextView) dialogView.findViewById(R.id.tv_sd_cancel);

		tv_sd_message.setText(message);
		if ("quit".equals(type)) {
			initQuit();
		} else if ("editdata".equals(type)) {
			initEditdata();
		}

		builder.setView(dialogView);
		create = builder.create();
		create.show();

	}

	public ShowAlertDialog(final RegisterActivity activity, String message) {
		builder = new AlertDialog.Builder(activity,
				AlertDialog.THEME_HOLO_LIGHT);
		dialogView = View.inflate(UIUtils.getContext(),
				R.layout.register_dialog, null);
		tv_sd_message = (TextView) dialogView.findViewById(R.id.tv_rd_message);
		tv_sd_yes = (TextView) dialogView.findViewById(R.id.tv_rd_yes);
		tv_sd_cancel = (TextView) dialogView.findViewById(R.id.tv_rd_cancel);

		tv_sd_message.setText(message);

		builder.setView(dialogView);
		create = builder.create();

		tv_sd_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.getUserId();
				create.dismiss();
			}
		});
		tv_sd_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				create.dismiss();
			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
				// TODO Auto-generated method stub
				activity.setSexSelect(3, true);
			}
		});
		create.show();

	}

	// 登录页面
	private String phoneNumber;
	private TimeCountUtils timeCount;
	private RelativeLayout rl_progress;
	private EditText et_lfd_tel;
	private EditText et_lfd_phonecode;
	private LinearLayout rl_lfd_male;
	private LinearLayout rl_lfd_female;
	private TextView tv_lfd_getphonecode;

	public ShowAlertDialog(final LoginActivity activity) {
		builder = new AlertDialog.Builder(activity,
				AlertDialog.THEME_HOLO_LIGHT);
		dialogView = View.inflate(UIUtils.getContext(),
				R.layout.login_fromsouso_dialog, null);

		builder.setView(dialogView);
		create = builder.create();
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		rl_progress = (RelativeLayout) dialogView
				.findViewById(R.id.rl_progress);

		et_lfd_tel = (EditText) dialogView.findViewById(R.id.et_lfd_tel);
		et_lfd_phonecode = (EditText) dialogView
				.findViewById(R.id.et_lfd_phonecode);
		rl_lfd_male = (LinearLayout) dialogView.findViewById(R.id.rl_lfd_male);
		rl_lfd_female = (LinearLayout) dialogView
				.findViewById(R.id.rl_lfd_female);

		tv_lfd_getphonecode = (TextView) dialogView
				.findViewById(R.id.tv_lfd_getphonecode);

		RelativeLayout rl_lfd_rl1 = (RelativeLayout) dialogView
				.findViewById(R.id.rl_lfd_rl1);

		TextView tv_lfd_tel = (TextView) dialogView
				.findViewById(R.id.tv_lfd_tel);

		View v_lfd_view1 = dialogView.findViewById(R.id.v_lfd_view1);
		View v_lfd_view2 = dialogView.findViewById(R.id.v_lfd_view2);

		if ("0".equals(PrefUtils.getString(GlobalConstant.USER_TEL_VERIFY, "0"))) {
			tv_lfd_tel.setVisibility(View.GONE);
			v_lfd_view1.setVisibility(View.GONE);
			v_lfd_view2.setVisibility(View.GONE);
		} else {
			tv_lfd_tel.setVisibility(View.VISIBLE);
			v_lfd_view1.setVisibility(View.VISIBLE);
			v_lfd_view2.setVisibility(View.VISIBLE);
			rl_lfd_rl1.setVisibility(View.GONE);
			et_lfd_tel.setVisibility(View.GONE);
			et_lfd_phonecode.setVisibility(View.GONE);
			tv_lfd_getphonecode.setVisibility(View.GONE);
		}
		tv_lfd_tel.setText("您已绑定手机号:"
				+ PrefUtils.getString(GlobalConstant.USER_TEL, ""));
		tv_lfd_getphonecode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (CheckUtils.getInstance().isMobile(
						et_lfd_tel.getText().toString())) {
					if (timeCount == null) {
						timeCount = new TimeCountUtils(60000, 1000,
								tv_lfd_getphonecode, et_lfd_tel);
					}
					tv_lfd_getphonecode.setSelected(false);
					tv_lfd_getphonecode.setClickable(false);
					tv_lfd_getphonecode.setFocusable(false);
					timeCount.start();
					phoneNumber = et_lfd_tel.getText().toString();
					// 手机网络请求 发送验证码
					httpHelp.sendGet(NetworkConfig.getPhoneCode(phoneNumber),
							BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

								@Override
								public void onSucceed(BaseBaen bean) {
									if (bean == null) {
										timeCount.onFinish();
										timeCount.cancel();
										return;
									}
									if (!"1".equals(bean.status)) {
										timeCount.onFinish();
										timeCount.cancel();
									}
									UIUtils.showToast(UIUtils.getContext(),
											bean.msg);
								}
							});

				} else {
					UIUtils.showToast(UIUtils.getContext(), "请输入正确的手机");
				}
			}
		});
		et_lfd_tel.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				if (CheckUtils.getInstance().isMobile(
						et_lfd_tel.getText().toString())) {
					tv_lfd_getphonecode.setSelected(true);
					tv_lfd_getphonecode.setClickable(true);
					tv_lfd_getphonecode.setFocusable(true);
					if (timeCount != null && !timeCount.isFinish()) {
						tv_lfd_getphonecode.setText("重新发送");
						timeCount.onFinish();
						timeCount.cancel();
					} else if (et_lfd_tel.getText().toString()
							.equals(phoneNumber)) {
						tv_lfd_getphonecode.setText("重新发送");
					}
				} else {
					tv_lfd_getphonecode.setText("获取验证码");
					tv_lfd_getphonecode.setSelected(false);
					tv_lfd_getphonecode.setClickable(false);
					tv_lfd_getphonecode.setFocusable(false);
					if (timeCount != null && !timeCount.isFinish()) {
						timeCount.onFinish();
						timeCount.cancel();
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		rl_lfd_male.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (et_lfd_phonecode.getText().toString().length() == 4
						|| "1".equals(PrefUtils.getString(
								GlobalConstant.USER_TEL_VERIFY, ""))) {
					if (rl_progress != null)
						rl_progress.setVisibility(View.VISIBLE);
					setGenterSelect(0);
					RequestParams params = new RequestParams();
					params.addBodyParameter("tel",
							phoneNumber != null ? phoneNumber : "");
					params.addBodyParameter("code", et_lfd_phonecode.getText()
							.toString());
					params.addBodyParameter("sex", "1");
					httpHelp.sendPost(
							NetworkConfig.getRegister_go_register_active(),
							params, BaseBaen.class,
							new MyRequestCallBack<BaseBaen>() {

								@Override
								public void onSucceed(BaseBaen bean) {
									setGenterSelect(2);
									if (timeCount != null) {
										timeCount.onFinish();
										timeCount.cancel();
									}
									if (rl_progress != null)
										rl_progress.setVisibility(View.GONE);
									if (bean == null) {
										return;
									}
									UIUtils.showToast(UIUtils.getContext(),
											bean.msg);
									if ("1".equals(bean.status)) {
										create.dismiss();
										if (CheckUtils.getInstance().isMobile(
												phoneNumber)) {
											PrefUtils.setString(
													GlobalConstant.USER_TEL,
													phoneNumber);
										}
										PrefUtils.setBoolean(
												UIUtils.getContext(),
												GlobalConstant.IS_LOGINED, true);
										activity.Login(
												PrefUtils
														.getString(
																GlobalConstant.USER_HXUID,
																""),
												PrefUtils
														.getString(
																GlobalConstant.USER_HXPWD,
																""));
									}
								}
							});
				} else {
					UIUtils.showToast(UIUtils.getContext(), "请输入正确的验证码");
				}

			}
		});
		rl_lfd_female.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (et_lfd_phonecode.getText().toString().length() == 4
						|| "1".equals(PrefUtils.getString(
								GlobalConstant.USER_TEL_VERIFY, ""))) {
					if (rl_progress != null)
						rl_progress.setVisibility(View.VISIBLE);
					setGenterSelect(1);
					RequestParams params = new RequestParams();
					params.addBodyParameter("tel",
							phoneNumber != null ? phoneNumber : "");
					params.addBodyParameter("code", et_lfd_phonecode.getText()
							.toString());
					params.addBodyParameter("sex", "0");
					httpHelp.sendPost(
							NetworkConfig.getRegister_go_register_active(),
							params, BaseBaen.class,
							new MyRequestCallBack<BaseBaen>() {

								@Override
								public void onSucceed(BaseBaen bean) {
									setGenterSelect(2);
									if (timeCount != null) {
										timeCount.onFinish();
										timeCount.cancel();
									}
									if (rl_progress != null)
										rl_progress.setVisibility(View.GONE);
									if (bean == null) {
										return;
									}
									UIUtils.showToast(UIUtils.getContext(),
											bean.msg);
									if ("1".equals(bean.status)) {
										create.dismiss();
										if (CheckUtils.getInstance().isMobile(
												phoneNumber)) {
											PrefUtils.setString(
													GlobalConstant.USER_TEL,
													phoneNumber);
										}
										PrefUtils.setBoolean(
												UIUtils.getContext(),
												GlobalConstant.IS_LOGINED, true);
										activity.Login(
												PrefUtils
														.getString(
																GlobalConstant.USER_HXUID,
																""),
												PrefUtils
														.getString(
																GlobalConstant.USER_HXPWD,
																""));
									}
								}
							});
				} else {
					UIUtils.showToast(UIUtils.getContext(), "请输入正确的验证码");
				}
			}
		});
		rl_progress.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

			}
		});
		builder.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface arg0) {
			}
		});
		create.show();

	}

	protected void setGenterSelect(int type) {
		if (type == 0) {
			rl_lfd_male.setSelected(true);
		} else {
			rl_lfd_male.setSelected(false);
		}
		if (type == 1) {
			rl_lfd_female.setSelected(true);
		} else {
			rl_lfd_female.setSelected(false);
		}
		if (type == 2) {
			rl_lfd_male.setSelected(false);
			rl_lfd_female.setSelected(false);
		}
	}

	private void initEditdata() {
		tv_sd_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.finish();
				create.dismiss();
			}
		});
		tv_sd_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				create.dismiss();
			}
		});
	}

	void logout() {
		final ProgressDialog pd = new ProgressDialog(
				MyApplication.getMainActivity());
		String st = MyApplication.getMainActivity().getResources()
				.getString(R.string.Are_logged_out);
		pd.setMessage(st);
		pd.setCanceledOnTouchOutside(false);
		pd.show();
		MySDKHelper.getInstance().logout(true, new EMCallBack() {

			@Override
			public void onSuccess() {
				MyApplication.getMainActivity().runOnUiThread(new Runnable() {
					public void run() {
						pd.dismiss();
						// 重新显示登陆页面
						((MainActivity) MyApplication.getMainActivity())
								.finish();
						MyApplication.getMainActivity().startActivity(
								new Intent(MyApplication.getMainActivity(),
										LoginActivity.class));

					}
				});
			}

			@Override
			public void onProgress(int progress, String status) {

			}

			@Override
			public void onError(int code, String message) {
				MyApplication.getMainActivity().runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						pd.dismiss();
						Toast.makeText(MyApplication.getMainActivity(),
								"unbind devicetokens failed",
								Toast.LENGTH_SHORT).show();

					}
				});
			}
		});
	}

	private void initQuit() {
		tv_sd_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// DbOpenHelper_dynamic.getInstance().closeDB();//确认关闭数据库
				create.dismiss();
				Intent i = new Intent(activity, LoginActivity.class);
				i.putExtra("splash",
						PrefUtils.getString(GlobalConstant.USER_ID, ""));
				PrefUtils.clearSP(UIUtils.getContext());
				activity.startActivity(i);
				activity.finish();
				/*
				 * 退出账户清除所有的缓存
				 */
				DataCleanManager.getInstance().cleanApplicationData(
						UIUtils.getContext(),
						GlobalConstant.HEAD_ICON_SAVEPATH,
						GlobalConstant.DT_UPDATE_ICON_SAVEPATH,
						GlobalConstant.YJZQ_HEAD_ICON_SAVEPATH,
						GlobalConstant.DYNAMIC_SMALLIMAGE_SAVEPATH,
						GlobalConstant.DYNAMIC_BIGIMAGE_SAVEPATH,
						GlobalConstant.DYNAMIC_HEADIMAGE_SAVEPATH,
						GlobalConstant.DYNAMIC_MOVICE_SAVEPATH,
						GlobalConstant.DYNAMIC_VIDEOIMGBG_SAVEPATH);
				logout();
			}
		});
		tv_sd_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				create.dismiss();
			}
		});
	}

}
