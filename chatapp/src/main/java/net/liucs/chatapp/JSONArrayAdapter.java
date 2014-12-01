package net.liucs.chatapp;

import android.widget.BaseAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JSONArrayAdapter<Context> extends BaseAdapter {

    protected Context context;
    protected JSONArray array;

    public JSONArrayAdapter(Context context, JSONArray array) {
        this.context = context;
        this.array = array;
    }

    public void setArray(JSONArray array) {
        this.array = array;
    }

    protected JSONObject getJSONObject(int position) {
        try {
            return array.getJSONObject(position);
        } catch(JSONException exn) {
            exn.printStackTrace();
            return new JSONObject();
        }
    }

    protected String getJSONString(JSONObject obj, String key) {
        try {
            return obj.getString(key);
        } catch(JSONException exn) {
            exn.printStackTrace();
            return null;
        }
    }


    @Override
    public int getCount() {
        return array.length();
    }

    @Override
    public Object getItem(int position) {
        return getJSONObject(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
