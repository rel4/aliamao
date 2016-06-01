package com.aliamauri.meat.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.ContactsContract;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.aliamauri.meat.bean.PhoneBean;
import com.aliamauri.meat.bean.cont.TelFriend.TelFriends;

public class PhoneInfoUtils {
	private static TelephonyManager telephonyManager;

	/**
	 * 获取电话号码
	 */

	public static String getPhoneNumber(Context context) {
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager.getLine1Number() != null) {
			int length = telephonyManager.getLine1Number().length();
			if (length > 11) {
				String tel = telephonyManager.getLine1Number().trim()
						.substring(length - 11, length);
				return tel;
			}
			return telephonyManager.getLine1Number().trim();
		} else {
			return null;
		}

	}

	public static String getPhoneImei(Context context) {
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return telephonyManager.getDeviceId();
	}

	public static String getPhoneMac(Context context) {
		telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();

		if (info.getMacAddress() != null) {

			String mac = "";
			char[] macArray = info.getMacAddress().toCharArray();
			for (int i = 0; i < macArray.length; i++) {
				if (isNumeric(macArray[i]) || check(macArray[i])) {
					mac += macArray[i];
				}
			}
			return mac;
		} else {
			return null;
		}

	}

	public static boolean isNumeric(char macArray) {

		if (!Character.isDigit(macArray)) {
			return false;
		}

		return true;
	}

	public static boolean check(char macArray) {
		// char c = macArray.charAt(0);
		if (((macArray >= 'a' && macArray <= 'z') || (macArray >= 'A' && macArray <= 'Z'))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 获取没帐号的通讯录好友
	 */
	public static int getNoAccountTel(List<TelFriends> tfs, String tel) {
		if (tel == null || "".equals(tel)) {
			return -1;
		}
		for (int i = 0; i < tfs.size(); i++) {
			if (tel.equals(tfs.get(i).tel)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 获取通讯录所有号码
	 */
	public static String getMapNameByTel(Map<String, String> map, String tel) {
		String name = "";
		if (tel == null || "".equals(tel)) {
			return name;
		}
		for (Map.Entry<String, String> entry : map.entrySet()) {
			if (tel.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return name;
	}

	/**
	 * 获取通讯录所有号码
	 */
	public static Map<String, String> getPhone(ContentResolver cr) {
		Map<String, String> map = new HashMap<>();
		List<PhoneBean> phoneBean = PhoneInfoUtils.getPerson(cr);
		for (PhoneBean pb : phoneBean) {
			for (String p : pb.getPhone()) {
				if (p == null || "".equals(p)) {

				} else {
					if (p.length() > 11) {
						p = p.trim().substring(p.length() - 11, p.length());
					}
					if (CheckUtils.getInstance().isMobile(p)) {
						if (!p.equals(getPhoneNumber(UIUtils.getContext()))) {
							// phone += p + ",";
							if (map.get(pb.getName()) != null) {
								map.put(pb.getName(), map.get(pb.getName())
										+ "," + p);
							} else {
								map.put(pb.getName(), p);
							}
						}
					}
				}
			}
		}
		return map;
	}

	public static void test(ContentResolver cr) {
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);

		// 向下移动一下光标

		while (cursor.moveToNext())

		{

			// 取得联系人名字

			int nameFieldColumnIndex = cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);

			String contact = cursor.getString(nameFieldColumnIndex);// 取得电话号码

			int phoneIndex = 0;

			phoneIndex = cursor
					.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			String phoneNumber = cursor.getString(phoneIndex);
			Log.e("phoneInfo", contact + ":" + phoneNumber + "");
			// string += (contact + ":" + number + "");

		}

		cursor.close();
	}

	public static List<PhoneBean> getPerson(ContentResolver cr) {

		// Map<String, String> map = new HashMap<String, String>();
		Uri uri = ContactsContract.Data.CONTENT_URI;// 2.0以上系统使用ContactsContract.Data访问联系人
		Cursor cursor = cr.query(uri, null, null, null, "display_name");// 显示联系人时按显示名字排序
		cursor.moveToFirst();
		List<PhoneBean> list = new ArrayList<PhoneBean>();

		int Index_CONTACT_ID = cursor
				.getColumnIndex(ContactsContract.Data.CONTACT_ID);// 获得CONTACT_ID在ContactsContract.Data中的列数
		int Index_DATA1 = cursor.getColumnIndex(ContactsContract.Data.DATA1);// 获得DATA1在ContactsContract.Data中的列数
		int Index_MIMETYPE = cursor
				.getColumnIndex(ContactsContract.Data.MIMETYPE);// 获得MIMETYPE在ContactsContract.Data中的列数

		while (cursor.getCount() > cursor.getPosition()) {
			PhoneBean person = null;
			String id = cursor.getString(Index_CONTACT_ID);// 获得CONTACT_ID列的内容
			String info = cursor.getString(Index_DATA1);// 获得DATA1列的内容
			String mimeType = cursor.getString(Index_MIMETYPE);//
			// 获得MIMETYPE列的内容
			// 遍历查询当前行对应的联系人信息是否已添加到list中
			// for (int n = 0; n < list.size(); n++) {
			// if (list.get(n).getID() != null) {
			// if (list.get(n).getID().equals(id)) {
			// person = list.get(n);
			// // person.setName(contact);
			// break;
			// }
			// }
			// }

			// if (person == null) {
			person = new PhoneBean();
			// person.setName(contact);
			person.setID(id);
			int nameFieldColumnIndex = cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME);
			if (nameFieldColumnIndex > 0) {
				// String contact = cursor.getString(nameFieldColumnIndex);
				person.setName(cursor.getString(nameFieldColumnIndex));
			}

			if (mimeType.equals("vnd.android.cursor.item/phone_v2"))//
			// 该行数据为电话号码
			{
				person.addPhone(info);
				list.add(person);
			}
			// }
			// else if (mimeType.equals("vnd.android.cursor.item/name"))//
			// 该行数据为名字
			// {
			// person.setName(info);
			// 1458721741964
			// }1458721746400
			// 取得联系人名字
			// Log.e("phoneInfoUtils_name=", person.getName() + "");
			cursor.moveToNext();
		}
		return list;
	}

	public static List<PhoneBean> getPersonList(ContentResolver cr) {
		List<PhoneBean> list = new ArrayList<PhoneBean>();
		Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
				null, null, null);
		int contactIdIndex = 0;
		int nameIndex = 0;

		if (cursor.getCount() > 0) {
			contactIdIndex = cursor
					.getColumnIndex(ContactsContract.Contacts._ID);
			nameIndex = cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		}
		while (cursor.moveToNext()) {
			String contactId = cursor.getString(contactIdIndex);
			String name = cursor.getString(nameIndex);
			PhoneBean person = new PhoneBean();
			person.setName(name);
			/*
			 * 查找该联系人的phone信息
			 */
			Cursor phones = cr.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
							+ contactId, null, null);
			int phoneIndex = 0;
			if (phones.getCount() > 0) {
				phoneIndex = phones
						.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
			}
			while (phones.moveToNext()) {
				String phoneNumber = phones.getString(phoneIndex);
				person.addPhone(phoneNumber);
			}
			list.add(person);
			phones.close();
		}
		cursor.close();
		return list;
	}
}
