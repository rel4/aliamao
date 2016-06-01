package com.aliamauri.meat.bean.hljy;

import java.io.Serializable;
import java.util.ArrayList;

import com.aliamauri.meat.bean.BaseBaen;
/**
 * 婚恋交友--一见钟情---附近
 * @author limaokeji-windosc
 *
 */
public class Yjzq_fjBean extends BaseBaen implements Serializable {
	public String distance;
	public ArrayList<Cont> cont;
	public class Cont implements Serializable{
		public String distance;	
		public String face;	
		public String hy_num;	
		public String id;	
		public String isopen;	
		public String jd;	
		public String name;	
		public String nickname;	
		public String time;	
		public String uid;	
		public String wd;	
	}
}
