package com.aliamauri.meat.global;

import com.aliamauri.meat.utils.SDUtils;
import com.aliamauri.meat.utils.UIUtils;

public interface GlobalConstant {
	String APP_CHANNEL = "2000";
	String APP_CHANNEL_C = "xiaomi";// 用于上小米，百度平台的时候传给后台的
	/**
	 * 附近页面 tag
	 */
	int NEARBY_PAGE = 0;
	/**
	 * 通讯录 页面 tag
	 */
	int ADDR_PAGE = 1;
	/**
	 * 判断当前状态是否为登陆状态
	 */
	String IS_LOGINED = "is_logined";

	/**
	 * 视频ID和总集数
	 */
	String TV_ID_AND_TVNUM = "tv_id_and_num";
	/**
	 * 搜索 页面 tag
	 */
	int SEARCH_PAGE = 2;

	/**
	 * 发现 页面 tag
	 */
	int FIND_PAGE = 3;
	/**
	 * 我的 页面 tag
	 */
	int MY_PAGE = 4;
	// intnet id
	String INTENT_ID = "intet_id";
	// intnet名字
	String INTENT_NAME = "intent_name";
	String INTENT_BUNDLE = "intent_bundle";
	String INTENT_DATA = "intent_data";
	// 数值开始
	int ZERO = 0;
	int ONE = 1;
	int TWO = 2;
	int THREE = 3;
	int FOUR = 4;
	// 数值结束

	/**
	 * 保存apk路径
	 */
	String DL_APK_PATH = SDUtils.getRootFile() + "/apks/";
	/**
	 * sp保存动态拍照上传图片路径
	 */
	String DT_UPDATE_ICON_SAVEPATH = SDUtils.getSDPath() + "/.limao/.dt/";
	/**
	 * sp保存头像图片路径
	 */
	String HEAD_ICON_SAVEPATH = SDUtils.getSDPath() + "/.limao/";
	/**
	 * sdcard保存一见钟情好友头像图片路径
	 */
	String YJZQ_HEAD_ICON_SAVEPATH = SDUtils.getSDPath() + "/.limao/.yjzq/";
	/**
	 * sdcard保存动态数据中的小图片图片路径
	 */
	String DYNAMIC_SMALLIMAGE_SAVEPATH = SDUtils.getSDPath()
			+ "/.limao/.dynamic/.smallImage/";
	/**
	 * sdcard保存动态数据中的大图片图片路径
	 */
	String DYNAMIC_BIGIMAGE_SAVEPATH = SDUtils.getSDPath()
			+ "/.limao/.dynamic/.bigImage/";
	/**
	 * sdcard保存动态数据中的好友头像图片路径
	 */
	String DYNAMIC_HEADIMAGE_SAVEPATH = SDUtils.getSDPath()
			+ "/.limao/.dynamic/.headImage/";
	/**
	 * sdcard保存动态数据中的语音数据的路径
	 */
	String DYNAMIC_MOVICE_SAVEPATH = SDUtils.getSDPath()
			+ "/.limao/.dynamic/.movice/";
	/**
	 * sdcard保存动态数据中的保存视频背景图的路径
	 */
	String DYNAMIC_VIDEOIMGBG_SAVEPATH = SDUtils.getSDPath()
			+ "/.limao/.dynamic/.videoImgbg/";
	/**
	 * sp保存图片路径具体图片
	 */
	String USER_FACE = "user_face";
	/**
	 * 图片路径具体图片
	 */
	String HEAD_ICON_PATH = SDUtils.getSDPath() + "/.limao/instantmessage.jpg";
	/**
	 * sp保存图片路径具体图片2
	 */
	String HEAD_ICON_PATH_BACKUP = SDUtils.getSDPath() + "/.limao/backup.jpg";
	/**
	 * sp保存图片路径具体图片3
	 */
	String HEAD_ICON_UPLOAD_PATH = SDUtils.getSDPath() + "/.limao/upload.jpg";
	/**
	 * 视频ID
	 */
	String TV_ID = "1";
	/**
	 * 搜索关键词
	 */
	String APP_SEARCH_KEY = "2";
	/**
	 * sp保存搜索词
	 */
	String SERACH_CACHE_KEY = "serach_cache_key";
	/**
	 * 切割标志
	 */
	String FLAG_APP_SPLIT = "#_`_#";
	/**
	 * 附近——动态fragment
	 */
	// String CHILE_DT_TAG = "dongtai";

