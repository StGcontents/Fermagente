package com.contents.stg.fermagente.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.contents.stg.fermagente.R;

import java.io.File;

public class FeedHolder extends RecyclerView.ViewHolder {

    private AsyncLoadImage task;
    private TextView commentView, placeView, timeView, starsView;
    private ImageView selfieView;

    public TextView getCommentView() {
        return commentView;
    }

    public TextView getPlaceView() { return placeView; }

    public TextView getTimeView() { return timeView; }

    public TextView getStarsView() { return starsView; }

    private void reset() {
        if (task != null)
            task.cancel(true);
        task = new AsyncLoadImage();
    }

    public void setImage(File file) {
        reset();
        System.out.println("FILE: " + file.getAbsolutePath());
        task.execute(file);
    }

    public FeedHolder(View itemView) {
        super(itemView);
        commentView = (TextView) itemView.findViewById(R.id.comment);
        placeView = (TextView) itemView.findViewById(R.id.place_text);
        timeView = (TextView) itemView.findViewById(R.id.time_text);
        starsView = (TextView) itemView.findViewById(R.id.star_text);

        selfieView = (ImageView) itemView.findViewById(R.id.image_selfie);
    }

    private class AsyncLoadImage extends AsyncTask<File, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(File... params) {
            if (params.length == 0)
                return null;

            System.out.println("INITIATE");

            File file = params[0];

            Bitmap bitmap = null;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(file.getAbsolutePath(), options);

                double width = options.outWidth, height = options.outHeight;
                DisplayMetrics metrics = FeedRecyclerAdapter.requestDisplayMetrics();
                int size;
                if (width > height)
                    size = (int) width / metrics.widthPixels;
                else
                    size = (int) height * 3 / metrics.heightPixels;
                options.inSampleSize = size;

                options.inJustDecodeBounds = false;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            }
            catch (Exception ignore) {
                System.out.println("WTF");
                ignore.printStackTrace();
            }

            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null)
                selfieView.setImageBitmap(bitmap);
        }
    }
}
