package ind.hailin.dailynus.web;

import android.support.annotation.NonNull;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

/**
 * Created by hailin on 2017/5/27.
 */

public class MyHttpMethods {
    /**
     * This class encapsulates http post request.
     */



    public static void setPostRequest(@NonNull HttpURLConnection conn) {
        setPostRequest(conn, 15000);
    }

    public static void setGetRequest(@NonNull HttpURLConnection conn){
        setGetRequest(conn, 15000);
    }

    public static void setPostRequest(@NonNull HttpURLConnection conn, int timeout){
        conn.setDoOutput(true);
        conn.setChunkedStreamingMode(0);
        conn.setConnectTimeout(timeout);
        conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
    }

    public static void setGetRequest(@NonNull HttpURLConnection conn, int timeout){
        conn.setConnectTimeout(timeout);
        conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
        try {
            conn.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
    }

    public static void writeToServer(@NonNull HttpURLConnection conn, @NonNull String str) throws IOException {
        writeToServer(conn, str.getBytes("utf-8"));
    }

    public static void writeToServer(@NonNull HttpURLConnection conn, @NonNull byte[] bytes) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(conn.getOutputStream());
        bufferedOutputStream.write(bytes);
        bufferedOutputStream.flush();
        bufferedOutputStream.close();
    }

    public static String readFromServer(@NonNull HttpURLConnection conn) throws IOException {
        String str = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        while((str = reader.readLine())!=null) {
            stringBuilder.append(str);
        }
        reader.close();
        return stringBuilder.toString();
    }

    public static void disconnect(HttpURLConnection conn){
        if(conn != null)
            conn.disconnect();
    }

}
