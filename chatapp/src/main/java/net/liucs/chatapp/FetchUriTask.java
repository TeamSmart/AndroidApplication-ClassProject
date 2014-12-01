package net.liucs.chatapp;

import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;

public class FetchUriTask<Result>
extends AsyncTask<HttpUriRequest, Void, Result> {

    static enum Status {
        RUNNING, SUCCESS, FAILURE, CANCELLED
    };

    private Function1<InputStream, Result> decode;
    private ArrayList<AsyncListener<Result, String>> listeners =
            new ArrayList<AsyncListener<Result, String>>();
    private Status status = Status.RUNNING;
    private Result result;
    private String error;

    public FetchUriTask(Function1<InputStream, Result> decode) {
        this.decode = decode;
    }

    @Override
    protected Result doInBackground(HttpUriRequest... params) {
        try {
            HttpUriRequest req = params[0];
            DefaultHttpClient c = new DefaultHttpClient();
            HttpResponse r = c.execute(req);
            StatusLine sl  = r.getStatusLine();
            if(sl.getStatusCode() == HttpURLConnection.HTTP_OK) {
                HttpEntity e = r.getEntity();
                return decode.apply(e.getContent());
            } else {
                error = sl.getReasonPhrase();
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            error = e.getMessage();
            return null;
        }
    }

    @Override
    protected void onPostExecute(Result result) {
        if(null == result) {
            status = Status.FAILURE;
            for(AsyncListener<Result, String> listener : listeners) {
                listener.onFailure(error);
            }
        } else {
            this.result = result;
            status = Status.SUCCESS;
            for(AsyncListener<Result, String> listener : listeners) {
                listener.onSuccess(result);
            }
        }
    }

    @Override
    protected void onCancelled(Result result) {
        status = Status.CANCELLED;
        for(AsyncListener<Result, String> listener : listeners) {
            listener.onCancel();
        }
    }

    public FetchUriTask<Result> attach(AsyncListener<Result, String> listener) {
        listeners.add(listener);
        switch(status) {
            case SUCCESS:
                listener.onSuccess(result);
                break;
            case FAILURE:
                listener.onFailure(error);
                break;
            case CANCELLED:
                listener.onCancel();
                break;
            default: // Still running...
        }
        return this;
    }

    public void clear() {
        listeners.clear();
    }
}
