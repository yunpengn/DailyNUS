package ind.hailin.dailynus.web;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.exception.DesException;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.DesEncryption;
import ind.hailin.dailynus.utils.MyUtils;

/**
 * Created by hailin on 2017/5/30.
 * manage file upload
 */

public class UploadManager {
    public static final String TAG = "UploadManager";

    private Thread thread;
    private UploadRunnable uploadRunnable;
    private String propertyName;

    public UploadManager(int type) {
        switch (type) {
            case Constants.UPLOAD_TYPE_SIGNUP:
                propertyName = "signupUrl";
                break;
            case Constants.UPLOAD_TYPE_USER:
                propertyName = "users";
                break;
            case Constants.UPLOAD_TYPE_GROUPS:
                propertyName = "groups";
                break;
            case Constants.UPLOAD_TYPE_MODULE_GROUP:
                propertyName = "moduleGroup";
                break;
            case Constants.UPLOAD_TYPE_NORMAL_GROUP:
                propertyName = "normalGroup";
                break;
            case Constants.UPLOAD_TYPE_CHAT_DIALOG:
                propertyName = "chatDialog";
                break;
        }
    }

    public void queryUploadStream(Activity activity, Handler handler, String username, String filename, ByteArrayOutputStream outputStream) {
        try {
            String requestBody = android.util.Base64.encodeToString(outputStream.toByteArray(), android.util.Base64.DEFAULT);

            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);
            urlString = urlString + username + "/" + filename;

            doUpload(activity, handler, urlString, requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = Constants.UPLOAD_FAIL;
            handler.sendMessage(message);
        }
    }

    public void queryUploadObject(Activity activity, Handler handler, String username, Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(object);
            Log.d(TAG, "requestBody: " + requestBody);
            requestBody = new String(DesEncryption.encryption(requestBody), "utf-8");

            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);
            urlString = urlString + username;

            doUpload(activity, handler, urlString, requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = Constants.UPLOAD_FAIL;
            handler.sendMessage(message);
        }
    }

    public void queryUploadObject(Activity activity, Handler handler, Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String requestBody = objectMapper.writeValueAsString(object);
            Log.d(TAG, "requestBody: " + requestBody);
            requestBody = new String(DesEncryption.encryption(requestBody), "utf-8");

            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);

            doUpload(activity, handler, urlString, requestBody);
        } catch (Exception e) {
            e.printStackTrace();
            Message message = Message.obtain();
            message.what = Constants.UPLOAD_FAIL;
            handler.sendMessage(message);
        }
    }

    private void doUpload(Activity activity, Handler handler, String urlString, String requestBody) {
        if (thread != null && thread.isAlive()) {
            return;
        }
        if (!MyUtils.isInternetConnected(activity)) {
            Message message = Message.obtain();
            message.what = Constants.NO_INTERNET_CONNECTION;
            handler.sendMessage(message);
            return;
        }

        Log.d(TAG, "start Thread, url=" + urlString);
        uploadRunnable = new UploadRunnable(handler, urlString, requestBody);
        thread = new Thread(uploadRunnable);
        thread.start();
    }

    public void stop() {
        if (thread != null && thread.isAlive())
            MyHttpMethods.disconnect(uploadRunnable.getConn());
    }

    private class UploadRunnable implements Runnable {

        private Handler handler;
        private String urlString;
        private String requestBody;

        private URL url;
        private HttpURLConnection conn;

        public UploadRunnable(Handler handler, String urlString, String requestBody) {
            this.handler = handler;
            this.urlString = urlString;
            this.requestBody = requestBody;
        }

        @Override
        public void run() {
            Message message = Message.obtain();
            try {
                url = new URL(urlString);
                conn = (HttpURLConnection) url.openConnection();
                MyHttpMethods.setPostRequest(conn);
                MyHttpMethods.writeToServer(conn, requestBody);

                int code = conn.getResponseCode();
                Log.d(TAG, "ResponseCode: "+code);
                if (code == 200) {
                    String result = MyHttpMethods.readFromServer(conn);
                    Log.d(TAG, "result: "+result);
                    if (result.equals("UploadSuccess")) {
                        message.what = Constants.UPLOAD_SUCCESS;
                    } else {
                        message.what = Constants.UPLOAD_FAIL;
                    }
                } else {
                    message.what = Constants.UPLOAD_FAIL;
                }
            } catch (IOException e) {
                e.printStackTrace();
                message.what = Constants.UPLOAD_FAIL;
            } catch (Exception e) {
                e.printStackTrace();
                message.what = Constants.UPLOAD_FAIL;
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
