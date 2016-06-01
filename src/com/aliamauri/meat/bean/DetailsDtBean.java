package com.aliamauri.meat.bean;

import java.util.ArrayList;

import com.aliamauri.meat.bean.DtBean.Cont.Tlist;

public class DetailsDtBean extends BaseBaen {
	public Cont cont;
	public class Cont{
		public ArrayList<Commentlist> commentlist;
		public Tlist latestdetail;
		public class Commentlist{
			public String aid;
			public String createtime;
			public String face;
			public String id;
			public String isopen;
			public String msg;
			public String nickname;
			public String oface;
			public String onickname;
			public String ouid;
			public String pid;
			public String uid;
		}
	}
}
