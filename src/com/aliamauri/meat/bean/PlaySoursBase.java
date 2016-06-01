package com.aliamauri.meat.bean;

import java.util.List;

public class PlaySoursBase extends BaseBaen {
	public Cont cont;
	public String url;

	public class Cont {
		public Ckuinfo ckuinfo;
		public Shareinfo shareinfo;
		public List<Cfilmlist> cfilmlist;

		public class Shareinfo {
			public String sharetitle;
			public String sharedesc;
			public String shareurl;
			public String sharepic;
		}

		public class Ckuinfo {
			public String id;
			public String name;
			public String enname;
			public String rec;
			public String desc;
			public String sdesc;
			public String actor;
			public String derector;
			public String click;
			public String area_id;
			public String ctype_id;
			public String jujinum;
			public String cfilm_id;
			public String cfilm_isdefine;
			public String cfilm_de_id;
			public String updatetime;
			public String bf_num;
			public String area;
			public String typename;

		}

		public class Cfilmlist {
			public String id;
			public String name;
			public String hash;
			public String up;
			public String down;
			public String publisherurl;
			public String sid;
			public String surl;
			public String jindex;
			public String sort;
			public String updatetime;
			public String pic;
			public String lmlinkurl;
			public String type;
			public String picdown;
			public String lmhash;
			public long filesize;
		}
	}
}
