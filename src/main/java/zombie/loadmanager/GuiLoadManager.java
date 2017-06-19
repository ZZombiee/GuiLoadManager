package zombie.loadmanager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


/**
 * Created by guoshengsheng on 2017/6/14.
 */
public class GuiLoadManager {
    private static GuiLoadConfig config;
    private static GuiLoadManager loadManager;
    private List<Bitmap> defaultBitmaps;
    private List<Bitmap> realBitmaps;
    private String[] exeUrl;
    private int version = -1;
    private boolean needDownload = false;
    private boolean downloading = false;

    public static GuiLoadManager getInstance() {
        return loadManager == null ? new GuiLoadManager() : loadManager;
    }

    public GuiLoadManager init(GuiLoadConfig config) {
        this.config = config;
        return this;
    }

    public GuiLoadManager setDefaultPages(List<Bitmap> bitmaps) {
        this.defaultBitmaps = bitmaps;
        return this;
    }

    public GuiLoadManager loadUrl(String[] urls) {
        exeUrl = urls;
        return this;
    }

    private void clearDirPages() {
        File dir = new File(config.getTask().getDirPath());
        if (dir.exists() && dir.isDirectory() && dir.listFiles().length > 0) {
            for (File file : dir.listFiles()) {
                file.delete();
            }
            dir.delete();
        }
    }

    public GuiLoadManager execute() {
        if (defaultBitmaps == null || defaultBitmaps.size() == 0) {
            throw new RuntimeException("default must not be null or empty");
        }
        realBitmaps = readDirPages().size() == 0 ? defaultBitmaps : realBitmaps;
        needDownload = readDirPages().size() == 0 ? true : needDownload;
        if (needDownload && exeUrl != null && exeUrl.length != 0 && version > 0) {
            clearDirPages();
            executeTask();
        }
        return this;
    }

    private void executeTask() {
        downloading = true;
        Observable.from(exeUrl)
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        return config.getTask().setUrl(s).execute();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnTerminate(new Action0() {
                    @Override
                    public void call() {
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                        if (config.getTask().getCallback() != null)
                            config.getTask().getCallback().loadSuccess();
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (config.getTask().getCallback() != null)
                            config.getTask().getCallback().loadUnknowError(e);
                    }

                    @Override
                    public void onNext(Bitmap vo) {
                    }
                });
    }

    private Bitmap getBitmap(String s) {
        return null;
    }

    public List<Bitmap> get() {
        if (realBitmaps != null && realBitmaps.size() > 0) {
            return realBitmaps;
        }
        return defaultBitmaps;
    }

    public GuiLoadManager setVersion(int version) {
        needDownload = version != LoadSpUtils.getInt(config.getContext(), "version", -1);
        this.version = version;
        LoadSpUtils.putInt(config.getContext(), "version", version);
        return this;
    }

    public GuiLoadManager setCallback(BaseLoadCallback callback) {
        config.getTask().setLoadCallback(callback);
        return this;
    }

    private List<Bitmap> readDirPages() {
        realBitmaps = new ArrayList<>();
        File dir = new File(config.getTask().getDirPath());
        if (dir.exists() && dir.listFiles().length > 0) {
            for (File file : dir.listFiles()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                realBitmaps.add(bitmap);
            }
        }
        return realBitmaps;
    }
}
