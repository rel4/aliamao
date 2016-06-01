package com.aliamauri.meat.bean.cmd;

import com.aliamauri.meat.activity.IM.domain.User.ContactType;

public class ContactInvitBean extends BaseCmdBaen {
	public String fromuid;
	public String fromnickname;
	public String fromuidhx;
	public String touid;
	public String tonickname;
	public String msg;
	public String fromface;
	public String logid;
	public String time;
	/******************************/
	
	public String fromsex;
	public String frompland;
	public String fromjob;
	public String frombirth;
	public String fromhobby;
	/**
	 * 邀请是否是自己发送
	 * 状态：０为正常发送
	 * 状态 ：1 为发送再反馈的信息
	 * 
	 */
	public String isInviteFromMe;
	public ContactType contactType;
}
