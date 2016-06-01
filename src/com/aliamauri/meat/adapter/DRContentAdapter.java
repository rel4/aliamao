package com.aliamauri.meat.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.DrBean.Cont;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.holder.BaseViewHolder;
import com.aliamauri.meat.utils.MyBDmapUtlis;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.baidu.mapapi.model.LatLng;

public abstract class DRContentAdapter extends MyBaseAdapter<Cont> implements
		OnClickListener {

	public DRContentAdapter(Context context, List<Cont> cont, int layoutId) {
		super(context, cont, layoutId);
	}

	public abstract void goToUserBaseDate(int position);

	@Override
	public void convert(BaseViewHolder holder, Cont t) {

		holder.setText(R.id.tv_user_name, t.nickname)
				.setText(R.id.tv_dr_introduce, t.name)
				.setText(R.id.tv_dr_distance, getDistance(t))
				.setText(R.id.tv_dr_num, TextUtils.isEmpty(t.bf)?"0":t.bf + "人数")
				.setImageURL(R.id.iv_user_bg, t.pic)
				.setImageURL(R.id.civ_user_icon, t.face)
				.setOnClickListener(R.id.rl_user_base_data, this)
				.setTag(R.id.rl_user_base_data, holder.getPosition());
	}

	/**
	 * 将经纬度转换成距离
	 * 
	 * @param t
	 * @return
	 */
	private String getDistance(Cont t) {
		if (t == null) {
			return null;
		}
		String[] location = PrefUtils.getString(GlobalConstant.USER_LOCATION,
				"0&&0").split("&&");
		String mWd = String.valueOf(location[0]);
		String mJd = String.valueOf(location[location.length - 1]);
		return new MyBDmapUtlis()
				.getCurrentDistance(
						new LatLng(
								Double.valueOf(TextUtils.isEmpty(t.wd) ? "0.00"
										: t.wd), Double.valueOf(TextUtils
										.isEmpty(t.jd) ? "0.00" : t.jd)),
						new LatLng(
								Double.valueOf(TextUtils.isEmpty(mWd) ? "0.00"
										: mWd), Double.valueOf(TextUtils
										.isEmpty(mJd) ? "0.00" : mJd)));
	}

	@Override
	public void onClick(View v) {
		goToUserBaseDate((int) v.getTag());
	}
}
