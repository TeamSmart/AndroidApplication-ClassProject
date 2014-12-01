package net.liucs.chatapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonWriter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;

public class SignInFragment extends Fragment
implements View.OnClickListener, AsyncListener<Boolean,String> {

    private static final Logger log = LoggerFactory.getLogger(HelloActivity.class);
    private static EditText usernameText, passwordText, passwordText2;
    private String username;
    private ProgressBar progress;
    private TextView errorText;

    public SignInFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
        usernameText = (EditText) v.findViewById(R.id.usernameText);
        passwordText = (EditText) v.findViewById(R.id.passwordText);
        passwordText2 = (EditText) v.findViewById(R.id.passwordText2);
        errorText = (TextView) v.findViewById(R.id.errorText);
        progress = (ProgressBar) v.findViewById(R.id.progressBar);
        Button signInButton = (Button) v.findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);
        Button registerButton = (Button) v.findViewById(R.id.registerButton);
        registerButton.setOnClickListener(this);
        return v;
    }

    private static void setURI(HttpRequestBase req, String uri) {
        try {
            req.setURI(new URI(uri));
        } catch(URISyntaxException exn) {
            exn.printStackTrace(); // shouldn't happen
        }
    }

    private void execReq(HttpUriRequest req) {
        progress.setVisibility(View.VISIBLE);
        new FetchUriTask<Boolean>(new Function1<InputStream, Boolean>() {
            @Override
            public Boolean apply(InputStream arg) throws Exception {
                return true;
            }
        }).attach(this).execute(req);
    }

    @Override
    public void onClick(View v) {
        username = usernameText.getText().toString().trim();
        String p1 = passwordText.getText().toString();
        if(username.length()==0 || p1.length()==0) {
            errorText.setText("Enter username and password.");
            errorText.setVisibility(View.VISIBLE);
            return;
        }
        errorText.setVisibility(View.INVISIBLE); // clear previous message
        HttpPost post = new HttpPost();
        try {
            Writer wr = new StringWriter();
            JsonWriter jw = new JsonWriter(wr);
            jw.beginObject();
            jw.name("user").value(username);
            jw.name("password").value(p1);
            jw.endObject();
            jw.close();
            post.setEntity(new StringEntity(wr.toString()));
        } catch(Exception exn) {
            exn.printStackTrace(); // shouldn't happen
            log.info(exn.getMessage());
            return;
        }
        switch(v.getId()) {
            case R.id.signInButton:
                // Check with server to ensure user exists
                setURI(post, API.LOGIN);
                execReq(post);
                break;
            case R.id.registerButton:
                if(passwordText2.getVisibility() != View.VISIBLE) {
                    passwordText2.setVisibility(View.VISIBLE);
                } else {
                    String p2 = passwordText2.getText().toString();
                    if(!p1.equals(p2)) {
                        errorText.setText("Passwords do not match.");
                        errorText.setVisibility(View.VISIBLE);
                        return;
                    }
                    setURI(post, API.REGISTER);
                    execReq(post);
                }
                break;
        }
    }

    @Override
    public void onSuccess(Boolean aBoolean) {
        progress.setVisibility(View.INVISIBLE);
        HelloActivity activity = (HelloActivity) getActivity();
        activity.signIn(username);
    }

    @Override
    public void onFailure(String s) {
        progress.setVisibility(View.INVISIBLE);
        errorText.setText(s);
        errorText.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCancel() {
        progress.setVisibility(View.INVISIBLE);
    }
}
