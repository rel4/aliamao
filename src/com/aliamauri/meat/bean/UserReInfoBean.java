package com.aliamauri.meat.bean;

import java.util.List;

public class UserReInfoBean extends BaseBaen {
	public Cont cont;

	public class Cont {
		public String friendscount;
		public String chatusercount;
		public String blacklistcount;
		public List<Photolist> photolist;

		public class Photolist {
			public String id;
			public String uid;
			public String imgurl;
			public String imgurlori;
			public String isopen;
			public String createtime;
		}
	}

}
