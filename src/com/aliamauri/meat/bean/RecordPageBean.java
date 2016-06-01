package com.aliamauri.meat.bean;

import java.io.Serializable;
import java.util.List;

public class RecordPageBean extends BaseBaen implements Serializable {
	public String url;
	public Cont cont;
	public List<List_all> cont2;

	public class Cont implements Serializable {
		public List<List_all> list_old;
		public List<List_all> list_today;
		public List<List_all> list_week;
	}

	public class List_all implements Serializable {
		public String film_id;
		public String film_name;
		public String pic;
		public String time;
		public String id;

	}

}
