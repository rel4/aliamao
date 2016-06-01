package com.aliamauri.meat.bean.hljy;

import java.util.ArrayList;
/**
 * 进入公告详情页面的bean
 * @author limaokeji-windosc
 *
 */
public class Ggxq_detailBean {
	public Cont cont;
	public class Cont{
		public Baseinfo baseinfo;
		public ArrayList<Replylist> replylist;
		public class Baseinfo{
			public String face;
			public String hy_num;
			public String id;
			public String isopen;
			public String jd;
			public String name;
			public String nickname;
			public String time;
			public String uid;
			public String wd;
		}
		public class Replylist{
			public String id;
			public String uid;
			public String name;
			public String notice_id;
			public String time;
			public String jd;
			public String wd;
			public String face;
			public String nickname;
		}
	}
	public String status;
	public Url url;
	public class Url{
		public String replylist;
	}
}
