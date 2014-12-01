package net.liucs.jsondemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

public class JsonMessageAdapter extends BaseAdapter {

    private final int AVATAR_SIZE = 80;

    private JSONArray array;
    private Context context;
    private Map<String, Bitmap> avatarCache = new HashMap<String, Bitmap>();

    public JsonMessageAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return array.getJSONObject(position);
        } catch(JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.single_row, parent, false);
        final TextView dateText = (TextView) row.findViewById(R.id.dateText);
        final TextView messageText = (TextView) row.findViewById(R.id.messageText);
        final ImageView imageView = (ImageView) row.findViewById(R.id.imageView);
        try {
            JSONObject mesg = array.getJSONObject(position);
            dateText.setText(mesg.getString("timestamp"));
            messageText.setText(formatMessage(mesg));
            String sender = mesg.getString("sender");
            final String key = sender.equals("admin")? "league@contrapunctus.net" : sender;
            Bitmap bitmap = avatarCache.get(key);
            if(bitmap != null) imageView.setImageBitmap(bitmap);
            else {
                MessageDigest md = MessageDigest.getInstance("MD5");
                String hex = bytesToHex(md.digest(key.getBytes()));
                Log.i(MainActivity.TAG, "sender " + key + " md5 " + hex);
                Uri gravatar = Uri.parse(
                        "http://www.gravatar.com/avatar/" + hex +
                                "?d=retro&s=" + AVATAR_SIZE);
                new FetchImageTask(new AsyncListener<Bitmap, String>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        avatarCache.put(key, bitmap);
                        imageView.setImageBitmap(bitmap);
                    }

                    @Override
                    public void onFailure(String s) { }

                    @Override
                    public void onCancel() { }
                }).execute(gravatar);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return row;
    }


    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private String formatMessage(JSONObject mesg) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(mesg.getString("content"));
        sb.append(" (");
        sb.append(mesg.getString("sender"));
        sb.append(" → ");
        sb.append(mesg.getString("recipient"));
        sb.append(")");
        return sb.toString();
    }
}
