package com.aliamauri.meat.bean;

import java.util.List;

public class CollectionBean extends BaseBaen {
	public List<Cont> cont;

	public class Cont {
		public String id;
		public String film_id;
		public String film_name;
		public String time;
		public String pic;
	}
}
