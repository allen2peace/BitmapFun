package adssdk.alddin.com.imagefun;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.util.Log;

/**
 * Created by bonan on 2018/9/30
 */

public class LruCacheManager {

    private LruCache<String, Bitmap> mLruCache;

    public LruCacheManager(Context context) {
        int cacheSizeInKb = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;
        int cache2 = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass() * 1024;
        Log.i("tag", "LruCacheManager: " + cacheSizeInKb + ", cache2= " + cache2);

        mLruCache = new LruCache<String, Bitmap>(cacheSizeInKb) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }

    public Bitmap getBitmapFromMemCache(String key) {
        Bitmap memVal = null;
        if (mLruCache != null) {
            memVal = mLruCache.get(key);
        }
        return memVal;
    }

    public void addBitmapToMemCache(Bitmap bitmap, String key) {
        if (mLruCache != null && getBitmapFromMemCache(key) == null) {
            if (key != null && bitmap != null) {
                mLruCache.put(key, bitmap);
            }
        }
    }


}
