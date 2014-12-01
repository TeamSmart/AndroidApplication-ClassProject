package net.liucs.chatapp;

public class API {

    public static String PREFIX = "http://chatapp.liucs.net/api/";

    public static String THREADS =
            PREFIX + "threads/%s/";

    public static String MESSAGES_BETWEEN =
            PREFIX + "messages/%s/%s/";

    public static String MESSAGES =
            PREFIX + "messages/";

    public static String LOGIN =
            PREFIX + "login/";

    public static String REGISTER =
            PREFIX + "register/";
}
