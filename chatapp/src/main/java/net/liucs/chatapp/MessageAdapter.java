package net.liucs.chatapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MessageAdapter extends JSONArrayAdapter<MessagesActivity> {

    private String me;

    public MessageAdapter(MessagesActivity context, JSONArray array, String me) {
        super(context, array);
        this.me = me;
    }

    private static String reformatDate(String timestamp) {
        SimpleDateFormat jsonDateFmt = new SimpleDateFormat("yyyy-MM-dd;HH:mm:ss");
        SimpleDateFormat appDateFmt = new SimpleDateFormat("h:mm aa EEE MMMM d");
        try {
            Date d = jsonDateFmt.parse(timestamp.replace('T',';'));
            return appDateFmt.format(d);
        } catch(ParseException x) {
            x.printStackTrace();
            return x.getMessage();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        JSONObject obj = getJSONObject(position);
        String sender = getJSONString(obj, "sender");
        int layout = me.equalsIgnoreCase(sender)? R.layout.item_message_me : R.layout.item_message_you;
        View v = inflater.inflate(layout, parent, false);
        TextView dateText = (TextView) v.findViewById(R.id.dateText);
        dateText.setText(reformatDate(getJSONString(obj, "timestamp")));
        TextView contentText = (TextView) v.findViewById(R.id.contentText);
        contentText.setText(getJSONString(obj, "content"));
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        String avatar = getJSONString(obj, "avatar");
        context.loadAvatar(avatar, imageView);
        return v;
    }
}
