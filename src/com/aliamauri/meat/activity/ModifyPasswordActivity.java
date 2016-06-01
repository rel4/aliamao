package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.ListContBean;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 修改密码
 * 
 * @author ych
 * 
 */
public class ModifyPasswordActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private EditText et_modifypassword_newpwd;
	private EditText et_modifypassword_againpwd;
	private EditText et_modifypassword_oldpwd;
	private TextView tv_modifypassword_submit;
	private TextView tv_modifypassword_reset;

	private RelativeLayout rl_modifypassword_oldpwd;

	@Override
	protected View getRootView() {
		View view = View.inflate(ModifyPasswordActivity.this,
				R.layout.modify_password, null);
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "修改密码";
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

		et_modifypassword_oldpwd = (EditText) findViewById(R.id.et_modifypassword_oldpwd);
		et_modifypassword_newpwd = (EditText) findViewById(R.id.et_modifypassword_newpwd);
		et_modifypassword_againpwd = (EditText) findViewById(R.id.et_modifypassword_againpwd);

		tv_modifypassword_submit = (TextView) findViewById(R.id.tv_modifypassword_submit);
		tv_modifypassword_reset = (TextView) findViewById(R.id.tv_modifypassword_reset);

		rl_modifypassword_oldpwd = (RelativeLayout) findViewById(R.id.rl_modifypassword_oldpwd);

	}

	@Override
	protected void setListener() {
		tv_modifypassword_submit.setOnClickListener(this);
		tv_modifypassword_reset.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_modifypassword_submit:
			String oldpwd = et_modifypassword_oldpwd.getText().toString()
					.trim();
			String newpwd1 = et_modifypassword_newpwd.getText().toString()
					.trim();
			String newpwd2 = et_modifypassword_againpwd.getText().toString()
					.trim();
			if (!newpwd1.equals(newpwd2)) {
				UIUtils.showToast(ModifyPasswordActivity.this, "新密码不一致");
			} else if ("".equals(oldpwd) || "".equals(newpwd1)
					|| "".equals(newpwd2)) {
				UIUtils.showToast(ModifyPasswordActivity.this, "密码不能为空");
			} else if (!CheckUtils.getInstance().isPassWord(oldpwd)
					|| !CheckUtils.getInstance().isPassWord(newpwd1)
					|| !CheckUtils.getInstance().isPassWord(newpwd2)) {
				UIUtils.showToast(ModifyPasswordActivity.this, "请输入6-16位数字、字母");
			} else {
				// UIUtils.showToast(UIUtils.getContext(), "已提交修改");
				httpHelp.sendGet(NetworkConfig.setPassword(oldpwd, newpwd1),
						ListContBean.class,
						new MyRequestCallBack<ListContBean>() {

							@Override
							public void onSucceed(ListContBean bean) {
								if (bean == null) {
									return;
								}
								UIUtils.showToast(UIUtils.getContext(),
										bean.msg);

								if ("1".equals(bean.status)) {
									finish();
									MyApplication
											.getMainActivity()
											.startActivity(
													new Intent(
															ModifyPasswordActivity.this,
															LoginActivity.class));
									MyApplication.getMainActivity().finish();
								}

							}
						});
			}

			break;
		case R.id.tv_modifypassword_reset:
			et_modifypassword_oldpwd.setText("");
			et_modifypassword_newpwd.setText("");
			et_modifypassword_againpwd.setText("");
			break;
		case R.id.iv_title_backicon:
			finish();
			break;

		default:
			break;
		}
	}
}
