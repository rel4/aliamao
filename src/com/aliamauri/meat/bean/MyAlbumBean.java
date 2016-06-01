package com.aliamauri.meat.bean;

import java.util.List;

public class MyAlbumBean {
	public String status;
	public String msg;
	public List<Cont> cont;

	public class Cont {
		public String id;
		public String uid;
		public String isopen;
		public String imgurl;
		public String imgurlori;
		public String createtime;
	}

}
