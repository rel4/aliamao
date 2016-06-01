package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * ID搜索
 * 
 * @author ych
 * 
 */
public class NickNameActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title_title;
	private TextView tv_title_right;
	private EditText et_nickname_name;
	private Intent nickname;
	private ImageButton ib_nickname_delete;
	private String hintText = "";

	@Override
	protected View getRootView() {
		View view = View.inflate(NickNameActivity.this,
				R.layout.mydata_nickname, null);
		nickname = getIntent();
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
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("保存");
		ib_nickname_delete = (ImageButton) findViewById(R.id.ib_nickname_delete);
		et_nickname_name = (EditText) findViewById(R.id.et_nickname_name);
		if (nickname.getStringExtra("editdata") != null) {
			hintText = nickname.getExtras().get("editdata") + "";
			et_nickname_name.setText(nickname.getStringExtra("editdata") + "");
			et_nickname_name.setSelection(et_nickname_name.getText().toString()
					.length());
		}
		if (nickname.getStringExtra("talkdata") != null) {
			tv_title_title.setText("话题名字");
		} else {
			tv_title_title.setText("用户昵称");
		}

	}

	@Override
	protected void setListener() {
		ib_nickname_delete.setOnClickListener(this);
		tv_title_right.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			String nickname = et_nickname_name.getText().toString().trim();
			Intent i = new Intent();
			if ("".equals(nickname)) {
				UIUtils.showToast(UIUtils.getContext(), "请输入昵称");
			} else if (!CheckUtils.getInstance().isNickName(nickname)) {
				UIUtils.showToast(UIUtils.getContext(), "请输入1-12个字符的字母，数字、中文");
			} else {
				i.putExtra("editdata", et_nickname_name.getText().toString()
						.trim());
				setResult(2, i);
				finish();
			}
			break;
		case R.id.ib_nickname_delete:
			et_nickname_name.setText("");
			et_nickname_name.setHint("");
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}
}
