package com.aliamauri.meat.adapter;

import java.util.List;

import android.content.Context;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.Comlist;
import com.aliamauri.meat.holder.BaseViewHolder;

/**
 * 视频播放页面 好友评论条目adapter
 * @author limaokeji-windosc
 * @param <T>
 */
public class CommentContentAdapter extends MyBaseAdapter<Comlist> {
	public CommentContentAdapter(Context context, List<Comlist> datas, int layoutId) {
		super(context, datas, layoutId);
	}
	@Override
	public void convert(BaseViewHolder holder, Comlist com) {
		holder.setText(R.id.tv_item_user_hf, com.replynum+"回复")
		.setText(R.id.tv_item_user_content, com.msg)
		.setText(R.id.tv_item_user_name, com.nickname)
		.setImageURL(R.id.civ_item_user_icon, com.face)
		.setText(R.id.tv_item_user_time, com.time);
	}

}
