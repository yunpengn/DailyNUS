package ind.hailin.dailynus.web;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.exception.DesException;
import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.DesEncryption;
import ind.hailin.dailynus.utils.MyUtils;

/**
 * Created by hailin on 2017/5/27.
 * manage http request
 */

public class LoginManager {

    private Thread thread;
    private LoginRunnable loginRunnable;

    public LoginManager() {
    }

    public void queryLogin(Activity activity, Handler handler, String username, String password, boolean isEncrypted) {
        if (thread != null && thread.isAlive()) {
            return;
        }
        if (!MyUtils.isInternetConnected(activity)) {
            Message message = Message.obtain();
            message.what = Constants.NO_INTERNET_CONNECTION;
            handler.sendMessage(message);
            return;
        }

        try {
            if (isEncrypted) {
                password = DesEncryption.decryption(password);
            }
            final String requestBody = username + ":" + password;
            DataApplication application = (DataApplication) activity.getApplication();

            String urlString = application.getProperties("url.properties", "loginUrl");
            // Login
            queryLogin(handler, urlString, requestBody);
        } catch (IOException e) {
            e.printStackTrace();
            handleException(handler);
        } catch (DesException e) {
            e.printStackTrace();
            handleException(handler);
        }
    }

    public void queryLogin(Handler handler, String urlString, String requestBody) {
        loginRunnable = new LoginRunnable(handler, urlString, requestBody);
        thread = new Thread(loginRunnable);
        thread.start();
    }

    public void stop() {
        if (thread != null && thread.isAlive())
            MyHttpMethods.disconnect(loginRunnable.getConn());
    }

    private void handleException(Handler handler) {
        Message message = Message.obtain();
        message.what = Constants.LOGIN_EXCEPTION;
        handler.sendMessage(message);
    }

    private class LoginRunnable implements Runnable {

        private Handler handler;
        private String urlString;
        private String requestBody;

        private URL url;
        private HttpURLConnection conn;

        public LoginRunnable(Handler handler, String urlString, String requestBody) {
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
                MyHttpMethods.writeToServer(conn, DesEncryption.encryption(requestBody));

                int code = conn.getResponseCode();
                if (code == 200) {
                    String result = MyHttpMethods.readFromServer(conn);

                    if (result.equals("AuthenticationException")) {
                        message.what = Constants.LOGIN_AUTHENTICATION_ERROR;
                    } else if (result.equals("LoginSuccess")) {
                        message.what = Constants.LOGIN_SUCCESS;
                        message.obj = requestBody;
                    } else {
                        message.what = Constants.LOGIN_EXCEPTION;
                    }
                } else {
                    message.what = Constants.SERVER_ERROR;
                }
            } catch (IOException e) {
                e.printStackTrace();
                message.what = Constants.LOGIN_EXCEPTION;
            } catch (Exception e) {
                e.printStackTrace();
                message.what = Constants.LOGIN_EXCEPTION;
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
