package net.liucs.tabfragments.app;

import android.os.Parcel;
import android.os.Parcelable;

public class City implements Parcelable {
    String name;
    String blurb;
    int drawable;

    City(String name, int drawable, String blurb) {
        this.name = name;
        this.drawable = drawable;
        this.blurb = blurb;
    }

    City(Parcel in) {
        name = in.readString();
        drawable = in.readInt();
        blurb = in.readString();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(name);
        out.writeInt(drawable);
        out.writeString(blurb);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
