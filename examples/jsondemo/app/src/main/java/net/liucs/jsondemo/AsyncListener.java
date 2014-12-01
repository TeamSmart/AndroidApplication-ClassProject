package net.liucs.jsondemo;

public interface AsyncListener<Result, Error> {
    void onSuccess(Result result);
    void onFailure(Error error);
    void onCancel();
}
