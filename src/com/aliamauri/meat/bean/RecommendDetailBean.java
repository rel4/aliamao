package com.aliamauri.meat.bean;

import java.util.List;

public class RecommendDetailBean extends BaseBaen {
	public Cont cont;

	public class Cont {
		public Baseinfo baseinfo;
		public List<Listinfo> listinfo;

		public class Baseinfo {
			public String id;
			public String name;
			public String shortname;
			public String desc;
			public String isopen;
			public String updatetime;
			public String click;
		}

		public class Listinfo {
			public boolean isMianTiatal;
			public String id;
			public String catname;
			public String catdesc;
			public String special_id;
			public String sort;
			public String isopen;
			public String updatetime;
			public List<Cdata> cdata;

			public class Cdata {
				public boolean isMianTiatal = false;
				public String id;
				public String name;
				public String pic;
				public String derector;
				public String actor;
				public String desc;
				public String type;
				public int position;

				public String updatetime;
			}
		}
	}
}
