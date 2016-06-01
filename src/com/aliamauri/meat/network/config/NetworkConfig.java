package com.aliamauri.meat.network.config;

import com.aliamauri.meat.global.GlobalConstant;
import com.aliamauri.meat.service.UploadService.UplaodType;
import com.aliamauri.meat.utils.PhoneInfoUtils;
import com.aliamauri.meat.utils.PrefUtils;
import com.aliamauri.meat.utils.UIUtils;

/**
 * 网络配置文件
 * 
 * @author jjm
 * 
 */
public class NetworkConfig {

	/**
	 * 域名
	 */
	// private static final String JIAOYOUDOMAIN =
	// "http://tliaotian.limaoso.com/mapi.php/";

	private static final String BASEDOMAIN = "http://mmmliaotian10.limaoso.com/";

	private static final String JIAOYOUDOMAIN = BASEDOMAIN + "mapi.php/";
	// private static final String JIAOYOUDOMAIN =
	// "http://test.immiao.com:1700/mapi.php/";
	private static final String SEARCHURL = BASEDOMAIN + "msearch.php/";
	// private static final String SEARCHURL =
	// "http://test.immiao.com:1700/msearch.php/";

	private static final String SOUSUO = BASEDOMAIN + "mapi.php/";
	private static final String UPLOAD = "http://f1.immiao.com/";
	// private static final String textUrl =
	// "http://test.immiao.com:1700/mapi.php/";

	// private static final String SOUSO =
	// "http://mmmapi.limaoso.com/msearch.php/";
	// private static final String JIAOYOUDOMAIN =
	// "http://tliaotian.limaoso.com/mapi.php/";
	public static final String FMT = "&fmt=sx";

	// private static final String JIAOYOUDOMAIN = "192.168.150.1/";
	// private static final String DOMAIN =
	// "http://192.168.1.115:2000/mapi.php/";

	// 获取ucode
	public static String getUcode() {
		return PrefUtils.getString(UIUtils.getContext(), GlobalConstant.UCODE,
				null);
	}

	/**
	 * 发现
	 * 
	 * @return
	 */
	public static String getFind_tjyy(int page) {

		if (page == 1) {
			return SEARCHURL + "Index/faxian/?ucode=" + getUcode() + FMT;
		} else {
			return SEARCHURL + "Index/faxian/?page=" + page + FMT + "&ucode="
					+ getUcode();
		}
	}

	/**
	 * 打开应用
	 * 
	 * @return
	 */
	public static String getSplash() {
		return JIAOYOUDOMAIN + "Public/openAppShejiao?imei="
				+ PhoneInfoUtils.getPhoneImei(UIUtils.getContext()) + "&mac="
				+ PhoneInfoUtils.getPhoneMac(UIUtils.getContext()) + "" + FMT;
	}

	/**
	 * 获取手机验证码
	 * 
	 * @return
	 */
	public static String getPhoneCode(String tel) {
		return JIAOYOUDOMAIN + "Public/register_user_tel_post?tel=" + tel + FMT;
	}

	/**
	 * 注册1
	 * 
	 * @return
	 */
	public static String getRegister(String tel, String code) {
		return JIAOYOUDOMAIN + "Public/register_user_tel_code_val?tel=" + tel
				+ "&code=" + code + "&ucode=" + getUcode() + FMT;
	}

	/**
	 * 注册2
	 * 
	 * @return
	 */
	public static String getRegister(String tel, String code, String pwd,
			String pwd2, String uid) {
		return JIAOYOUDOMAIN + "Public/registerByTel?tel=" + tel + "&code="
				+ code + "&pwd=" + pwd + "&pwd2=" + pwd2 + "&uid=" + uid
				+ "&imei=" + PhoneInfoUtils.getPhoneImei(UIUtils.getContext())
				+ "&mac=" + PhoneInfoUtils.getPhoneMac(UIUtils.getContext())
				+ FMT;

	}

	/**
	 * 获取新用户id
	 * 
	 * @return
	 */
	public static String getNewId() {
		return JIAOYOUDOMAIN + "Public/autoRegisterID?imei="
				+ PhoneInfoUtils.getPhoneImei(UIUtils.getContext()) + "&mac="
				+ PhoneInfoUtils.getPhoneMac(UIUtils.getContext()) + FMT;

	}

	/**
	 * 获取用户信息
	 * 
	 * @return
	 */
	public static String getUserInfo() {
		return JIAOYOUDOMAIN
				+ "User/getUserInfo?ucode="
				+ PrefUtils.getString(UIUtils.getContext(),
						GlobalConstant.UCODE, "") + FMT;

	}

	/**
	 * 登录
	 * 
	 * @return
	 */
	public static String getLogin(String uid, String pwd) {
		return JIAOYOUDOMAIN + "Public/userLoginByUidPwd?uid=" + uid + "&pwd="
				+ pwd + FMT;

	}

	/**
	 * 获取好友列表
	 * 
	 * @return
	 */
	public static String getFriend() {
		return JIAOYOUDOMAIN + "Friends/getMyFriends?ucode=" + getUcode() + FMT;
	}

