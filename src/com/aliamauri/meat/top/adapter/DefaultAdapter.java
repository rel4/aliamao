package com.aliamauri.meat.top.adapter;

import java.util.List;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.aliamauri.meat.top.holder.BaseHolder;
import com.aliamauri.meat.top.holder.MoreHolder;
import com.aliamauri.meat.top.manager.ThreadManager;
import com.aliamauri.meat.utils.UIUtils;

public abstract class DefaultAdapter<T> extends BaseAdapter implements
		OnItemClickListener {
	private String TAG = "DefaultAdapter";
	protected List<T> datas;
	public static final int ITEM_MORE = 0;
	public static final int ITEM_DEFAULT = 1;
	public static final int ITEM_TOP_NEWS = 2;
	private AbsListView lv;

	public DefaultAdapter(List<T> datas, AbsListView lv) {
		this.datas = datas;
		lv.setOnItemClickListener(this);
		this.lv = lv;
	}

	public DefaultAdapter(List<T> datas) {
		this.datas = datas;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// 需要对position 做一个修正 减去HeadView.count;
		// position = position - lv.getHeaderViewsCount();
		onItemClickInner(position);

	}

	/**
	 * 条目点击的方法 子类复写方法 实现条目的点击事件逻辑
	 * 
	 * @param position
	 */
	protected void onItemClickInner(int position) {
		Toast.makeText(UIUtils.getContext(), "点击的条目:" + position, 0).show();
	}

	@Override
	public int getCount() {
		// 下面多了加载更多的条目
		// LogUtil.e(TAG, "条目数量： " + datas.size());
		// if (lv == null) {
		// return datas == null ? 0 : datas.size();
		// }
		return datas == null ? 0 : datas.size();

		// return datas == null ? 1 : datas.size() + 1;
	}

	// 当前条目类型有几种
	@Override
	public int getViewTypeCount() {
		// 当前ListView 条目的类型有几种
		// 由于多了一个加载更多 所有条目类型两种
		// if (lv == null) {
		// return super.getViewTypeCount() + getChileViewTypeCount();
		// }
		return super.getViewTypeCount() + 1 + getChildViewTypeCount();
	}

	/**
	 * 子类复写添加的条目数
	 * 
	 * @return
	 */
	protected int getChildViewTypeCount() {
		return 0;
	}

	// 根据listView条目的位置 返回当前条目是什么类型的
	@Override
	public int getItemViewType(int position) {// 每个条目类型 在数组的位置 0 1
												// 数组只有两个元素取决于getViewTypeCount
												// if (position == datas.size())
												// {
		// return ITEM_MORE;// 当加载更多这种类型 在类型数组的第0个位置
		// }
		return getChildInnerViewType(position);// 让默认类型 在类型的数组中第1个位置
	}

	/**
	 * 
	 * @param position
	 * @return
	 */
	protected int getChildInnerViewType(int position) {
		return ITEM_DEFAULT;
	}

	@Override
	public Object getItem(int position) {

		return datas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressWarnings("unchecked")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 判断当前条目是什么类型
		BaseHolder<T> holder = null;
		// LogUtil.e(TAG, "getView: " + position);
		switch (getItemViewType(position)) {
		case ITEM_MORE:// 加载更多类型
			if (convertView == null) {
				holder = getMoreHolder();
			} else {
				holder = (BaseHolder<T>) convertView.getTag();
			}
			break;
		case ITEM_TOP_NEWS:// 加载更多类型
			if (convertView == null) {
				holder = getTopHolder();
			} else {
				holder = (BaseHolder<T>) convertView.getTag();
			}
			T topData = datas.get(position);
			holder.refreshView(topData);
			break;
		default:

			if (convertView == null) {
				holder = getHolder();
			} else {
				holder = (BaseHolder<T>) convertView.getTag();
			}
			T data = datas.get(position);
			holder.refreshView(data);
			break;
		}

		return holder.getContentView();
	}

	protected abstract BaseHolder<T> getTopHolder();

	private MoreHolder holder;

	private BaseHolder getMoreHolder() {
		if (holder == null) {
			holder = new MoreHolder(this, hasMore());
		}
		return holder;
	}

	public int hasMore() {
		return MoreHolder.HAS_MORE;
	}

	protected abstract BaseHolder<T> getHolder();

	/**
	 * 加载更多
	 */
	public void loadMore() {
		ThreadManager.getInstance().executeLongTask(new Runnable() {

			@Override
			public void run() {
				SystemClock.sleep(1000);
				final List<T> newData = onLoad();
				UIUtils.runInMainThread(new Runnable() {

					@SuppressWarnings("unchecked")
					@Override
					public void run() {
						if (newData == null) { // 加载失败了
							getMoreHolder().refreshView(MoreHolder.LOAD_ERROR);
						} else {
							if (newData.size() == 0) {
								getMoreHolder().refreshView(
										MoreHolder.HAS_NO_MORE);
							} else {
								getMoreHolder()
										.refreshView(MoreHolder.HAS_MORE);

								datas.addAll(newData);// 把新加载的数据 添加到之前的数据集合上
								notifyDataSetChanged();// 更新界面

							}
						}
					}
				});

			}
		});
	}

	/**
	 * 加载额外数据
	 * 
	 * @return
	 */
	protected List<T> onLoad() {
		return datas;

	};

}
