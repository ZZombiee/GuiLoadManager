package zombie.loadmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by guoshengsheng on 2017/6/14.
 */
public class LoadTask {
    public static final String DEFAULT_LOAD_TASK_KEY = "default_load_task_key";
    private Context mContext;
    private static String DIR_PATH;
    private static String FILE_PREFIX;
    private static Suffix FILE_SUFFIX;
    private BaseLoadCallback callback;
    private String iconUrl;
    private int rate = 1;
    private BitmapFactory.Options options;
    private Bitmap bitmap;
    private InputStream is;
    private URL url;

    public LoadTask(Context context) {
        mContext = context;
        DIR_PATH = Environment.getExternalStorageDirectory() + "/" + mContext.getPackageName() + "/pages/";
        FILE_PREFIX = "page";
        FILE_SUFFIX = Suffix.NUMBER;
    }

    /**
     * 设置文件前缀
     */
    public LoadTask setPrefix(String prefix) {
        FILE_PREFIX = prefix;
        return this;
    }

    public String getPrefix() {
        return FILE_PREFIX;
    }

    public String getDirPath() {
        return DIR_PATH;
    }

    protected Context getContext() {
        return mContext;
    }

    /**
     * 设置文件后缀
     */
    public LoadTask setSuffix(Suffix suffix) {
        FILE_SUFFIX = suffix;
        return this;
    }

    public LoadTask setDirPath(String dirPath) {
        DIR_PATH = dirPath;
        return this;
    }

    public LoadTask setUrl(String url) {
        iconUrl = url;
        return this;
    }

    public LoadTask setRate(int rate) {
        this.rate = rate;
        return this;
    }

    public Bitmap execute() {
        if (iconUrl == null) {
            return null;
        }
        try {
            url = new URL(iconUrl);
            is = url.openStream();
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = rate;
            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(is, null, options);
            File dir = new File(DIR_PATH);
            File file = new File(DIR_PATH, FILE_PREFIX + "_" + System.currentTimeMillis() + ".png");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (Exception e) {

        }
        return bitmap;
    }

    public BaseLoadCallback getCallback() {
        if (callback != null) {
            return callback;
        }
        return null;
    }

    public enum Suffix {
        NUMBER, ENGLISH
    }

    public void setLoadCallback(BaseLoadCallback callback) {
        this.callback = callback;
    }
}
