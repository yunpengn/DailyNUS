package ind.hailin.dailynus.handler;

import android.os.Handler;
import android.os.Message;

import ind.hailin.dailynus.utils.Constants;

/**
 * Created by hailin on 2017/5/31.
 */

public abstract class UploadHandler extends Handler {

    @Override
    public void handleMessage(Message msg) {
        switch (msg.what){
            case Constants.UPLOAD_SUCCESS:
                success(msg);
                break;
            case Constants.UPLOAD_FAIL:
                failure(msg);
                break;
        }
    }

    public abstract void success(Message msg);
    public abstract void failure(Message msg);
}
