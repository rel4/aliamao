package com.aliamauri.meat.bean;

import java.util.List;

public class ChannelBean  extends BaseBaen{
	public ChannelCont cont;
	public class ChannelCont{
		public List<Channel> list;
		public List<Channel> listall;
		public class Channel {
			public int typeid;
			public String typename;
		}
	}
}
