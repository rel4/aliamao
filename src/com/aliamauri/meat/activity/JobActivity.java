package com.aliamauri.meat.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 职位
 * 
 * @author ych
 * 
 */
public class JobActivity extends BaseActivity implements OnClickListener {
	private TextView tv_title_right;
	private ListView lv_job_all;
	private String[] jobTVtype;
	private String[] jobTVicon;
	private JobAdapter jobAdapter;
	private Intent jobIntent;
	private static String choseJob = "";

	@Override
	protected View getRootView() {
		View view = View.inflate(JobActivity.this, R.layout.job, null);
		jobIntent = getIntent();
		choseJob = jobIntent.getExtras().get("editdata").toString().trim();
		return view;
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "职业";
	}

	@Override
	protected void initView() {
		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("保存");

		lv_job_all = (ListView) findViewById(R.id.lv_job_all);
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
	}

	@Override
	protected void initNet() {
		initData();
		setLVListener();
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
	
	private void setLVListener() {
		lv_job_all.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				choseJob = jobTVtype[position].trim();
				jobAdapter.notifyDataSetChanged();

			}
		});
	}

	private void initData() {
		String job = "计算机/互联网/通信,生产/工艺/制造,医疗/护理/制药,金融/银行/投资/保险,"
				+ "商业/服务业/个体经营/,文化/广告/传媒,娱乐/艺术/表演,律师/法务,"
				+ "教育/培训,公务员/行政/事业单位,学生,无";
		jobTVtype = job.split(",");
		String icon = "IT,制造,医疗,金融,商业,文化,艺术,法律,教育,行政,学生,无";
		jobTVicon = icon.split(",");
		jobAdapter = new JobAdapter();
		lv_job_all.setAdapter(jobAdapter);
	}

	class JobAdapter extends BaseAdapter {
		ViewHolder viewHolder;

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return jobTVtype.length;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView != null) {
				viewHolder = (ViewHolder) convertView.getTag();
			} else {
				viewHolder = new ViewHolder();
				convertView = View.inflate(JobActivity.this, R.layout.job_item,
						null);
				viewHolder.iv_jobitem_check = (ImageView) convertView
						.findViewById(R.id.iv_jobitem_check);
				viewHolder.tv_jobitem_icon = (TextView) convertView
						.findViewById(R.id.tv_jobitem_icon);
				viewHolder.tv_jobitem_type = (TextView) convertView
						.findViewById(R.id.tv_jobitem_type);
				convertView.setTag(viewHolder);
			}
			if (jobTVtype[position].trim().equals(choseJob)) {
				viewHolder.iv_jobitem_check.setVisibility(View.VISIBLE);

			} else {
				viewHolder.iv_jobitem_check.setVisibility(View.GONE);
			}
			viewHolder.tv_jobitem_type.setText(jobTVtype[position]);
			settv_jobitem_icon(position);
			return convertView;
		}

		private void settv_jobitem_icon(int position) {
			viewHolder.tv_jobitem_icon.setText(jobTVicon[position]);
			if (position == 0 || position == 1 || position == 2) {
				viewHolder.tv_jobitem_icon
						.setBackgroundResource(R.drawable.job_type1);
			} else if (position == 3 || position == 4) {
				viewHolder.tv_jobitem_icon
						.setBackgroundResource(R.drawable.job_type2);
			} else if (position == 5 || position == 6) {
				viewHolder.tv_jobitem_icon
						.setBackgroundResource(R.drawable.job_type3);
			} else if (position == 7 || position == 8 || position == 9) {
				viewHolder.tv_jobitem_icon
						.setBackgroundResource(R.drawable.job_type4);
			} else if (position == 10) {
				viewHolder.tv_jobitem_icon
						.setBackgroundResource(R.drawable.job_type5);
			} else if (position == 11) {
				viewHolder.tv_jobitem_icon.setVisibility(View.GONE);
			}
		}

	}

	class ViewHolder {
		public TextView tv_jobitem_icon;
		public TextView tv_jobitem_type;
		public ImageView iv_jobitem_check;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			jobIntent.putExtra("editdata", choseJob);
			setResult(7, jobIntent);
			finish();
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
