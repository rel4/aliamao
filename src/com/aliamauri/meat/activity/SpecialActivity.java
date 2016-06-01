package com.aliamauri.meat.activity;

import android.content.Intent;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.UIUtils;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class SpecialActivity extends BaseActivity {
	private HttpHelp httpHelp;
	private PullToRefreshListView ptr_aspecial_list;
	private SpecialAdapter specialAdapter;

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
		if (specialAdapter == null) {
			specialAdapter = new SpecialAdapter();
			ptr_aspecial_list.setAdapter(specialAdapter);
			ptr_aspecial_list.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					UIUtils.showToast(UIUtils.getContext(), "" + (position - 1));
					startActivity(new Intent(SpecialActivity.this,
							SpecialItemActivity.class));
				}
			});
		} else {
			specialAdapter.notifyDataSetChanged();
		}
	}

	class SpecialAdapter extends BaseAdapter {

		private ViewHolder viewHolder;
		private SpannableString ss;

		@Override
		public int getCount() {
			return 5;
		}  

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(UIUtils.getContext(),
						R.layout.item_recommend_special_list, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_film_name = (TextView) convertView
						.findViewById(R.id.tv_film_name);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			ss = new SpannableString(test + position);

			ss.setSpan(new ForegroundColorSpan(Color.RED), 0, 5,
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			ss.setSpan(new ForegroundColorSpan(Color.GREEN), 5, ss.length(),
					Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			viewHolder.tv_film_name.setText(ss);
			return convertView;
		}

	}

	private String test = "今天天气好吗？挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的挺好的";

	class ViewHolder {
		public TextView tv_film_name;
	}
}
