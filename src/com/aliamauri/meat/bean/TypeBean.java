package com.aliamauri.meat.bean;

import java.util.List;

import com.aliamauri.meat.bean.cont.TypeCont;

public class TypeBean extends BaseBaen {
	public List<Cont> cont;

	public class Cont {
		public String id;
		public String name;
		public List<Son> son;
	}

	public class Son {
		public String id;
		public String name;
		public List<TypeCont> son;
	}
}
