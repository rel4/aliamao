package com.aliamauri.meat.bean.hljy;

import java.util.ArrayList;
/**
 * 发现---婚恋交友---热门公告接口
 * @author limaokeji-windosc
 *
 */
public class RmggBean {
	public Cont cont;
	public String status;
	public Url url;
	public class Cont {
		public ArrayList<Gonggao> gonggao;
		public ArrayList<Xingqu> xingqu;
		
		public class Gonggao{
			public String face;
			public String hy_num;
			public String id;
			public String nickname;
			public String isopen;
			public String name;
			public String time;
			public String uid;
		}
		public class Xingqu{
			public String face;
			public String id;
			public String isopen;
			public String nickname;
		}
	}
	public class Url{
		public String gonggao;
		public String xingqu;
	}
	
}
