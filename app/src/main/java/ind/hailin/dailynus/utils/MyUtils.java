package ind.hailin.dailynus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.Settings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.entity.ChatDialogues;
import ind.hailin.dailynus.entity.Users;

/**
 * Created by hailin on 2017/5/15.
 * A util class handling checking, timing and converting tasks.
 */

public class MyUtils {

    /**
     * check Internet connect
     */
    public static boolean isInternetConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * check date validity
     */
    public static boolean isDateValid(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        if (year > calendar.get(Calendar.YEAR))
            return false;
        if (year == calendar.get(Calendar.YEAR) && month > calendar.get(Calendar.MONTH))
            return false;
        if (year == calendar.get(Calendar.YEAR) && month == calendar.get(Calendar.MONTH) && dayOfMonth > calendar.get(Calendar.DAY_OF_MONTH))
            return false;
        return true;
    }

    /**
     * check whether cached file is expired
     */
    public static boolean isExpired(long fileModifiedTime) {
        long duration = System.currentTimeMillis() - fileModifiedTime;
        int expiredHour = 24;
        if (duration > 24 * 60 * 60 * 1000)
            return true;
        else
            return false;
    }

    /**
     * convert inputstream to string
     */
    public static String readStringFromInputStream(InputStream inputStream) throws IOException {
        String str = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while ((str = reader.readLine()) != null) {
            stringBuilder.append(str);
        }
        str = stringBuilder.toString();
        return str;
    }

    /**
     * convert Date into description_string
     * used in HomeMainAdapter.class
     */
    public static String convertDateToTime(Date date) {
        //TODO
        return "Sat";
    }


    /**
     * restructure chat dialogues list, store unread number in ChatDialogues.bak2
     */
    public static List<ChatDialogues> restructureList(List<ChatDialogues> list) {
        List<ChatDialogues> result = new ArrayList<>();
        List<Integer> senderList = new ArrayList<>();

        for (int i = list.size() - 1; i >= 0; i--) {
            int index = senderList.indexOf(list.get(i).getSenderId());
            if (index == -1) {
                ChatDialogues chatDialogues = list.get(i);
                chatDialogues.setBak2(1 + "");
                result.add(chatDialogues);
                senderList.add(chatDialogues.getSenderId());
            } else {
                ChatDialogues chatDialogues = result.get(index);
                chatDialogues.setBak2((Integer.parseInt(chatDialogues.getBak2()) + 1) + "");
                result.set(index, chatDialogues);
            }
        }
        return result;
    }

    /**
     * get the map of sender's id and username (in order to check and cache their avatar)
     */
    public static Map<Integer, String> getSenderId(List<ChatDialogues> list) {
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            if (!map.containsKey(list.get(i).getSenderId())) {
                map.put(list.get(i).getSenderId(), list.get(i).getBak1());
            }
        }
        return map;
    }

}
