package ind.hailin.dailynus.handler;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.utils.Constants;

/**
 * Created by hailin on 2017/5/31.
 */

public abstract class QueryJsonHandler extends Handler{

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case Constants.JSON_QUERY_SUCCESS:
                success(msg);
                break;
            case Constants.NO_INTERNET_CONNECTION:
            case Constants.NO_JSON_RETURN:
            case Constants.TARGET_SERVER_ERROR:
            case Constants.JSON_QUERY_EXCEPTION:
            case Constants.JSON_QUERY_URL_ERROR:
                failure(msg);
                break;
        }
    }

    public abstract void success(Message msg);
    public abstract void failure(Message msg);
}
