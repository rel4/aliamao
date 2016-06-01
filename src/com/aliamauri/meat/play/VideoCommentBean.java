package com.aliamauri.meat.play;

import java.util.List;

import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.Comlist;

public class VideoCommentBean extends BaseBaen {

	private static final long serialVersionUID = -969832653491057155L;
	public Cont cont;
	public class Cont{
		public List<Comlist> commentList;
		public MainComment mainComment;
		public class MainComment{
			public String face;
			public String id;
			public String nickname;
			public String msg;
			public String replynum;
			public String time;
			public String uid;
			
		}
	}
}
