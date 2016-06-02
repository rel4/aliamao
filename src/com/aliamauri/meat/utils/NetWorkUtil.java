package com.aliamauri.meat.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NetWorkUtil {

	public static boolean isWifiAvailable(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected() && networkInfo
				.getType() == ConnectivityManager.TYPE_WIFI);
	}

	/**
	 * 获取MAC地址
	 * 
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		if (context == null) {
			return "";
		}

		String localMac = null;
		if (isWifiAvailable(context)) {
			localMac = getWifiMacAddress(context);
		}

		if (localMac != null && localMac.length() > 0) {
			localMac = localMac.replace(":", "-").toLowerCase();
			return localMac;
		}

		localMac = getMacFromCallCmd();
		if (localMac != null) {
			localMac = localMac.replace(":", "-").toLowerCase();
		}

		return localMac;
	}

	private static String getWifiMacAddress(Context context) {
		String localMac = null;
		try {
			WifiManager wifi = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			if (wifi.isWifiEnabled()) {
				localMac = info.getMacAddress();
				if (localMac != null) {
					localMac = localMac.replace(":", "-").toLowerCase();
					return localMac;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 通过callCmd("busybox ifconfig","HWaddr")获取mac地址
	 * 
	 * @attention 需要设备装有busybox工具
	 * @return Mac Address
	 */
	private static String getMacFromCallCmd() {
		String result = "";
		result = callCmd("busybox ifconfig", "HWaddr");

		if (result == null || result.length() <= 0) {
			return null;
		}

		// DebugLog.v("tag", "cmd result : " + result);

		// 对该行数据进行解析
		// 例如：eth0 Link encap:Ethernet HWaddr 00:16:E8:3E:DF:67
		if (result.length() > 0 && result.contains("HWaddr") == true) {
			String Mac = result.substring(result.indexOf("HWaddr") + 6,
					result.length() - 1);
			if (Mac.length() > 1) {
				result = Mac.replaceAll(" ", "");
			}
		}

		return result;
	}

	public static String callCmd(String cmd, String filter) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);

			// 执行命令cmd，只取结果中含有filter的这一行
			while ((line = br.readLine()) != null
					&& line.contains(filter) == false) {
			}

			result = line;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean IsNetWorkEnable(Context context) {
		try {
			ConnectivityManager connectivity = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {

				// ToastUtil.showMessage(context, "无法连接网络");
				return false;
			}

			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected()) {
				// 判断当前网络是否已经连接
				if (info.getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// ToastUtil.showMessage(context, "无法连接网络");
		return false;
	}

	public static final int NETWORK_TYPE_UNAVAILABLE = -1;
	// private static final int NETWORK_TYPE_MOBILE = -100;
	public static final int NETWORK_TYPE_WIFI = -101;

	public static final int NETWORK_CLASS_WIFI = -101;
	public static final int NETWORK_CLASS_UNAVAILABLE = -1;
	/** Unknown network class. */
	public static final int NETWORK_CLASS_UNKNOWN = 0;
	/** Class of broadly defined "2G" networks. */
	public static final int NETWORK_CLASS_2_G = 1;
	/** Class of broadly defined "3G" networks. */
	public static final int NETWORK_CLASS_3_G = 2;
	/** Class of broadly defined "4G" networks. */
	public static final int NETWORK_CLASS_4_G = 3;

	private static DecimalFormat df = new DecimalFormat("#.##");

	// 适配低版本手机
	/** Network type is unknown */
	public static final int NETWORK_TYPE_UNKNOWN = 0;
	/** Current network is GPRS */
	public static final int NETWORK_TYPE_GPRS = 1;
	/** Current network is EDGE */
	public static final int NETWORK_TYPE_EDGE = 2;
	/** Current network is UMTS */
	public static final int NETWORK_TYPE_UMTS = 3;
	/** Current network is CDMA: Either IS95A or IS95B */
	public static final int NETWORK_TYPE_CDMA = 4;
	/** Current network is EVDO revision 0 */
	public static final int NETWORK_TYPE_EVDO_0 = 5;
	/** Current network is EVDO revision A */
	public static final int NETWORK_TYPE_EVDO_A = 6;
	/** Current network is 1xRTT */
	public static final int NETWORK_TYPE_1xRTT = 7;
	/** Current network is HSDPA */
	public static final int NETWORK_TYPE_HSDPA = 8;
	/** Current network is HSUPA */
	public static final int NETWORK_TYPE_HSUPA = 9;
	/** Current network is HSPA */
	public static final int NETWORK_TYPE_HSPA = 10;
	/** Current network is iDen */
	public static final int NETWORK_TYPE_IDEN = 11;
	/** Current network is EVDO revision B */
	public static final int NETWORK_TYPE_EVDO_B = 12;
	/** Current network is LTE */
	public static final int NETWORK_TYPE_LTE = 13;
	/** Current network is eHRPD */
	public static final int NETWORK_TYPE_EHRPD = 14;
	/** Current network is HSPA+ */
	public static final int NETWORK_TYPE_HSPAP = 15;

	/**
	 * 格式化大小
	 * 
	 * @param size
	 * @return
	 */
	public static String formatSize(long size) {
		String unit = "B";
		float len = size;
		if (len > 900) {
			len /= 1024f;
			unit = "KB";
		}
		if (len > 900) {
			len /= 1024f;
			unit = "MB";
		}
		if (len > 900) {
			len /= 1024f;
			unit = "GB";
		}
		if (len > 900) {
			len /= 1024f;
			unit = "TB";
		}
		return df.format(len) + unit;
	}

	public static String formatSizeBySecond(long size) {
		String unit = "B";
		float len = size;
		if (len > 900) {
			len /= 1024f;
			unit = "KB";
		}
		if (len > 900) {
			len /= 1024f;
			unit = "MB";
		}
		if (len > 900) {
			len /= 1024f;
			unit = "GB";
		}
		if (len > 900) {
			len /= 1024f;
			unit = "TB";
		}
		return df.format(len) + unit + "/s";
	}

	public static String format(long size) {
		String unit = "B";
		float len = size;
		if (len > 1000) {
			len /= 1024f;
			unit = "KB";
			if (len > 1000) {
				len /= 1024f;
				unit = "MB";
				if (len > 1000) {
					len /= 1024f;
					unit = "GB";
				}
			}
		}
		return df.format(len) + "\n" + unit + "/s";
	}
	
	/**
	 * 获取运营商
	 * 
	 * @return
	 */
	public static String getProvider(Context context) {
		String provider = "未知";
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context
					.getSystemService(Context.TELEPHONY_SERVICE);
			String IMSI = telephonyManager.getSubscriberId();
			Log.v("tag", "getProvider.IMSI:" + IMSI);
			if (IMSI == null) {
				if (TelephonyManager.SIM_STATE_READY == telephonyManager
						.getSimState()) {
					String operator = telephonyManager.getSimOperator();
					Log.v("tag", "getProvider.operator:" + operator);
					if (operator != null) {
						if (operator.equals("46000")
								|| operator.equals("46002")
								|| operator.equals("46007")) {
							provider = "中国移动";
						} else if (operator.equals("46001")) {
							provider = "中国联通";
						} else if (operator.equals("46003")) {
							provider = "中国电信";
						}
					}
				}
			} else {
				if (IMSI.startsWith("46000") || IMSI.startsWith("46002")
						|| IMSI.startsWith("46007")) {
					provider = "中国移动";
				} else if (IMSI.startsWith("46001")) {
					provider = "中国联通";
				} else if (IMSI.startsWith("46003")) {
					provider = "中国电信";
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return provider;
	}

	/**
	 * 获取网络类型
	 * 
	 * @return
	 */
	public static String getCurrentNetworkType(Context context) {
		int networkClass = getNetworkClass(context);
		String type = "未知";
		switch (networkClass) {
		case NETWORK_CLASS_UNAVAILABLE:
			type = "无";
			break;
		case NETWORK_CLASS_WIFI:
			type = "Wi-Fi";
			break;
		case NETWORK_CLASS_2_G:
			type = "2G";
			break;
		case NETWORK_CLASS_3_G:
			type = "3G";
			break;
		case NETWORK_CLASS_4_G:
			type = "4G";
			break;
		case NETWORK_CLASS_UNKNOWN:
			type = "未知";
			break;
		}
		return type;
	}

	private static int getNetworkClassByType(int networkType) {
		switch (networkType) {
		case NETWORK_TYPE_UNAVAILABLE:
			return NETWORK_CLASS_UNAVAILABLE;
		case NETWORK_TYPE_WIFI:
			return NETWORK_CLASS_WIFI;
		case NETWORK_TYPE_GPRS:
		case NETWORK_TYPE_EDGE:
		case NETWORK_TYPE_CDMA:
		case NETWORK_TYPE_1xRTT:
		case NETWORK_TYPE_IDEN:
			return NETWORK_CLASS_2_G;
		case NETWORK_TYPE_UMTS:
		case NETWORK_TYPE_EVDO_0:
		case NETWORK_TYPE_EVDO_A:
		case NETWORK_TYPE_HSDPA:
		case NETWORK_TYPE_HSUPA:
		case NETWORK_TYPE_HSPA:
		case NETWORK_TYPE_EVDO_B:
		case NETWORK_TYPE_EHRPD:
		case NETWORK_TYPE_HSPAP:
			return NETWORK_CLASS_3_G;
		case NETWORK_TYPE_LTE:
			return NETWORK_CLASS_4_G;
		default:
			return NETWORK_CLASS_UNKNOWN;
		}
	}

	public static int getNetworkClass(Context context) {
		int networkType = NETWORK_TYPE_UNKNOWN;
		try {
			final NetworkInfo network = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = NETWORK_TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					TelephonyManager telephonyManager = (TelephonyManager) context
							.getSystemService(Context.TELEPHONY_SERVICE);
					networkType = telephonyManager.getNetworkType();
				}
			} else {
				networkType = NETWORK_TYPE_UNAVAILABLE;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return getNetworkClassByType(networkType);

	}

	public static String getWifiRssi(Context context) {
		int asu = 85;
		try {
			final NetworkInfo network = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					WifiManager wifiManager = (WifiManager) context
							.getSystemService(Context.WIFI_SERVICE);

					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					if (wifiInfo != null) {
						asu = wifiInfo.getRssi();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return asu + "dBm";
	}

	public static String getWifiSsid(Context context) {
		String ssid = "";
		try {
			final NetworkInfo network = ((ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE))
					.getActiveNetworkInfo();
			if (network != null && network.isAvailable()
					&& network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					WifiManager wifiManager = (WifiManager) context
							.getSystemService(Context.WIFI_SERVICE);

					WifiInfo wifiInfo = wifiManager.getConnectionInfo();
					if (wifiInfo != null) {
						ssid = wifiInfo.getSSID();
						if (ssid == null) {
							ssid = "";
						}
						ssid = ssid.replaceAll("\"", "");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ssid;
	}

	/**
	 * 检查sim卡状态
	 * 
	 * @param ctx
	 * @return
	 */
	public static boolean checkSimState(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT
				|| tm.getSimState() == TelephonyManager.SIM_STATE_UNKNOWN) {
			return false;
		}

		return true;
	}

	/**
	 * 获取imei
	 */
	public static String getImei(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = mTelephonyMgr.getDeviceId();
		if (imei == null) {
			imei = "000000000000000";
		}
		return imei;
	}

	public static String getPhoneImsi(Context context) {
		TelephonyManager mTelephonyMgr = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return mTelephonyMgr.getSubscriberId();
	}

	public static long getUidRxBytes(Context context) {
		long ret = 0;
		PackageManager pm = context.getPackageManager();
		ApplicationInfo ai = null;

		try {
			ai = pm.getApplicationInfo(UIUtils.getContext().getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e1) {
			// e1.printStackTrace();
			// MyLog.printStackTrace(e1);
		}

		if (ai != null) {
			ret = TrafficStats.getUidRxBytes(ai.uid) == TrafficStats.UNSUPPORTED ? 0
					: (TrafficStats.getTotalRxBytes() / 1024);
		}

		return ret;
	}
	/**
	 * 格式化文件大小
	 * @param fileS
	 * @return
	 */
	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#0.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	// public static CellInfo getNetInfo() {
	// CellInfo info = new CellInfo();
	// try {
	// TelephonyManager mTelephonyManager = (TelephonyManager) ConfigManager
	// .getContext().getSystemService(Context.TELEPHONY_SERVICE);
	// String operator = mTelephonyManager.getNetworkOperator();
	// if (operator != null) {
	// /** 通过operator获取 MCC 和MNC */
	// if (operator.length() > 3) {
	// String mcc = operator.substring(0, 3);
	// String mnc = operator.substring(3);
	// info.setMcc(mcc);
	// info.setMnc(mnc);
	// }
	// }
	//
	// int lac = 0;
	// int cellId = 0;
	// int phoneType = mTelephonyManager.getPhoneType();
	// if (phoneType == TelephonyManager.PHONE_TYPE_GSM) {
	// GsmCellLocation location = (GsmCellLocation) mTelephonyManager
	// .getCellLocation();
	// /** 通过GsmCellLocation获取中国移动和联通 LAC 和cellID */
	// lac = location.getLac();
	// cellId = location.getCid();
	// } else if (phoneType == TelephonyManager.PHONE_TYPE_CDMA) {
	// CdmaCellLocation location = (CdmaCellLocation) mTelephonyManager
	// .getCellLocation();
	// lac = location.getNetworkId();
	// cellId = location.getBaseStationId();
	// cellId /= 16;
	// }
	// if (lac == 0 || cellId == 0) {
	// List<NeighboringCellInfo> infos = mTelephonyManager
	// .getNeighboringCellInfo();
	// int lc = 0;
	// int ci = 0;
	// int rssi = 0;
	// for (NeighboringCellInfo cell : infos) {
	// // 根据邻区总数进行循环
	// if (lc == 0 || ci == 0) {
	// lc = cell.getLac();
	// ci = cell.getCid();
	// rssi = cell.getRssi();
	// }
	// // sb.append(" LAC : " + info.getLac());
	// // // 取出当前邻区的LAC
	// // sb.append(" CID : " + info.getCid());
	// // // 取出当前邻区的CID
	// // sb.append(" BSSS : " + (-113 + 2 * info.getRssi()) +
	// // "\n"); // 获取邻区基站信号强度
	// }
	// rssi = -113 + 2 * rssi;
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return info;
	// }

}