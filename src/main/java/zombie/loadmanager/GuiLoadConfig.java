package zombie.loadmanager;

import android.content.Context;

/**
 * Created by guoshengsheng on 2017/6/14.
 */
public class GuiLoadConfig {
    private LoadTask task;
    private int rate = 1;

    private GuiLoadConfig(Builder builder) {
        this.task = builder.task;
        this.rate = builder.rate;
    }

    public LoadTask getTask() {
        return this.task;
    }

    public Context getContext() {
        return this.task.getContext();
    }

    public static class Builder {
        private LoadTask task;
        private int rate = 1;

        public Builder(Context context) {
            task = new LoadTask(context);
        }

        public Builder setSuffix(LoadTask.Suffix suffix) {
            if (task != null)
                task.setSuffix(suffix);
            return this;
        }

        public Builder setPrefix(String prefix) {
            if (task != null)
                task.setPrefix(prefix);
            return this;
        }

        public Builder setDirPath(String dirPath) {
            if (task != null)
                task.setDirPath(dirPath);
            return this;
        }

        public Builder setRate(int rate) {
            if (task != null)
                task.setRate(rate);
            return this;
        }

        public GuiLoadConfig build() {
            return new GuiLoadConfig(this);
        }
    }
}
