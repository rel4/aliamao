package com.aliamauri.meat.top.adapter;

import java.util.List;

import android.widget.AbsListView;

import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.BaseTopBean;
import com.aliamauri.meat.bean.DTVListBaen.DTvCont;
import com.aliamauri.meat.top.holder.BaseHolder;
import com.aliamauri.meat.top.holder.ListBaseHolder;
import com.aliamauri.meat.top.holder.TopNewsHolder;

public class ListBaseAdapter extends DefaultAdapter<BaseTopBean> {

	public ListBaseAdapter(List<BaseTopBean> datas, AbsListView lv) {
		super(datas, lv);
	}

	public ListBaseAdapter(List<BaseTopBean> datas) {
		super(datas);
	}

	@Override
	protected BaseHolder<BaseTopBean> getHolder() {
		ListBaseHolder listBaseHolder = new ListBaseHolder();
		return listBaseHolder;
	}

	@Override
	protected int getChildInnerViewType(int position) {
		BaseTopBean b = datas.get(position);
		if (b != null && b instanceof DTvCont) {
			DTvCont dTvCont = (DTvCont) b;
			if (2 == (dTvCont.type)) {
				return ITEM_TOP_NEWS;
			}
		}
		return super.getChildInnerViewType(position);
	}

	@Override
	protected int getChildViewTypeCount() {
		// TODO Auto-generated method stub
		return super.getChildViewTypeCount() + 1;
	}

	@Override
	protected BaseHolder<BaseTopBean> getTopHolder() {
		TopNewsHolder topNewsHolder = new TopNewsHolder();
		return topNewsHolder;
	}
}
