package com.aliamauri.meat.activity.search_activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.adapter.AppBaseAdapter;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.RecommendDetailBean;
import com.aliamauri.meat.bean.RecommendDetailBean.Cont.Baseinfo;
import com.aliamauri.meat.bean.RecommendDetailBean.Cont.Listinfo;
import com.aliamauri.meat.bean.RecommendDetailBean.Cont.Listinfo.Cdata;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.play.PlaySourceActivity;
import com.aliamauri.meat.utils.UIUtils;
import com.umeng.analytics.MobclickAgent;

/**
 * 专题详情
 * 
 * @author admin
 * 
 */
public class RecommendDetailActivity extends BaseActivity implements
		OnItemClickListener {
	private String recid;
	private String recname;
	private ListView lv_recommend_detail;
	private HttpHelp httpHelp;
	private RecommendDetailBean mBean;
	private List<Cdata> cdatas;
	private List<Listinfo> listInfos;

	@Override
	protected void initView() {
		Intent intent = getIntent();
		if (intent == null) {
			finish();
		}
		recid = intent.getStringExtra(GlobalConstant.INTENT_ID);
		recname = intent.getStringExtra(GlobalConstant.INTENT_NAME);
		init();
	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	protected View getRootView() {
		// TODO Auto-generated method stub
		return UIUtils.inflate(R.layout.activity_recommend_detail);
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
	
	protected void initNet() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		httpHelp.sendGet(NetworkConfig.getRecommendDetail(recid),
				RecommendDetailBean.class,
				new MyRequestCallBack<RecommendDetailBean>() {

					@Override
					public void onSucceed(RecommendDetailBean bean) {
						if (bean == null || bean.cont.listinfo == null
								|| bean.cont.listinfo.size() == 0) {
							return;
						}
						mBean = bean;
						listInfos = mBean.cont.listinfo;
						if (cdatas == null) {
							cdatas = new ArrayList<Cdata>();
						}
						for (int i = 0; i < listInfos.size(); i++) {
							List<Cdata> cdata = listInfos.get(i).cdata;
							if (cdata.size() > 0) {
								cdata.get(0).isMianTiatal = true;
								cdata.get(0).position = i;
							}
							cdatas.addAll(cdata);
						}
						setAapter();
					}
				});
	}

	protected void setAapter() {
		if (recommendDetailAdapter == null) {
			recommendDetailAdapter = new RecommendDetailAdapter(
					cdatas.size() + 1);
		}
		lv_recommend_detail.setAdapter(recommendDetailAdapter);

	}

	private void init() {
		lv_recommend_detail = (ListView) findViewById(R.id.lv_recommend_detail);
		lv_recommend_detail.setOnItemClickListener(this);
		((TextView) findViewById(R.id.tv_title_title)).setText(recname);

	}

	// @Override
	// protected String getCurrentTitle() {
	// return "专题详情";
	// }
	public static final int HEAD_VIEW = 0;
	public static final int DUFULT_VIEW = 1;
	private RecommendDetailAdapter recommendDetailAdapter;

	private class RecommendDetailAdapter extends AppBaseAdapter {

		public RecommendDetailAdapter(int count) {
			super(count);
		}

		@Override
		public int getViewTypeCount() {
			// TODO Auto-generated method stub
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0) {
				return HEAD_VIEW;
			}
			return DUFULT_VIEW;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int itemViewType = getItemViewType(position);
			ViewHeadHolder headHolder = null;
			DufultViewHolder dufultHolder = null;
			if (convertView == null) {
				switch (itemViewType) {
				case HEAD_VIEW:
					convertView = UIUtils
							.inflate(R.layout.item_recommend_detail_head);
					headHolder = new ViewHeadHolder();
					headHolder.setView(convertView);
					break;
				case DUFULT_VIEW:
					convertView = UIUtils
							.inflate(R.layout.item_recommend_detail);
					dufultHolder = new DufultViewHolder();
					dufultHolder.setView(convertView);
					break;
				}
			} else {
				switch (itemViewType) {
				case HEAD_VIEW:
					headHolder = (ViewHeadHolder) convertView.getTag();
					break;
				case DUFULT_VIEW:
					dufultHolder = (DufultViewHolder) convertView.getTag();
					break;
				}
			}
			switch (itemViewType) {
			case HEAD_VIEW:
				headHolder.setDate();
				break;
			case DUFULT_VIEW:
				dufultHolder.setDate(position - 1);
				break;
			}
			return convertView;
		}
	}

	private class ViewHeadHolder {

		private TextView tv_detail_tital, tv_detail_context, tv_datail_time,
				tv_datail_reader_num;

		public void setDate() {
			Baseinfo baseinfo = mBean.cont.baseinfo;
			if (baseinfo == null) {
				return;
			}
			tv_datail_time.setText(baseinfo.updatetime);
			tv_detail_context.setText(baseinfo.desc);
			tv_detail_tital.setText(baseinfo.name);
			tv_datail_reader_num.setText(baseinfo.click + "阅读");
		}

		public void setView(View convertView) {
			Baseinfo baseinfo = mBean.cont.baseinfo;
			if (baseinfo == null) {
				convertView.setVisibility(View.GONE);

			} else {
				convertView.setVisibility(View.VISIBLE);
			}
			tv_detail_tital = (TextView) convertView
					.findViewById(R.id.tv_detail_tital);
			tv_detail_context = (TextView) convertView
					.findViewById(R.id.tv_detail_context);
			tv_datail_time = (TextView) convertView
					.findViewById(R.id.tv_datail_time);
			tv_datail_reader_num = (TextView) convertView
					.findViewById(R.id.tv_datail_reader_num);
			convertView.setTag(this);

		}

	}

	private class DufultViewHolder {
		private View detail_main_tital_layout;
		private TextView tv_main_detail_catname, tv_main_detail_context,
				tv_detail_reader_num, tv_detail_name, tv_detail_des;
		private ImageView iv_tv_iocn;

		// private int mainPosition = 0;

		public void setView(View convertView) {

			detail_main_tital_layout = convertView
					.findViewById(R.id.detail_main_tital_layout);
			tv_main_detail_catname = (TextView) convertView
					.findViewById(R.id.tv_main_detail_catname);
			tv_main_detail_context = (TextView) convertView
					.findViewById(R.id.tv_main_detail_context);
			tv_detail_name = (TextView) convertView
					.findViewById(R.id.tv_detail_name);
			tv_detail_des = (TextView) convertView
					.findViewById(R.id.tv_detail_des);
			tv_detail_reader_num = (TextView) convertView
					.findViewById(R.id.tv_detail_reader_num);
			iv_tv_iocn = (ImageView) convertView.findViewById(R.id.iv_tv_iocn);

			convertView.setTag(this);
		}

		public void setDate(int position) {
			Cdata cdata = cdatas.get(position);
			int mainPosition = cdata.position;
			int size = listInfos.size();
			if (size > 1 && mainPosition < size) {
				Listinfo listinfo = mBean.cont.listinfo.get(mainPosition);
				if (cdata.isMianTiatal) {
					detail_main_tital_layout.setVisibility(View.VISIBLE);

					tv_main_detail_catname.setText(listinfo.catname);
					if ("".endsWith(listinfo.catdesc)) {
						tv_main_detail_context.setVisibility(View.GONE);
					} else {
						tv_main_detail_context.setVisibility(View.VISIBLE);
						tv_main_detail_context.setText(listinfo.catdesc);
					}
					// mainPosition++;
				} else {
					detail_main_tital_layout.setVisibility(View.GONE);
				}
			}

			if (httpHelp == null) {
				httpHelp = new HttpHelp();
			}
			httpHelp.showImage(iv_tv_iocn, cdata.pic);
			tv_detail_name.setText(cdata.name);
			String type = cdata.type;
			if (type == null || "".equals(type) || "0".equals(type)) {
				type = "暂无";
			}
			tv_detail_reader_num.setText("类型: " + type);
			tv_detail_des.setText(cdata.desc);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		if (mBean == null || position == 0 || cdatas == null
				|| cdatas.size() == 0) {
			return;
		}
		final String cdatasId = cdatas.get(position - 1).id;
		if ("".equals(cdatasId)) {
			return;
		}
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		httpHelp.sendGet(NetworkConfig.getReqPlay(cdatasId, 0), BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						if (bean != null && bean.status.equals("1")) {
							Intent intent = new Intent(
									RecommendDetailActivity.this,
									PlaySourceActivity.class);
							intent.putExtra(GlobalConstant.TV_ID, cdatasId);
							startActivity(intent);
						} else {
							UIUtils.showToast(UIUtils.getContext(), "暂时没有数据");
						}

					}
				});

	}

}
