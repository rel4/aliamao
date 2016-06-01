package com.aliamauri.meat.bean;

public class LoginBean {
	public String status;
	public String msg;
	public Cont cont;

	public class Cont {
		public String uid;
		public String nickname;
		public String face;
		public String sex;
		public String birth;
		public String ucode;
		public Txinfo txinfo;
		public String isinfocomplete;

		public String email;
		public String tel;
		public String email_verify;
		public String tel_verify;
		public String iseditpwd;
		public String signature;
		public String pland;
		public String job;
		public String hobby;
		public String shejiaoactive;

		public String darentype;

		public class Txinfo {
			public String hxuid;
			public String hxpwd;
		}
	}
}
