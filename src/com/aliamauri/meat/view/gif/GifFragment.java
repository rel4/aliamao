package com.aliamauri.meat.view.gif;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.activity.MainActivity;
import com.aliamauri.meat.activity.TakeVideoActivity;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.play.PlayActivity;
import com.aliamauri.meat.top.ChannelActivity;

public class GifFragment extends Fragment {
	// private GifView gifView;
	// private GifView gifView2;

	private int gifId;
	private int gifId2;
	private View view;
	private int type = 0;

	private MainActivity mainActivity;
	private ChannelActivity channelActivity;
	private PlayActivity playActivity;

	private String text;
	private TextView tv_gif_channel_text;

	private RelativeLayout iv_end_left;
	private RelativeLayout iv_end_right;
	private ImageView iv_hand_left;
	private ImageView iv_hand_right;
	private int[] start_left;
	private int[] end_left;
	private int[] start_right;
	private int[] end_right;

	private TranslateAnimation animation_left;
	private TranslateAnimation animation_right;
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 1:
				if (start_left == null) {
					start_left = new int[2];
				}
				if (end_left == null) {
					end_left = new int[2];
				}
				if (iv_hand_left != null) {
					iv_hand_left.getLocationInWindow(start_left);
				}
				if (iv_end_left != null) {
					iv_end_left.getLocationInWindow(end_left);
				}
				startAnim_left(start_left, end_left, iv_hand_left, iv_end_left);
				break;
			case 2:
				if (start_right == null) {
					start_right = new int[2];
				}
				if (end_right == null) {
					end_right = new int[2];
				}
				if (iv_hand_right != null) {
					iv_hand_right.getLocationInWindow(start_right);
				}
				if (iv_end_right != null) {
					iv_end_right.getLocationInWindow(end_right);
				}
				startAnim_right(start_right, end_right, iv_hand_right,
						iv_end_right);
				break;
			default:
				break;
			}
		};
	};

	private void startAnim_left(final int[] startLocation,
			final int[] endLocation, final ImageView moveView,
			final RelativeLayout iv_end) {
		if (animation_left == null) {
			int minuend = 0;
			if (iv_end != null) {
				minuend = (iv_end.getBottom() - iv_end.getTop()) / 2;
			}
			animation_left = new TranslateAnimation(0, endLocation[0]
					- startLocation[0] - minuend, 0, endLocation[1]
					- startLocation[1] + minuend);
			animation_left.setDuration(1000);
//			animation_left.set
			moveView.startAnimation(animation_left);
			animation_left.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					startAnim_left(startLocation, endLocation, moveView, iv_end);
					// handler.sendEmptyMessage(1);
				}
			});
			animation_left.start();
		} else {
			moveView.startAnimation(animation_left);
		}
	}

	private void startAnim_right(final int[] startLocation,
			final int[] endLocation, final ImageView moveView,
			final RelativeLayout iv_end) {
		if (animation_right == null) {
			int minuend = 0;
			if (iv_end != null) {
				minuend = (iv_end.getBottom() - iv_end.getTop()) / 2;
			}
			animation_right = new TranslateAnimation(0, endLocation[0]
					- startLocation[0] - minuend, 0, endLocation[1]
					- startLocation[1] + minuend);
			animation_right.setDuration(1000);
			moveView.startAnimation(animation_right);
			animation_right.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation arg0) {
				}

				@Override
				public void onAnimationRepeat(Animation arg0) {
				}

				@Override
				public void onAnimationEnd(Animation arg0) {
					startAnim_right(startLocation, endLocation, moveView,
							iv_end);
					// handler.sendEmptyMessage(1);
				}
			});
			animation_right.start();
		} else {
			moveView.startAnimation(animation_right);
		}
	}

	public GifFragment(PlayActivity playActivity, int type, int gifId,
			int gifId2) {
		this.type = type;
		this.playActivity = playActivity;
		this.gifId = gifId;
		this.gifId2 = gifId2;
	}

	public GifFragment(MainActivity mainActivity, int type, int gifId) {
		this.gifId = gifId;
		this.type = type;
		this.mainActivity = mainActivity;
	}

	public GifFragment(ChannelActivity channelActivity, int type, int gifId,
			String text) {
		this.gifId = gifId;
		this.type = type;
		this.channelActivity = channelActivity;
		this.text = text;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		switch (type) {
		//
		case GlobalConstant.ZERO:
			view = inflater.inflate(R.layout.gif_fragment_gotochannel, null);
			break;
		case GlobalConstant.ONE:
			view = inflater.inflate(R.layout.gif_fragment_channel_del, null);
			tv_gif_channel_text = (TextView) view
					.findViewById(R.id.tv_gif_channel_text);
			break;
		case GlobalConstant.TWO:
			view = inflater.inflate(R.layout.gif_fragment_channel_add, null);
			tv_gif_channel_text = (TextView) view
					.findViewById(R.id.tv_gif_channel_text);
			break;
		case GlobalConstant.THREE:
			view = inflater.inflate(R.layout.gif_fragment_gototakevideo, null);
			break;
		case GlobalConstant.FOUR:
			view = inflater.inflate(R.layout.gif_fragment_play, null);
			break;
		default:
			view = inflater.inflate(R.layout.gif_fragment_gototakevideo, null);
			break;
		}
		if (type == 4) {
			iv_hand_right = (ImageView) view.findViewById(R.id.iv_hand_right);
			iv_end_right = (RelativeLayout) view
					.findViewById(R.id.iv_end_right);
			iv_hand_left = (ImageView) view.findViewById(R.id.iv_hand_left);
			iv_end_left = (RelativeLayout) view.findViewById(R.id.iv_end_left);
			handler.sendEmptyMessageDelayed(1,150);
			handler.sendEmptyMessageDelayed(2, 120);
		} else {
			iv_hand_left = (ImageView) view.findViewById(R.id.iv_hand_left);
			iv_end_left = (RelativeLayout) view.findViewById(R.id.iv_end_left);
			handler.sendEmptyMessageDelayed(1, 150);
		}

		if (tv_gif_channel_text != null && text != null) {
			tv_gif_channel_text.setText(text);
		}
		view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideTheAssert();
				switch (type) {
				case GlobalConstant.ZERO:
					if (mainActivity != null) {
						mainActivity.startActivity(new Intent(mainActivity,
								ChannelActivity.class));
					}
					break;
				case GlobalConstant.ONE:
					if (channelActivity != null) {
						channelActivity.del(1);
					}
					break;
				case GlobalConstant.TWO:
					// if (channelActivity != null) {
					// channelActivity.add(2);
					// }
					break;
				case GlobalConstant.THREE:
					if (mainActivity != null) {
						mainActivity.startActivity(new Intent(mainActivity,
								TakeVideoActivity.class));
					}
					break;
				case GlobalConstant.FOUR:
					break;
				default:
					break;
				}
			}
		});
		return view;
	}

	/**
	 * 
	 * 
	 */
	private void hideTheAssert() {
		getFragmentManager().beginTransaction().remove(this).commit();
	}
}