package com.aliamauri.meat.bean;

import java.util.ArrayList;

/**
 * 附近---资源库
 * @author limaokeji-windosc
 *
 */
public class ResourceLibraryBean extends BaseBaen {
	public ArrayList<Cont> cont;
	public class Cont{
		public String content;
		public String createtime;
		public String down;
		public String face;
		public String id;
		public String isopen;
		public String isact;
		public String name;
		public String nickname;
		public String uid;
		public String up;
	}
}
