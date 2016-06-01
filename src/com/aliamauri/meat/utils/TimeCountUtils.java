package com.aliamauri.meat.utils;

import android.os.CountDownTimer;
import android.widget.EditText;
import android.widget.TextView;

public class TimeCountUtils extends CountDownTimer {
	private TextView textView;
	private boolean isFinish = false;
	private EditText et_lfd_tel;

	public TimeCountUtils(long millisInFuture, long countDownInterval,
			TextView textView, EditText et_lfd_tel) {
		// 参数依次为总时长,和计时的时间间隔
		super(millisInFuture, countDownInterval);
		this.textView = textView;
		this.et_lfd_tel = et_lfd_tel;
		this.textView.setSelected(false);
		this.textView.setClickable(false);
		this.textView.setFocusable(false);

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
		if (CheckUtils.getInstance().isMobile(et_lfd_tel.getText().toString())) {
			this.textView.setSelected(true);
			this.textView.setClickable(true);
			this.textView.setFocusable(true);
		}

	}
}