package com.aliamauri.meat.bean;

public class UploadPicBean extends BaseBaen {

	public Cont cont;

	public class Cont {
		public String imgid;
		public String imgurl;
		public String imgurl_http;
		public String imgurl_ori;
		public String imgurl_ori_http;
		public String url;// 文件路径
		public String oriurl;// 缩约图路径
		public String bigimgurl; // 大图路径
		public String videoimgurl;// 视频截图路径

	}

}
