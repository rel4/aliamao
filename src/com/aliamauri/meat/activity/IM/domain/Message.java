package com.aliamauri.meat.activity.IM.domain;

public class Message {

	// 时间
	private long time;
	// 添加理由
	private String reason;
	// 请求好友的id
	private String toContactId;
	// 标示
	private String fromContactNick;

	private String logid;

	/**
	 * 消息发送来源
	 * 状态 0  正常发送信息（不是反馈信息）
	 * 状态 1  反馈信息（发送信息后返回信息）
	 */
	private String isInviteFromMe;

	/**
	 * 信息来源的用户ID
	 */
	private String fromContactId;
	/**
	 * 信息来源用户环信ID
	 */
	private String fromContactHxid;
	/**
	 * 信息来源用户头像
	 */
	private String fromContactFacePath;
	/**
	 * 信息来源用户昵称
	 */
	private String fromContactUserNikeName;
	/**
	 * 信息来源用户状态
	 */
	private MesageStatus status;

	// 群id
	private String groupId;
	// 群名称
	private String groupName;

	private int id;

	public enum MesageStatus {
		/** 被邀请 */
		BEINVITEED,
		/** 被拒绝 */
		BEREFUSED,
		/** 对方同意 */
		BEAGREED,
		/** 对方申请 */
		BEAPPLYED,
		/** 我同意了对方的请求 */
		AGREED,
		/** 我拒绝了对方的请求 */
		REFUSED

	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getToContactId() {
		return toContactId;
	}

	public void setToContactId(String toContactId) {
		this.toContactId = toContactId;
	}

	public String getFromContactNick() {
		return fromContactNick;
	}

	public void setFromContactNick(String fromContactNick) {
		this.fromContactNick = fromContactNick;
	}

	public String getLogid() {
		return logid;
	}

	public void setLogid(String logid) {
		this.logid = logid;
	}

	public String getIsInviteFromMe() {
		return isInviteFromMe;
	}

	public void setIsInviteFromMe(String isInviteFromMe) {
		this.isInviteFromMe = isInviteFromMe;
	}

	public String getFromContactId() {
		return fromContactId;
	}

	public void setFromContactId(String fromContactId) {
		this.fromContactId = fromContactId;
	}

	public String getFromContactHxid() {
		return fromContactHxid;
	}

	public void setFromContactHxid(String fromContactHxid) {
		this.fromContactHxid = fromContactHxid;
	}

	public String getFromContactFacePath() {
		return fromContactFacePath;
	}

	public void setFromContactFacePath(String fromContactFacePath) {
		this.fromContactFacePath = fromContactFacePath;
	}

	

	public String getFromContactUserNikeName() {
		return fromContactUserNikeName;
	}

	public void setFromContactUserNikeName(String fromContactUserNikeName) {
		this.fromContactUserNikeName = fromContactUserNikeName;
	}

	public MesageStatus getStatus() {
		return status;
	}

	public void setStatus(MesageStatus status) {
		this.status = status;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