	/**
	 * 附近——人fragment
	 */
	// String CHILE_R_TAG = "ren";
	/**
	 * 附近——话题fragment
	 */
	// String CHILE_HT_TAG = "huati";
	/**
	 * 附近fragment Tag
	 */
	String F_NEARBY_TAG = "nearby";
	/**
	 * 通讯录fragment Tag
	 */
	String F_ADDRESS_TAG = "address";
	/**
	 * 查找fragment Tag
	 */
	String F_SEARCH_TAG = "search";
	/**
	 * 发现fragment Tag
	 */
	String F_FIND_TAG = "find";
	/**
	 * 我的fragment Tag
	 */
	String F_MY_TAG = "my";

	/**
	 * 当前动态条目的状态 tag
	 */
	String DT_CURRENT_DATA_NAME = "dt_current_data";

	/**
	 * 转发，发布条目的类型总数
	 */
	int LV_CONTENT_TYPE_TOTE = 5;
	/**
	 * 转发发送页面条目的第一种类型，语音
	 */
	int ITEM_TYPE_VOICE = 0;
	/**
	 * 转发发送页面条目的第二种类型，文字
	 */
	int ITEM_TYPE_TEXT = 1;
	/**
	 * 转发发送页面条目的第三种类型，用户转发的内容
	 */
	int ITEM_TYPE_RETRANS_CONTENT = 2;
	/**
	 * 转发发送页面条目的第四种类型，显示用户想要转发的标签
	 */
	int ITEM_TYPE_SHOW_TAG = 3;
	/**
	 * 转发发送页面条目的第五种类型，显示用户选择的标签
	 */
	int ITEM_TYPE_SELECT_TAG = 4;

	/**
	 * 用户信息开始
	 */
	String USER_ID = "user_id";// 保存用户id
	String GET_USERINFO = "get_userinfo"; // 判断有没有从网络中获取用户信息
	// String USER_PHONENUMBER = "user_phonenumber";// 保存用户电话号码
	String USERINFO = "userinfo";
	String UCODE = "ucode";// 保存用户ucode
	String USER_NICKNAME = "user_nickname";// 保存用户姓名
	String USER_SEX = "user_sex";// 保存用户性别
	String ISSMVAL = "issmval";// 保存用户是否认证
	String USER_BIRTH = "user_birth";// 保存用户年龄 生日
	String USER_SIGNATURE = "user_signature";// 个性签名
	String USER_PLAND = "user_pland";// 用户现居地
	String USER_JOB = "user_job";// 用户职位
	String USER_HOBBY = "user_hobby";// 用户爱好
	String USER_TEL = "user_tel";// 用户绑定&注册的电话
	String USER_TEL_VERIFY = "user_tel_verify";// 用户是否有电话验证
	String USER_EMAIL = "user_email";// 用户绑定的邮箱
	String USER_EMAIL_VERIFY = "user_email_verify";// 用户是否有绑定的邮箱
	String USER_ISEDITPWD = "user_iseditpwd";// 判定是否有修改过密码，，社交没用到
	String EDIT_USER_HEADICON = "edit_user_headicon";// 保存是否有修改过用户的信息，来用判定是否继续网络请求提交用户数据
	String EDIT_USER_INFO = "edit_user_info";// 是否修改过用户头像，来用判定是否继续网络请求提交用户数据 。
	String USER_HXUID = "user_hxuid";
	String USER_HXPWD = "user_hxpwd";

	String USER_CITY = "user_city";// 在附近喵客筛选中的城市

	String USER_FOLLOWSCOUNT = "user_followscount";
	String USER_FANSCOUNT = "user_fanscount";
	String USER_DOYENTYPE = "user_doyentype";
	/**
	 * 用户信息结束
	 */
	/**
	 * 转发发布页面每个标签的tag 共6个
	 * 
	 */
	String TAG_1 = "TAG_1";
	String TAG_2 = "TAG_2";
	String TAG_3 = "TAG_3";
	String TAG_4 = "TAG_4";
	String TAG_5 = "TAG_5";
	String TAG_6 = "TAG_6";

