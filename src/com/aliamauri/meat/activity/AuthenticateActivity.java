package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.AuthenticateBean;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 认证 *
 * 
 * @author ych
 * 
 */
public class AuthenticateActivity extends BaseActivity implements
		OnClickListener {
	private HttpHelp httpHelp;
	private AuthenticateBean aBean;

	private LinearLayout ll_authenticate_authenti;
	private LinearLayout ll_authenticate_state;
	private RelativeLayout rl_authenticate_statefale;

	private TextView tv_autheticate_go;
	private TextView tv_authenticate_state;
	private TextView tv_authenticate_statename;
	private TextView tv_authenticate_stateidcard;

	private EditText et_authenticate_usrname;
	private EditText et_authenticate_idnumber;

	private ImageView iv_authenticate_usrname;
	private ImageView iv_authenticate_idnumber;

	private Intent authenIntent;

	@Override
	protected View getRootView() {
		View view = View.inflate(AuthenticateActivity.this,
				R.layout.authenticate, null);
		httpHelp = new HttpHelp();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "实名认证";
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
		tv_autheticate_go = (TextView) findViewById(R.id.tv_autheticate_go);
		ll_authenticate_authenti = (LinearLayout) findViewById(R.id.ll_authenticate_authenti);
		ll_authenticate_state = (LinearLayout) findViewById(R.id.ll_authenticate_state);
		rl_authenticate_statefale = (RelativeLayout) findViewById(R.id.rl_authenticate_statefale);
		tv_authenticate_state = (TextView) findViewById(R.id.tv_authenticate_state);
		tv_authenticate_statename = (TextView) findViewById(R.id.tv_authenticate_statename);
		tv_authenticate_stateidcard = (TextView) findViewById(R.id.tv_authenticate_stateidcard);
		et_authenticate_usrname = (EditText) findViewById(R.id.et_authenticate_usrname);
		et_authenticate_idnumber = (EditText) findViewById(R.id.et_authenticate_idnumber);
		iv_authenticate_usrname = (ImageView) findViewById(R.id.iv_authenticate_usrname);
		iv_authenticate_idnumber = (ImageView) findViewById(R.id.iv_authenticate_idnumber);
		setOnFoucusChange(et_authenticate_usrname, iv_authenticate_usrname);
		setOnFoucusChange(et_authenticate_idnumber, iv_authenticate_idnumber);
	}

	@Override
	protected void setListener() {
		tv_autheticate_go.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		netWork();
	}

	private void netWork() {
		httpHelp.sendGet(NetworkConfig.getAuthenticateState(),
				AuthenticateBean.class,
				new MyRequestCallBack<AuthenticateBean>() {

					@Override
					public void onSucceed(AuthenticateBean bean) {
						if (bean == null)
							return;
						aBean = bean;
						initData(bean);
					}
				});
	}

	private void setOnFoucusChange(EditText editText, final ImageView iamgeView) {
		editText.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				iamgeView.setSelected(arg1);
			}
		});
	}

	private void initData(AuthenticateBean bean) {
		if ("0".equals(bean.cont.isvalrealinfo)) {
			tv_autheticate_go.setVisibility(View.VISIBLE);
			tv_autheticate_go.setText("马上认证");
			KeyBoardUtils.openKeybord(et_authenticate_usrname,
					AuthenticateActivity.this);
			ll_authenticate_authenti.setVisibility(View.VISIBLE);
			ll_authenticate_state.setVisibility(View.GONE);
		} else if ("1".equals(bean.cont.isvalrealinfo)) {
			tv_autheticate_go.setVisibility(View.GONE);
			ll_authenticate_authenti.setVisibility(View.GONE);
			ll_authenticate_state.setVisibility(View.VISIBLE);
			rl_authenticate_statefale.setVisibility(View.GONE);
			tv_authenticate_statename.setText(bean.cont.realname);
			tv_authenticate_stateidcard.setText(bean.cont.idnumber);
			tv_authenticate_state.setText("认证成功");

		} else if ("2".equals(bean.cont.isvalrealinfo)) {
			tv_autheticate_go.setVisibility(View.GONE);
			ll_authenticate_authenti.setVisibility(View.GONE);
			ll_authenticate_state.setVisibility(View.VISIBLE);
			rl_authenticate_statefale.setVisibility(View.GONE);
			tv_authenticate_statename.setText(bean.cont.realname);
			tv_authenticate_stateidcard.setText(bean.cont.idnumber);
			tv_authenticate_state.setText("认证中....");

		} else if ("3".equals(bean.cont.isvalrealinfo)) {
			tv_autheticate_go.setVisibility(View.VISIBLE);
			tv_autheticate_go.setText("重新认证");
			ll_authenticate_authenti.setVisibility(View.GONE);
			ll_authenticate_state.setVisibility(View.VISIBLE);
			rl_authenticate_statefale.setVisibility(View.GONE);
			tv_authenticate_state.setVisibility(View.GONE);
			tv_authenticate_statename.setText(bean.cont.realname);
			tv_authenticate_stateidcard.setText(bean.cont.idnumber);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon:
			finish();
			break;
		case R.id.tv_autheticate_go:
			String idnumber = et_authenticate_idnumber.getText().toString()
					.trim();
			if ("0".equals(aBean.cont.isvalrealinfo)) {
				if (CheckUtils.getInstance().isIDCard(idnumber)) {
					httpHelp.sendGet(NetworkConfig
							.getAuthenticate(et_authenticate_usrname.getText()
									.toString().trim(), idnumber),
							BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

								@Override
								public void onSucceed(BaseBaen bean) {
									if (bean == null)
										return;
									UIUtils.showToast(UIUtils.getContext(),
											bean.msg);
								}
							});
				} else {
					UIUtils.showToast(UIUtils.getContext(), "请输入18位身份证号");
				}
			} else if ("1".equals(aBean.cont.isvalrealinfo)) {

			} else if ("2".equals(aBean.cont.isvalrealinfo)) {

			} else if ("3".equals(aBean.cont.isvalrealinfo)) {
			}
			break;
		default:
			break;
		}
	}

}
