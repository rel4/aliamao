package com.aliamauri.meat.view.TextViewSpanClickable;

import android.view.View;

/**
 *
 */
public interface TextBlankClickListener {
	 /**
     * 点击了整个条目
     * @param view
	 * @param model1 
     */
    void onBlankClick(View view, CommentModel model1);
   
    /**
     * 点击被回复者的姓名点击
     * @param view
     * @param model 
     */
    void onReplyClick(View view, CommentModel model);
    /**
     * 点击回复者的姓名点击事件
     * @param view
     * @param model 
     */
    void onReviewClick(View view, CommentModel model);
}
