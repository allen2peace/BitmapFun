package adssdk.alddin.com.imagefun;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by bonan on 2018/9/29
 */

public class DownloadImageTask extends AsyncTask<String, Integer, Bitmap> {

    private WeakReference<ImageView> imageViewWeakReference;
    private LruCacheManager lruCacheManager;
    private String imageUrl, position;

    public DownloadImageTask(LruCacheManager lruCacheManager, ImageView imageView) {
        imageViewWeakReference = new WeakReference<ImageView>(imageView);
        this.lruCacheManager = lruCacheManager;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        imageUrl = strings[0];
        position = strings[1];
        return downloadBitmap(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        ImageView imageView = getAttachedImageView();
        if (imageView != null) {
            imageView.setImageBitmap(bitmap);
        }
        lruCacheManager.addBitmapToMemCache(bitmap, imageUrl + position);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
//        Log.d("tag", "进度：" + values);
    }

    private ImageView getAttachedImageView() {
        ImageView imageView = imageViewWeakReference.get();

        DownloadImageTask curTask = getCurDownloadTask(imageView);

        if (curTask == this) {
//            Log.d("tag", "curTask == this");
            return imageView;
        } else {
//            Log.d("tag", "curTask 不等于 this");
        }
        return null;
    }

    private DownloadImageTask getCurDownloadTask(ImageView imageView) {

        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsynDrawable) {
                return ((AsynDrawable) drawable).getDownloadTask();
            }
        }
        return null;
    }

    public Bitmap getNetWorkBitmap(String urlString) {
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(urlString);
            HttpURLConnection urlConn = (HttpURLConnection) imgUrl.openConnection();
            urlConn.setDoInput(true);
            urlConn.connect();
            InputStream is = urlConn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

            is.close();
        } catch (MalformedURLException e) {
            System.out.println("[getNetWorkBitmap->]MalformedURLException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("[getNetWorkBitmap->]IOException");
            e.printStackTrace();
        }
        return bitmap;
    }

    public Bitmap downloadBitmap(String urlStr) {

        HttpURLConnection connection = null;

        Bitmap bitmap = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();


            InputStream in = new BufferedInputStream(connection.getInputStream());

            bitmap = BitmapFactory.decodeStream(in);

            //获取文件流大小，用于更新进度
            int length = connection.getContentLength();
            int len = 0, total_length = 0, value = 0;
            byte[] data = new byte[1024];

            while ((len = in.read(data)) != -1) {
                total_length += len;
                value = (int) ((total_length / (float) length) * 100);
//                Log.d("tag", "value= " + value);
                //调用update函数，更新进度
                publishProgress(value);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }
        return bitmap;
    }

}