	// 保存条件查找的最近数据
	/**
	 * 性别
	 */
	String CONDITION_SEX = "condition_sex";
	/**
	 * 年龄
	 */
	String CONDITION_AGE = "condition_age";
	/**
	 * 距离
	 */
	String CONDITION_FAR = "condition_far";
	/**
	 * 最新动态标记
	 */
	int TYPE_ZXDT = 1;
	/**
	 * 最新动热标记
	 */
	int TYPE_ZRDT = 2;
	/**
	 * 朋友圈标记
	 */
	int TYPE_PYQ = 3;
	/**
	 * 消息设置开始
	 */
	String SM_GETMESSAGE = "sm_getmessage";// 消息设置中设置是否接收新消息提醒
	String SM_VOICE = "sm_voice";// 消息设置中设置有声音
	String SM_VIBRATE = "sm_vibrate";// 消息设置中设置是否有振动
	String SM_SHOWMESSAGE = "sm_showmessage";// 消息设置中设置提醒
	String SM_NIGHTMESSAGE = "sm_nightmessage";// 消息设置是否夜间免打扰
	/**
	 * 消息设置结束
	 */
	/**
	 * 用于判断当前是发布动态还是修改个人资料的情况 用于获取用户选的照片类
	 */
	String DT_TAG = "braodcast_dt";
	/**
	 * 进入转发页面通过该标记来获取动态id
	 */
	String RETRANS_USER_TAG = "retrans_user_tag";
	/**
	 * 判断当前是通过条目进入的详细动态页还是通过评论按钮进入的动态页
	 */
	String DT_ISOPEN_COMMENT_LAYOUT = "dt_isopen_comment_layout";
	/**
	 * 判断是否完善资料了
	 */
	String IS_INFOCOMPLETE = "is_infocomplete";
	/**
	 * 获取用户上传所有照片的标记
	 */
	String SHOW_DETAILS_IMAGES = "show_details_images";
	/**
	 * 获取用户用户当前点击照片的位置
	 */
	String SHOW_IMAGE_POSITION = "show_image_position";
	/**
	 * 进入公告详情页面的标记
	 */
	String GO_GGXQ_TAG = "go_ggxq_tag";
	/**
	 * 热门公告页面的标记
	 */
	String RMGG_TAG = "rmgg_tag";
	/**
	 * 速配约会页面的标记
	 */
	String SPYH_TAG = "spyh_tag";
	/**
	 * 进入公告详情页面的id
	 */
	String GO_GGXQ_ID = "go_ggxq_id";
	/**
	 * 回复朋友的bean_tag
	 */
	String HYPY_TAG = "hypy_tag";
	/**
	 * 保存版本号
	 */
	String SAVE_IGNORE_VERSIONCODE = "save_ignore_versioncode";
	/**
	 * 保存apk路径
	 */
	String SAVE_APK_PATH = SDUtils.getSDPath() + "/apks/InstantMessage.apk";
	/**
	 * 最热影视标记
	 */
	int TYPE_ZRYS = 1;
	/**
	 * 最热影视标记
	 */
	int TYPE_ZXYS = 2;
	/**
	 * 标签标记
	 */
	int TYPE_TAG = 3;
	/**
	 * 短视频播放路径
	 */
	String SHOW_SHORT_VIDEO_PATH = "show_short_video_path";
	/**
	 * 资源库传入搜索页的标记
	 */
	String RESOURCESEARCH_TAG = "resourcesearch_tag";
	/**
	 * 动态详情页，评论处添加好友的标记
	 */
	String COMMENT_ADD_FRIEND = "comment_add_friend";
	Integer COMPARTMENT = 1000;
	/**
	 * 聊天界面HXid
	 */
	String FLAG_CHAT_HX_USER_ID = "userId";
	/**
	 * 聊天界面的用户昵称
	 */
	String FLAG_CHAT_USER_NIKE = "userNike";
	/**
	 * 聊天界面聊天類型
	 */
	String FLAG_CHAT_TYPE = "chatType";
	/**
	 * 进入我的，或者其他好友的动态的标记
	 */
	String DYNAMIC_TAG = "dynamic_tag";
	/**
	 * 获取公告详情页的回应数标记
	 */
	String DATA_TAG = "data";
	/**
	 * 传入动态详情页面的该用户的uid
	 */
	String DT_CURRENT_DATA_UID = "dt_current_data_uid";
	/**
	 * 通过服务上传相册，语音，视频等数据
	 */
	String ACTIVITYTOSERVICE_TAG = "activitytoservice_tag";
	/**
	 * 发布动态后台服务开始上传
	 */
	String SERVICE_START = "service_start";
	/**
	 * 发布动态后台服务上传完成
	 */
	String SERVICE_STOP = "service_stop";
	/**
	 * 数据库--动态表
	 */
	int DYNAMIC_TABLE = 100;
	/**
	 * 数据库--转发动态表
	 */
	int DYNAMIC_RELAY_TABLE = 101;
	/**
	 * 数据库--语音表
	 */
	int VOICE_TABLE = 102;
	/**
	 * 数据库--视频表
	 */
	int VIDEO_TABLE = 103;
	/**
	 * 数据库--相册表动表
	 */
	int IMAGE_TABLE = 104;
	/**
	 * 用户的坐标位置标记 格式：伟度+&&+经度
	 */
	String USER_LOCATION = "user_location";
	/**
	 * 屏蔽好友标记__shield
	 */
	String SHIELD_TAG = "shield";
	/**
	 * 删除好友标记__delete
	 */
	String DELETE_TAG = "delete";
	/**
	 * 用户发布动态，资源未上传
	 */
	String UPLOADTYPE_1 = "1";
	/**
	 * 用户发布动态，资源正在上传数据库，还未上传成功的时候
	 */
	String UPLOADTYPE_2 = "2";
	/**
	 * 用户发布动态，上传成功
	 */
	String UPLOADTYPE_3 = "3";
	/**
	 * 编辑资料页面修改名称标记
	 */
	String UPDATEUSERNAME = "updateusername";
	/**
	 * 图片大小最大值
	 */
	long IMAGEFILESIZE = 1024 * 1024 * 1;
	/**
	 * 当前用户具体位置的文字信息
	 */
	String USER_LOCATION_MSG = "location_msg";
	/**
	 * APP名称
	 */
	String ROOT_DIR_NAME = UIUtils
			.getContext()
			.getPackageName()
			.substring(
					UIUtils.getContext().getPackageName().lastIndexOf(".") + 1);
	/**
	 * 缓存页面去播放页面标志
	 */
	String FLAG_PALY_CACHE_HASH = "FLAG_PALY_CACHE_HASH";

