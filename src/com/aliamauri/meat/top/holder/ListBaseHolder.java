package com.aliamauri.meat.top.holder;

import java.util.List;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.BaseTopBean;
import com.aliamauri.meat.bean.DTVListBaen.DTvCont;
import com.aliamauri.meat.bean.DTVListBaen.DTvCont.TwoDTVCont;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;

public class ListBaseHolder extends BaseHolder<BaseTopBean> {
	/********************************/
	private HttpHelp httpHelp;

	private CircleImageView iv_avater_one, iv_avater_two, iv_avater_three,
			iv_avater_four;
	private ImageView tv_bg;
	private TextView tv_film_dz, tv_film_name, tv_film_pl, tv_user_msg_one,
			tv_user_msg_two, tv_user_msg_three, tv_user_msg_four,
			tv_ifdtl_nickname, tv_ifdtl_num;

	private CircleImageView civ_ifdtl_headicon;

	@Override
	public void refreshView(BaseTopBean bean) {
		DTvCont dTvCont = null;
		if (bean instanceof DTvCont) {
			dTvCont = (DTvCont) bean;
		}
		if (dTvCont == null) {
			return;
		}
		if (httpHelp == null) {
			httpHelp = new HttpHelp();
		}

		httpHelp.showImage(civ_ifdtl_headicon, dTvCont.face + "##");
		LogUtil.e("test", dTvCont.name);
		// String name = dTvCont.nickname.replace(".", "").trim();
		tv_ifdtl_nickname.setText(dTvCont.nickname != null ? dTvCont.nickname
				.trim() : "正在加载……");
		tv_ifdtl_num.setText(dTvCont.bf != null ? dTvCont.bf + "观看".trim()
				: "正在加载……");

		bitmapUtils.display(tv_bg, dTvCont.pic);
		tv_film_dz.setText(dTvCont.dz + "");
		tv_film_pl.setText(dTvCont.pl + "");
		tv_film_name.setText(dTvCont.name);
		tv_user_msg_one.setVisibility(View.GONE);
		tv_user_msg_two.setVisibility(View.GONE);
		tv_user_msg_three.setVisibility(View.GONE);
		tv_user_msg_four.setVisibility(View.GONE);
		iv_avater_one.setVisibility(View.GONE);
		iv_avater_two.setVisibility(View.GONE);
		iv_avater_three.setVisibility(View.GONE);
		iv_avater_four.setVisibility(View.GONE);

		List<TwoDTVCont> cont = dTvCont.cont;
		if (cont != null) {
			int size = cont.size();
			if (size >= 1) {
				TwoDTVCont twoDTVCont = cont.get(0);
				String msg1 = twoDTVCont.msg;
				if (TextUtils.isEmpty(msg1)) {

				} else {
					iv_avater_one.setVisibility(View.VISIBLE);
					tv_user_msg_one.setVisibility(View.VISIBLE);
					tv_user_msg_one.setText(msg1);
					bitmapUtils.display(iv_avater_one, twoDTVCont.face);
				}

			}
			if (size >= 2) {
				TwoDTVCont twoDTVCont2 = cont.get(1);

				String msg2 = twoDTVCont2.msg;
				if (TextUtils.isEmpty(msg2)) {

				} else {
					iv_avater_two.setVisibility(View.VISIBLE);
					bitmapUtils.display(iv_avater_two, twoDTVCont2.face);
					tv_user_msg_two.setVisibility(View.VISIBLE);
					tv_user_msg_two.setText(msg2);
				}
			}

			if (size >= 3) {
				TwoDTVCont twoDTVCont3 = cont.get(2);
				String msg3 = twoDTVCont3.msg;
				if (TextUtils.isEmpty(msg3)) {

				} else {
					iv_avater_three.setVisibility(View.VISIBLE);
					tv_user_msg_three.setVisibility(View.VISIBLE);
					tv_user_msg_three.setText(msg3);
					bitmapUtils.display(iv_avater_three, twoDTVCont3.face);
				}

			}
			if (size >= 4) {
				TwoDTVCont twoDTVCont4 = cont.get(3);
				String msg4 = twoDTVCont4.msg;
				if (TextUtils.isEmpty(msg4)) {

				} else {
					iv_avater_four.setVisibility(View.VISIBLE);
					tv_user_msg_four.setVisibility(View.VISIBLE);
					tv_user_msg_four.setText(msg4);
					bitmapUtils.display(iv_avater_four, twoDTVCont4.face);
				}

			}

		}
	}

	@Override
	protected View initView() {
		View v = UIUtils.inflate(R.layout.item_fragment_dt_tv_list);
		tv_bg = (ImageView) v.findViewById(R.id.tv_bg);
		tv_film_dz = (TextView) v.findViewById(R.id.tv_film_dz);
		tv_film_name = (TextView) v.findViewById(R.id.tv_film_name);
		tv_film_pl = (TextView) v.findViewById(R.id.tv_film_pl);
		tv_user_msg_one = (TextView) v.findViewById(R.id.tv_user_msg_one);
		tv_user_msg_two = (TextView) v.findViewById(R.id.tv_user_msg_two);
		tv_user_msg_three = (TextView) v.findViewById(R.id.tv_user_msg_three);
		tv_user_msg_four = (TextView) v.findViewById(R.id.tv_user_msg_four);
		iv_avater_one = (CircleImageView) v.findViewById(R.id.iv_avater_one);
		iv_avater_two = (CircleImageView) v.findViewById(R.id.iv_avater_two);
		iv_avater_three = (CircleImageView) v
				.findViewById(R.id.iv_avater_three);
		iv_avater_four = (CircleImageView) v.findViewById(R.id.iv_avater_four);

		tv_ifdtl_nickname = (TextView) v.findViewById(R.id.tv_ifdtl_nickname);
		tv_ifdtl_num = (TextView) v.findViewById(R.id.tv_ifdtl_num);
		civ_ifdtl_headicon = (CircleImageView) v
				.findViewById(R.id.civ_ifdtl_headicon);

		return v;
	}

	// private HttpHelp mHelp;
	//
	// private HttpHelp getHttpHelp() {
	// if (mHelp == null) {
	// mHelp = new HttpHelp();
	// }
	// return mHelp;
	// }

}
