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
    private GuiLoadConfig config;
    private List<Bitmap> defaultBitmaps = new ArrayList<>();
    private List<Bitmap> realBitmaps = new ArrayList<>();
    private String[] exeUrl;
    private int version = 1;
    private boolean needDownload = false;
    private boolean downloading = false;
    private boolean isGreenChannel = false;
    private boolean downloadComplete = false;
    private int pageCount = 0;
    private String GUI_PAGES_COUNT = "page_count";
    private String GUI_ADVERTISEMENT_COUNT = "gui_advertisement_count";
    private String HOME_ADVERTISEMENT_COUNT = "home_advertisement_count";
    private String countName;


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

    public GuiLoadManager loadUrl(String urls) {
        exeUrl = new String[1];
        exeUrl[0] = urls;
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

    public GuiLoadManager isGreenChannel() {
        isGreenChannel = true;
        return this;
    }

    public GuiLoadManager execute() {
        if (defaultBitmaps == null || defaultBitmaps.size() == 0) {
            if (config.getTask().getType() == LoadTask.LoadType.GUI_PAGES && !isGreenChannel)
                throw new RuntimeException("default must not be null or empty");
        }
//        realBitmaps = readDirPages().size() == 0 ? defaultBitmaps : realBitmaps;
        realBitmaps = readDirPages();
        needDownload = readDirPages().size() == 0 ? true : needDownload;
        if (needDownload && exeUrl != null && exeUrl.length != 0 && version > 0) {
            downloading = true;
            clearDirPages();
            executeTask();
        } else {
            LoadSpUtils.putBoolean(config.getContext(), "nextLoad", false);
        }
        return this;
    }

    private void executeTask() {
        LoadSpUtils.putInt(config.getContext(), getCountName(), exeUrl.length);
        final List<Bitmap> executeBitmap = new ArrayList<>();
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
                        downloadComplete = true;
                        if (config.getTask().getType() == LoadTask.LoadType.GUI_PAGES)
                            LoadSpUtils.putBoolean(UIUtils.getContext(), "nextLoad", true);
						if (callback != null)
                            callback.getResult(executeBitmap);
                        if (config.getTask().getCallback() != null)
                            config.getTask().getCallback().loadSuccess(executeBitmap);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (config.getTask().getCallback() != null)
                            config.getTask().getCallback().loadUnknowError(e);
                    }

                    @Override
                    public void onNext(Bitmap vo) {
                    	  executeBitmap.add(vo);
                        if (config.getTask().getCallback() != null)
                            config.getTask().getCallback().loadNext(vo);
                    }
                });
    }

    private Bitmap getBitmap(String s) {
        return null;
    }

    public List<Bitmap> get() {
        pageCount = LoadSpUtils.getInt(config.getContext(), getCountName(), 0);
        //正在下载未下载完，返回默认
        if (downloading && !downloadComplete) {
			if (callback != null)
                callback.getResult(defaultBitmaps);
            return defaultBitmaps;
        }
        //本地有图片
        if (realBitmaps != null && realBitmaps.size() > 0) {
            if (realBitmaps.size() == pageCount) {
				if (callback != null)
                    callback.getResult(realBitmaps);
                return realBitmaps;
            } else {
                //上次未下载完毕，清除残余文件
                clearDirPages();
            }
        }
		if (callback != null)
            callback.getResult(defaultBitmaps == null ? new ArrayList<Bitmap>() : defaultBitmaps);
        return defaultBitmaps == null ? new ArrayList<Bitmap>() : defaultBitmaps;
    }

    public GuiLoadManager setType(LoadTask.LoadType type) {
        config.getTask().setType(type);
        setCountName();
        return this;
    }

    private void setCountName() {
        countName = "";
        if (config.getTask().getType() == LoadTask.LoadType.GUI_PAGES) {
            countName = GUI_PAGES_COUNT;
        } else if (config.getTask().getType() == LoadTask.LoadType.HOME_ADVERTISEMENT) {
            countName = HOME_ADVERTISEMENT_COUNT;
        } else if (config.getTask().getType() == LoadTask.LoadType.GUI_ADVERTIMENT) {
            countName = GUI_ADVERTISEMENT_COUNT;
        } else {
            countName = "default";
        }
    }

    public String getCountName() {
        return countName;
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
	
	private ManagerCallback callback;

    public GuiLoadManager setResultCallback(ManagerCallback callback) {
        this.callback = callback;
        return this;
    }

    public interface ManagerCallback {
        void getResult(List<Bitmap> list);
    }
}
