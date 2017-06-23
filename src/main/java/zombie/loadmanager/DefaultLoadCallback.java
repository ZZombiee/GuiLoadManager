package zombie.loadmanager;

import android.graphics.Bitmap;

/**
 * Created by guoshengsheng on 2017/6/15.
 */
public abstract class DefaultLoadCallback implements BaseLoadCallback {

    @Override
    public void loadExist(Bitmap[] bitmaps) {

    }

    @Override
    public void loadNext(Bitmap bitmap) {

    }

    @Override
    public void loadUnknowError(Throwable throwable) {

    }
}
