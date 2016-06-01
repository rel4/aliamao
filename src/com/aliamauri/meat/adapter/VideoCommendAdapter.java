package com.aliamauri.meat.adapter;
import java.util.List;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.PlayBaen.Cont.Videoother;
import com.aliamauri.meat.network.httphelp.HttpHelp;

/**
 * 推荐视频的样式设置
 */
public abstract class VideoCommendAdapter extends RecyclerView.Adapter<VideoCommendAdapter.ViewHolder> implements OnClickListener {	
	/** 推荐影片数据的集合*/
	private List<Videoother> mList;  
	private HttpHelp mHttpHelp;  

	/**
	 * 设置点击事件
	 * @param position
	 */
	public abstract void setClick(int position);

	public VideoCommendAdapter(List<Videoother> list) {
		this.mList =  list;
		mHttpHelp = new HttpHelp();
	}

	// 创建新View，被LayoutManager所调用
	@Override
	public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
		View view = LayoutInflater.from(viewGroup.getContext()).inflate(
				R.layout.video_commend_layout, viewGroup, false);
		ViewHolder vh = new ViewHolder(view);
		return vh;
	}

	// 将数据与界面进行绑定的操作
	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int position) {
		mHttpHelp.showImage(viewHolder.iv_video_commend_iocn,mList.get(position).hpic);
		viewHolder.iv_video_commend_iocn.setTag(position);
		viewHolder.iv_video_commend_iocn.setOnClickListener(this);
		viewHolder.tv_video_commend_name.setText(mList.get(position).name);
		
	}
	
	// 获取数据的数量
	@Override
	public int getItemCount() {
		return mList.size();
	}

	// 自定义的ViewHolder，持有每个Item的的所有界面元素
	public class ViewHolder extends RecyclerView.ViewHolder {
		public ImageView iv_video_commend_iocn;
		public TextView tv_video_commend_name;

		public ViewHolder(View view) {
			super(view);
			iv_video_commend_iocn =  $(view, R.id.iv_video_commend_iocn);
			tv_video_commend_name =  $(view, R.id.tv_video_commend_name);
		}
	}

	@Override
	public void onClick(View v) {
		Object position = v.getTag();
		if(position instanceof Integer){
			setClick((int)position);
		}
		
	}
	
	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public  <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}
}
