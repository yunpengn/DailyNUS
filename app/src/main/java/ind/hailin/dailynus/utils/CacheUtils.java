package ind.hailin.dailynus.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.application.DataApplication;
import ind.hailin.dailynus.entity.ChatDialogues;
import ind.hailin.dailynus.entity.Groups;
import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.exception.DataExpiredException;

/**
 * Created by hailin on 2017/5/30.
 * This is a util class for data caching
 */

public class CacheUtils {

    /**
     * cache someone (id) 's avatar
     */
    public static void cacheAvatarById(Context context, int id, byte[] data) throws IOException {
        File file = new File(context.getCacheDir(), id + "_avatar");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    /**
     * cache chat dialogues list
     */
    public static void cacheChatDialogues(Context context, List<ChatDialogues> list) throws IOException{
        Users user = DataApplication.getApplication().getUser();
        File file = new File(context.getCacheDir(), "List_ChatDialogues_" + user.getId());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
        objectOutputStream.writeObject(list);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /**
     * cache group Map
     */
    public static void cacheGroupMap(Context context, Map<String, List<Groups>> map) throws IOException{
        Users user = DataApplication.getApplication().getUser();
        File file = new File(context.getCacheDir(), "Map_Group_" + user.getId());
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream(file));
        objectOutputStream.writeObject(map);
        objectOutputStream.flush();
        objectOutputStream.close();
    }

    /**
     * get avatar (348*348) from cache
     */
    public static Bitmap getAvatarFromCache(Context context) throws FileNotFoundException {
        Users user = DataApplication.getApplication().getUser();
        File file = new File(context.getCacheDir(), user.getId() + "_avatar");
        FileInputStream fileInputStream = new FileInputStream(file);
        return BitmapFactory.decodeStream(fileInputStream);
    }

    /**
     * get someone (id) 's avatar (348*348) from cache
     */
    public static Bitmap getAvatarFromCacheById(Context context, int id) throws FileNotFoundException {
        File file = new File(context.getCacheDir(), id + "_avatar");
        FileInputStream fileInputStream = new FileInputStream(file);
        return BitmapFactory.decodeStream(fileInputStream);
    }

    /**
     * get chat dialogues from cache
     */
    public static List<ChatDialogues> getChatDialoguesFromCache(Context context) throws IOException, ClassNotFoundException, DataExpiredException {
        Users user = DataApplication.getApplication().getUser();
        File file = new File(context.getCacheDir(), "List_ChatDialogues_" + user.getId());
        if (MyUtils.isExpired(file.lastModified())){
            throw new DataExpiredException();
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
        List<ChatDialogues> list = (List<ChatDialogues>) objectInputStream.readObject();
        objectInputStream.close();
        return list;
    }

    /**
     * get group map from cache
     */
    public static Map<String, List<Groups>> getGroupMapFromCache(Context context) throws IOException, ClassNotFoundException, DataExpiredException {
        Users user = DataApplication.getApplication().getUser();
        File file = new File(context.getCacheDir(), "Map_Group_" + user.getId());
        if (MyUtils.isExpired(file.lastModified())){
            throw new DataExpiredException();
        }
        ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream(file));
        Map<String, List<Groups>> map = (Map<String, List<Groups>>) objectInputStream.readObject();
        objectInputStream.close();
        return map;
    }

}
