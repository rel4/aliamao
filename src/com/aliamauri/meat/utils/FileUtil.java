package com.aliamauri.meat.utils;

import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.MultimediaInfo;

import java.io.File;

public class FileUtil {

	public static void CreateFile(String path) {
		if (path == null) {
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			// try {
			file.mkdirs();
			// // file.createNewFile();
			// // } catch (IOException e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
		}
	}

	public static long getDuration(String path) {
		if (path == null || "".equals(path.trim())) {
			return 0;
		}
		File source = new File(path);
		if (!source.exists()) {
			return 0;
		}
		Encoder encoder = new Encoder();
		try {
			MultimediaInfo m = encoder.getInfo(source);
			long ls = m.getDuration();
			return ls / 1000;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
