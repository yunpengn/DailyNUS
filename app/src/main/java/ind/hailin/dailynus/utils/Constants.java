package ind.hailin.dailynus.utils;

/**
 * Created by hailin on 2017/5/27.
 * This static class is used for storing all constants
 */

public class Constants {

    public static final int NO_INTERNET_CONNECTION = 1;

    /**
     * Constants for login process
     */
    public static final int LOGIN_SUCCESS = 13;
    public static final int LOGIN_AUTHENTICATION_ERROR = 14;
    public static final int LOGIN_EXCEPTION = 15;
    public static final int NO_REMEMBER_ME = 16;
    public static final int SERVER_ERROR = 17;

    /**
     * Constants for uploading file
     */
    public static final int UPLOAD_SUCCESS = 51;
    public static final int UPLOAD_FAIL = 52;

    /**
     * Constants for getting json (download file)
     */
    public static final int JSON_QUERY_SUCCESS = 113;
    public static final int NO_JSON_RETURN = 114;
    public static final int TARGET_SERVER_ERROR = 115;
    public static final int JSON_QUERY_EXCEPTION = 116;
    public static final int JSON_QUERY_URL_ERROR = 117;

    /**
     * Constants of RequestCode
     */
    public static final int REQUEST_USERINFO_TO_AVATAR = 213;
    public static final int REQUEST_HOME_TO_WEBVIEW = 214;
    public static final int REQUEST_HOME_TO_PROFILE = 215;

    /**
     * Constants of ResultCode
     */
    public static final int RESULT_AVATAR_TO_USERINFO = 313;
    public static final int RESULT_WEBVIEW_TO_HOME = 314;
    public static final int RESULT_PROFILE_TO_HOME_CHANGE = 315;

    /**
     * Constants of json query type
     */
    public static final int JSON_TYPE_FACULTY_MAJOR = 413;
    public static final int JSON_TYPE_ALL_USERSNAME = 414;
    public static final int JSON_TYPE_USER = 415;
    public static final int JSON_TYPE_GROUPS = 416;
    public static final int JSON_TYPE_MODULE_GROUP = 417;
    public static final int JSON_TYPE_NORMAL_GROUP = 418;
    public static final int JSON_TYPE_CHAT_DIALOG = 419;

    /**
     * Constants of upload query type
     */
    public static final int UPLOAD_TYPE_SIGNUP = 453;
    public static final int UPLOAD_TYPE_USER = 454;
    public static final int UPLOAD_TYPE_GROUPS = 455;
    public static final int UPLOAD_TYPE_MODULE_GROUP = 456;
    public static final int UPLOAD_TYPE_NORMAL_GROUP = 457;
    public static final int UPLOAD_TYPE_CHAT_DIALOG = 458;

    /**
     * Constants for pic cropping
     */
    public static final int CROP_AND_UPLOAD_SUCCESS = 501;
    public static final int CROP_NO_CHANGE = 502;

    /**
     * Constants for loading fragment actionCode
     */
    public static final int ACTION_DOWNLOAD = 601;
    public static final int ACTION_UPLOAD = 602;

    /**
     * Constants for receiver_type
     */
    public static final int RECEIVER_PERSON = 5;
    public static final int RECEIVER_NORMAL_GROUP = 6;
    public static final int RECEIVER_MODULE_GROUP = 7;

    /**
     * Constants for other purpose
     */
    public static final String[] gender_choice = new String[]{"male", "female"};
}
