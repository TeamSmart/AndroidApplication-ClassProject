package net.liucs.jsondemo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Pair;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;

public class FetchImageTask extends AsyncTask<Uri, Void, Bitmap> {

    private AsyncListener<Bitmap, String> listener;
    private String error;

    public FetchImageTask(AsyncListener<Bitmap, String> listener) {
        this.listener = listener;
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        try {
            URL url = new URL(params[0].toString());
            InputStream in = url.openStream();
            return BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if(null == bitmap) listener.onFailure(error);
        else listener.onSuccess(bitmap);
    }

    @Override
    protected void onCancelled() {
        listener.onCancel();
    }
}
