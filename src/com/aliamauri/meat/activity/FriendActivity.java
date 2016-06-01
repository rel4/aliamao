package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.Cont;
import com.aliamauri.meat.bean.FriendBean;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CaculationUtils;
import com.aliamauri.meat.utils.CharacterParser;
import com.aliamauri.meat.utils.PinyinComparator;
import com.aliamauri.meat.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 好友
 * 
 * @author ych
 * 
 */
public class FriendActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;

	private TextView tv_title_right;
	private ListView lv_friend_all;
	private FriendBean fBean;
	private EditText et_friend_select;
	private FriendAdapter friendAdapter;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private PinyinComparator pinyinComparator;

	@Override
	protected View getRootView() {
		View view = View.inflate(FriendActivity.this, R.layout.friend, null);
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
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "好友";
	}

	@Override
	protected void initView() {
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();

		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("添加");
		et_friend_select = (EditText) findViewById(R.id.et_friend_select);

		lv_friend_all = (ListView) findViewById(R.id.lv_friend_all);
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
		et_friend_select.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void initNet() {
		httpHelp = new HttpHelp();
		netWork();
		super.initNet();
	}

	private void netWork() {
		httpHelp.sendGet(NetworkConfig.getFriend(), FriendBean.class,
				new MyRequestCallBack<FriendBean>() {

					@Override
					public void onSucceed(FriendBean bean) {
						if (bean == null)
							return;
						fBean = bean;
						initData(bean);
					}
				});
	}

	private void initData(FriendBean bean) {
		filledData(bean);
		// 根据a-z进行排序源数据
		Collections.sort(bean.cont, pinyinComparator);
		friendAdapter = new FriendAdapter(bean);
		lv_friend_all.setAdapter(friendAdapter);

	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<Cont> cont = new ArrayList<Cont>();
		if (TextUtils.isEmpty(filterStr)) {
			cont = fBean.cont;
		} else {
			cont.clear();
			for (Cont c : fBean.cont) {
				String name = c.nickname;
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					cont.add(c);
				}
			}
		}

		// 根据a-z进行排序
		// fBean.cont.addAll(cont);
		// 根据a-z进行排序
		Collections.sort(cont, pinyinComparator);
		friendAdapter.updateListView(cont);
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(FriendBean date) {

		for (int i = 0; i < date.cont.size(); i++) {
			// 汉字转换成拼音
			String pinyin = characterParser
					.getSelling(date.cont.get(i).nickname);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				date.cont.get(i).title = sortString.toUpperCase();
			} else {
				date.cont.get(i).title = "#";
			}

		}

	}

	class FriendAdapter extends BaseAdapter implements SectionIndexer {
		private List<Cont> cont = new ArrayList<Cont>();
		private ViewHolder viewHolder;

		public FriendAdapter(FriendBean friendBean) {

			this.cont.addAll(friendBean.cont);
		}

		/**
		 * 当ListView数据发生变化时,调用此方法来更新ListView
		 * 
		 * @param list
		 */
		public void updateListView(List<Cont> cont) {
			this.cont.clear();
			this.cont.addAll(cont);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return this.cont.size();
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
				convertView = View.inflate(FriendActivity.this,
						R.layout.friend_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tv_friend_classify = (TextView) convertView
						.findViewById(R.id.tv_friend_classify);
				viewHolder.tv_friend_name = (TextView) convertView
						.findViewById(R.id.tv_friend_name);
				viewHolder.tv_frienditem_sex = (TextView) convertView
						.findViewById(R.id.tv_frienditem_sex);
				viewHolder.tv_frienditem_distance = (TextView) convertView
						.findViewById(R.id.tv_frienditem_distance);
				viewHolder.ci_frienditem_icon = (CircleImageView) convertView
						.findViewById(R.id.ci_frienditem_icon);
				viewHolder.tv_fi_authenticate = (TextView) convertView
						.findViewById(R.id.tv_fi_authenticate);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			httpHelp.showImage(viewHolder.ci_frienditem_icon,
					cont.get(position).face + "##");
			viewHolder.tv_friend_name.setText(cont.get(position).nickname);
			viewHolder.tv_frienditem_distance
					.setText(cont.get(position).distance);
			if ("1".equals(cont.get(position).sex)) {
				viewHolder.tv_frienditem_sex.setSelected(false);
			} else if ("0".equals(cont.get(position).sex)) {
				viewHolder.tv_frienditem_sex.setSelected(true);
			}
			if (cont.get(position).birth == null) {
				viewHolder.tv_frienditem_sex.setText("未知");
			} else {
				viewHolder.tv_frienditem_sex.setText(CaculationUtils
						.calculateDatePoor(cont.get(position).birth));
			}

			if ("0".equals(cont.get(position).issmval)) {
				viewHolder.tv_fi_authenticate.setVisibility(View.GONE);
			} else if ("1".equals(cont.get(position).issmval)) {
				viewHolder.tv_fi_authenticate.setVisibility(View.VISIBLE);
			}
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)) {
				viewHolder.tv_friend_classify.setVisibility(View.VISIBLE);
				viewHolder.tv_friend_classify.setText(cont.get(position).title);
			} else {
				viewHolder.tv_friend_classify.setVisibility(View.GONE);
			}
			return convertView;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {
			return cont.get(position).title.charAt(0);
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = cont.get(i).title;
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

		@Override
		public Object[] getSections() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	class ViewHolder {
		private CircleImageView ci_frienditem_icon;
		private TextView tv_friend_classify;
		private TextView tv_friend_name;
		private TextView tv_frienditem_sex;
		private TextView tv_frienditem_distance;
		private TextView tv_fi_authenticate;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_title_right:
			startActivity(new Intent(this, SelectUserActivity.class));
			break;
		case R.id.iv_title_backicon:
			finish();
			break;
		default:
			break;
		}
	}

}
