package ind.hailin.dailynus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;

/**
 * Created by hailin on 2017/5/15.
 */

public class MyUtils {
    /**
     * convert drawable to bitmap
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }

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

}
