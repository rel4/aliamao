package com.aliamauri.meat.top.holder;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.top.adapter.DefaultAdapter;
import com.aliamauri.meat.utils.LogUtil;
import com.aliamauri.meat.utils.UIUtils;

public class MoreHolder extends BaseHolder<Integer> {
	public static int HAS_MORE = 0;// 有额外数据
	public static int HAS_NO_MORE = 1;// 没有数据
	public static int LOAD_ERROR = 2;// 加载失败
	private RelativeLayout rl_more_loading;
	private RelativeLayout rl_more_error;

	@Override
	protected View initView() {
		View contentView = UIUtils.inflate(R.layout.load_more);
		rl_more_loading = (RelativeLayout) contentView
				.findViewById(R.id.rl_more_loading);
		rl_more_error = (RelativeLayout) contentView
				.findViewById(R.id.rl_more_error);
		loading_error_txt = (TextView) contentView.findViewById(R.id.loading_error_txt);
		return contentView;
	}

	private int state;
	private DefaultAdapter adapter;
	private TextView loading_error_txt;

	public MoreHolder(DefaultAdapter adapter, int state) {
		this.adapter = adapter;
		this.state = state;
		LogUtil.e(this, "状态： " + state);
		refreshView(state);
	}

	@Override
	public void refreshView(Integer data) {
		if (data == HAS_MORE) {
			// 有数据
			// 显示加载中的界面
			if (loading_error_txt!=null) {
				loading_error_txt.setText(UIUtils.getContext().getString(R.string.load_error));
			}
			rl_more_loading.setVisibility(View.VISIBLE);
			rl_more_error.setVisibility(View.GONE); // GONE 隐藏 也不占地方 移除屏幕了
													// INVisiBle 只是隐藏
		} else if (data == LOAD_ERROR) {
			// 显示加载失败的界面
			rl_more_loading.setVisibility(View.GONE);
			rl_more_error.setVisibility(View.VISIBLE);
		} else if (data == HAS_NO_MORE) {
			// 界面不显示
			if (loading_error_txt!=null) {
				loading_error_txt.setText(UIUtils.getContext().getString(R.string.has_no_more));
			}
			rl_more_loading.setVisibility(View.GONE);
			rl_more_error.setVisibility(View.VISIBLE);
		}
	}

	// 该方法调用了 证明MoreHolder显示的界面已经挂载到条目上了 也就是说MoreHOlder里面的view对象就显示了
	//
	@Override
	public View getContentView() {
		if (state == HAS_MORE) {
			loadMore();// 当界面可见的时候加载更多数据
		}
		return super.getContentView();
	}

	private void loadMore() {
		LogUtil.e(this, "加载更多");
		adapter.loadMore();// 调用适配器的加载更多
	}

}
