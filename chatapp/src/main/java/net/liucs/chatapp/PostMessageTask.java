package net.liucs.chatapp;

import android.os.AsyncTask;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PostMessageTask extends AsyncTask<String, Void, Integer> {

    private static final Logger log = LoggerFactory.getLogger(PostMessageTask.class);

    private Messenger messenger;
    private String error;

    public PostMessageTask(Messenger messenger) {
        this.messenger = messenger;
    }

    @Override
    protected Integer doInBackground(String... params) {
        String sender = params[0];
        String recipient = params[1];
        String content = params[2];
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost(API.MESSAGES);
        try {
            JSONObject object = new JSONObject();
            object.put("sender", sender);
            object.put("recipient", recipient);
            object.put("content", content);
            StringEntity entity = new StringEntity(object.toString());
            post.setEntity(entity);
            post.setHeader("Content-Type", "application/json");
            HttpResponse response = client.execute(post);
            int status = response.getStatusLine().getStatusCode();
            log.info("POST status is {}", status);
            return status;
        } catch(Exception exn) {
            error = exn.getMessage();
            exn.printStackTrace();
        }
        return -1;
    }

    @Override
    protected void onPostExecute(Integer status) {
        super.onPostExecute(status);
        boolean sent = false;
        if(status == -1) {
            Toast.makeText(messenger.getContext(),
                    error, Toast.LENGTH_LONG).show();
        } else if(201 != status) {
            Toast.makeText(messenger.getContext(),
                    "POST status " + status,
                    Toast.LENGTH_SHORT).show();
        } else {
            sent = true;
        }
        messenger.refresh(sent);
    }
}
