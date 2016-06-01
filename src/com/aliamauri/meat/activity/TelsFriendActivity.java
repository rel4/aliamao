package com.aliamauri.meat.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.CmdManager;
import com.aliamauri.meat.Manager.CmdManager.CmdManagerCallBack;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.TelsFriendBean;
import com.aliamauri.meat.bean.cont.TelFriend.TelFriends;
import com.aliamauri.meat.db.telsfriend.TelsFriendDao;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CharacterParser;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.PhoneInfoUtils;
import com.aliamauri.meat.utils.PinyinComparatorTelFriend;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

public class TelsFriendActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private TelsFriendBean telsBean;
	private TelsAdapter telsAdapter;
	private PullToRefreshListView ptr_tpl_title;

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	private PinyinComparatorTelFriend pinyinComparator;

	private EditText et_atf_search;
	private TextView tv_atf_search;
	private TextView tv_at_forshow1;

	private List<TelFriends> listFriend;

	@Override
	protected View getRootView() {
		return View.inflate(UIUtils.getContext(), R.layout.activity_telfriend,
				null);
	}

	/**
	 * 设置activity切换动画
	 * 
	 * @return
	 */
	@Override
	protected int setActivityAnimaMode() {
		return 4;
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
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparatorTelFriend();
		et_atf_search = (EditText) findViewById(R.id.et_atf_search);
		ptr_tpl_title = (PullToRefreshListView) findViewById(R.id.ptr_tpl_title);
		tv_atf_search = (TextView) findViewById(R.id.tv_atf_search);
		tv_at_forshow1 = (TextView) findViewById(R.id.tv_at_forshow1);
		listFriend = new ArrayList<TelFriends>();
		listFriend.addAll(TelsFriendDao.getInstance().getAll());
		// 根据a-z进行排序源数据
		Collections.sort(listFriend, pinyinComparator);
		initListView();
		Thread daemon = new Thread(new SimpleDaemons());
		daemon.setDaemon(true);
		daemon.start();
	}

	@Override
	protected void setListener() {
		tv_atf_search.setOnClickListener(this);
	}

	protected void setListenerAfterNet() {
		et_atf_search.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					tv_atf_search.setVisibility(View.VISIBLE);
				} else {
					tv_atf_search.setVisibility(View.GONE);
				}
			}
		});
		et_atf_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				filterData(s.toString());
				if ("".equals(et_atf_search.getText().toString().trim())) {
					tv_at_forshow1.setVisibility(View.VISIBLE);
				} else {
					tv_at_forshow1.setVisibility(View.GONE);
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});
	}

	/**
	 * 根据输入框中的值来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<TelFriends> cont = new ArrayList<TelFriends>();
		if (TextUtils.isEmpty(filterStr)) {
			cont = listFriend;
		} else {
			cont.clear();
			for (TelFriends tf : listFriend) {
				String name = tf.telName;
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					cont.add(tf);
				}
			}
		}

		// 根据a-z进行排序
		// fBean.cont.addAll(cont);
		// 根据a-z进行排序
		Collections.sort(cont, pinyinComparator);
		telsAdapter.updateListView(cont);
	}

	String tels = "";
	Map<String, String> map;

	protected void initNetData() {
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}
		RequestParams params = new RequestParams();
		// "17488881001,17488881002,17488881003,17488881004,17488881005,17488881006";
		try {
			map = PhoneInfoUtils.getPhone(getContentResolver());
		} catch (Exception e) {
			UIUtils.showToast(UIUtils.getContext(), "没有获取到权限");
			e.printStackTrace();
		}
		if (map != null) {
			for (Map.Entry<String, String> entry : map.entrySet()) {
				tels += entry.getValue() + ",";
			}
			params.addBodyParameter("tels", tels);
			httpHelp.sendPost(NetworkConfig.getMyfriendsByTXL(), params,
					TelsFriendBean.class,

					new MyRequestCallBack<TelsFriendBean>() {

						@Override
						public void onSucceed(TelsFriendBean bean) {
							LogUtil.e("hello",
									"网络请求成功后time=" + System.currentTimeMillis());
							if (bean == null)
								return;
							if ("1".equals(bean.status)) {
								telsBean = bean;
								addTels();
							} else {
								UIUtils.showToast(UIUtils.getContext(),
										"网络请求失败");
							}

						}

					});
		}

	}

	private TelFriends tf;
	private String[] phonesArr;

	// 添加没有注册过喵喵的通讯录联系人
	private void addTels() {
		try {
			List<TelFriends> tfs = new ArrayList<TelFriends>();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				if (entry.getValue().contains(",")) {
					phonesArr = entry.getValue().split(",");
					for (int i = 0; i < phonesArr.length; i++) {
						if (!PrefUtils.getString(GlobalConstant.USER_TEL, "")
								.equals(phonesArr[i])
								&& PhoneInfoUtils.getNoAccountTel(listFriend,
										phonesArr[i]) < 0) {
							tf = new TelFriends();
							tf.id = "";
							tf.face = "";
							tf.uid = "";
							tf.nickname = "";
							tf.is_friend = "0";
							tf.tel = phonesArr[i];
							tf.telName = entry.getKey();
							filledData(tf);
							TelsFriendDao.getInstance().SaveDL(tf);
							tfs.add(tf);
						}
					}
				} else {
					if (!PrefUtils.getString(GlobalConstant.USER_TEL, "")
							.equals(entry.getValue())
							&& PhoneInfoUtils.getNoAccountTel(listFriend,
									entry.getValue()) < 0) {
						tf = new TelFriends();
						tf.id = "";
						tf.face = "";
						tf.uid = "";
						tf.nickname = "";
						tf.is_friend = "0";
						tf.tel = entry.getValue();
						tf.telName = entry.getKey();
						filledData(tf);
						TelsFriendDao.getInstance().SaveDL(tf);
						tfs.add(tf);
					}
				}
			}
			listFriend.addAll(tfs);
			int isme = -1;
			for (int i = 0; i < telsBean.cont.tongxunlu.size(); i++) {
				filledData(telsBean.cont.tongxunlu.get(i));
				if (PrefUtils.getString(GlobalConstant.USER_TEL, "").equals(
						telsBean.cont.tongxunlu.get(i).tel)) {
					isme = i;
				} else {
					telsBean.cont.tongxunlu.get(i).telName = PhoneInfoUtils
							.getMapNameByTel(map,
									telsBean.cont.tongxunlu.get(i).tel);
				}
			}
			if (isme >= 0) {
				telsBean.cont.tongxunlu.remove(isme);
			}
			for (int i = 0; i < telsBean.cont.tongxunlu.size(); i++) {
				int pos = PhoneInfoUtils.getNoAccountTel(listFriend,
						telsBean.cont.tongxunlu.get(i).tel);
				if (pos >= 0) {
					listFriend.get(pos).id = telsBean.cont.tongxunlu.get(i).id;
					listFriend.get(pos).face = telsBean.cont.tongxunlu.get(i).face;
					listFriend.get(pos).uid = telsBean.cont.tongxunlu.get(i).uid;
					listFriend.get(pos).nickname = telsBean.cont.tongxunlu
							.get(i).nickname;
					listFriend.get(pos).is_friend = telsBean.cont.tongxunlu
							.get(i).is_friend;
					// listFriend.get(i).tel=telsBean.cont.tongxunlu.get(i).tel;
					// listFriend.get(i).title=telsBean.cont.tongxunlu.get(i).title;
					// listFriend.get(i).telName=telsBean.cont.tongxunlu.get(i).telName;
					// listFriend.add(telsBean.cont.tongxunlu.get(i));
					TelsFriendDao.getInstance().SaveDL(listFriend.get(pos));
				} else {
					TelsFriendDao.getInstance().SaveDL(
							telsBean.cont.tongxunlu.get(i));
					listFriend.add(telsBean.cont.tongxunlu.get(i));
				}
			}

			// 根据a-z进行排序源数据
			Collections.sort(listFriend, pinyinComparator);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		initListView();
		setListenerAfterNet();
	}

	class SimpleDaemons implements Runnable {

		@Override
		public void run() {
			try {
				// while (true) {
				initNetData();
				// }
			} catch (Exception e) {
				System.out.println("sleep() interrupted");
			}
		}
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(TelFriends tf) {
		if (tf.telName == null || "".equals(tf.telName)) {
			tf.title = "#";
			return;
		}
		// 汉字转换成拼音
		String pinyin = characterParser.getSelling(tf.telName);
		if (StringUtils.isEmpty(pinyin)) {
			tf.title = "#";
			return;
		}
		String sortString = pinyin.substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortString.matches("[A-Z]")) {
			tf.title = sortString.toUpperCase();
		} else {
			tf.title = "#";
		}

	}

	protected void initListView() {
		if (telsAdapter == null) {
			telsAdapter = new TelsAdapter(listFriend);
			ptr_tpl_title.setAdapter(telsAdapter);
			ptr_tpl_title.setMode(Mode.DISABLED);

		} else {
			telsAdapter.updateListView(listFriend);
			// telsAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "通讯录好友";
	}

	private List<TelFriends> tfsAdapter;

	class TelsAdapter extends BaseAdapter {
		private TelsHolder telsHolder;
		private int pos;

		public TelsAdapter(List<TelFriends> tels) {
			if (tfsAdapter == null) {
				tfsAdapter = new ArrayList<TelFriends>();
			}
			tfsAdapter.addAll(tels);
		}

		public void updateListView(List<TelFriends> cont) {
			tfsAdapter.clear();
			tfsAdapter.addAll(cont);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			if (tfsAdapter == null) {
				tfsAdapter = new ArrayList<TelFriends>();
			}
			return tfsAdapter.size();
			// if (telsBean == null || telsBean.cont == null
			// || telsBean.cont.tongxunlu == null) {
			// return 0;
			// } else {
			// return telsBean.cont.tongxunlu.size();
			// }
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
		public View getView(int position, View converView, ViewGroup arg2) {
			if (converView == null) { //
				telsHolder = new TelsHolder();
				converView = View.inflate(UIUtils.getContext(),
						R.layout.new_friend_item, null);
				telsHolder.civ_nfi_headicon = (CircleImageView) converView
						.findViewById(R.id.civ_nfi_headicon);
				telsHolder.civ_nfi_nickname = (TextView) converView
						.findViewById(R.id.civ_nfi_nickname);
				telsHolder.civ_nfi_add = (TextView) converView
						.findViewById(R.id.civ_nfi_add);
				telsHolder.civ_nfi_telname = (TextView) converView
						.findViewById(R.id.civ_nfi_telname);
				telsHolder.tv_friend_classify = (TextView) converView
						.findViewById(R.id.tv_friend_classify);
				converView.setTag(telsHolder);
			} else {
				telsHolder = (TelsHolder) converView.getTag();
			}
			// getNetDate();
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);

			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (section > 0 && position == getPositionForSection(section)) {
				telsHolder.tv_friend_classify.setVisibility(View.VISIBLE);
				telsHolder.tv_friend_classify
						.setText(tfsAdapter.get(position).title);
			} else {
				telsHolder.tv_friend_classify.setVisibility(View.GONE);
			}
			telsHolder.civ_nfi_telname
					.setText(tfsAdapter.get(position).telName);
			if (tfsAdapter.get(position).face == null
					|| "".equals(tfsAdapter.get(position).face)) {
				telsHolder.civ_nfi_headicon
						.setImageResource(R.drawable.default_avatar);
			} else {
				httpHelp.showImage(telsHolder.civ_nfi_headicon,
						tfsAdapter.get(position).face);
			}
			// telsHolder.civ_nfi_nickname.setText("喵喵:"
			// + tfsAdapter.get(position).nickname);
			telsHolder.civ_nfi_nickname.setText("手机号:"
					+ tfsAdapter.get(position).tel);
			if ("1".equals(tfsAdapter.get(position).is_friend)) {
				telsHolder.civ_nfi_add.setText("已添加");
				telsHolder.civ_nfi_add.setSelected(false);
			} else {
				if (tfsAdapter.get(position).local_state == 1) {
					telsHolder.civ_nfi_add.setText("已发送");
					telsHolder.civ_nfi_add.setSelected(false);
				} else {
					telsHolder.civ_nfi_add.setText("添加");
					telsHolder.civ_nfi_add.setSelected(true);
					addOnclick = new AddOnclick(position,
							tfsAdapter.get(position).id,
							tfsAdapter.get(position).tel,
							telsHolder.civ_nfi_add);
					telsHolder.civ_nfi_add.setOnClickListener(addOnclick);
				}
			}
			return converView;
		}

		/**
		 * 根据ListView的当前位置获取分类的首字母的Char ascii值
		 */
		public int getSectionForPosition(int position) {
			if (tfsAdapter.get(position).title != null) {
				return tfsAdapter.get(position).title.charAt(0);
			} else {
				return -1;
			}
		}

		/**
		 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
		 */
		public int getPositionForSection(int section) {
			for (int i = 0; i < getCount(); i++) {
				String sortStr = tfsAdapter.get(i).title;
				char firstChar = sortStr.toUpperCase().charAt(0);
				if (firstChar == section) {
					return i;
				}
			}

			return -1;
		}

	}

	class TelsHolder {
		public CircleImageView civ_nfi_headicon;
		public TextView civ_nfi_nickname;
		public TextView civ_nfi_add;
		public TextView civ_nfi_telname;
		public TextView tv_friend_classify;
	}

	private AddOnclick addOnclick;

	class AddOnclick implements OnClickListener {
		private String friednId;
		private TextView civ_nfi_add;
		private String phoneNumber;
		private int position;

		public AddOnclick(int position, String friednId, String phoneNumber,
				TextView civ_nfi_add) {
			this.friednId = friednId;
			this.civ_nfi_add = civ_nfi_add;
			this.phoneNumber = phoneNumber;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (civ_nfi_add.isSelected()) {
				if (friednId == null || "".equals(friednId)) {
					// UIUtils.showToast(UIUtils.getContext(), "发送到手机");
					RequestParams params = new RequestParams();
					params.addBodyParameter("mobile", phoneNumber);
					httpHelp.sendPost(NetworkConfig.getSendMessage(), params,
							BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

								@Override
								public void onSucceed(BaseBaen bean) {
									if (bean == null) {
										return;
									}
									UIUtils.showToast(UIUtils.getContext(),
											bean.msg);
									if ("1".equals(bean.status)) {
										tfsAdapter.get(position).local_state = 1;
										int pos = PhoneInfoUtils
												.getNoAccountTel(
														listFriend,
														tfsAdapter
																.get(position).tel);
										if (pos >= 0) {
											listFriend.get(pos).local_state = 1;
										}
										if (civ_nfi_add != null) {
											civ_nfi_add.setSelected(false);
											civ_nfi_add.setText("已发送");
										}
									}

								}
							});
				} else {
					CmdManager.getInstance().addContact(friednId, null,
							new CmdManagerCallBack() {

								@Override
								public void onState(boolean isSucceed) {
									if (isSucceed) {
										UIUtils.showToast(UIUtils.getContext(),
												"发送请求成功");
										if (civ_nfi_add != null) {
											tfsAdapter.get(position).local_state = 1;
											int pos = PhoneInfoUtils
													.getNoAccountTel(
															listFriend,
															tfsAdapter
																	.get(position).tel);
											if (pos >= 0) {
												listFriend.get(pos).local_state = 1;
											}
											civ_nfi_add.setSelected(false);
											civ_nfi_add.setText("已发送");
											civ_nfi_add.setClickable(false);
										}
									} else {
										UIUtils.showToast(UIUtils.getContext(),
												"添加失败");
									}
								}
							});
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_atf_search:
			KeyBoardUtils.closeKeybord(UIUtils.getContext(), et_atf_search);
			et_atf_search.setText("");
			et_atf_search.clearFocus();
			// et_atf_search.setFocusable(false);
			break;

		default:
			break;
		}
	}
}
