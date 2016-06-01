package com.aliamauri.meat.bean;

import java.util.ArrayList;
import java.util.List;

public class PhoneBean {
	private String name;
	private List<String> phone;
	private String id;

	public PhoneBean() {
		phone = new ArrayList<String>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getPhone() {
		return phone;
	}

	public void addPhone(String phone) {
		this.phone.add(phone);
	}

	public String getID() {
		return id;
	}

	public void setID(String _id) {
		this.id = _id;
	}
}
