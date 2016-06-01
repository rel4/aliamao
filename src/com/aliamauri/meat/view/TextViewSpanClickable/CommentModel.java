package com.aliamauri.meat.view.TextViewSpanClickable;

import java.io.Serializable;

/**
 * 评论列表
 */
public class CommentModel implements Serializable {

    private static final long serialVersionUID = -3063305610315810693L;
    //评论用户ID
    private String reviewUid;
    //评论用户名字
    private String reviewUName;
    //被回复用户ID 如果是0  就不是回复
    private String replyUid;
    //被回复用户名字
    private String replyUName;
    //评论内容
    private String reviewContent;

    public String getReviewUid() {
        return reviewUid;
    }

    public void setReviewUid(String reviewUid) {
        this.reviewUid = reviewUid;
    }

    public String getReviewUName() {
        return reviewUName;
    }

    public void setReviewUName(String reviewUName) {
        this.reviewUName = reviewUName;
    }

    public String getReplyUid() {
        return replyUid;
    }

    public void setReplyUid(String replyUid) {
        this.replyUid = replyUid;
    }

    public String getReplyUName() {
        return replyUName;
    }

    public void setReplyUName(String replyUName) {
        this.replyUName = replyUName;
    }

    public String getReviewContent() {
        return reviewContent;
    }

    public void setReviewContent(String reviewContent) {
        this.reviewContent = reviewContent;
    }
}
