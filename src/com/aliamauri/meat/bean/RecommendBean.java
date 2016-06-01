package com.aliamauri.meat.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 为你推荐页面---->javaBean 频道界面的----->javaBean
 * 
 * @author liang
 * 
 */
public class RecommendBean extends BaseBaen implements Serializable {
	public Cont cont;
	public Url url;

	public class Cont implements Serializable {
		public List<BaseDate> faxian;
		public List<BaseDate> hotkeys;
		public List<BaseDate> recku;
		public List<BaseDate> siteurl1;

		public class BaseDate implements Serializable {
			public String downloadurl;
			public String id;
			public String mdesc;
			public String name;
			public String num;
			public String pic;
			public String url;
			public String isnew;
			public String updatetime;
			public String sdesc;
			public String hasfilm;
		}

	}

	public class Url implements Serializable {
		public String faxian;
		public String hotkeys;
		public String recku;
	}

}
