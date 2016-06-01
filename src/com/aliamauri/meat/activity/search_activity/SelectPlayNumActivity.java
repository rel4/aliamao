package com.aliamauri.meat.activity.search_activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.adapter.AppBaseAdapter;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.SearchRseBean.Cont.Cfilm_area;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.StringNumUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

public class SelectPlayNumActivity extends Activity implements
		OnItemClickListener, OnClickListener {

	private int tv_all_num;
	private int k_id;// 库ID
	private GridView gv_tv_num;
	private int tvNum;// 剧集
	private HttpHelp httpHelp;
	private Cfilm_area cfilm_area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(getInitView());
		initView();
		// initNet();
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
	
	// @Override
	protected View getInitView() {
		cfilm_area = (Cfilm_area) getIntent().getSerializableExtra(
				GlobalConstant.TV_ID_AND_TVNUM);

		if (cfilm_area != null) {

			tv_all_num = (int) StringNumUtils.string2Num(cfilm_area.jujinum);
			k_id = (int) StringNumUtils.string2Num(cfilm_area.id);
		}
		return UIUtils.inflate(R.layout.activity_select_play_num);
	}

	// @Override
	protected void initView() {
		gv_tv_num = (GridView) findViewById(R.id.gv_tv_num);
		((TextView) findViewById(R.id.tv_title)).setText("选集");
		findViewById(R.id.ibtn_left_home_back).setOnClickListener(this);
		ItemAdapter itemAdapter = new ItemAdapter(tv_all_num);
		gv_tv_num.setAdapter(itemAdapter);
		gv_tv_num.setOnItemClickListener(this);
	}

	public void setItemAdapter(GridView view, int position) {

		// 选集条目点击事件
		// view.setOnItemClickListener(this);
	}

	private class ItemAdapter extends AppBaseAdapter {
		private int pagePosition;

		public ItemAdapter(int position) {
			super(position);
			this.pagePosition = position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolderCross holder;
			if (convertView == null) {
				holder = new ViewHolderCross();
				convertView = View.inflate(SelectPlayNumActivity.this,
						R.layout.item_scroll_view, null);
				holder.setView(convertView);
			} else {
				holder = (ViewHolderCross) convertView.getTag();
			}
			holder.setPopuData(pagePosition, position);
			return convertView;
		}
	}

	private class ViewHolderCross {
		TextView tv_scroll;

		public void setView(View convertView) {
			tv_scroll = (TextView) convertView.findViewById(R.id.tv_scroll);
			convertView.setTag(this);
		}

		public void setPopuData(final int pagePosition, final int position) {
			tv_scroll.setText(position + 1 + "");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}

		httpHelp.sendGet(NetworkConfig.getReqPlay(k_id + "", (position + 1)),
				BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean == null || "0".equals(bean.status)) {
							UIUtils.showToast(UIUtils.getContext(),
									(position + 1) + "集暂时没有资源");
							return;
						}
//						Intent intent = new Intent(SelectPlayNumActivity.this,
//								aaaaa.class);
//						intent.putExtra(GlobalConstant.TV_ID, cfilm_area.id
//								+ GlobalConstant.FLAG_APP_SPLIT
//								+ (position + 1));
//						startActivity(intent);
					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibtn_left_home_back:
			finish();
			break;

		}

	}

}
