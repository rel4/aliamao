package com.aliamauri.meat.activity.IM.db;

import java.util.ArrayList;
import java.util.List;

import com.aliamauri.meat.utils.LogUtil;
import com.easemob.chat.EMMessage;
import com.easemob.chat.core.i;

/**
 * 环信dao层
 * 
 * @author jb
 * 
 */
public class HXDao {
	private static HXDao xdao = new HXDao();
	private i dao;

	private HXDao() {
		if (dao == null) {
			try {
				dao = i.a();
			} catch (Exception e) {
				if (LogUtil.getDeBugState()) {
					throw new RuntimeException(
							"***************环信黑名单数据库错*********");
				}
				LogUtil.e("HXDao", "***************环信黑名单数据库错*********");
			}

		}
	}

	public static HXDao getInstance() {

		return xdao;
	}

	/*************************** 操作黑名单 ******************************/
	/**
	 * 保存好友list
	 * 
	 * @param contactList
	 */
	public void saveBlackListContactList(List<String> hxids) {
		if (dao != null) {

			dao.d(hxids);
		}
	}

	/**
	 * 获取好友list
	 * 
	 * @return
	 */
	public List<String> getBlackListContactList() {
		if (dao == null) {
			return new ArrayList<String>();
		}
		return dao.j();
	}

	/**
	 * 删除一个联系人
	 * 
	 * @param username
	 */
	public void deleteBlackListContact(String hxid) {
		if (dao != null) {

			dao.n(hxid);
		}
	}

	/**
	 * 保存一个联系人
	 * 
	 * @param user
	 */
	public void saveBlackListContact(String hxid) {
		if (dao != null) {

			dao.o(hxid);
		}
	}

	/********************************** 操作信息 ****************************************/

	public void saveEMMessage(EMMessage paramEMMessage) {
		if (dao != null) {

			dao.a(paramEMMessage);
		}
	}
}
