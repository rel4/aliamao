package com.aliamauri.meat.bean.hljy;

import java.util.ArrayList;

import com.aliamauri.meat.bean.BaseBaen;

public class XH_taBean extends BaseBaen {
	public ArrayList<Cont> cont;
	public class Cont{
		public String face;
		public String id;
		public String isopen;
		public String issmval;
		public String nickname;
		public String sex;
		public String signature;
		public String distance;
	}
}
