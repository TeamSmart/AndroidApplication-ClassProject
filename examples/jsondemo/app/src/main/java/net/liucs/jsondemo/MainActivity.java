package net.liucs.jsondemo;

import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.json.JSONArray;


public class MainActivity extends ActionBarActivity
implements AsyncListener<JSONArray, String>
{

    public static final String TAG = "JsonDemo";
    public static final Uri MESSAGES_URL = Uri.parse("http://chatapp.liucs.net/api/messages/");

    private Button fetchButton;
    private TextView helloText;
    private ProgressBar progressBar;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fetchButton = (Button) findViewById(R.id.fetchButton);
        helloText = (TextView) findViewById(R.id.helloText);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        listView = (ListView) findViewById(R.id.listView);

        fetchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFetch();
            }
        });
    }

    private void startFetch() {
        progressBar.setVisibility(View.VISIBLE);
        fetchButton.setEnabled(false);
        helloText.setText("Fetching…");
        new FetchJsonTask(MainActivity.this).execute(MESSAGES_URL);
    }

    private void stopFetch() {
        progressBar.setVisibility(View.INVISIBLE);
        fetchButton.setEnabled(true);
    }

    @Override
    public void onSuccess(JSONArray array) {
        stopFetch();
        helloText.setText("got " + array.length() + " messages.");
        listView.setAdapter(new JsonMessageAdapter(this, array));
    }

    @Override
    public void onFailure(String error) {
        stopFetch();
        helloText.setText(error);
    }

    @Override
    public void onCancel() {
        stopFetch();
        helloText.setText("cancelled");
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
