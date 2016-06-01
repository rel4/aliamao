package com.aliamauri.meat.top.protocol;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.top.manager.HttpHelper;
import com.aliamauri.meat.top.utils.FileUtils;
import com.aliamauri.meat.utils.LogUtil;
import com.lidroid.xutils.util.IOUtils;

public abstract class BaseProtocol<T> {
	public T load(int channelid, int page) {
		String json;
		// 加载本地数据
		json = loadFromLocal(channelid,page);
		json = null;
		if (json == null) {
			// 请求服务器
			json = loadServer(channelid,page);
			if (json != null) {
				// 保存到本地
//				save2local(json, channelid,page);
			}
		}
		if (json != null) {
			// 解析json
			return parserJson(json);
		} else {
			return null;
		}
	}

	// 读取缓存
	private String loadFromLocal(int channelid, int page) {
		String dirPath = FileUtils.getCacheDir();
		File file = new File(dirPath, getKey() + getParams() + "_" + channelid+"_"+page);
		if (file.exists()) {
			FileReader reader = null;
			try {
				reader = new FileReader(file);
				BufferedReader br = new BufferedReader(reader);
				long outOfData = Long.parseLong(br.readLine());
				long currentTime = System.currentTimeMillis();
				if (currentTime > outOfData) {
					return null;
				}
				String str;
				// 内存的输出流,输出到内存中
				StringWriter sw = new StringWriter();
				while ((str = br.readLine()) != null) {
					sw.write(str);
				}
				String result = sw.toString();
				LogUtil.i(this, "读取到了缓存");
				return result;
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(reader);
			}
		}
		return null;
	}

	// //缓存文件 为了节省用户的流量 保存到本地
	private void save2local(String json, int channelid, int page) {
		// 1 一条条数据 存储到数据库中
		// 2 把整个json文件 存放到一个文件夹中
		String dirPath = FileUtils.getCacheDir();
		File file = new File(dirPath, getKey() + getParams() + "_" + channelid+"_"+page);
		BufferedWriter fw = null;
		try {
			fw = new BufferedWriter(new FileWriter(file));
			fw.write(System.currentTimeMillis() + 60000 + "");
			fw.newLine();// 换行
			fw.write(json);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(fw);
		}

	}

	/**
	 * 从服务器获取数据
	 * 
	 * @param channelid
	 * @param page
	 * @return
	 */
	private String loadServer(int channelid, int page) {
		String json = HttpHelper.sendGet(NetworkConfig.getDTVListAll(channelid, page));
		return json;
	}

	/**
	 * 交给子类去实现
	 * 
	 * @return
	 */
	protected String getParams() {
		return "";
	}

	/**
	 * 关键的标识
	 * 
	 * @return
	 */
	public abstract String getKey();

	/**
	 * 解析json
	 * 
	 * @param json
	 */
	protected abstract T parserJson(String json);

}