	/**
	 * 关闭播放来源的广播
	 */
	String CLOSE_PLAY_SOURCE_ACTIVITY = "close_play_source_activity";
	/***
	 * 本地联系人保存超时
	 */
	String CONTACT_TIME_OUT = "contact_time_out";
	/**
	 * 好友id
	 */
	String FRIEND_ID = "FRIEND_ID";
	/**
	 * 好友名字
	 */
	String FRIEND_NICKNAME = "FRIEND_NICKNAME";
	/**
	 * 是否屏蔽该好友
	 */
	String FRIEND_IS_SHIELD = "FRIEND_IS_SHIELD";
	/**
	 * 下载的升级包软件版本号
	 */
	String UPLOADVERSIONCODE = "UPLOADVERSIONCODE";
	/**
	 * 升级包的主要信息
	 */
	String UPLOAD_DESC = "UPLOAD_DESC";
	/**
	 * 升级包的名字
	 */
	String UPLOAD_VNAME = "UPLOAD_VNAME";
	/**
	 * 升级包的大小
	 */
	String UPLOAD_FSIZE = "UPLOAD_FSIZE";
	/**
	 * 升级包的时间
	 */
	String UPLOAD_PDATE = "UPLOAD_PDATE";

	/**
	 * 首页标题sp数据开始
	 */
	String CHILD_FRAGMENT_MOVIES = "child_fragment_movies";
	/**
	 * 播放视频的id标志
	 */
	String PLAY_VIDEO_ID = "PLAY_VIDEO_ID";
	/**
	 * 获取该条评论的id
	 */
	String COMMENTDATAURL = "COMMENTDATAURL";
	/**
	 * 新手引导开始
	 */
	String NOT_NEWHAND_GOTOSETTING = "not_newhand_gotosetting";
	String NOT_NEWHAND_GOTOTAKEVIDEO = "not_newhand_gototakevideo";
	String NOT_NEWHAND_CHANNEL = "not_newhand_channel";
	String NOT_NEWHAND_PLAY = "not_newhand_play";
	/**
	 * 新手引导结束
	 */
	/**
	 * 当前用户的环信id
	 */
	String HXUSER_ID = "HXUSER_ID";
}
