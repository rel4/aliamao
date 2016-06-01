package com.aliamauri.meat.bean;


public class SplashBean {
	public String msg;
	public String status;
	public Cont cont;

	public class Cont {
		public Userinfo userinfo;
		public Uusetting uusetting;

		public class Userinfo {
			public String id;
			public String nickname;
			public String tel;
			public String tel_verify;
		}

		public class Uusetting {
			public Loginbg loginbg;

			public class Loginbg {
				public String pic;
				public String time;
			}
		}
	}
}
