package com.aliamauri.meat.bean;

public class AuthenticateBean {
	public String status;
	public String msg;
	public Cont cont;

	public class Cont {
		public String isvalrealinfo;
		public String realname;
		public String idnumber;
		public String posttime;
		public String valtime;
	}
}
