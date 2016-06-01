package com.aliamauri.meat.bean;

import java.io.Serializable;
import java.util.List;

public class NetworkMenuDetailPagerBase extends BaseBaen {
	public Cont cont;

	public class Cont implements Serializable {
		public List<Recdata> list;
		public Recdata recdata;

		public class Recdata implements Serializable {
			public String id;
			public String click;
			public String updatetime;
			public String shortname;
			public String name;
			public String pic1;
			public String pic2;
			public String pic3;
			public String isopen;
			public String rec;
			public String sort;
			public String hasfilm;
		}

	}

}
