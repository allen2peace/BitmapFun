package adssdk.alddin.com.imagefun;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import java.lang.ref.WeakReference;

/**
 * Created by bonan on 2018/9/29
 */

public class AsynDrawable extends BitmapDrawable {

    private WeakReference<DownloadImageTask> taskWeakReference;

    public AsynDrawable(Resources resources, Bitmap bitmap, DownloadImageTask task) {
        super(resources, bitmap);
        taskWeakReference = new WeakReference<DownloadImageTask>(task);
    }

    public DownloadImageTask getDownloadTask() {
        return taskWeakReference.get();
    }
}
