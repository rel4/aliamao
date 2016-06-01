package com.aliamauri.meat.bean;

import java.util.List;

public class ReplyChildBean extends BaseBaen {
	public Cont cont;
	public String url;

	public class Cont {
		public Currrent_comment currrent_comment;
		public List<Son> son;

		public class Currrent_comment {
			public String id;
			public String uid;
			public String time;
			public String face ;
			public String msg;
			public String nickname;
		}
	}
}
