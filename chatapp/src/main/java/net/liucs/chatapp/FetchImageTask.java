package net.liucs.chatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import org.apache.http.client.methods.HttpGet;
import java.io.InputStream;

public class FetchImageTask implements Function1<InputStream, Bitmap> {

    public static final int GRAVATAR_SIZE = 64;
    public static final String GRAVATAR_DEFAULT = "monsterid";

    @Override
    public Bitmap apply(InputStream is) throws Exception {
        return BitmapFactory.decodeStream(is);
    }

    private static FetchImageTask instance = new FetchImageTask();

    public static FetchUriTask<Bitmap> newInstance() {
        return new FetchUriTask<Bitmap>(instance);
    }

    public static FetchUriTask<Bitmap> fetchGravatar(Context context, String uri) {
        FetchUriTask<Bitmap> ft = newInstance();
        float d = context.getResources().getDisplayMetrics().density;
        int sz = (int) (d * 64);
        uri = uri + "?d=" + GRAVATAR_DEFAULT + "&s=" + sz;
        HttpGet get = new HttpGet(uri);
        ft.execute(get);
        return ft;
    }
}
