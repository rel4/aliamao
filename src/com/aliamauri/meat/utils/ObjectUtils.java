package com.aliamauri.meat.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.aliamauri.meat.service.UploadService.UplaodType;
import com.aliamauri.meat.service.UploadService.UploadFileBean;

public class ObjectUtils {
	/**
	 * 保存MAP的数据
	 * @param map 数据
	 */
	public static void saveCacheMap(Map<String, Map<UploadFileBean, UplaodType>> map,String spName)
	  {
	    try
	    {
	      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
	      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
	      localObjectOutputStream.writeObject(map);
	      String str = URLEncoder.encode(localByteArrayOutputStream.toString("ISO-8859-1"), "UTF-8");
	      localObjectOutputStream.close();
	      localByteArrayOutputStream.close();
//	      SharedPreferences.Editor localEditor = paramContext.getApplicationContext().getSharedPreferences("CacheList", 0).edit();
//	      localEditor.putString("List", str);
//	      localEditor.commit();
	      PrefUtils.setString(spName, str);
	      return;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	  }
	/**
	 * 保存List的数据
	 * @param map 数据
	 */
	public static void saveCacheList(List<String> list,String spName)
	  {
	    try
	    {
	      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
	      ObjectOutputStream localObjectOutputStream = new ObjectOutputStream(localByteArrayOutputStream);
	      localObjectOutputStream.writeObject(list);
	      String str = URLEncoder.encode(localByteArrayOutputStream.toString("ISO-8859-1"), "UTF-8");
	      localObjectOutputStream.close();
	      localByteArrayOutputStream.close();
//	      SharedPreferences.Editor localEditor = paramContext.getApplicationContext().getSharedPreferences("CacheList", 0).edit();
//	      localEditor.putString("List", str);
//	      localEditor.commit();
	      PrefUtils.setString(spName, str);
	      return;
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	    }
	  }
	
	/**
	 * 获取map对象的数据
	 * @param paramContext
	 * @param spName
	 * @return
	 */
	 public static Map<String, Map<UploadFileBean, UplaodType>> loadMap(Context paramContext,String spName)
	  {
	    Object localObject;
	    try
	    {
		  String str =PrefUtils.getString("CacheList", "none");
//	      String str = paramContext.getApplicationContext().getSharedPreferences("List", 0).getString("List", "none");
	      if (str.equals("none"))
	        return new HashMap<String, Map<UploadFileBean, UplaodType>>();
	      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(URLDecoder.decode(str, "UTF-8").getBytes("ISO-8859-1"));
	      ObjectInputStream localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
	      localObject = (Map<UploadFileBean, UplaodType>)localObjectInputStream.readObject();
	      localObjectInputStream.close();
	      localByteArrayInputStream.close();
	      if (localObject == null)
	      {
	        Map<String, Map<UploadFileBean, UplaodType>> localArrayList = new LinkedHashMap<String, Map<UploadFileBean, UplaodType>>();
	        return localArrayList;
	      }
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	      localObject = new LinkedHashMap<UploadFileBean, UplaodType>();
	    }
	    return (Map<String, Map<UploadFileBean, UplaodType>>)localObject;
	  }
	 /**
	  * 获取list 数据
	  * @param paramContext
	  * @param spName
	  * @return
	  */
	 public static List<String> loadList(Context paramContext,String spName)
	  {
	    Object localObject;
	    try
	    {
		  String str =PrefUtils.getString(spName, "none");
//	      String str = paramContext.getApplicationContext().getSharedPreferences("List", 0).getString("List", "none");
	      if (str.equals("none"))
	        return new ArrayList<String>();
	      ByteArrayInputStream localByteArrayInputStream = new ByteArrayInputStream(URLDecoder.decode(str, "UTF-8").getBytes("ISO-8859-1"));
	      ObjectInputStream localObjectInputStream = new ObjectInputStream(localByteArrayInputStream);
	      localObject = (List<String>)localObjectInputStream.readObject();
	      localObjectInputStream.close();
	      localByteArrayInputStream.close();
	      if (localObject == null)
	      {
	        return new ArrayList<String>();
	      }
	    }
	    catch (Exception localException)
	    {
	      localException.printStackTrace();
	      localObject = new LinkedHashMap<UploadFileBean, UplaodType>();
	    }
	    return (List<String>)localObject;
	  }
}
