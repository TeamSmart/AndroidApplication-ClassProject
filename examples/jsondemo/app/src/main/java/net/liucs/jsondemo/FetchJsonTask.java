package net.liucs.jsondemo;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FetchJsonTask extends AsyncTask<Uri, Void, JSONArray> {

    private AsyncListener<JSONArray, String> listener;
    private String error;

    public FetchJsonTask(AsyncListener<JSONArray, String> listener) {
        this.listener = listener;
    }

    @Override
    protected JSONArray doInBackground(Uri... params) {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(params[0].toString());
        Log.i(MainActivity.TAG, "fetching " + params[0]);
        try {
            HttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            if(200 == status) {
                HttpEntity entity = response.getEntity();
                return parseJson(new InputStreamReader(entity.getContent()));
            } else {
                error = Integer.toString(status);
            }
        } catch(Exception e) {
            e.printStackTrace();
            error = e.getMessage();
        }
        return null;
    }

    private JSONArray parseJson(InputStreamReader isr) {
        BufferedReader br = new BufferedReader(isr);
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return new JSONArray(sb.toString());
        } catch(Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONArray array) {
        if(error != null) listener.onFailure(error);
        else listener.onSuccess(array);
    }

    @Override
    protected void onCancelled() {
        listener.onCancel();
    }
}
