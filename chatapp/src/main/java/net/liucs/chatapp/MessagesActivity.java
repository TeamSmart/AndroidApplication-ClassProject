package net.liucs.chatapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;


public class MessagesActivity extends ActionBarActivity
implements AsyncListener<JSONArray, String>, View.OnClickListener,
    Runnable, Messenger
{

    private static final Logger log = LoggerFactory.getLogger(MessagesActivity.class);

    private Handler handler;
    private String me, you;
    private TextView errorText;
    private ListView listView;
    private EditText editText;
    private Button sendButton;
    private HashMap<String, FetchUriTask<Bitmap>> avatarMap =
            new HashMap<String, FetchUriTask<Bitmap>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);
        handler = new Handler();
        errorText = (TextView) findViewById(R.id.errorText);
        listView = (ListView) findViewById(R.id.listView);
        editText = (EditText) findViewById(R.id.editText);
        sendButton = (Button) findViewById(R.id.sendButton);
        Intent i = getIntent();
        this.me = i.getStringExtra("me");
        this.you = i.getStringExtra("you");
        log.info("In activity between {} and {}.", me, you);
        getSupportActionBar().setTitle(this.you);
        sendButton.setOnClickListener(this);
        run();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.messages, menu);
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

    @Override
    public void run() {
        handler.removeCallbacks(this);
        String mesgUri = String.format(API.MESSAGES_BETWEEN, me, you);
        HttpGet get = new HttpGet(mesgUri);
        FetchJsonTask.fetchArray().attach(this).execute(get);
    }

    @Override
    public void onSuccess(JSONArray a) {
        log.info("Got {} messages", a.length());
        errorText.setVisibility(View.GONE);
        listView.setAdapter(new MessageAdapter(this, a, me));
        handler.postDelayed(this, 15 * 1000);
    }

    @Override
    public void onFailure(String s) {
        errorText.setText(s);
        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancel() {

    }

    public void loadAvatar(final String uri, final ImageView imageView) {
        FetchUriTask<Bitmap> task = avatarMap.get(uri);
        AsyncListener<Bitmap, String> listener = new AsyncListener<Bitmap, String>() {
            @Override
            public void onSuccess(Bitmap bitmap) {
                log.info("Got " + uri);
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onFailure(String s) { }

            @Override
            public void onCancel() { }
        };
        if(null == task) {
            log.info("Fetching " + uri);
            task = FetchImageTask.fetchGravatar(this, uri);
            avatarMap.put(uri, task);
        }
        task.attach(listener);
    }

    @Override
    public void onClick(View v) {
        editText.setEnabled(false);
        sendButton.setEnabled(false);
        String content = editText.getText().toString();
        new PostMessageTask(this).execute(me, you, content);
    }

    @Override
    public Context getContext() {
        return this;
    }

    public void refresh(boolean sent) {
        if(sent) {
            run();
        }
        editText.setText("");
        editText.setEnabled(true);
        editText.clearFocus();
        sendButton.setEnabled(true);
    }
}
