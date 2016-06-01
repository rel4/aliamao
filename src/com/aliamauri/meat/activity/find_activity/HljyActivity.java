package com.aliamauri.meat.activity.find_activity;

import java.util.ArrayList;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.OtherDataActivity;
import com.aliamauri.meat.bean.hljy.RmggBean.Cont.Xingqu;
import com.aliamauri.meat.bean.hljy.XqxtBean;
import com.aliamauri.meat.fragment.impl_hljy.HotAffichePage;
import com.aliamauri.meat.fragment.impl_hljy.XieHouTAPage;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.umeng.analytics.MobclickAgent;

/**
 * 发现----婚恋交友界面
 *  
 * @author limaokeji-windosc
 * 
 */
public class HljyActivity extends FragmentActivity implements OnClickListener {
	private final int HOTAFFICHE_PAGE = 0; // 热门公告 tag
	private final int XHTA_PAGE = 1; // 邂逅TA tag

	private FrameLayout mFl_find_hljy_content; // 显示内容区域
	private TextView mTv_find_hljy_rmgg_title; // 热门公告标题
	private View mV_find_hljy_rmgg_line; // 热门公告线
	private TextView mTv_find_hljy_xhta_title; // 邂逅ta标题
	private View mV_find_hljy_xhta_line; // 邂逅Ta线
	private RelativeLayout mRl_find_hljy_rmgg; // 热门公告点击区域
	private RelativeLayout mRl_find_hljy_xhta; // 邂逅他点击区域
	private FragmentManager mFm; // 获取fragment管理器

	public RecyclerView mRv_base_hljy_usericon; // 推荐好友的gv布局
	public ImageView mIv_btn_base_hljy_swap; // 换一批推荐好友布局
	private TextView mBtn_base_hljy_spyh; // btn_速配约会
	private TextView mBtn_base_hljy_yjzq; // btn_一见钟情
	private HttpHelp mHttpHelp;
	public ArrayList<Xingqu> mXingqus;
	private int mLoadingPage;
	private MyAdapter myAdapter;// 设置兴趣相同ta的适配器
	private HotAffichePage hap; // 热门公告 fragment
	private XieHouTAPage xhtp; // 邂逅Ta fragment
	private String mUser_Id;

	
	@Override
	protected void onResume() {
		super.onResume();
		  MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		 MobclickAgent.onPause(this);
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_hljy);

