package com.aliamauri.meat.weight.SwipeListView;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.view.CircleImageView;

public class SwipeAdapter extends BaseAdapter {
	private HttpHelp httpHelp;
	/**
	 * 上下文对象
	 */
	private Context mContext = null;
	private List<User> cont;

	private int mRightWidth = 0;
	private boolean title_right_check = false;
	private boolean selectall = false;// 判断有没有点击全选了
	private boolean unselectall = false;// 判断有没有点击全选了///从全选按键中设置，点击了全选状态下

	private boolean isRemove = false;// 判断是否在处理移除黑名单操作，，把控件还原

	private SwipeListView swipeListView;

	public void setTitle_right_check(boolean title_right_check) {
		this.title_right_check = title_right_check;
	}

	public void setSelectall(boolean selectall) {
		this.selectall = selectall;
	}

	public void setUnselectall(boolean unselectall) {
		this.unselectall = unselectall;
	}

	public void setRemove(boolean isRemove) {
		this.isRemove = isRemove;
	}

	/**
	 * @param mainActivity
	 */
	public SwipeAdapter(Context ctx, List<User> cont, int rightWidth,
			SwipeListView slv) {
		httpHelp = new HttpHelp();
		mContext = ctx;
		this.cont = cont;
		mRightWidth = rightWidth;
		swipeListView = slv;
	}

	@Override
	public int getCount() {
		return cont.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.black_list_item, parent, false);
			holder = new ViewHolder();
			holder.item_left = (RelativeLayout) convertView
					.findViewById(R.id.item_left);
			holder.item_right = (RelativeLayout) convertView
					.findViewById(R.id.item_right);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.tv_title);
			holder.tv_msg = (TextView) convertView.findViewById(R.id.tv_msg);

			holder.item_right_txt = (TextView) convertView
					.findViewById(R.id.item_right_txt);

			holder.iv_blacklistitem_orangecircle = (ImageView) convertView
					.findViewById(R.id.iv_blacklistitem_orangecircle);

			holder.ci_selectcondition_icon = (CircleImageView) convertView
					.findViewById(R.id.ci_selectcondition_icon);
			holder.tv_bli_name = (TextView) convertView
					.findViewById(R.id.tv_bli_name);
			holder.lp1 = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT);
			holder.lp2 = new LayoutParams(mRightWidth,
					LayoutParams.MATCH_PARENT);
			convertView.setTag(holder);
		} else {// 有直接获得ViewHolder
			holder = (ViewHolder) convertView.getTag();
		}
		User user = cont.get(position);
		if (user != null) {
			httpHelp.showImage(
					holder.ci_selectcondition_icon,
					(TextUtils.isEmpty(user.getNativeAvatar()) ? user
							.getAvatar() : user.getNativeAvatar()) + "##");
			holder.tv_bli_name.setText(TextUtils.isEmpty(user.getNick()) ? user
					.getUserId() : user.getNick());

			holder.item_right.setLayoutParams(holder.lp2);

			holder.item_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onRightItemClick(v, position);
					}
				}
			});

			if (title_right_check) {
				swipeListView.hiddenRight(convertView);
				holder.iv_blacklistitem_orangecircle
						.setVisibility(View.VISIBLE);
				// holder.item_right.setVisibility(View.GONE);
				if (isRemove) {
					holder.iv_blacklistitem_orangecircle.setSelected(false);
				} else {
					if (selectall) {
						holder.iv_blacklistitem_orangecircle.setSelected(true);
					} else if (unselectall) {
						holder.iv_blacklistitem_orangecircle.setSelected(false);
					}

				}
				// holder.iv_blacklistitem_orangecircle.setSelected(false);

			} else {
				// holder.item_right.setVisibility(View.VISIBLE);
				holder.iv_blacklistitem_orangecircle.setSelected(false);
				holder.iv_blacklistitem_orangecircle.setVisibility(View.GONE);
			}
		}

		return convertView;
	}

	static class ViewHolder {
		RelativeLayout item_left;
		RelativeLayout item_right;

		TextView tv_title;
		TextView tv_msg;

		CircleImageView ci_selectcondition_icon;
		TextView tv_bli_name;

		TextView item_right_txt;
		ImageView iv_blacklistitem_orangecircle;
		LinearLayout.LayoutParams lp1;
		LinearLayout.LayoutParams lp2;
	}

	/**
	 * 单击事件监听器
	 */
	public onRightItemClickListener mListener = null;

	public void setOnRightItemClickListener(onRightItemClickListener listener) {
		mListener = listener;
	}

	public interface onRightItemClickListener {
		void onRightItemClick(View v, int position);
	}

	public void onfreshData(List<User> blackList) {
		this.cont = blackList;
		notifyDataSetInvalidated();
	}
}
