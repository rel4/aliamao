package com.aliamauri.meat.bean;

import java.util.List;

public class HtBean extends BaseBaen {
	public Cont cont;
	public Url url;

	public class Url {
		public String reclist;
		public String tlist;
	}

	public class Cont {

		public List<AllList> reclist;
		public List<AllList> tlist;

		public class AllList {
			public String adminuid;
			public String createtime;
			public String desc;
			public String hxgroupid;
			public String id;
			public String isopen;
			public String mnum;
			public String mtotal;
			public String name;
			public String pic;
			public String rec;
			public String tags;
		}
		
	}
}