		mUser_Id = PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_ID, "");

		mHttpHelp = new HttpHelp();
		mLoadingPage = 1;
		initView();
		getNetDate(true);
		mFm = getSupportFragmentManager();
		setPageSelection(HOTAFFICHE_PAGE);// 设置默认选中页面
	}

	private void initView() {
		((TextView) $(R.id.tv_title_title)).setText("婚恋交友");
		$(R.id.ll_title_talk).setVisibility(View.GONE);
		$(R.id.tv_title_right).setVisibility(View.GONE);
		mFl_find_hljy_content = $(R.id.fl_find_hljy_content);
		$(R.id.iv_title_backicon).setOnClickListener(this);
		mRl_find_hljy_rmgg = $(R.id.rl_find_hljy_rmgg);
		mRl_find_hljy_xhta = $(R.id.rl_find_hljy_xhta);
		mRl_find_hljy_rmgg.setOnClickListener(this);
		mRl_find_hljy_xhta.setOnClickListener(this);
		mTv_find_hljy_rmgg_title = $(R.id.tv_find_hljy_rmgg_title);
		mV_find_hljy_rmgg_line = $(R.id.v_find_hljy_rmgg_line);
		mTv_find_hljy_xhta_title = $(R.id.tv_find_hljy_xhta_title);
		mV_find_hljy_xhta_line = $(R.id.v_find_hljy_xhta_line);

		mRv_base_hljy_usericon = $(R.id.rv_base_hljy_usericon);
		mRv_base_hljy_usericon.setHasFixedSize(true);
		mRv_base_hljy_usericon.addItemDecoration(new SpaceItemDecoration(
				getResources().getDimensionPixelSize(R.dimen.x30)));
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRv_base_hljy_usericon.setLayoutManager(mLayoutManager);

		mIv_btn_base_hljy_swap = $(R.id.iv_btn_base_hljy_swap);
		mBtn_base_hljy_spyh = $(R.id.btn_base_hljy_spyh);
		mBtn_base_hljy_yjzq = $(R.id.btn_base_hljy_yjzq);
		mIv_btn_base_hljy_swap.setOnClickListener(this);
		mBtn_base_hljy_spyh.setOnClickListener(this);
		mBtn_base_hljy_yjzq.setOnClickListener(this);

	}

	/**
	 * 设置RecyclerView的间距
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

		private int space;

		public SpaceItemDecoration(int space) {
			this.space = space;
		}

		@Override
		public void getItemOffsets(Rect outRect, View view,
				RecyclerView parent, RecyclerView.State state) {

			outRect.right = space;
		}
	}

	/**
	 * 获取点击后的页面改变
	 * 
	 * @param index
	 */
	private void setPageSelection(int index) {
		resetBtn();
		FragmentTransaction transaction = mFm.beginTransaction();
		switch (index) {
		case HOTAFFICHE_PAGE:
			setCurrentState(HOTAFFICHE_PAGE);
			if (hap == null) {
				hap = new HotAffichePage();
			}
			transaction.replace(R.id.fl_find_hljy_content, hap);
			break;
		case XHTA_PAGE:
			setCurrentState(XHTA_PAGE);

			if (xhtp == null) {
				xhtp = new XieHouTAPage();
			}
			transaction.replace(R.id.fl_find_hljy_content, xhtp);
			break;

		}

		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_title_backicon: // 返回按钮
			finish();
			break;
		case R.id.rl_find_hljy_rmgg: // 热门公告点击区域
			setPageSelection(HOTAFFICHE_PAGE);
			break;
		case R.id.rl_find_hljy_xhta: // 邂逅Ta点击区域
			setPageSelection(XHTA_PAGE);
			break;
		case R.id.iv_btn_base_hljy_swap:
			getNetDate(false);
			break;
		case R.id.btn_base_hljy_spyh:
			startActivity(new Intent(this, SpyhActivity.class));
			break;
		case R.id.btn_base_hljy_yjzq:
			startActivity(new Intent(this, YjzqActivity.class));
			break;
		default:
			break;
		}
	}

	/**
	 * 点击按钮获取网络数据
	 * 
	 * @param b
	 */
	private void getNetDate(final boolean b) {
		mIv_btn_base_hljy_swap.setClickable(false);

		mHttpHelp.sendGet(NetworkConfig.get_xqxt_Url(mLoadingPage),
				XqxtBean.class, new MyRequestCallBack<XqxtBean>() {

					@Override
					public void onSucceed(XqxtBean bean) {
						if (bean == null || bean.status == null
								|| bean.cont == null) {
							UIUtils.showToast(HljyActivity.this,
									"没有找到数据~~");
							return;
						}

						switch (bean.status) {
						case "1":
							mIv_btn_base_hljy_swap.setClickable(true);
							if (bean.cont.size() < 5) {
								UIUtils.showToast(HljyActivity.this,
										"没有数据了~~");
								return;
							}

							if (b) {
								mXingqus = bean.cont;
								myAdapter = new MyAdapter();
								mRv_base_hljy_usericon.setAdapter(myAdapter);
							} else {
								mXingqus.addAll(bean.cont);
								myAdapter.notifyDataSetChanged();
								mRv_base_hljy_usericon
										.smoothScrollToPosition(mXingqus.size());
							}
							mLoadingPage++;
							break;
						case "2":
							UIUtils.showToast(HljyActivity.this,
									"你还没有登陆~~~");
							break;

						default:
							break;
						}
						mIv_btn_base_hljy_swap.setClickable(true);

					}
				});

	}

	public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
		// 创建新View，被LayoutManager所调用
		@Override
		public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
			View view = LayoutInflater.from(viewGroup.getContext()).inflate(
					R.layout.base_friend_hljy, viewGroup, false);
			ViewHolder vh = new ViewHolder(view);
			return vh;
		}

		// 将数据与界面进行绑定的操作
		@Override
		public void onBindViewHolder(ViewHolder viewHolder, int position) {
			mHttpHelp.showImage(viewHolder.civ_user_icon,
					mXingqus.get(position).face + "##");
			viewHolder.civ_user_icon
					.setOnClickListener(new MyOnClick(position));
		}

		// 获取数据的数量
		@Override
		public int getItemCount() {
			return mXingqus.size();
		}

		// 自定义的ViewHolder，持有每个Item的的所有界面元素
		public class ViewHolder extends RecyclerView.ViewHolder {
			public CircleImageView civ_user_icon;

			public ViewHolder(View view) {
				super(view);
				civ_user_icon = $(view, R.id.civ_friend_hljy);
			}
		}

	}

	/**
	 * 设置兴趣相同的ta点击事件
	 * 
	 * @author limaokeji-windosc
	 * 
	 */
	class MyOnClick implements OnClickListener {

		private int position;

		public MyOnClick(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.civ_friend_hljy:
				getFrinedPage(position);
				break;
			default:
				break;
			}

		}
	}

	/**
	 * 
	 * 点击好友头像进入好友界面
	 * 
	 * @param position
	 */
	public void getFrinedPage(int position) {
		if (!(mUser_Id.equals(mXingqus.get(position).id))) { // 当前是朋友发布的动态
			Intent intent = new Intent(this, OtherDataActivity.class);
			intent.putExtra(GlobalConstant.COMMENT_ADD_FRIEND,
					mXingqus.get(position).id);
			startActivity(intent);
		}
	}

	/**
	 * 设置当前图标的状态
	 * 
	 * @param nearbyPage
	 */
	private void setCurrentState(int num) {// 点击标签的时候改变图标和文字的颜色,状态
		switch (num) {
		case GlobalConstant.NEARBY_PAGE:

			mTv_find_hljy_rmgg_title.setTextColor(getResources().getColor(
					R.color.main_color));
			mV_find_hljy_rmgg_line.setVisibility(View.VISIBLE);
			mRl_find_hljy_rmgg.setEnabled(false);
			mRl_find_hljy_xhta.setEnabled(true);
			break;
		case GlobalConstant.ADDR_PAGE:
			mTv_find_hljy_xhta_title.setTextColor(getResources().getColor(
					R.color.main_color));
			mV_find_hljy_xhta_line.setVisibility(View.VISIBLE);
			mRl_find_hljy_rmgg.setEnabled(true);
			mRl_find_hljy_xhta.setEnabled(false);
			break;

		default:
			break;
		}

	}

	/**
	 * 选择界面的时候重置所有的按钮，图片，文字的颜色
	 */
	private void resetBtn() {

		mTv_find_hljy_rmgg_title.setTextColor(getResources().getColor(
				R.color.word_black));
		mV_find_hljy_rmgg_line.setVisibility(View.GONE);
		mTv_find_hljy_xhta_title.setTextColor(getResources().getColor(
				R.color.word_black));
		mV_find_hljy_xhta_line.setVisibility(View.GONE);

	}

	/**
	 * 查找控件id
	 * 
	 * @param rootView
	 * @param id
	 * @return
	 */
	public <T extends View> T $(View rootView, int id) {
		return (T) rootView.findViewById(id);
	}

	/**
	 * 根据id查找控件
	 * 
	 * @param id
	 * @return
	 */
	public <T extends View> T $(int id) {
		return (T) findViewById(id);
	}
}
