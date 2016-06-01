package com.aliamauri.meat.weight;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aliamauri.meat.R;


public abstract class Hide_dialog extends DialogFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getDialog().getWindow().setBackgroundDrawable(new ColorDrawable());
		getDialog().setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(DialogInterface arg0, int arg1, KeyEvent arg2) {
				
				return true;
			}
		});
		View view = inflater.inflate(setDialogView(), container);
		initView(view);
		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		setStyle(DialogFragment.STYLE_NORMAL, R.style.MyDialogStyleBottom);
		super.onCreate(savedInstanceState);
	}

	/**
	 * 初始化view
	 * 
	 * @param view
	 */
	public abstract void initView(View view);

	/**
	 * 设置制定布局
	 * 
	 * @return
	 */
	public abstract int setDialogView();

}
