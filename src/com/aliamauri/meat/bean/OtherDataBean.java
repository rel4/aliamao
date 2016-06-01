package com.aliamauri.meat.bean;

import java.util.List;

public class OtherDataBean extends BaseBaen {
	public Cont cont;
	public class Cont{
		public String blacks_i;
		public Detail detail;
		public String friends_i;
		public String is_shield;
		public List<Photolist> photolist;
		
		public class Detail{
			public String age;
			public String constellation;
			public String face;
			public String hobby;
			public String pland;
			public String id;
			public String issmval;
			public String job;
			public String nickname;
			public String sex;
			public String signature;
		}
		public class Photolist{
			public String createtime;
			public String id;
			public String imgurl;
			public String imgurlori;
			public String isopen;
			public String uid;
		}
		
	}
}
