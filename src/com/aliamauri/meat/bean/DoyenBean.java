package com.aliamauri.meat.bean;

import java.util.List;

import com.aliamauri.meat.bean.cont.MyVideoCont;

public class DoyenBean extends BaseBaen {
	public Cont cont;

	public class Cont {
		public String nickname;
		public String face;
		public String signature;
		public String age;
		public String sex;
		public String pland;
		public String isFriend;
		public String isCare;
		public String followsnum;
		public String fansnum;

		public List<MyVideoCont> hotList;
		public List<MyVideoCont> newestList;

	}
}
