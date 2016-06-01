package com.aliamauri.meat.utils;

import java.util.ArrayList;
import java.util.Map;

import android.content.res.Resources;
import android.view.View;
import android.view.View.OnClickListener;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.ResourceLibraryBean.Cont;
import com.aliamauri.meat.global.MyApplication;
import com.aliamauri.meat.holder.ViewHolder_YS;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;

/**
 * 顶踩工具类
 * 
 * @author limaokeji-windosc
 * 
 */
public class ClickZanBtn_utils implements OnClickListener {



	private ViewHolder_YS mYs; // 当前view模型
	private ArrayList<Cont> mConts; // 当前请求的网络数据
	private int mPosition; // 当前条目的位置
	private Resources mResources;
	private Boolean isDing;// 是否点击顶按钮
	private Boolean isCai;// 是否点击踩按钮
	private HttpHelp mHttpHelp;

	public ClickZanBtn_utils(ViewHolder_YS ys, ArrayList<Cont> conts,
			int position) {
		this.mYs = ys;
		this.mConts = conts;
		this.mPosition = position;
		mHttpHelp = new HttpHelp();
		isDing = true;
		isCai = true;
		mResources = UIUtils.getContext().getResources();
		showIsClick();
	}

	/**
	 * 显示是否点过赞
	 */
	private void showIsClick() {
		//************************服务器端数据*****************************
		InitIcon(mConts.get(mPosition).isact);
		//************************服务器端数据*****************************
		//************************本地端数据*****************************
		if(MyApplication.UpDowns !=null && MyApplication.UpDowns.size()>0){
			for(Map.Entry<String, String> entry : MyApplication.UpDowns.entrySet()){
				if(mConts.get(mPosition).id.equals((String)entry.getKey())){
					String value = entry.getValue();
					String[] split = value.split("&&&");
					mYs.tv_voide_item_ding.setText(split[0]);
					mYs.tv_voide_item_cai.setText(split[1]);
					InitIcon(split[2]);
				}
			}
		}
		//************************本地端数据*****************************
	}
/**
 * 根据传入的类型来判断当前的顶踩图标
 * @param type
 */
	private void InitIcon(String type) {
		restoreIcon();
		switch (type) {
		case "1": // 该用户已顶过
			mYs.iv_voide_item_ding.setBackgroundResource(R.drawable.ding_y);
			break;
		case "2":// 该用户已踩过
			mYs.iv_voide_item_cai.setBackgroundResource(R.drawable.cai_y);
			break;
		default: // 什么都没有做的
			restoreIcon();
			break;
		}
	}

	@Override
	public void onClick(View v) {
		if ("0".equals(mConts.get(mPosition).isact)) {
			switch ((String) (v.getTag())) {
			case "1":
				if (isDing && isCai) { // 顶和踩之间只能操作一次
					sendNetAction("1");
				} else {
					UIUtils.showToast(UIUtils.getContext(), "已点击过了……");
				}

				break;
			case "2":
				if (isDing && isCai) { // 顶和踩之间只能操作一次
					sendNetAction("2");
				} else {
					UIUtils.showToast(UIUtils.getContext(), "已点击过了……");
				}

				break;
			}
		} else {
			UIUtils.showToast(UIUtils.getContext(), "已点击过了……");
		}

	}

	/**
	 * 顶踩发送网络确认
	 * 
	 * @param string      1顶，2踩
	 */
	private void sendNetAction(final String type) {
		if(mHttpHelp != null){
			mHttpHelp.stopHttpNET();
		}
		mHttpHelp.sendGet(NetworkConfig.get_ding_cai_url(type,mConts.get(mPosition).id),BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

			@Override
			public void onSucceed(BaseBaen bean) {
				if(bean == null || bean.status == null){
					isDing = true;
					isCai = true;
					UIUtils.showToast(UIUtils.getContext(), "操作失败~~");
					return ;
				}
				switch (bean.status) {
				case "1":
					if("1".equals(type)){
						isDing = false;
						mYs.iv_voide_item_ding.setBackgroundResource(R.drawable.ding_y);
						mYs.tv_voide_item_ding.setText(String.valueOf(Integer.valueOf(mConts.get(mPosition).up) + 1));
						//在全局类中新建一个map集合，每次点击踩，或者顶，就拼接字符串记录到集合中，
						MyApplication.UpDowns.put(mConts.get(mPosition).id,String.valueOf(Integer.valueOf(mConts.get(mPosition).up) + 1)+"&&&"+mConts.get(mPosition).down+"&&&"+"1");                    
						UIUtils.showToast(UIUtils.getContext(), "顶赞成功~~");
					}else{
						isCai = false;
						mYs.iv_voide_item_cai.setBackgroundResource(R.drawable.cai_y);
						mYs.tv_voide_item_cai.setText(String.valueOf(Integer.valueOf(mConts.get(mPosition).down) + 1));
						MyApplication.UpDowns.put(mConts.get(mPosition).id,mConts.get(mPosition).up+"&&&"+String.valueOf(Integer.valueOf(mConts.get(mPosition).down) + 1)+"&&&"+"2");
						UIUtils.showToast(UIUtils.getContext(), "踩赞成功~~");
					}
					break;
				case "4":
					UIUtils.showToast(UIUtils.getContext(), "已经点过了~~");
					break;
				default:
					isDing = true;
					isCai = true;
					UIUtils.showToast(UIUtils.getContext(), "操作失败~~");
					break;
				}
			}
		});
	}

	/**
	 * 将图片复原成初始样子
	 */
	private void restoreIcon() {
		mYs.iv_voide_item_cai.setBackgroundResource(R.drawable.cai_n);
		mYs.iv_voide_item_ding.setBackgroundResource(R.drawable.ding_n);
	}

}
