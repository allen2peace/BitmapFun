package adssdk.alddin.com.imagefun;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by bonan on 2018/9/29
 */

public class ImageGridFragment extends Fragment implements AdapterView.OnItemClickListener {

    ImageAdapter adapter;
    LruCacheManager lruCacheManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new ImageAdapter(getActivity());
        lruCacheManager = new LruCacheManager(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_grid_fragment, container, false);
        GridView mGridView = view.findViewById(R.id.gridview);

        mGridView.setAdapter(adapter);
        mGridView.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("tag", "onItemClick");
    }

    private class ImageAdapter extends BaseAdapter {

        Context mContext;

        ImageAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            return Images.imageThumbUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return Images.imageThumbUrls[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.layout_item, null);
                holder.imageView = convertView.findViewById(R.id.image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

//            Log.d("tag", "position= " + position);

            String thumbUrl = Images.imageThumbUrls[position];
            if (lruCacheManager.getBitmapFromMemCache(thumbUrl + position) != null) {
                Log.i("tag", "getView: Cache : " + position);
                holder.imageView.setImageBitmap(lruCacheManager.getBitmapFromMemCache(thumbUrl + position));
            } else {
                Log.i("tag", "getView: DownLoad: " + position);
                DownloadImageTask task = new DownloadImageTask(lruCacheManager, holder.imageView);
                holder.imageView.setImageDrawable(new AsynDrawable(getResources(),
                        BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher), task));
                task.execute(thumbUrl, position + "");
            }
            return convertView;
        }

    }

    class ViewHolder {
        ImageView imageView;
    }

}
