package com.aliamauri.meat.bean;

import java.util.List;

public class DTVListBaen extends BaseBaen {

	public List<DTvCont> cont;
	public List<DTvCont> zhiding;

	public class DTvCont extends BaseTopBean {

		public String param;
		public int moviesType;
		public int type;
		public String bf;
		public String dz;

		public String name;

		public String uid;
		public String hpic;
		public String rec;
		public String pl;
		public String nickname;
		public String face;

		public List<TwoDTVCont> cont;

		public class TwoDTVCont {
			public String face;
			public String id;
			public String msg;
			public String nikename;
			public String uid;
		}
	}
}
