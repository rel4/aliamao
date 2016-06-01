package com.aliamauri.meat.activity;

import java.util.List;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseTopBean;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.top.adapter.ListBaseAdapter;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SpecialItemActivity extends BaseActivity {
	private HttpHelp httpHelp;
	private PullToRefreshListView ptr_aspecial_list;
	private ListBaseAdapter listBaseAdapter;
	private List<BaseTopBean> datas;

	@Override
	protected View getRootView() {
		return View.inflate(UIUtils.getContext(), R.layout.activity_special,
				null);
	}

	@Override
	protected void initView() {
		ptr_aspecial_list = (PullToRefreshListView) findViewById(R.id.ptr_aspecial_list);
		initLVData();
	}

	@Override
	protected void initNet() {
		super.initNet();
	}

	public HttpHelp getHttpHelp() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		return httpHelp;
	}

	private void initLVData() {
		if (listBaseAdapter == null) {
			listBaseAdapter = new ListBaseAdapter(datas);
			ptr_aspecial_list.setAdapter(listBaseAdapter);
		} else {
			listBaseAdapter.notifyDataSetChanged();
		}
	}

}
