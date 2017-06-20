package zombie.loadmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by guoshengsheng on 2017/6/14.
 */
public class LoadTask {
    public static final String DEFAULT_LOAD_TASK_KEY = "default_load_task_key";
    private Context mContext;
    private static String GUI_PAGES_DIR_PATH;
    private static String GUI_ADVERTISEMENT_DIR_PATH;
    private static String HOME_ADVERTISEMENT_DIR_PATH;
    private static String FILE_PREFIX;
    private LoadType fileLoadType;
    private BaseLoadCallback callback;
    private String iconUrl;
    private String taskPath = "";
    private int rate = 1;
    private BitmapFactory.Options options;
    private Bitmap bitmap;
    private InputStream is;
    private URL url;

    public LoadTask(Context context) {
        mContext = context;
        GUI_PAGES_DIR_PATH = Environment.getExternalStorageDirectory() + "/" + mContext.getPackageName() + "/pages/";
        GUI_ADVERTISEMENT_DIR_PATH = Environment.getExternalStorageDirectory() + "/" + mContext.getPackageName() + "/guiAdvertiment/";
        HOME_ADVERTISEMENT_DIR_PATH = Environment.getExternalStorageDirectory() + "/" + mContext.getPackageName() + "/homeAdvertiment/";
        FILE_PREFIX = "page";
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

    public LoadType getType() {
        return fileLoadType == null ? LoadType.GUI_PAGES : fileLoadType;
    }

    public String getDirPath() {
        if (getType() == LoadType.GUI_ADVERTIMENT) {
            taskPath = GUI_ADVERTISEMENT_DIR_PATH;
        } else if (getType() == LoadType.GUI_PAGES) {
            taskPath = GUI_PAGES_DIR_PATH;
        } else if (getType() == LoadType.HOME_ADVERTISEMENT) {
            taskPath = HOME_ADVERTISEMENT_DIR_PATH;
        }
        return TextUtils.isEmpty(taskPath) ? GUI_PAGES_DIR_PATH : taskPath;
    }

    protected Context getContext() {
        return mContext;
    }

    public LoadTask setType(LoadType type) {
        fileLoadType = type;
        return this;
    }


    public LoadTask setDirPath(String dirPath) {
        GUI_PAGES_DIR_PATH = dirPath;
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
            if (bitmap == null) {
                byte[] data = readStream(is);
                if (data != null) {
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                }
            }
            File dir = new File(getDirPath());
            File file = new File(getDirPath(), FILE_PREFIX + "_" + System.currentTimeMillis() + ".png");
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

    private byte[] readStream(InputStream is) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        IOUtils.close(outStream);
        IOUtils.close(is);
        return outStream.toByteArray();
    }

    public BaseLoadCallback getCallback() {
        if (callback != null) {
            return callback;
        }
        return null;
    }

    public enum LoadType {
        GUI_ADVERTIMENT, GUI_PAGES, HOME_ADVERTISEMENT
    }

    public void setLoadCallback(BaseLoadCallback callback) {
        this.callback = callback;
    }
}
