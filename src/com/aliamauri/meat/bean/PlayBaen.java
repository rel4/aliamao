package com.aliamauri.meat.bean;

import java.io.Serializable;
import java.util.List;

public class PlayBaen extends BaseBaen implements Serializable {

	private static final long serialVersionUID = 3107123466185158366L;
	public Cont cont;

	public class Cont {
		public Userinfo userinfo;
		public List<Videoother> videoother;
		public Vinfo vinfo;
		public List<Comlist> comlist;
		public Shareinfo shareinfo;

		public class Shareinfo {
			public String sharetitle;
			public String sharedesc;
			public String shareurl;
			public String sharepic;
		}

		public class Userinfo {
			public String face;
			public String fansnum;
			public String isguanzhu;
			public String uid;
			public String nickname;
			public String darentype;

		}

		public class Videoother {
			public String hpic;
			public String id;
			public String name;

		}

		public class Vinfo {
			public String bf_num;
			public String down;
			public String litpic;
			public String lmhash;
			public String pl_num;
			public String desc;
			public String name;
			public String isupdown; // 为1 的则是顶， 2为踩 0是未操作
			public String up;
			public String url;
			public String vt;
		}
	}
}
