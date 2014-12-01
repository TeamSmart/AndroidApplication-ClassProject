package net.liucs.asyncdemo.app;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends ActionBarActivity {

    Button button;
    TextView helloText;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        helloText = (TextView) findViewById(R.id.textView);
        listView = (ListView) findViewById(R.id.listView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // What if I want to do something that takes a long time?
                helloText.setText("Starting work...");
                new FetchJsonTask().execute(Uri.parse("http://chatapp.liucs.net/api/messages/"));
                helloText.setText("THANKS FOR CLICKING");
            }
        });
    }


    class FetchJsonTask extends AsyncTask<Uri, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(Uri... params) {
            // Anything here happens on a background thread,
            // and does not block the UI thread from updating.
            Log.i("AsyncDemo", "About to fetch from " + params[0]);
            DefaultHttpClient hc = new DefaultHttpClient();
            HttpGet get = new HttpGet(params[0].toString());
            try {
                HttpResponse response = hc.execute(get);
                int status = response.getStatusLine().getStatusCode();
                Log.i("AsyncDemo", "Got back HTTP " + status);
                if(status != 200) return null;
                HttpEntity entity = response.getEntity();
                String content = readEntireStream(entity.getContent());
                JSONArray array = new JSONArray(content);
                Log.i("AsyncDemo", "Got " + array.length() + " JSON messages.");
                return array;
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONArray array) {
            // This method will be run on the UI thread.
            // Its parameter is the result that we returned from
            // doInBackground.
            helloText.setText("Got " + array.length() + " messages.");
            try {
                // extract data we want from JSON array into Java array
                String[] data = new String[array.length()];
                for (int i = 0; i < array.length(); i++) {
                    data[i] = array.getJSONObject(i).getString("content");
                }
                listView.setAdapter(new ArrayAdapter<String>(
                        MainActivity.this,
                        R.layout.single_row,
                        R.id.textView,
                        data
                ));
            } catch(JSONException e) {
                e.printStackTrace();
        }
        }
    }


    private static String readEntireStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        while((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
