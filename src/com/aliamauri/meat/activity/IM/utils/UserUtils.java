package com.aliamauri.meat.activity.IM.utils;

import java.io.File;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.IM.MySDKHelper;
import com.aliamauri.meat.activity.IM.controller.SDKHelper;
import com.aliamauri.meat.activity.IM.domain.User;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.utils.PrefUtils;
import com.squareup.picasso.Picasso;

public class UserUtils {
    /**
     * 根据username获取相应user，由于demo没有真实的用户数据，这里给的模拟的数据；
     * @param username
     * @return
     */
    public static User getUserInfo(String username){
        User user = ((MySDKHelper)SDKHelper.getInstance()).getContactList().get(username);
        if(user == null){
            user = new User(username);
            user.setNick("暂无");
        }
            
        if(user != null){
            //demo没有这些数据，临时填充
        	if(TextUtils.isEmpty(user.getNick()))
        		user.setNick(user.getUserId());
        }
        return user;
    }
    
    /**
     * 设置用户头像
     * @param username
     */
    public static void setUserAvatar(Context context, String username, ImageView imageView){
    	User user = getUserInfo(username);
    	String nativeAvatar = user.getNativeAvatar();
    	if (TextUtils.isEmpty(nativeAvatar)) {
    		 if(user != null && !TextUtils.isEmpty(user.getAvatar())){
    	            Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
    	        }else{
    	            Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
    	        }
//			UserInfoManager.getInstance().upAvatarNative(user);
		}else {
			Uri uri = Uri.fromFile(new File(nativeAvatar));
			 Picasso.with(context).load(uri).placeholder(R.drawable.default_avatar).into(imageView);
		}
    	
       
    }
    
    /**
     * 设置当前用户头像
     */
	public static void setCurrentUserAvatar(Context context, ImageView imageView) {
		
		User user = ((MySDKHelper)SDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
		if (user != null && user.getAvatar() != null) {
//			Picasso.with(context).load(user.getAvatar()).placeholder(R.drawable.default_avatar).into(imageView);
			new HttpHelp().showImage(imageView, PrefUtils.getString(GlobalConstant.USER_FACE, "")+"##");
		} else {
			Picasso.with(context).load(R.drawable.default_avatar).into(imageView);
		}
	}
    
    /**
     * 设置用户昵称
     */
    public static void setUserNick(String username,TextView textView){
    	User user = getUserInfo(username);
    	if(user != null){
    		if (TextUtils.isEmpty(user.getNick())) {
				textView.setText(user.getUserId());
			}else {
				textView.setText(user.getNick());
			}
    	}
    }
    
    /**
     * 设置当前用户昵称
     */
    public static void setCurrentUserNick(TextView textView){
    	User user = ((MySDKHelper)SDKHelper.getInstance()).getUserProfileManager().getCurrentUserInfo();
    	if(textView != null){
    		textView.setText(user.getNick());
    	}
    }
    
    /**
     * 保存或更新某个用户
     * @param user
     */
	public static void saveUserInfo(User newUser) {
		if (newUser == null || newUser.getUsername() == null) {
			return;
		}
		((MySDKHelper) SDKHelper.getInstance()).saveContact(newUser);
	}
    
}
