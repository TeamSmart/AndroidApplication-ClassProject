package net.liucs.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConversationsFragment extends Fragment
implements AsyncListener<JSONArray, String>, AdapterView.OnItemClickListener {

    private static final Logger log = LoggerFactory.getLogger(ConversationsFragment.class);
    private static final String NEW_CONVO = "NEW_CONVO";
    private static final String ARG_USER = "user";

    private String username;
    private GridView listView;
    private JSONArray array;
    private ConversationAdapter adapter;

    public static ConversationsFragment newInstance(String username) {
        ConversationsFragment fragment = new ConversationsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER, username);
        fragment.setArguments(args);
        return fragment;
    }

    public ConversationsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            username = getArguments().getString(ARG_USER);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        HttpGet get = new HttpGet(String.format(API.THREADS, username));
        FetchJsonTask.fetchArray().attach(this).execute(get);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_conversations, container, false);
        listView = (GridView) v.findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        TextView welcomeText = (TextView) v.findViewById(R.id.welcomeText);
        welcomeText.setText("Welcome, " + username);
        return v;
    }

    @Override
    public void onSuccess(JSONArray a) {
        this.array = a;
        log.info("Got back {} conversations.", a.length());
        try {
            a.put(a.length(), NEW_CONVO);
        } catch(JSONException x) {
            x.printStackTrace();
        }
        if(adapter != null) {
            adapter.setArray(a);
            adapter.notifyDataSetChanged();
        } else {
            adapter = new ConversationAdapter(getActivity(), a);
            listView.setAdapter(adapter);
        }
    }

    @Override
    public void onFailure(String s) {

    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(array != null) {
            if(position == array.length()-1) { // New conversation
                Intent i = new Intent(getActivity(), NewConversationActivity.class);
                i.putExtra("me", this.username);
                startActivity(i);
                return;
            }
            try {
                String user = array.getJSONObject(position).getString("user");
                log.info("Show user {}" , user);
                Intent i = new Intent(getActivity(), MessagesActivity.class);
                i.putExtra("me", this.username);
                i.putExtra("you", user);
                startActivity(i);
            } catch(JSONException exn) {
                exn.printStackTrace();
            }
        }
    }
}
