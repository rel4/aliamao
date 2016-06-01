package com.aliamauri.meat.utils;


public class SQLiteUtils {
	// private final String DATABASE_PATH = android.os.Environment
	// .getExternalStorageDirectory().getAbsolutePath() + "/dictionary";
	// private final String DATABASE_FILENAME = "dictionary.db3";
	//
	// private SQLiteDatabase openDatabase() {
	// try {
	// // 获得dictionary.db文件的绝对路径
	// String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
	// File dir = new File(DATABASE_PATH);
	// // 如果/sdcard/dictionary目录中存在，创建这个目录
	// if (!dir.exists())
	// dir.mkdir();
	// // 如果在/sdcard/dictionary目录中不存在
	// // dictionary.db文件，则从res\raw目录中复制这个文件到
	// // SD卡的目录（/sdcard/dictionary）
	// if (!(new File(databaseFilename)).exists()) {
	// // 获得封装dictionary.db文件的InputStream对象
	// // InputStream is = MyApplication.getMainActivity().getResources()
	// // .openRawResource(R.raw.dictionary);
	// FileOutputStream fos = new FileOutputStream(databaseFilename);
	// byte[] buffer = new byte[8192];
	// int count = 0;
	// // 开始复制dictionary.db文件
	// while ((count = is.read(buffer)) > 0) {
	// fos.write(buffer, 0, count);
	// }
	//
	// fos.close();
	// is.close();
	// }
	// // 打开/sdcard/dictionary目录中的dictionary.db文件
	// SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
	// databaseFilename, null);
	// return database;
	// } catch (Exception e) {
	// }
	// return null;
	// }
}
