package ind.hailin.dailynus.web;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

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

    private Thread thread;
    private DownloadRunnable downloadRunnable;
    private String propertyName;
    private int timeout;

    public QueryJsonManager(int timeout, int type){
        this.timeout = timeout;
        switch (type){
            case Constants.JSON_TYPE_FACULTY_MAJOR:
                propertyName = "facultyDepartments.json";
                break;
        }
    }

    public void queryFacultyDepartments(Activity activity, Handler handler){
        if (!MyUtils.isInternetConnected(activity)) {
            Message message = Message.obtain();
            message.what = Constants.NO_INTERNET_CONNECTION;
            handler.sendMessage(message);
            return ;
        }

        try {
            DataApplication application = (DataApplication) activity.getApplication();
            String urlString = application.getProperties("url.properties", propertyName);

            downloadRunnable = new DownloadRunnable(handler, urlString);
            thread = new Thread(downloadRunnable);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stop(){
        if(thread != null && thread.isAlive())
            MyHttpMethods.disconnect(downloadRunnable.getConn());
    }

    private class DownloadRunnable implements Runnable{

        private Handler handler;
        private String urlString;

        private URL url;
        private HttpURLConnection conn;

        public DownloadRunnable(Handler handler, String urlString){
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
                    InputStream jsonStream = conn.getInputStream();

                    if (jsonStream != null){
                        Map<String, List<String>> facultyMajorMap = MyJsonParsers.getFacultyMajor(jsonStream);
                        if (facultyMajorMap != null){
                            message.obj = facultyMajorMap;
                            message.what = Constants.JSON_QUERY_SUCCESS;
                        } else {
                            message.what = Constants.NO_JSON_RETURN;
                        }
                        jsonStream.close();
                    } else{
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
            } catch (Exception e){
                e.printStackTrace();
                message.what = Constants.JSON_QUERY_EXCEPTION;
            }finally {
                MyHttpMethods.disconnect(conn);
                handler.sendMessage(message);
            }
        }

        public HttpURLConnection getConn(){
            return conn;
        }
    }
}
