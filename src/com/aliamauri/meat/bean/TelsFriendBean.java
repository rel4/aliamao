package com.aliamauri.meat.bean;

import java.util.List;

import com.aliamauri.meat.bean.cont.TelFriend.TelFriends;

public class TelsFriendBean extends BaseBaen {
	public Cont cont;

	public class Cont {
		public List<TelFriends> tongxunlu;
		public List<Talks> chatgroup;

		public class Talks {
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
