package com.aliamauri.meat.Manager;

import java.io.File;

import android.content.Context;

import com.aliamauri.meat.activity.IM.db.UserDao;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.activity.IM.utils.UserUtils;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.SDUtils;
import com.aliamauri.meat.utils.UIUtils;

/**
 * 用户信息管理
 * 
 * @author admin
 * 
 */
public class UserInfoManager {
	private HttpHelp mhHelp;
	private UserDao mUserDao;
	private String infoRootPath = SDUtils.getInternalMemoryPath()
			+ File.separator + "data" + File.separator
			+ UIUtils.getContext().getPackageName() + File.separator + "info"
			+ File.separator;
	private static UserInfoManager unu = new UserInfoManager();

	public static synchronized UserInfoManager getInstance() {
		return unu;
	}

	private Context mContext;

	private UserInfoManager() {
		mContext = UIUtils.getContext();
	}
	private UserDao getUserDao(){
		if(mUserDao==null)
		
		mUserDao = new UserDao(mContext);
		
		return mUserDao;
		
	}

//	/**
//	 * 更新头像到本地
//	 * 
//	 * @param user
//	 */
//	public void upAvatarNative(final User user) {
//
//		if (user == null || TextUtils.isEmpty(user.getAvatar())) {
//			return;
//		}
//		if (mhHelp == null) {
//			mhHelp = new HttpHelp();
//		}
//		final String targetPath = infoRootPath + "icon" + File.separator
//				+ user.getUsername() + ".png";
//		File file = new File(targetPath);
//		if (file.exists()) {
//			file.delete();
//		}
//		mhHelp.downLoad(user.getAvatar(), targetPath, new LoadRequestCallBack<File>() {
//
//			@Override
//			public void onSucceed(ResponseInfo<File> t) {
//				user.setNativeAvatar(targetPath);
//				getUserDao().saveContact(user);
//				
//			}
//
//			@Override
//			public void onFailure(HttpException arg0, String arg1) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void onLoading(long total, long current, boolean isUploading) {
//				// TODO Auto-generated method stub
//				
//			}
//		});
//	}

	/**
	 * 删除本地头像
	 * 
	 * @param uid
	 *            HX id
	 */
	public void deleteNativeAvatar(String uid) {
		final String targetPath = infoRootPath + "icon" + File.separator + uid
				+ ".png";
		File file = new File(targetPath);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 修改用户信息
	 */
	public void setUserInfo(User user) {
		if (user == null) {
			return;
		}
		UserUtils.saveUserInfo(user);
	}
}
