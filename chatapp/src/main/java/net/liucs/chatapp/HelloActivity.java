package net.liucs.chatapp;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloActivity extends ActionBarActivity
implements AsyncListener<Bitmap, String> {

    private static final Logger log = LoggerFactory.getLogger(HelloActivity.class);
    private static final String PREFS = "ChatAppPrefs";
    private static final String PREF_USER = "username";

    private SharedPreferences prefs;

    @Override
    public void onCreate(Bundle in) {
        super.onCreate(in);
        setContentView(R.layout.activity_hello);
        prefs = getSharedPreferences(PREFS, 0);
        String user = prefs.getString(PREF_USER, null);
        if(null == in) {
            Fragment f = null == user? new SignInFragment() :
                    ConversationsFragment.newInstance(user);
            getSupportFragmentManager().beginTransaction()
                    .add(android.R.id.content, f)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.signOutAction:
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void saveUsername(String username) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putString(PREF_USER, username);
        edit.commit();
    }

    public void signIn(String username) {
        saveUsername(username);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, ConversationsFragment.newInstance(username))
                .commit();
    }

    public void signOut() {
        saveUsername(null);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SignInFragment())
                .commit();
    }

    @Override
    public void onSuccess(Bitmap bitmap) {
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        iv.setImageBitmap(bitmap);
    }

    @Override
    public void onFailure(String s) {

    }

    @Override
    public void onCancel() {

    }
}