	/**
	 * 从服务器获取好友列表
	 * 
	 * @return
	 */
	public static String getContactFromService() {
		return JIAOYOUDOMAIN + "Friends/getMyFriendsList?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 上传头像
	 * 
	 * @return
	 */
	public static String setHeadIcon() {
		// return
		// "http://mapi.limaoso.com/mapi.php/User/getInfoNums?ucode=6c24ec76c9b7fa9da00aafcd0fb2f3e8";
		return JIAOYOUDOMAIN + "User/uploadMyFace?ucode=" + getUcode() + FMT;
		// return JIAOYOUDOMAIN + "User/uploadMyFace/";
	}

	/**
	 * 获取好友列表
	 * 
	 * @return
	 */
	public static String getUpdateUserInfo(String face, String nickname,
			String birth, String sex, String signature, String pland,
			String job, String hobby) {

		return JIAOYOUDOMAIN + "User/updateUserinfoAll?ucode=" + getUcode()
				+ "&face=" + face + "&nickname=" + nickname + "&birth=" + birth
				+ "&sex=" + sex + "&signature=" + signature + "&pland=" + pland
				+ "&job=" + job + "&hobby=" + hobby + FMT;
	}

	/**
	 * 获取实名认证信息
	 * 
	 * @return
	 */
	public static String getAuthenticateState() {
		return JIAOYOUDOMAIN + "Usercodeval/getRealInfoOne?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 实名认证
	 * 
	 * @return
	 */
	public static String getAuthenticate(String realname, String idnumber) {
		return JIAOYOUDOMAIN + "Usercodeval/postRealInfo?ucode=" + getUcode()
				+ "&realname=" + realname + "&idnumber=" + idnumber + FMT;
	}

	/**
	 * 获取我加入的话题
	 * 
	 * @return
	 */
	public static String getMyChatGroupJoinList(String pagesize, int page) {
		return JIAOYOUDOMAIN + "Chatgroup/getMyChatGroupJoinList?ucode="
				+ getUcode() + "&pagesize=" + pagesize + "&page=" + page + FMT;
	}

	/**
	 * 获取我创建的话题
	 * 
	 * @return
	 */
	public static String getMyChatGroupCreateList(String pagesize, int page) {
		return JIAOYOUDOMAIN + "Chatgroup/getMyChatGroupCreateList?ucode="
				+ getUcode() + "&pagesize=" + pagesize + "&page=" + page + FMT;
	}

	/**
	 * 手机发验证码
	 * 
	 * @return
	 */
	// http://limaoso.cntttt.com/mapi/User/getMyFav/
	public static String getPhoneCodeAccount(String phone) {

		return JIAOYOUDOMAIN + "User/tel_msg_post?ucode=" + getUcode()
				+ "&tel=" + phone + FMT;

	}

	/**
	 * 手机验证
	 * 
	 * @return
	 */
	// http://limaoso.cntttt.com/mapi/User/getMyFav/
	public static String getPhoneCheck(String phone, String code) {

		return JIAOYOUDOMAIN + "User/tel_verify?ucode=" + getUcode() + "&tel="
				+ phone + "&code=" + code + FMT;
	}

	/**
	 * 邮箱发验证码
	 * 
	 * @return
	 */
	// http://limaoso.cntttt.com/mapi/User/getMyFav/
	public static String getMailCode(String email) {

		return JIAOYOUDOMAIN + "User/email_post?ucode=" + getUcode()
				+ "&email=" + email + FMT;

	}

	/**
	 * 邮箱验证
	 * 
	 * @return
	 */
	// http://limaoso.cntttt.com/mapi/User/getMyFav/
	public static String getMailCheck(String email, String code) {

		return JIAOYOUDOMAIN + "User/email_verify?ucode=" + getUcode()
				+ "&email=" + email + "&code=" + code + FMT;

	}

	/**
	 * 修改密码
	 * 
	 * @return
	 */
	public static String setPassword(String oldpwd, String newpwd) {
		return JIAOYOUDOMAIN + "User/editPwd/?ucode=" + getUcode() + "&oldpwd="
				+ oldpwd + "&newpwd=" + newpwd + FMT;
	}

	/**
	 * 获取用户信息，相册，好友数等
	 * 
	 * @return
	 */
	public static String getUserRelInfo() {
		return JIAOYOUDOMAIN + "User/getUserRelInfoss?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 获取相册
	 * 
	 * @return
	 */
	public static String getUserAlbum(String Pagesize, int page) {
		return JIAOYOUDOMAIN + "Userphotos/getUserphotoslist?ucode="
				+ getUcode() + "&pagesize=" + Pagesize + "&page=" + page + FMT;
	}

	/**
	 * 获取推荐群
	 * 
	 * @return
	 */
	public static String getChatGroupList(int Pagesize, String rec, int page,
			String name) {
		return JIAOYOUDOMAIN + "Chatgroup/getChatGroupList?ucode=" + getUcode()
				+ "&pagesize=" + Pagesize + "&rec=" + rec + "&page=" + page
				+ "&name=" + name + FMT;
	}

	/**
	 * 获取推荐群
	 * 
	 * @param page
	 * 
	 * @return
	 */
	public static String getuserSearch(String minage, String maxage,
			String sex, String name, int page) {
		return JIAOYOUDOMAIN + "Uuser/userSearch?ucode=" + getUcode()
				+ "&minage=" + minage + "&maxage=" + maxage + "&sex=" + sex
				+ "&page=" + page + FMT;
	}

	/**
	 * 获取话题的url
	 * 
	 * @param pagesize
	 *            自定义每页的数据条目总数
	 * 
	 * @param page
	 *            请求的页数
	 * @param name
	 *            查询时候所用的字段名 null为非查询时候
	 * @return
	 */
	public static String get_ht_url(int pagesize, int page, String name) {
		return JIAOYOUDOMAIN + "Chatgroup/getChatGroupIndex/ucode/"
				+ getUcode() + "&pagesize=" + pagesize + "&page=" + page
				+ "&name=" + name + FMT;
	}

	/**
	 * 获取最新网址
	 * 
	 * @param tag
	 *            根据tag判断当前状态
	 * 
	 * @param curid
	 *            当前id数
	 * @return
	 */
	public static String getDynamicUrl_new(String tag, String curid) {
		return JIAOYOUDOMAIN + "Userlatest/getUserLatestList?ucode="
				+ getUcode() + "&action=" + tag + "&curid=" + curid + FMT;
	}

	/**
	 * 获取朋友圈网址
	 * 
	 * @param tag
	 *            根据tag判断当前状态
	 * 
	 * @param curid
	 *            当前id数
	 * @return
	 */
	public static String getDynamicUrl_friend(String tag, String curid) {
		return JIAOYOUDOMAIN + "Userlatest/getFriendLatesList?ucode="
				+ getUcode() + "&action=" + tag + "&curid=" + curid + FMT;
	}

	/**
	 * 获取最热网址
	 * 
	 * @param page
	 *            当前的页数
	 * @return
	 */
	public static String getDynamicUrl_hot() {
		// return JIAOYOUDOMAIN + "Userlatest/getUserLatestListHot?ucode="
		// + getUcode() + FMT;
		return JIAOYOUDOMAIN + "Userlatest/getVideolatest?ucode=" + getUcode()
				+ "&action=new&curid=0" + FMT;
	}

	/**
	 * 获取通讯录好友
	 * 
	 * @return
	 */
	public static String getMyfriendsByTXL() {
		return JIAOYOUDOMAIN + "Friends/getMyfriendsByTXL?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 上传图片
	 * 
	 * @return
	 */

	public static String imgUploads() {
		return "http://mmedia2.limaoso.com:9191/mapi.php/Index/uploadPhoto?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 获取话题信息
	 * 
	 * @return
	 */
	public static String getUserGroupInfo(String groupid) {
		return JIAOYOUDOMAIN + "Chatgroup/getUserGroupInfo?ucode=" + getUcode()
				+ "&groupid=" + groupid + FMT;
	}

	/**
	 * 查询好友ID
	 * 
	 * @param name
	 * @return
	 */
	public static String getFindFriend(String name) {
		return JIAOYOUDOMAIN + "Txcommon/getUserTxinfo?uid=" + name + "&ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 添加好友
	 * 
	 * @param logid
	 * @param mUid
	 * @param fUid
	 * @return
	 */
	public static String getAcceptInvitation(String touid, String action,
			String logid) {
		return JIAOYOUDOMAIN + "Friends/dealAddUserRequest?touid=" + touid
				+ "&action=" + action + "&logid=" + logid + "&ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 点赞功能按钮
	 * 
	 * @param id
	 * @return
	 */
	public static String getDianZanUrl(String id) {
		return JIAOYOUDOMAIN + "Userlatest/userLatestDianzan?ucode="
				+ getUcode() + "&id=" + id + FMT;
	}

	/**
	 * 删除动态功能
	 * 
	 * @param id
	 * @return
	 */
	public static String getDeleta_DtUrl(String id) {
		return JIAOYOUDOMAIN + "Userlatest/userLatestdel?ucode=" + getUcode()
				+ "&id=" + id + FMT;
	}

	/**
	 * 删除好友
	 * 
	 * @param uid
	 * @return
	 */
	public static String getDeleteContactUrl(String uid) {
		return JIAOYOUDOMAIN + "Friends/delMyfriends" + "?deluid=" + uid
				+ "&ucode=" + getUcode() + FMT;
	}

	/**
	 * 好友添加请求
	 * 
	 * @param userid
	 * @return
	 */
	public static String getAddContactUrl(String touid, String msg) {
		return JIAOYOUDOMAIN + "Friends/goAddFriendUserRequest?touid=" + touid
				+ "&msg=" + msg + "&ucode=" + getUcode() + FMT;
	}

	/**
	 * 获取话题标签
	 * 
	 * @return
	 */
	public static String getChatGroupTagsr(int pagesize, int page) {
		return JIAOYOUDOMAIN + "Uuser/getChatGroupTags?ucode=" + getUcode()
				+ "&pagesize=" + pagesize + "&page=" + page + FMT;
	}

	/**
	 * 获取爱好标签
	 * 
	 * @return
	 */
	public static String getUserTags(int pagesize, int page) {
		return JIAOYOUDOMAIN + "Uuser/getUserTags?ucode=" + getUcode()
				+ "&pagesize=" + pagesize + "&page=" + page + FMT;
	}

	/**
	 * 附近 --人
	 * 
	 * @param page_normal
	 * @return
	 */
	public static String getNearbyUser(int page, String province, String city) {
		return JIAOYOUDOMAIN + "Uuser/getMyCityPeople?ucode=" + getUcode()
				+ "&page=" + page + FMT + "&province=" + province + "&city="
				+ city;
		// return JIAOYOUDOMAIN + "Uuser/userSearch?ucode=" + getUcode()
		// + "&page=" + page + FMT;
	}

	/**
	 * 获取找回密码url
	 */
	public static String getRetrievePassword() {

		return JIAOYOUDOMAIN + "Public/userFindPassword?" + FMT;

	}

	/**
	 * 通过Hx获取用户信息
	 * 
	 * @return
	 */
	public static String getHxToInfosUrl() {
		return JIAOYOUDOMAIN + "Txcommon/getUserTxInfoByHxID" + "?fmt=sx" + FMT;
	}

	/**
	 * 附近--人--筛选功能
	 * 
	 * @param page_search
	 * @param sex
	 *            性别 0--女，1--男，-1全部
	 * @param ageMin
	 *            最小年龄
	 * @param ageMax
	 *            最大年龄
	 * @param firMin
	 *            最小距离
	 * @param firMax
	 *            最大距离
	 * @return
	 */
	public static String getSearchUser(int page, String sex, String ageMin,
			String ageMax, String firMin, String firMax, String province,
			String city) {
		// return JIAOYOUDOMAIN + "Uuser/userSearch?ucode=" + getUcode()
		// + "&page=" + page + "&minage=" + ageMin + "&maxage=" + ageMax
		// + "&sex=" + sex + FMT;

		return JIAOYOUDOMAIN + "Uuser/userSearch?ucode=" + getUcode()
				+ "&page=" + page + "&province=" + province + "&city=" + city
				+ "&minage=" + ageMin + "&maxage=" + ageMax + "&sex=" + sex
				+ FMT;
	}

	/**
	 * 附近--动态，，动态详情页
	 * 
	 * @param mCurrentItem_id
	 * @param pageNum
	 * @return
	 */
	public static String getDetails_dt_url(String mCurrentItem_id) {
		return JIAOYOUDOMAIN + "Userlatest/getUserlatestDetail?ucode="
				+ getUcode() + "&id=" + mCurrentItem_id + FMT;
	}

	/**
	 * 发布动态页面获取网络推荐的标签
	 * 
	 * @param tAGPAGESIZE
	 * @param tag_page
	 * @return
	 */
	public static String getDtTagUrl(int pagesize, int tag_page) {
		return JIAOYOUDOMAIN + "Tags/getTags?ucode=" + getUcode()
				+ "&pagesize=" + pagesize + "&page=" + tag_page + FMT;
	}

	/**
	 * 发布动态页面综合上传的url
	 * 
	 * @return
	 */
	public static String getDtAllUrl() {
		return JIAOYOUDOMAIN + "Userlatest/addUserlatest?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 动态详情界面发表评论
	 * 
	 * @return
	 */
	public static String getConmmentUrl() {
		return JIAOYOUDOMAIN + "Userlatest/addLatestComment?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 动态详情界面发表评论
	 * 
	 * @return
	 */
	public static String groupUserlist(String groupid) {
		return JIAOYOUDOMAIN + "Chatgroup/groupUserlist?ucode=" + getUcode()
				+ "&groupid=" + groupid + FMT;
	}

	/**
	 * 更新群头像
	 * 
	 * @return
	 */
	public static String uploadGroupFace() {
		return JIAOYOUDOMAIN + "Chatgroup/uploadGroupFace?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 更新群资料
	 * 
	 * @return
	 */
	public static String editChatGroupBaseInfo() {
		return JIAOYOUDOMAIN + "Chatgroup/editChatGroupBaseInfo?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 公告详情页---热门公告的条目id
	 * 
	 * @param mId
	 * @return
	 */
	public static String get_rmgg_detail_url(String mId) {
		return JIAOYOUDOMAIN + "Userhunlian/getYjzqDetail?ucode=" + getUcode()
				+ "&id=" + mId + FMT;
	}

	/**
	 * 公告详情页---速配约会的条目id
	 * 
	 * @param mId
	 * @return
	 */
	public static String get_spyh_detail_url(String mId) {
		return JIAOYOUDOMAIN + "Userhunlian/getSpyhDetail?ucode=" + getUcode()
				+ "&id=" + mId + FMT;
	}

	/**
	 * 公告详情页---热门公告条目----发表回应
	 * 
	 * @param mId
	 * @return
	 */
	public static String getGgxq_rmgg_url() {
		return JIAOYOUDOMAIN + "Userhunlian/addNoticeYjzqReply?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 公告详情页---速配约会条目----发表回应
	 * 
	 * @param mId
	 * @return
	 */
	public static String getGgxq_spyh_url() {
		return JIAOYOUDOMAIN + "Userhunlian/addNoticeSpyhReply?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 婚恋交友----兴趣相同的ta----url
	 * 
	 * @param mLoadingPage
	 * @return
	 */
	public static String get_xqxt_Url(int mLoadingPage) {
		return JIAOYOUDOMAIN + "Userhunlian/getXingquUser?ucode=" + getUcode()
				+ "&page=" + mLoadingPage + "&pagesize=7" + FMT;
	}

	/**
	 * 婚恋交友----速配约会内容列表----url
	 * 
	 * @param mLoadingPage
	 * @return
	 */
	public static String getSPYH_item_url(int mLoadPage) {
		return JIAOYOUDOMAIN + "Userhunlian/getSpyhListPage?ucode="
				+ getUcode() + "&page=" + mLoadPage + FMT;
	}

	/**
	 * 婚恋交友----发布速配约会内容----url
	 * 
	 * @param mLoadingPage
	 * @return
	 */
	public static String get_add_spyh_url() {
		return JIAOYOUDOMAIN + "Userhunlian/addSpyhData?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 婚恋交友----一键钟情----获取附近好友---url
	 * 
	 * @param mLoadPage
	 * @param wd
	 * @param jd
	 * @return
	 */
	public static String getFj_friend_url(int mLoadPage, double wd, double jd) {
		return JIAOYOUDOMAIN + "Userhunlian/getYjzqGonggaoList?ucode="
				+ getUcode() + "&page=" + mLoadPage + "&wd=" + wd + "&jd=" + jd
				+ "&pagesize=10" + FMT;
	}

	/**
	 * 婚恋交友----一键钟情----发布信息---url
	 * 
	 * @param mLoadPage
	 * @param mWd
	 * @param mJd
	 * @return
	 */
	public static String getFBXX_url() {
		return JIAOYOUDOMAIN + "Userhunlian/addYjzqData?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 婚恋交友----热门公告展示列表---url
	 * 
	 * @param mLoadPage
	 * @param mWd
	 * @param mJd
	 * @return
	 */
	public static String getHLJY_url() {
		return JIAOYOUDOMAIN + "Userhunlian/getRemenGonggao?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 婚恋交友----邂逅展示列表---url
	 * 
	 * @param mLoadPage
	 * @param wd
	 * @param jd
	 * @return
	 */
	public static String getXh_url(int mLoadPage_xh, String jd, String wd) {
		return JIAOYOUDOMAIN + "Userhunlian/getXiehouPage?ucode=" + getUcode()
				+ "&page=" + mLoadPage_xh + "&wd=" + wd + "&jd=" + jd + FMT;
	}

	/**
	 * 版本信息
	 * 
	 * @param versionflag
	 * @param versionCode
	 * @return
	 */
	public static String getgetVersionInfo(String versionflag, int versionCode) {
		return JIAOYOUDOMAIN + "Version/getVersionInfo?flag=" + versionflag
				+ "&vcode=" + versionCode + FMT;
	}

	/**
	 * 资源库 最热影视，最新影视url
	 * 
	 * @param tag
	 * @param page
	 * @return
	 */
	public static String getVideoUrl(String tag, int page) {
		return JIAOYOUDOMAIN + "Pubsource/getSourceList/?order=" + tag
				+ "&page=" + page + "&ucode=" + getUcode() + FMT;

	}

	/**
	 * 资源库 发布影视
	 * 
	 * @return
	 */
	public static String getUpload_ys() {
		return JIAOYOUDOMAIN + "Pubsource/addDatas/?ucode=" + getUcode() + FMT;
	}

	/**
	 * 约会邀请
	 * 
	 * @param userid
	 * @param s
	 * @return
	 */
	public static String getappointmentInvitUrl(String userid, String s) {
		return JIAOYOUDOMAIN + "Userhunlian/goAddInvitationRequest/?ucode="
				+ getUcode() + FMT + "&touid=" + userid + "&msg=" + s;
	}

	/**
	 * 资源库 获取推荐标签
	 * 
	 * @return
	 */
	public static String getTagUrl_ys() {
		return JIAOYOUDOMAIN + "Pubsource/getSourceTagList/?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 资源库 标签--搜索结果页
	 * 
	 * @param key
	 * @param mCurrentPage
	 * @return
	 */
	public static String getResorceSearchUrl(String key, int mCurrentPage) {
		return JIAOYOUDOMAIN + "Pubsource/getSourceTagData/?ucode="
				+ getUcode() + "&key=" + key + "&page=" + mCurrentPage + FMT;
	}

	/**
	 * 注册第一步
	 * 
	 * @return
	 */
	public static String getRegisterFirstStep() {
		return JIAOYOUDOMAIN + "Public/register_user_tel_code_val?" + FMT;
	}

	/**
	 * 注册第二步
	 * 
	 * @return
	 */
	public static String getRegisterSecondStep(String ucode) {
		return JIAOYOUDOMAIN + "Public/register_step_2?ucode=" + ucode + FMT;
	}

	/**
	 * 资源库顶踩操作
	 * 
	 * @param type
	 *            1顶，2踩
	 * @return
	 */
	public static String get_ding_cai_url(String type, String actid) {
		return JIAOYOUDOMAIN + "Pubsource/goUpDownAction/?ucode=" + getUcode()
				+ "&type=" + type + "&actid=" + actid + FMT;
	}

	/**
	 * 获取好友的个人主页
	 * 
	 * @return
	 */
	public static String getUserInfor(String id) {

		return JIAOYOUDOMAIN + "Uuser/getUserinfos/?ucode=" + getUcode()
				+ "&uid=" + id + FMT;
	}

	/**
	 * 获取好友的个人主页的相册
	 * 
	 * @return
	 */
	public static String getUserInforPics(String id) {

		return JIAOYOUDOMAIN + "Uuser/getUserLatestPics/?ucode=" + getUcode()
				+ "&uid=" + id + FMT;
	}

	/**
	 * 获取我的动态或好友动态页面的url
	 * 
	 * @param mCurrentPage
	 * @return
	 */
	public static String getDynamicPage_url(String uid, int mCurrentPage) {
		return JIAOYOUDOMAIN + "Userlatest/getUserLatestByUid?ucode="
				+ getUcode() + "&muid=" + uid + "&page=" + mCurrentPage + FMT;
	}

	/**
	 * 从服务器获取黑名单列表
	 * 
	 * @return
	 */
	public static String getContactBlackListFromService() {
		return JIAOYOUDOMAIN + "Userblacklist/getUserBlackList?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 请求服务器添加黑名单关系
	 * 
	 * @param uid
	 * @return
	 */
	public static String getRequsetServiceToBlackList(String uid) {

		return JIAOYOUDOMAIN + "Userblacklist/addMyBlack?ucode=" + getUcode()
				+ "&actuid=" + uid + FMT;
	}

	/**
	 * 请求服务器解除黑名单关系
	 * 
	 * @param uid
	 * @return
	 */
	public static String getRequstServicerRemoveBlackList(String uid) {
		return JIAOYOUDOMAIN + "Userblacklist/delMyBlack?ucode=" + getUcode()
				+ "&actuid=" + uid + FMT;

	}

	/**
	 * 上传视频或音频路径
	 * 
	 * @param uplaodType
	 * @return
	 */
	public static String getUpLoadFilePath(UplaodType uplaodType) {
		if (uplaodType == null) {
			return null;
		}
		String url = "";

		switch (uplaodType) {
		case VIDEO_TYPE:
			url = "http://mmedia2.limaoso.com:9191/mapi.php/Index/uploadVideo?ucode="
					+ getUcode() + FMT;
			break;
		case AUDIO_TYPE:
			url = "http://mmedia1.limaoso.com:9191/mapi.php/Index/uploadVoice?ucode="
					+ getUcode() + FMT;
			break;
		case IMAGE_TYPE:
			url = "http://mmedia3.limaoso.com:9191/mapi.php/Index/uploadImg?ucode="
					+ getUcode() + FMT;
			break;

		}
		return url;
	}

	/**
	 * 屏蔽ta
	 */
	public static String SHIELDYES_TAG = "shieldyes";
	/**
	 * 关注ta
	 */
	public static String SHIELDNO_TAG = "shieldno";

	/**
	 * 动态详情--屏蔽ta按钮
	 * 
	 * @param uid
	 * @return
	 */
	public static String getShieldFriend(String uid, String tag) {
		if (SHIELDYES_TAG.equals(tag)) {
			return JIAOYOUDOMAIN + "Friends/goShieldUser?ucode=" + getUcode()
					+ "&fuid=" + uid + "&action=" + tag + FMT;
		} else {
			return JIAOYOUDOMAIN + "Userlatest/unshield?ucode=" + getUcode()
					+ "&muid=" + uid + FMT;
		}

	}

	/**
	 * 动态详情--删除评论按钮
	 * 
	 * @param id
	 * @return
	 */
	public static String getDeleteComment(String id) {
		return JIAOYOUDOMAIN + "userlatest/userCommentDel?ucode=" + getUcode()
				+ "&id=" + id + FMT;
	}

	/**
	 * 判断该条动态是否存在
	 * 
	 * @param id
	 * @return
	 */
	public static String getIsExist(String id) {
		return JIAOYOUDOMAIN + "Userlatest/latestIsExist?ucode=" + getUcode()
				+ "&id=" + id + FMT;
	}

	/**
	 * 判断该条动态是否存在
	 * 
	 * @param id
	 * @return
	 */
	public static String getSendMessage() {
		return JIAOYOUDOMAIN + "Friends/goSendInviting?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 视频资源来源页
	 * 
	 * @param id
	 * @param i
	 * @return
	 * 
	 */
	/***************************** 视频接口start *****************************/
	public static String getPlaySourceAll(String id, int jindex) {

		return SEARCHURL + "Index/getDetailCku/?id=" + id + "&jindex=" + jindex
				+ "&ucode=" + getUcode();
	}

	/**
	 * 搜索页1
	 * 
	 * @return
	 */
	public static String getSearchAll() {
		return SEARCHURL + "Index/getSearchDefaultAll/?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 搜索页2
	 * 
	 * @return
	 */
	public static String getSearchAll2() {
		return SEARCHURL + "Search/getSearchDefaultAll?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 搜索结果页
	 * 
	 * @param refreshPage
	 * @return
	 */
	public static String getSearchResult(String key, int refreshPage) {
		return SEARCHURL + "Index/search?page=" + refreshPage + "&key=" + key
				+ "&ucode=" + getUcode() + FMT;
	}

	/**
	 * 搜索结果页
	 * 
	 * @param refreshPage
	 * @return
	 */
	public static String getSearchResult2(String key, int refreshPage) {
		return SEARCHURL + "Search/go?page=" + refreshPage + "&key=" + key
				+ "&ucode=" + getUcode() + FMT;
	}

	/**
	 * 确认能否播放
	 * 
	 * @param id
	 *            播放ID
	 * @param num
	 *            播放集数
	 * @return
	 */
	public static String getReqPlay(String id, int num) {

		return SEARCHURL + "Index/ishasCfilmByJindex/?jindex=" + num + "&kuid="
				+ id + FMT;
	}

	/**
	 * 播放视频接口
	 * 
	 * @param tv_id
	 * @return
	 */
	public static String getPlayActivityUrl(String tv_id) {
		return SEARCHURL + "Videodetail/getDetailVideos?ckuid=" + tv_id
				+ "&ucode=" + getUcode();
	}

	public static String getReplyComment() {
		return SEARCHURL + "User/addComments?ucode=" + getUcode();
	}

	/****************************** 视频接口end ************************************/

	/**
	 * 记录页面
	 * 
	 * @return
	 */
	public static String getRecordPage(int page) {
		// return
		// "http://mmmliaotian2.limaoso.com/msearch.php/Userrel/getVideoHistory?ucode="
		// + getUcode();
		return SEARCHURL + "Userrel/getVideoHistory/?ucode=" + getUcode()
				+ "&page=" + page;
	}

	/**
	 * 记录页面 删除操作
	 * 
	 * @return
	 */
	public static String getRecordDelPage() {
		return SEARCHURL + "Userrel/delVideoHistoryAll?ucode=" + getUcode();
	}

	/**
	 * 收藏
	 * 
	 * @return
	 */
	public static String getColection(int page) {
		if (page == 1) {
			return SEARCHURL + "User/getMyFav?ucode=" + getUcode();

		} else {
			return SEARCHURL + "User/getMyFav?ucode=" + getUcode() + "&page="
					+ page;

		}
	}

	/**
	 * d 删除收藏
	 * 
	 * @return
	 */
	// http://limaoso.cntttt.com/mapi/User/getMyFav/
	public static String getColectionDel(String action, String fav_ids) {

		return SEARCHURL + "User/delMyFav?ucode=" + getUcode() + "&action="
				+ action + "&ids=" + fav_ids;

	}

	/**
	 * app详情
	 * 
	 * @return
	 */
	public static String getFindDetail(String id) {

		return SOUSUO + "Index/findDetail?id=" + id;
	}

	public static String getRegister_go_register_active() {
		return JIAOYOUDOMAIN + "Public/register_go_register_active?ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 修改好友备注姓名
	 * 
	 * @return
	 */
	public static String setRemark_url() {
		return JIAOYOUDOMAIN + "Friends/goRemark?ucode=" + getUcode() + FMT;
	}

	/**
	 * 为你推荐界面
	 * 
	 * @return
	 */
	public static String getRecommendIndex() {
		// http: // www.limaoso.com/mapi.php/Index/getSpecials/
		return SEARCHURL + "Index/getSpecials?ucode=" + getUcode() + FMT;
	}

	/**
	 * 为你推荐详情页面
	 * 
	 * @param recid
	 * @return
	 */
	public static String getRecommendDetail(String recid) {
		return SEARCHURL + "Index/getSpecialData?id=" + recid + FMT;
	}

	/**
	 * 启动页面统计
	 * 
	 * @param recid
	 * @return
	 */
	public static String getSplashCount() {
		return "http://epost.cntttt.com:1010/act/userinfo.php?action=postinfo"
				+ FMT;
	}

	/**
	 * 获取动态影视集合
	 * 
	 * @return
	 */
	public static String getDTVList(int page) {
		return SEARCHURL + "Movie/getRecommendVideo?page=" + page + "&ucode="
				+ getUcode() + FMT;
	}

	// /**
	// * 获取动态影视集合
	// *
	// * @return
	// */
	// public static String getDTVListAll(int channelid, int page) {
	// return SEARCHURL + "video/getRecommendVideo?page=" + page
	// + "&channelid=" + channelid + "&ucode=" + getUcode() + FMT;
	// }

	/**
	 * 获取动态影视集合
	 * 
	 * @return
	 */
	public static String getDTVListAll(int channelid, int page) {
		return SEARCHURL + "video/getRecommendVideo2?page=" + page
				+ "&channelid=" + channelid + "&ucode=" + getUcode() + FMT;
	}

	// public static String getDTShortVList(int page) {
	// return SEARCHURL + "video/getRecommendVideo?page=" + page + "&ucode="
	// + getUcode() + FMT;
	// }

	/**
	 * 获取用户设置频道的url
	 * 
	 * @return
	 */
	public static String getTitle_tag() {
		return SEARCHURL + "Userintrest/getIntrestAll?ucode=" + getUcode()
				+ FMT;
	}

	// /**
	// * 获取小视频接口
	// *
	// * @return
	// */
	// public static String getRecommendVideo(String type, int page) {
	// return SEARCHURL + "Video/getRecommendVideo/?type" + type + "&page="
	// + page + "&ucode=" + getUcode() + FMT;
	// }

	/**
	 * 获取上传视频类型接口
	 * 
	 * @return
	 */
	public static String getVideoType() {
		return SEARCHURL + "Video/getVideoType/?ucode=" + getUcode() + FMT;
	}

	/**
	 * 获取上传视频接口
	 * 
	 * @return
	 */
	public static String getupFile() {
		return UPLOAD + "upFile.php";
	}

	/**
	 * 设置用户提交的标题
	 * 
	 * @return
	 */
	public static String setUserChannel() {
		return SEARCHURL + "Userintrest/setIntrest?ucode=" + getUcode() + FMT;
	}

	/**
	 * 获取首页栏目
	 * 
	 * @return
	 */
	public static String getUserIntrest() {
		return SEARCHURL + "Userintrest/getUserIntrestSSS?ucode=" + getUcode()
				+ FMT;
	}

	/**
	 * 设置用户兴趣选项数据
	 * 
	 * @return
	 */
	public static String getsetIntrest() {
		return SEARCHURL + "Userintrest/setIntrest?ucode=" + getUcode() + FMT;
	}

	/**
	 * 用户上传小视频回调
	 * 
	 * @return
	 */
	public static String getAddVideo() {
		return SEARCHURL + "video/addVideo?ucode=" + getUcode() + FMT;
	}

	/**
	 * 获取详细评论内容
	 * 
	 * @param mId
	 *            该条评论的id
	 * @param current_tag
	 *            1:获取主评论，2，获取分页子评论
	 * @param mPage
	 *            当前分页的子评论
	 * @return
	 */
	public static String getCommentURL(String mId, String type, int mPage) {
		return SEARCHURL + "Videodetail/getVideoCommentsDetail?ucode="
				+ getUcode() + "&type=" + type + "&commentid=" + mId + "&page="
				+ mPage + FMT;
	}

	/**
	 * 获取播放界面的一级评论
	 * 
	 * @param id
	 * @param mPage
	 * @return
	 */
	public static String getPlayCommentUrl(String id, int mPage) {
		return SEARCHURL + "Videodetail/getVideoComments?ucode=" + getUcode()
				+ "&ckuid=" + id + "&page=" + mPage + FMT;
	}

	/**
	 * 视频界面顶踩操作 1 顶，2踩
	 * 
	 * @param type
	 * @return
	 */
	public static String getUpOrDownUrl(String id, String type) {
		return SEARCHURL + "Videodetail/goDingCai?ucode=" + getUcode()
				+ "&ckuid=" + id + "&type=" + type + FMT;
	}

	/**
	 * 提交评论接口
	 * 
	 * @return
	 */
	public static String commentTextUrl() {
		return SEARCHURL + "Videodetail/addComments?ucode=" + getUcode() + FMT;
	}

	/**
	 * 获取发布的视频
	 * 
	 * @return
	 */
	public static String getUserVideo(String uid, int page) {

		return SEARCHURL + "video/getUserVideo?uid2=" + uid + "&ucode="
				+ getUcode() + FMT;
	}

	/**
	 * 删除发布的视频
	 * 
	 * @return
	 */
	public static String getUserVideoDel(String uid) {
		return SEARCHURL + "video/delVideo?ucode=" + getUcode() + "&vid=" + uid
				+ FMT;
	}

	/**
	 * 达人详情页面
	 * 
	 * @return
	 */
	public static String getDarenDeital(String uid) {
		return SEARCHURL + "daren/darenDeital?ucode=" + getUcode() + "&uid2="
				+ uid + FMT;

	}

	/**
	 * 达人详情页面加载更多
	 * 
	 * @return
	 */
	public static String getUserHotVideo(String uid, int page) {
		return SEARCHURL + "video/getUserHotVideo?ucode=" + getUcode()
				+ "&page=" + page + "&uid2=" + uid + FMT;

	}

	// /**
	// * 获取达人界面的网页地址
	// *
	// * @param page
	// * @param type
	// * @return
	// */
	// public static String getDr_URL(int page, int type) {
	// return SEARCHURL + "daren/darenlist?ucode=" + getUcode() + "&page="
	// + page + "&type=" + type + FMT;
	// }

	/**
	 * 获取达人界面的网页地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getDr_URL(int page, int type) {
		return SEARCHURL + "daren/darenlist2?ucode=" + getUcode() + "&page="
				+ page + "&type=" + type + FMT;
	}

	/**
	 * 获取达人界面的网页地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getdelfollows(String uid) {
		return SEARCHURL + "Userrel/delfollows?ucode=" + getUcode() + "&touid="
				+ uid + FMT;
	}

	/**
	 * 获取达人界面的网页地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getgoFollow(String uid) {
		return SEARCHURL + "Videodetail/goFollow/?ucode=" + getUcode()
				+ "&touid=" + uid + FMT;
	}

	/**
	 * 删除历史记录
	 * 
	 * @param string
	 * @return
	 */
	public static String getDelVideoHistory(String fileID) {
		return SEARCHURL + "Userrel/delVideoHistory?ucode=" + getUcode()
				+ "&id=" + fileID;
	}

	/**
	 * 获取达人界面的网页地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getfans(String uid, int page) {
		return SEARCHURL + "Userrel/getfans/?ucode=" + getUcode() + "&uid="
				+ uid + "&page=" + page + FMT;
	}

	/**
	 * 获取达人界面的网页地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getfollows(String uid, int page) {
		return SEARCHURL + "Userrel/getfollows/?ucode=" + getUcode() + "&uid="
				+ uid + "&page=" + page + FMT;
	}

	/**
	 * 获取达人界面的网页地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getfofans() {
		return SEARCHURL + "Userrel/getfofans?ucode=" + getUcode() + FMT;
	}

	/**
	 * 获取邀请好友发视频地址
	 * 
	 * @param page
	 * @param type
	 * @return
	 */
	public static String getInviteUserGo(String id) {
		return JIAOYOUDOMAIN + "Friends/inviteUserGo?ucode=" + getUcode()
				+ "&touid=" + id + FMT;
	}

	/**
	 * 转发当前视频
	 * 
	 * @param mVideoID
	 * @return
	 */
	public static String goZF(String id) {
		return SEARCHURL + "Videodetail/goZf?ucode=" + getUcode() + "&ckuid="
				+ id + FMT;
	}
}
