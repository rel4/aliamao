package com.aliamauri.meat.bean;

import java.io.Serializable;
import java.util.List;

public class SearchRseBean extends BaseBaen {
	public Cont cont;

	public class Cont {
		public List<Cfilm_area> listku;
		public List<Cfilm_area> cfilm_area2;
		public List<Cfilm_area> cfilm_area3;
		public List<Cfilm_area> cfilm_area5;
		public class Cfilm_area extends BaseBaen implements Serializable {
			public String actor;
			public String derector;
			public String isopen;
			public String cku_id;
			public String bfclick;
			public String up;
			public String id;
			public String name;
			public String jujinum ;
			
			public String hasfilm;
			public String pic;
			public String ctype_id;
			public String cfilm_id;
			public String ctypename;
			public String filmid;
			public String flagdd;
		}

	}
}
