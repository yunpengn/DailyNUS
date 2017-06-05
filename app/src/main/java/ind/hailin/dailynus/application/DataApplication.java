package ind.hailin.dailynus.application;

import android.app.Application;
import android.content.res.AssetManager;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.util.Properties;

import ind.hailin.dailynus.entity.Users;

/**
 * Created by hailin on 2017/5/27.
 */

public class DataApplication extends Application {

    private static DataApplication application;
    private AssetManager assetsManager;
    private Properties properties;

    private Users user;

    public DataApplication() {
        application = this;
        user = new Users();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        CookieManager cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        assetsManager = getAssets();
        properties = new Properties();
    }

    public String getProperties(String filename, String key) throws IOException {
        properties.clear();
        properties.load(assetsManager.open(filename));
        return properties.getProperty(key);
    }

    public AssetManager getAssetsManager() {
        return this.assetsManager;
    }

    public static DataApplication getApplication() {
        return application;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }
}
