package net.liucs.chatapp;

import android.content.Context;

public interface Messenger {
    public void refresh(boolean sent);
    public Context getContext();
}
