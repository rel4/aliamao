package com.aliamauri.meat.activity.IM.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.Manager.ContactManager;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.db.AppointmentInvitationDao;
import com.aliamauri.meat.activity.IM.domain.AppointmentInvitMessage;
import com.aliamauri.meat.activity.IM.domain.Message;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.RefreshListView;
import com.umeng.analytics.MobclickAgent;

/**
 * 约会邀请页面
 * 
 * @author limaokeji-windosc
 * 
 */
public class DatingInvitationActivity extends BaseActivity implements
		OnItemClickListener {

	private RefreshListView mLv_content;
	private View view;
	private HttpHelp mHttpHelp;
	private List<AppointmentInvitMessage> mFriends;

	@Override
	protected View getRootView() {
		view = View.inflate(mActivity, R.layout.activity_dating_invitation,
				null);
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
		return "约会邀请";
	}

	@Override
	protected void initView() {
		mFriends = new AppointmentInvitationDao(mActivity).getMessagesList();
		mHttpHelp = new HttpHelp();
		mLv_content = (RefreshListView) $(view, R.id.lv_content);
		mLv_content.setAdapter(new MyListViewAdapter(mFriends));
	}

	class MyListViewAdapter extends BaseAdapter {

		private List<AppointmentInvitMessage> mFriends;

		public MyListViewAdapter(List<AppointmentInvitMessage> mFriends) {
			this.mFriends = mFriends;
		}

		@Override
		public int getCount() {
			if (mFriends != null && mFriends.size() > 0) {
				return mFriends.size();
			}
			return 0;
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
			ViewHolder_yhyq holder = null;
			if (convertView == null) {
				holder = new ViewHolder_yhyq();
				convertView = View.inflate(mActivity, R.layout.item_yhyq, null);
				holder.civ_yhyq_icon = $(convertView, R.id.civ_yhyq_icon);
				holder.tv_yhyq_username = $(convertView, R.id.tv_yhyq_username);
				holder.tv_yhyq_time = $(convertView, R.id.tv_yhyq_time);
				holder.tv_yhyq_introduction = $(convertView,
						R.id.tv_yhyq_introduction);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder_yhyq) convertView.getTag();
			}
			AppointmentInvitMessage appointmentInvitMessage = mFriends.get(position);
			if (appointmentInvitMessage!=null) {
				if (appointmentInvitMessage.getFromContactFacePath() != null) {
					mHttpHelp.showImage(holder.civ_yhyq_icon, mFriends
							.get(position).getFromContactFacePath() + "##");
				}
				if (appointmentInvitMessage.getFromContactUserNikeName() != null) {
					holder.tv_yhyq_username.setText(mFriends.get(position)
							.getFromContactNick());
				}
				if (appointmentInvitMessage.getTime() != 0) {
					holder.tv_yhyq_time.setText(new SimpleDateFormat("yyyy-MM-dd")
							.format(new Date(mFriends.get(position).getTime())));
				}
				if ("1".equals(appointmentInvitMessage.getIsInviteFromMe())) {
					holder.tv_yhyq_introduction.setText("等待约会哦！");
					convertView.setClickable(true);
					convertView.setFocusable(true);
				}else {
					holder.tv_yhyq_introduction.setText(Html
							.fromHtml("邀请您约会,是否马上<font color=\"#fca5a8\">" + "赴约"
									+ "</font>"));
					convertView.setClickable(false);
					convertView.setFocusable(false);
				}
				
			}
			
			return convertView;
		}

	}

	class ViewHolder_yhyq {

		public TextView tv_yhyq_time;
		public TextView tv_yhyq_username;
		public CircleImageView civ_yhyq_icon;
		public TextView tv_yhyq_introduction;
	}

	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	@Override
	protected void setListener() {
		mLv_content.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
		
		if (mFriends != null && mFriends.size() > 0) {
			AppointmentInvitMessage appointmentInvitMessage = mFriends.get(position);
			if (appointmentInvitMessage!=null) {
				addContactRelation(appointmentInvitMessage);
				startActivity(new Intent(this, ChatActivity.class)
				.putExtra(GlobalConstant.FLAG_CHAT_HX_USER_ID,appointmentInvitMessage.getFromContactHxid())
				.putExtra(GlobalConstant.FLAG_CHAT_USER_NIKE,appointmentInvitMessage.getFromContactNick()).
				putExtra(GlobalConstant.FLAG_CHAT_TYPE, ChatActivity.CHATTYPE_SINGLE));
			}
		}
	}
	/**
	 * 添加好友关系
	 * @param appointmentInvitMessage
	 */
	private void addContactRelation(AppointmentInvitMessage appointmentInvitMessage) {
		boolean isContains = ((MySDKHelper)SDKHelper.getInstance()).getContactList().containsKey(appointmentInvitMessage.getFromContactHxid());
		if (isContains) {
			return;
		}
		if (appointmentInvitMessage instanceof Message) {
			Message msg =	appointmentInvitMessage;
			ContactManager.getInstance(). contactAdded(msg);
		}
		
	}

}
