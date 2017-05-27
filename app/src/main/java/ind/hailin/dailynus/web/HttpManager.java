package ind.hailin.dailynus.web;

import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import ind.hailin.dailynus.utils.Constants;
import ind.hailin.dailynus.utils.DesEncryption;

/**
 * Created by hailin on 2017/5/27.
 */

public class HttpManager {

    public static void queryLogin(final Handler handler, final String urlString, final String responseBody){
        new Thread(new Runnable() {

            @Override
            public void run() {
                URL url = null;
                HttpURLConnection conn = null;
                Message message = Message.obtain();
                try {
                    url = new URL(urlString);
                    conn = (HttpURLConnection) url.openConnection();
                    MyHttpPost.setRequest(conn);
                    MyHttpPost.writeToServer(conn, DesEncryption.encryption(responseBody));

                    int code = conn.getResponseCode();
                    if(code == 200){
                        String result = MyHttpPost.readFromServer(conn);

                        if(result.equals("AuthenticationException")){
                            message.what = Constants.LOGIN_AUTHENTICATION_ERROR;
                        } else if(result.equals("LoginSuccess")){
                            message.what = Constants.LOGIN_SUCCESS;
                        } else {
                            message.what = Constants.LOGIN_EXCEPTION;
                        }
                    } else{
                        message.what = Constants.SERVER_ERROR;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    message.what = Constants.LOGIN_EXCEPTION;
                } catch (IOException e) {
                    e.printStackTrace();
                    message.what = Constants.LOGIN_EXCEPTION;
                } catch (Exception e){
                    e.printStackTrace();
                    message.what = Constants.LOGIN_EXCEPTION;
                } finally{
                    MyHttpPost.disconnect(conn);
                    handler.sendMessage(message);
                }
            }
        }).start();
    }
}
