package net.liucs.chatapp;

import org.json.JSONArray;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FetchJsonTask {

    public static String readAll(InputStream is) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public static FetchUriTask<JSONArray> fetchArray() {
        return new FetchUriTask<JSONArray>(new Function1<InputStream, JSONArray>() {
            @Override
            public JSONArray apply(InputStream is) throws Exception {
                return new JSONArray(readAll(is));
            }
        });
    }
}
