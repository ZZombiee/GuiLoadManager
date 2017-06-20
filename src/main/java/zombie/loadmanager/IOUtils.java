package zombie.loadmanager;

import java.io.Closeable;

/**
 * Created by guoshengsheng on 2017/6/20.
 */
public class IOUtils {
    public static void close(Closeable c) {
        try {
            if (c!=null){
                c.close();
                c = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
