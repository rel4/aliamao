package com.aliamauri.meat.activity;

import java.io.File;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aliamauri.meat.R;
import com.aliamauri.meat.bean.BaseBaen;
import com.aliamauri.meat.bean.ListContBean;
import com.aliamauri.meat.eventBus.EventForNotice;
import com.aliamauri.meat.eventBus.UpdateShowMsg;
import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.network.config.NetworkConfig;
import com.aliamauri.meat.network.httphelp.HttpHelp;
import com.aliamauri.meat.network.httphelp.HttpInterface.MyRequestCallBack;
import com.aliamauri.meat.utils.CaculationUtils;
import com.aliamauri.meat.utils.ChangeUtils;
import com.aliamauri.meat.utils.CheckUtils;
import com.aliamauri.meat.utils.IconCompress;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;
import com.aliamauri.meat.view.CircleImageView;
import com.aliamauri.meat.view.ShowAlertDialog;
import com.lidroid.xutils.http.RequestParams;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

/**
 * 修改资料
 * 
 * @author ych
 * 
 */
public class EditDataActivity extends BaseActivity implements OnClickListener {
	private HttpHelp httpHelp;
	private TextView tv_title_right;
	private ImageView iv_title_backicon;
	private String EDITDATA = "editdata";

	private RelativeLayout rl_editdata_headicon;
	private TextView tv_editdata_url;
	private final int HEADICON = 1;

	private RelativeLayout rl_editdata_nickname;
	private TextView tv_editdata_nickname;
	private final int NICKNAME = 2;

	private RelativeLayout rl_editdata_age;
	private TextView tv_editdata_age;
	private String birth = "";
	private final int AGE = 3;

	private RelativeLayout rl_editdata_gender;
	private TextView tv_editdata_gender;
	private final int GENDER = 4;

	private RelativeLayout rl_editdata_sign;
	private TextView tv_editdata_sign;
	private final int SIGN = 5;

	private RelativeLayout rl_editdata_pland;
	private TextView tv_editdata_pland;
	private final int PLAND = 6;

	private RelativeLayout rl_editdata_job;
	private TextView tv_editdata_job;
	private final int JOB = 7;

	private RelativeLayout rl_editdata_hobby;
	private TextView tv_editdata_hobby;
	private final int HOBBY = 8;

	// 资料
	private CircleImageView ci_editdata_headicon;
	private boolean change = false;// 判断是否有改变资料的
	private boolean Iconboo = false;// 判断是否修改了头像
	private boolean nameChange = false; // 判断是否修改名字
	private boolean booSubmit = false;// 防止多点击，多提交、、

	@Override
	protected View getRootView() {
		EventBus.getDefault().register(this);// 注册EVENTBUS
		View view = View.inflate(EditDataActivity.this, R.layout.edit_data,
				null);
		httpHelp = new HttpHelp();
		return view;
	}

