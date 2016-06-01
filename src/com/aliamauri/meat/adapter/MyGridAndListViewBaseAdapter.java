package com.aliamauri.meat.adapter;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * listview和GridView适配器基类
 * 
 * @author liang
 * @param <T>
 * @param <T>
 * 
 */
public abstract class MyGridAndListViewBaseAdapter extends BaseAdapter {
	public List list;  
	public boolean b ;   //当求得的集合大小为1/2时，传入true，集合大小不变的时候传入false

	public MyGridAndListViewBaseAdapter(List list,boolean b) {
		super();
		this.list =list;
		this.b = b;
	}

	@Override
	public int getCount() {
		if(b){
			return list.size()/3;
		}
		return list.size();
	}

	@Override
	public Object getItem(int arg0) {
		return list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public abstract View getView(int arg0, View arg1, ViewGroup arg2);
	
	

}
