package net.liucs.chatapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class NewConversationActivity extends ActionBarActivity
implements View.OnClickListener, Messenger {

    private EditText toText;
    private EditText messageText;
    private Button sendButton;
    private String me;
    private String you;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_conversation);
        me = getIntent().getStringExtra("me");
        TextView fromText = (TextView) findViewById(R.id.fromText);
        fromText.setText(me);
        toText = (EditText) findViewById(R.id.toText);
        messageText = (EditText) findViewById(R.id.messageText);
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        toText.setEnabled(false);
        messageText.setEnabled(false);
        sendButton.setEnabled(false);
        you = toText.getText().toString().trim();
        String content = messageText.getText().toString();
        new PostMessageTask(this).execute(me, you, content);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_conversation, menu);
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
    public void refresh(boolean sent) {
        if(sent) {
            Intent i = new Intent(this, MessagesActivity.class);
            i.putExtra("me", this.me);
            i.putExtra("you", this.you);
            startActivity(i);
        }
    }

    @Override
    public Context getContext() {
        return this;
    }
}
