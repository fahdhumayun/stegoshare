// By Guy Rubinstein

package com.example.fahd.stegoshare;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import java.util.ArrayList;

/**
 * modified by guyrubinstein on 12/5/17.
 */

public class ImageAdapter extends BaseAdapter {
    private boolean[] thumbnailsselection;
    private int ids[];
    private ArrayList<String> paths;
    private int count;
    private LayoutInflater mInflater;
    private Context activityContext;

    public ImageAdapter(Context context, boolean[] thumbNailSelection, int[] IDs, int Count) {
        activityContext = context;
        thumbnailsselection = thumbNailSelection;
        ids = IDs;
        count = Count;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ImageAdapter(Context context, boolean[] thumbNailSelection, ArrayList<String> Paths, int Count) {
        activityContext = context;
        thumbnailsselection = thumbNailSelection;
        count = Count;
        paths = Paths;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return count;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ImageAdapter.ViewHolder holder;
        if (convertView == null) {
            holder = new ImageAdapter.ViewHolder();
            convertView = mInflater.inflate(R.layout.custom_gallery_item, null);
            holder.imgThumb = convertView.findViewById(R.id.imgThumb);
            holder.chkImage = convertView.findViewById(R.id.chkImage);

            convertView.setTag(holder);
        } else {
            holder = (ImageAdapter.ViewHolder) convertView.getTag();
        }
        holder.chkImage.setId(position);
        holder.imgThumb.setId(position);
        holder.chkImage.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                int id = cb.getId();
                if (thumbnailsselection[id]) {
                    cb.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    cb.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });
        holder.imgThumb.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                int id = holder.chkImage.getId();
                if (thumbnailsselection[id]) {
                    holder.chkImage.setChecked(false);
                    thumbnailsselection[id] = false;
                } else {
                    holder.chkImage.setChecked(true);
                    thumbnailsselection[id] = true;
                }
            }
        });
        try {
            if (ids != null){
                setBitmap(holder.imgThumb, ids[position]);
            }
            else{
                setBitmap(holder.imgThumb, paths.get(position));
            }
        } catch (Throwable e) {
            Log.e("ImageAdapter", "getView: ", e.fillInStackTrace());
        }
        holder.chkImage.setChecked(thumbnailsselection[position]);
        holder.id = position;
        return convertView;
    }

    /**
     * Class method
     **/

    /**
     * This method used to set bitmap.
     * @param iv represented ImageView
     * @param id represented id
     */

    private void setBitmap(final ImageView iv, final int id) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return MediaStore.Images.Thumbnails.getThumbnail(activityContext.getApplicationContext().getContentResolver(), id, MediaStore.Images.Thumbnails.MICRO_KIND, null);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }

    private void setBitmap(final ImageView iv, final String path) {

        new AsyncTask<Void, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(Void... params) {
                return BitmapFactory.decodeFile(path);
            }

            @Override
            protected void onPostExecute(Bitmap result) {
                super.onPostExecute(result);
                iv.setImageBitmap(result);
            }
        }.execute();
    }

    /**
     * Inner class
     * @author tasol
     */
    class ViewHolder {
        ImageView imgThumb;
        CheckBox chkImage;
        int id;
    }
}


