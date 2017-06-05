package ind.hailin.dailynus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by hailin on 2017/5/30.
 */

public class MyPicUtils {

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
     * get bitmap from android.net.Uri
     */
    public static Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        return MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
    }

    /**
     * get bitmap from outputStream
     */
    public static Bitmap getBitmapFromOutputStream(ByteArrayOutputStream outputStream){
        return BitmapFactory.decodeByteArray(outputStream.toByteArray(), 0, outputStream.size());
    }

    /**
     * scale bitmap for caching
     */
    public static Bitmap scaleBitmapForCache(Bitmap bm) {
        return getResizedBitmap(bm, 348, 348, true);
    }

    /**
     * scale bitmap for avatar
     */
//    public static Bitmap scaleBitmapForAvatar(Bitmap bm){
//        return getResizedBitmap(bm, 72, 72);
//    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight, boolean isRecycle) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        if (isRecycle) bm.recycle();
        return resizedBitmap;
    }

    /**
     * convert bitmap to byteArrayOutputStream
     */
    public static ByteArrayOutputStream convertBitmapToOutputStream(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream;
    }

    /**
     * crop bitmap to be circular
     */

    public static Bitmap getCroppedBitmap(Bitmap bitmap){
        Bitmap output = cropBitmap(bitmap);
        bitmap.recycle();
        return output;
    }

    private static Bitmap cropBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), bitmap.getConfig());
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

}
