package ind.hailin.dailynus.web;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.MyJsonParsers;
import ind.hailin.dailynus.utils.MyUtils;

/**
 * Created by hailin on 2017/5/29.
 */

public class QueryJsonManager {
    public static final String TAG = "QueryJsonManager";

    private Thread thread;
    private DownloadRunnable downloadRunnable;
    private String propertyName;
    private int timeout;
    private int arg1 = -1;

    public QueryJsonManager(int timeout, int type) {
        this.timeout = timeout;
        switch (type) {
            case Constants.JSON_TYPE_FACULTY_MAJOR:
                propertyName = "facultyDepartments.json";
                break;
            case Constants.JSON_TYPE_ALL_USERSNAME:
                propertyName = "allUsersName.json";
                break;
            case Constants.JSON_TYPE_USER:
                propertyName = "users";
                break;
            case Constants.JSON_TYPE_GROUPS:
                propertyName = "groups";
                break;
            case Constants.JSON_TYPE_MODULE_GROUP:
                propertyName = "moduleGroup";
                break;
            case Constants.JSON_TYPE_NORMAL_GROUP:
                propertyName = "normalGroup";
                break;
            case Constants.JSON_TYPE_CHAT_DIALOG:
                propertyName = "chatDialog";
                break;
        }
    }

    public QueryJsonManager(int timeout, int type, int arg1){
        this(timeout, type);
        this.arg1 = arg1;
    }

    public void query(Activity activity, Handler handler) {
        try {
            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);

            doDownload(activity, handler, urlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void query(Activity activity, Handler handler, String username){
        try {
            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);
            urlString = urlString + username;

            doDownload(activity, handler, urlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void query(Activity activity, Handler handler, String username, String filename){
        try {
            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);
            urlString = urlString + username + "/" + filename;

            doDownload(activity, handler, urlString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doDownload(Activity activity, Handler handler, String urlString){
        if (thread != null && thread.isAlive()) {
            return;
        }
        if (!MyUtils.isInternetConnected(activity)) {
            Message message = Message.obtain();
            message.what = Constants.NO_INTERNET_CONNECTION;
            handler.sendMessage(message);
            return;
        }
        Log.d(TAG, urlString);
        downloadRunnable = new DownloadRunnable(handler, urlString);
        thread = new Thread(downloadRunnable);
        thread.start();

    }

    public void stop() {
        if (thread != null && thread.isAlive())
            MyHttpMethods.disconnect(downloadRunnable.getConn());
    }

    private class DownloadRunnable implements Runnable {

        private Handler handler;
        private String urlString;

        private URL url;
        private HttpURLConnection conn;

        public DownloadRunnable(Handler handler, String urlString) {
            this.handler = handler;
            this.urlString = urlString;
        }

        @Override
        public void run() {
            Message message = Message.obtain();
            try {
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                MyHttpMethods.setGetRequest(conn, timeout);

                int code = conn.getResponseCode();
                if (code == 200) {
                    InputStream inputStream = conn.getInputStream();

                    if (inputStream != null) {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] buffer = new byte[1024];
                        int len = -1;
                        while ((len = inputStream.read(buffer)) != -1) {
                            baos.write(buffer, 0, len);
                        }
                        baos.flush();

                        message.obj = new ByteArrayInputStream(baos.toByteArray());
                        message.what = Constants.JSON_QUERY_SUCCESS;
                        message.arg1 = arg1;

                        inputStream.close();
                    } else {
                        message.what = Constants.NO_JSON_RETURN;
                    }
                } else {
                    message.what = Constants.TARGET_SERVER_ERROR;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                message.what = Constants.JSON_QUERY_URL_ERROR;
            } catch (IOException e) {
                e.printStackTrace();
                message.what = Constants.JSON_QUERY_EXCEPTION;
            } catch (Exception e) {
                e.printStackTrace();
                message.what = Constants.JSON_QUERY_EXCEPTION;
            } finally {
                MyHttpMethods.disconnect(conn);
                handler.sendMessage(message);
            }
        }

        public HttpURLConnection getConn() {
            return conn;
        }
    }
}
