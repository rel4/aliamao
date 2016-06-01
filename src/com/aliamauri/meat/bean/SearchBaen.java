package com.aliamauri.meat.bean;

import java.util.List;

public class SearchBaen extends BaseBaen {
	public Cont cont;
	public Url url;

	public class Url {
		public String hotkeys;
		public String faxian;
	}

	public class Cont {
		public List<TV> hotkeys;
		public List<TV> faxian;

	}
//	public class TV implements Serializable {
//		public String id;
//		public String hasfilm;
//		public String name;
//		public String pic;
//		public String sdesc;
//		public String updatetime;
//		public String downloadurl;
//		public String num;
//		public String isnew;
//	}
}
