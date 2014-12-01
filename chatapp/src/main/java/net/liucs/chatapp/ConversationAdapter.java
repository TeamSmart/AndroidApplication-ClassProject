package net.liucs.chatapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;

public class ConversationAdapter extends JSONArrayAdapter<Context> {

    private static final Logger log = LoggerFactory.getLogger(ConversationAdapter.class);
    private HashMap<String, Bitmap> imageCache = new HashMap<String,Bitmap>();

    public ConversationAdapter(Context context, JSONArray array) {
        super(context, array);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.item_conversation, parent, false);
        TextView tv = (TextView) v.findViewById(R.id.textView);
        final ImageView iv = (ImageView) v.findViewById(R.id.imageView);
        if(position == array.length()-1) {
            tv.setText("New…");
            return v;
        }
        JSONObject obj = getJSONObject(position);
        tv.setText(getJSONString(obj, "user"));
        final String avatar = getJSONString(obj, "avatar");
        if(null != avatar && !avatar.equals("null")) {
            Bitmap bm = imageCache.get(avatar);
            if(bm != null) {
                log.info("AVATAR MAP HIT.");
                iv.setImageBitmap(bm);
            }
            else {
                FetchImageTask.fetchGravatar(context, avatar).attach(new AsyncListener<Bitmap, String>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        imageCache.put(avatar, bitmap);
                        iv.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(String s) {

                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }
        }
        return v;
    }
}
