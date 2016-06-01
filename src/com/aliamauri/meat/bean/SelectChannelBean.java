package com.aliamauri.meat.bean;

import java.util.List;

public class SelectChannelBean extends BaseBaen {
	private static final long serialVersionUID = -6813422264041300374L;
	public List<Cont> cont;
	public class Cont{
		public String typeid;
		public String typename;
	}
}