	public void onEventMainThread(EventForNotice event) {
		initData();
	}

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
	protected String getCurrentTitle() {
		// TODO Auto-generated method stub
		return "编辑资料";
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (!Iconboo)
			if (CheckUtils.getInstance().isIcon(
					GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
				File f = new File(GlobalConstant.HEAD_ICON_PATH_BACKUP);
				f.delete();
			}
	}

	@Override
	protected void initView() {

		tv_title_right = (TextView) findViewById(R.id.tv_title_right);
		tv_title_right.setVisibility(View.VISIBLE);
		tv_title_right.setText("保存");

		ci_editdata_headicon = (CircleImageView) findViewById(R.id.ci_editdata_headicon);
		tv_editdata_url = (TextView) findViewById(R.id.tv_editdata_url);

		rl_editdata_nickname = (RelativeLayout) findViewById(R.id.rl_editdata_nickname);
		tv_editdata_nickname = (TextView) findViewById(R.id.tv_editdata_nickname);

		rl_editdata_age = (RelativeLayout) findViewById(R.id.rl_editdata_age);
		tv_editdata_age = (TextView) findViewById(R.id.tv_editdata_age);

		rl_editdata_sign = (RelativeLayout) findViewById(R.id.rl_editdata_sign);
		tv_editdata_sign = (TextView) findViewById(R.id.tv_editdata_sign);

		rl_editdata_pland = (RelativeLayout) findViewById(R.id.rl_editdata_pland);
		tv_editdata_pland = (TextView) findViewById(R.id.tv_editdata_pland);

		rl_editdata_job = (RelativeLayout) findViewById(R.id.rl_editdata_job);
		tv_editdata_job = (TextView) findViewById(R.id.tv_editdata_job);

		rl_editdata_headicon = (RelativeLayout) findViewById(R.id.rl_editdata_headicon);
		rl_editdata_gender = (RelativeLayout) findViewById(R.id.rl_editdata_gender);
		tv_editdata_gender = (TextView) findViewById(R.id.tv_editdata_gender);

		rl_editdata_hobby = (RelativeLayout) findViewById(R.id.rl_editdata_hobby);
		tv_editdata_hobby = (TextView) findViewById(R.id.tv_editdata_hobby);

		iv_title_backicon = (ImageView) findViewById(R.id.iv_title_backicon);
		initData();
	}

	@Override
	protected void setListener() {
		tv_title_right.setOnClickListener(this);
		iv_title_backicon.setOnClickListener(this);
		rl_editdata_nickname.setOnClickListener(this);
		rl_editdata_age.setOnClickListener(this);
		rl_editdata_sign.setOnClickListener(this);
		rl_editdata_job.setOnClickListener(this);
		rl_editdata_headicon.setOnClickListener(this);
		rl_editdata_gender.setOnClickListener(this);
		rl_editdata_hobby.setOnClickListener(this);
		rl_editdata_pland.setOnClickListener(this);
	}

	private void initData() {
		httpHelp.showImage(ci_editdata_headicon, GlobalConstant.HEAD_ICON_PATH
				+ "##");
		tv_editdata_nickname.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_NICKNAME, ""));
		tv_editdata_gender.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_SEX, ""));
		tv_editdata_age
				.setText(CaculationUtils.calculateDatePoor(PrefUtils.getString(
						UIUtils.getContext(), GlobalConstant.USER_BIRTH, "")));
		tv_editdata_sign.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_SIGNATURE, ""));
		tv_editdata_pland.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_PLAND, ""));
		tv_editdata_job.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_JOB, ""));
		tv_editdata_hobby.setText(PrefUtils.getString(UIUtils.getContext(),
				GlobalConstant.USER_HOBBY, ""));
	}

	private void changeBitmap() {
		if (CheckUtils.getInstance().isIcon(
				GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
			Bitmap bm = BitmapFactory
					.decodeFile(GlobalConstant.HEAD_ICON_PATH_BACKUP);
			IconCompress.saveBitmap(bm, GlobalConstant.HEAD_ICON_SAVEPATH,
					"instantmessage.jpg");
			faceFile.delete();
		}
	}

	@Override
	protected void onDestroy() {
		if (CheckUtils.getInstance().isIcon(
				GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
			File f = new File(GlobalConstant.HEAD_ICON_PATH_BACKUP);
			f.delete();
		}
		EventBus.getDefault().unregister(this);// 反注册EventBus
		super.onDestroy();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (KeyEvent.KEYCODE_BACK == keyCode) {
			if (change || Iconboo) {
				ShowAlertDialog s = new ShowAlertDialog(this, "资料未保存，是否需要退出",
						"editdata");
			} else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void saveUserInfo() {
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_NICKNAME,
				tv_editdata_nickname.getText().toString().trim());
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_SEX,
				tv_editdata_gender.getText().toString().trim());
		if ("".equals(birth)) {

		} else {
			PrefUtils.setString(UIUtils.getContext(),
					GlobalConstant.USER_BIRTH, birth);
		}
		PrefUtils.setString(UIUtils.getContext(),
				GlobalConstant.USER_SIGNATURE, tv_editdata_sign.getText()
						.toString().trim());
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_JOB,
				tv_editdata_job.getText().toString().trim());
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_HOBBY,
				tv_editdata_hobby.getText().toString().trim());
		PrefUtils.setString(UIUtils.getContext(), GlobalConstant.USER_PLAND,
				tv_editdata_pland.getText().toString().trim());
	}

	private void UpdataUserInfo() {
		String url = NetworkConfig.getUpdateUserInfo(
				"",
				tv_editdata_nickname.getText().toString().trim(),
				"".equals(birth) ? PrefUtils.getString(
						GlobalConstant.USER_BIRTH, "") : birth, ChangeUtils
						.ChangeSexToNumber(tv_editdata_gender.getText()
								.toString().trim()), tv_editdata_sign.getText()
						.toString().trim(), tv_editdata_pland.getText()
						.toString().trim(), tv_editdata_job.getText()
						.toString().trim(), tv_editdata_hobby.getText()
						.toString().trim());
		httpHelp.sendGet(url, BaseBaen.class,
				new MyRequestCallBack<BaseBaen>() {
					@Override
					public void onSucceed(BaseBaen bean) {
						booSubmit = false;
						if (bean == null) {
							return;
						}
						UIUtils.showToast(UIUtils.getContext(), bean.msg);
						if ("1".equals(bean.status)) {
							change = false;
							saveUserInfo();
							// Intent intent = new Intent();
							// intent.setAction("updateUserInfo");
							// EditDataActivity.this.sendBroadcast(intent);
							if (Iconboo) {
								uploadPic();
							}
						}
					}
				});
	}

	private File faceFile;

	private void uploadPic() {
		RequestParams params = new RequestParams();
		if (CheckUtils.getInstance().isIcon(
				GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
			faceFile = new File(GlobalConstant.HEAD_ICON_PATH_BACKUP);
			params.addBodyParameter("uploadedfile", faceFile);
			httpHelp.sendPost(NetworkConfig.setHeadIcon(), params,
					ListContBean.class, new MyRequestCallBack<ListContBean>() {
						@Override
						public void onSucceed(ListContBean bean) {
							booSubmit = false;
							if (change) {
								UpdataUserInfo();
							}
							if (bean == null) {
								return;
							}
							UIUtils.showToast(UIUtils.getContext(), "头像上传成功");
							if ("1".equals(bean.status)) {
								Iconboo = false;
								changeBitmap();
								// Intent intent = new Intent();
								// intent.setAction("updateUserInfo");
								// EditDataActivity.this.sendBroadcast(intent);
							}
						}

					});
		}
	}

	private long exitTime = 0;

	@Override
	public void onClick(View v) {
		Intent mydata;
		switch (v.getId()) {
		case R.id.tv_title_right:
			if (Iconboo || change) {
				UIUtils.showToast(UIUtils.getContext(), "正在提交数据");
				if (booSubmit) {
					return;
				}
				booSubmit = true;
				if (Iconboo) {
					uploadPic();
				} else if (change) {
					UpdataUserInfo();
				}
				if (nameChange) {
					// 发送广播通知展示界面更新
					PrefUtils.setString(UIUtils.getContext(),
							GlobalConstant.USER_NICKNAME, tv_editdata_nickname
									.getText().toString().trim());
					EventBus.getDefault().post(
							new UpdateShowMsg(GlobalConstant.UPDATEUSERNAME,
									null, null, null, null));
				}
				// finish();
			} else {
				UIUtils.showToast(UIUtils.getContext(), "还没有修改资料");
			}
			break;
		case R.id.iv_title_backicon:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				if (change || Iconboo) {
					ShowAlertDialog s = new ShowAlertDialog(this,
							"资料未保存，是否需要退出", "editdata");
				} else {
					finish();
				}
			}
			break;
		case R.id.rl_editdata_headicon:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this,
						SelectPicPopupWindow.class);
				startActivityForResult(mydata, HEADICON);
			}
			break;
		case R.id.rl_editdata_nickname:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this,
						NickNameActivity.class);
				mydata.putExtra(EDITDATA, tv_editdata_nickname.getText()
						.toString().trim());
				startActivityForResult(mydata, NICKNAME);
			}
			break;
		case R.id.rl_editdata_age:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this,
						BirthdayActivity.class);
				mydata.putExtra(EDITDATA, PrefUtils.getString(
						GlobalConstant.USER_BIRTH, "1994-08-08"));
				startActivityForResult(mydata, AGE);
			}
			break;
		case R.id.rl_editdata_gender:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this,
						SelectGenderWindow.class);
				startActivityForResult(mydata, GENDER);
			}
			break;
		case R.id.rl_editdata_sign:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this,
						SignSytleActivity.class);
				mydata.putExtra(EDITDATA, tv_editdata_sign.getText().toString()
						.trim());
				startActivityForResult(mydata, SIGN);
			}
			break;
		case R.id.rl_editdata_pland:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this,
						SelectPlandWindow.class);
				mydata.putExtra(EDITDATA, tv_editdata_pland.getText()
						.toString().trim());
				startActivityForResult(mydata, PLAND);
			}
			break;
		case R.id.rl_editdata_job:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this, JobActivity.class);
				mydata.putExtra(EDITDATA, tv_editdata_job.getText().toString()
						.trim());
				startActivityForResult(mydata, JOB);
			}
			break;
		case R.id.rl_editdata_hobby:
			if (System.currentTimeMillis() - exitTime > GlobalConstant.COMPARTMENT) {
				exitTime = System.currentTimeMillis();
				mydata = new Intent(EditDataActivity.this, HobbyActivity.class);
				mydata.putExtra(EDITDATA, tv_editdata_hobby.getText()
						.toString().trim());
				startActivityForResult(mydata, HOBBY);
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == HEADICON) {
			if (CheckUtils.getInstance().isIcon(
					GlobalConstant.HEAD_ICON_PATH_BACKUP)) {
				Iconboo = true;
				httpHelp.showImage(ci_editdata_headicon,
						GlobalConstant.HEAD_ICON_PATH_BACKUP + "##");
			}
		}
		if (requestCode == NICKNAME) {
			if (data != null) {
				tv_editdata_nickname
						.setText(data.getStringExtra(EDITDATA) + "");
				change = true;
				nameChange = true;
			}
		}
		if (requestCode == AGE) {
			if (data != null) {
				birth = data.getStringExtra(EDITDATA) + "";
				tv_editdata_age.setText(CaculationUtils
						.calculateDatePoor(birth) + "");
				change = true;
			}
		}
		if (requestCode == GENDER) {
			if (data != null) {
				tv_editdata_gender.setText(data.getStringExtra(EDITDATA) + "");
				change = true;
			}
		}
		if (requestCode == SIGN) {
			if (data != null) {
				change = true;
				tv_editdata_sign.setText(data.getStringExtra(EDITDATA) + "");
			}
		}
		if (requestCode == PLAND) {
			if (data != null) {
				tv_editdata_pland.setText(data.getStringExtra(EDITDATA) + "");
				change = true;
			}
		}
		if (requestCode == JOB) {
			if (data != null) {
				tv_editdata_job.setText(data.getStringExtra(EDITDATA) + "");
				change = true;
			}
		}
		if (requestCode == HOBBY) {
			if (data != null) {
				tv_editdata_hobby.setText(data.getStringExtra(EDITDATA) + "");
				change = true;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
