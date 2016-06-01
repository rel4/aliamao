package com.aliamauri.meat.bean;

import java.util.List;

public class AttentionBean extends BaseBaen {
	public List<Cont> cont;
	public String num;

	public class Cont {
		public String uid;
		public String nickname;
		public String sex;
		public String signature;
		public String face;
		public String province;
		public String isfollow;
		public String birth;
		public String darentype;
	}

}
