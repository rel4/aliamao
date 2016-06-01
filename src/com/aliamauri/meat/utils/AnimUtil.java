package com.aliamauri.meat.utils;

import android.graphics.drawable.AnimationDrawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class AnimUtil {
	private RotateAnimation animation;

	public static void loadImgStart(ImageView paramImageView) {
		((AnimationDrawable) paramImageView.getBackground()).start();
	}

	public static void loadImgStop(ImageView paramImageView) {
		((AnimationDrawable) paramImageView.getBackground()).stop();
	}

	public  void startRotateAnimation(View iView) {
		if (iView==null) {
			throw new RuntimeException("-------------> 动画空间不能为空");
		}
		animation = new RotateAnimation(0f, 360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		animation.setDuration(500);// 设置动画持续时间
		/** 常用方法 */
		animation.setRepeatCount(2000);// 设置重复次数
		animation.setFillAfter(false);// 动画执行完后是否停留在执行完的状态
		iView.startAnimation(animation);
	}
	public  void stopRotateAnimation(View iView) {
		if (iView==null) {
			throw new RuntimeException("-------------> 动画空间不能为空");
		}
	
		iView.clearAnimation();
	}
}