package com.aliamauri.meat.bean;

import java.util.List;

public class CommentBean extends BaseBaen {
	public Cont cont;
	public Url url;

	public class Url {
		public String up;
		public String down;
	}

	public class Cont {
		public String title;
		public Parent parent;
		public String filmid;
		public List<Son> son;
		public Userinfo userinfo;

		public class Userinfo {
			public String uid;
			public String nickname;
		}

		public class Parent {
			public String id;
			public String aid;
			public String msg;
			public String time;
			public String replynum;
			public String nickname;
			public String face;

		}

	}
}
