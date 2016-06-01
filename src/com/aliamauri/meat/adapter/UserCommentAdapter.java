package com.aliamauri.meat.adapter;

import java.util.List;

import android.content.Context;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.Comlist;
import com.aliamauri.meat.holder.BaseViewHolder;

public class UserCommentAdapter extends MyBaseAdapter<Comlist> {

	public UserCommentAdapter(Context context, List<Comlist> datas,
			int layoutId) {
		super(context, datas, layoutId);
	}

	@Override
	public void convert(BaseViewHolder holder, Comlist com) {
		holder.setVisible(R.id.tv_item_user_hf, false)
				.setText(R.id.tv_item_user_content, com.msg)
				.setText(R.id.tv_item_user_name, com.nickname)
				.setText(R.id.tv_item_user_time, com.time)
				.setImageURL(R.id.civ_item_user_icon, com.face);
	}

}
