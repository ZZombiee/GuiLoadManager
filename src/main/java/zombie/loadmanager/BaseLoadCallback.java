package zombie.loadmanager;

import android.graphics.Bitmap;

/**
 * Created by guoshengsheng on 2017/6/14.
 */
public interface BaseLoadCallback {
    int SAME_PAGES_UNKNOW = 1001;
    int UPDATE_PAGES = 1002;
    int NULL_PAGES = 1003;
    int SAME_PAGES_EXIST = 1004;

    void loadSuccess(List<Bitmap> bitmapList);

    void loadUnknowError(Throwable throwable);

    void loadExist(Bitmap[] bitmaps);

    void loadNext(Bitmap bitmap);
}
