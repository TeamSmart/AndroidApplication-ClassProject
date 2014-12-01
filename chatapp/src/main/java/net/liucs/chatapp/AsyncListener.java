package net.liucs.chatapp;

public interface AsyncListener<Result, Error> {
    void onSuccess(Result result);
    void onFailure(Error error);
    void onCancel();
}
