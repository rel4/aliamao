package com.aliamauri.meat.bean;

import java.util.List;

public class TalkDataBean extends BaseBaen {
	public Cont cont;

	public class Cont {
		public Baseinfo baseinfo;
		public List<Groupuserlist> groupuserlist;
		public String userjoin;

		public class Baseinfo {
			public String id;
			public String name;
			public String desc;
			public String tags;
			public String mnum;
			public String mtotal;
			public String adminuid;
			public String hxgroupid;
			public String pic;
			public String createtime;
			public String isopen;
		}

		public class Groupuserlist {
			public String face;
			public String id;
			public String nickname;
		}
	}

}
