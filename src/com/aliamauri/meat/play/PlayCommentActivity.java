package com.aliamauri.meat.play;

import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.BaseActivity;
import com.aliamauri.meat.adapter.UserCommentAdapter;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.Comlist;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.KeyBoardUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.StringUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lidroid.xutils.http.RequestParams;

public class PlayCommentActivity extends BaseActivity implements OnRefreshListener<ListView>, OnClickListener {
	public static final String update_num_tag = "update_num_tag";
	/** 初次进入界面标志 */
	private final String A_Tag = "1";
	/** 加载更多2级评论标志 */
	private final String B_Tag = "2";
	private String current_tag;
	private TextView mTv_comment_user_name;
	private TextView mTv_comment_user_time;
	private CircleImageView mCiv_comment_user_icon;
	private TextView mTv_comment_user_content;
	private PullToRefreshListView mLv_content;
	private HttpHelp mHttphelp;
	private String mId;
	private int mPage = 1;
	/** 当前视频的id */
	private String mVideoID;

	@Override
	protected View getRootView() {
		mHttphelp = new HttpHelp();
		current_tag = A_Tag;
		return View.inflate(mActivity, R.layout.comment_item, null);
	}
	private List<Comlist> mCommentList;  
	private UserCommentAdapter mContentAdapter;
	private EditText mEtComment;
	@Override
	protected void initNet() {
		mHttphelp.sendGet(NetworkConfig.getCommentURL(mId,current_tag,mPage), VideoCommentBean.class, new MyRequestCallBack<VideoCommentBean>() {
			

			@Override
			public void onSucceed(VideoCommentBean bean) {
				if(bean == null || bean.cont == null || bean.status == null){
					UIUtils.showToast(mActivity, "网络异常");
					mLv_content.onRefreshComplete();
					return ;
				}
				if("1".equals(bean.status)){
				
					if(A_Tag.equals(current_tag)){
						mCommentList = bean.cont.commentList;
						mTv_comment_user_name.setText(bean.cont.mainComment.nickname);
						mTv_comment_user_content.setText(bean.cont.mainComment.msg);
						mTv_comment_user_time.setText(bean.cont.mainComment.time);
						mHttphelp.showImage(mCiv_comment_user_icon, bean.cont.mainComment.face);
						mContentAdapter = new UserCommentAdapter(mActivity,bean.cont.commentList, R.layout.item_comment_layout);
						mLv_content.setAdapter(mContentAdapter);
						mPage++;
					}else{
						if(bean.cont.commentList!=null && bean.cont.commentList.size()>0){
							mPage++;
							mCommentList.addAll(bean.cont.commentList);
							mContentAdapter.notifyDataSetChanged();
						}else{
							UIUtils.showToast(mActivity, "没有内容了");
						}
					}
				}else{
					UIUtils.showToast(mActivity, "网络异常");
					return ;
				}
				mLv_content.onRefreshComplete();
			}
		});
	}

	@Override
	protected void initView() {
		mVideoID = baseIntent.getStringExtra(GlobalConstant.PLAY_VIDEO_ID);
		mId = baseIntent.getStringExtra(GlobalConstant.COMMENTDATAURL);
		mCiv_comment_user_icon = $(R.id.civ_comment_user_icon);
		mEtComment = $(R.id.etComment);
		KeyBoardUtils.closeKeybord(mEtComment,UIUtils.getContext());
		mTv_comment_user_name = $(R.id.tv_comment_user_name);
		mTv_comment_user_time = $(R.id.tv_comment_user_time);
		$(R.id.btnSendComment).setOnClickListener(this);
		mTv_comment_user_content = $(R.id.tv_comment_user_content);
		mLv_content = $(R.id.ptrv_comment_content);
		mLv_content.setMode(Mode.PULL_FROM_END);
		mLv_content.setOnRefreshListener(this);
	}
	@Override
	public void onRefresh(PullToRefreshBase<ListView> refreshView) {
		current_tag = B_Tag;
		initNet();
	}
	@Override
	protected int setActivityAnimaMode() {
		return 4;
	}

	/**
	 * 发布评论
	 */
	private void publishCommend() {
		final String Comment_text = mEtComment.getText().toString().trim();
		if (!StringUtils.isEmpty(Comment_text)) {
			 RequestParams params = new RequestParams();
			 params.addBodyParameter("pid", mId);
			 params.addBodyParameter("msg", Comment_text);
			 params.addBodyParameter("ckuid", mVideoID);
			mHttphelp.sendPost(NetworkConfig.commentTextUrl(), params, BaseBaen.class, new MyRequestCallBack<BaseBaen>() {

				@Override
				public void onSucceed(BaseBaen bean) {
					if(bean == null || bean.status == null || bean.msg == null){
						UIUtils.showToast(mActivity, "网络异常");
						return ;
					}
					if("1".equals(bean.status)){
						KeyBoardUtils.closeKeybord(mEtComment,UIUtils.getContext());
						mEtComment.setText("");
						UIUtils.showToast(mActivity, bean.msg);
						Comlist list = new Comlist();
						list.face = PrefUtils.getString(GlobalConstant.USER_FACE,GlobalConstant.HEAD_ICON_PATH);
						list.time = "刚刚";
						list.msg = Comment_text;
						list.nickname = PrefUtils.getString(GlobalConstant.USER_NICKNAME,"");
						mCommentList.add(0, list);
					}else{
						UIUtils.showToast(mActivity, bean.msg);
						
					}
					
				}
			});
		}

	}

	@Override
	protected void onDestroy() {
		if(mHttphelp != null){
			mHttphelp.stopHttpNET();
		}
		KeyBoardUtils.closeKeybord(mEtComment, getApplicationContext());
//		EventBus.getDefault().post(new PlayVideo_event(update_num_tag, null));
		super.onDestroy();
	}
	@Override
	protected String getCurrentTitle() {
		return "详情";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnSendComment:
			publishCommend();
			break;

		default:
			break;
		}
		
	}

}
