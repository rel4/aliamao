package com.aliamauri.meat.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class DtBean extends BaseBaen implements Serializable {
	public Cont cont;

	public  class Cont  {
		public ArrayList<Tlist> tlist;
		public ArrayList<Recapp> recapp;
		public ArrayList<Recgroup> recgroup;
		
		public class Recgroup  {
			public String adminuid;    
			public String createtime;
			public String desc;
			public String hxgroupid;
			public String id;
			public String isopen;
			public String mnum;
			public String mtotal;
			public String name;
			public String pic;
			public String rec;
			public String tags;
		}

		public class Recapp  {
			public String createtime;
			public String desc;
			public String downloadurl;
			public String id;
			public String isopen;
			public String name;
			public String num;
			public String pic;
			public String sdesc;
			public String sort;
			public String stars;
			public String tid;
			public String updatetime;
		}

		public   class Tlist implements Serializable {
			public String createtime;
			public String distance;
			public String dz;
			public String face;
			public String iszan;
			public String id;
			public ArrayList<Imgs> imgs;
			public String infos;
			public String isnm;
			public String isopen;
			public String jd;
			public String nickname;
			public String pj;
			public String relinfo;
			public String tags;
			public String mmtype;
			public String type;
			public String uid;
			public ArrayList<Videos> videos;
			public ArrayList<Voices> voices;
			public String wd;
			public String yd;
			public String zf;
			public String zfinfo;
			public Zfinfox zfinfox;

			public class Zfinfox implements Serializable {
				public String createtime;
				public String dz;
				public String flid;
				public String fnickname;//上一级名称
				public String fuid;
				public String id;
				public ArrayList<Imgs> imgs;
				public String infos;
				public String isnm;
				public String isopen;
				public String jd;
				public String olid;
				public String onickname;//原始名称
				public String ouid;
				public String pj;
				public String relinfo;
				public String tags;
				public String type;
				public String uid;
				public ArrayList<Videos> videos;
				public ArrayList<Voices> voices;
				public String wd;
				public String yd;
				public String zf;
				public String zfinfo; 
			}

		
			public class Videos implements Serializable{
				public String id;
				public String src;
				public String srcbg;
				public String sc;
				public String filesize;
			}
			public class Imgs implements Serializable{
				public String id;
				public String img;
				public String imgori;
				public String info;

			}

			public class Voices implements Serializable{
				public String id;
				public String src;
				public String srcbg;
				public String sc;
				public String filesize;
			}
		}
	}
}
