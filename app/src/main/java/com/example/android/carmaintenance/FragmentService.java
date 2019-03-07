package com.example.android.carmaintenance;

import android.os.Parcel;
import android.os.Parcelable;

public class FragmentService implements Parcelable {

    private String serviceName;
    private int periodicity;
    private int lastTime;
    private boolean state;
    private double price;
    private String Id;


    public FragmentService(String serviceName, int periodicity, int lastTime, boolean state, double price, String id) {
        this.serviceName = serviceName;
        this.periodicity = periodicity;
        this.lastTime = lastTime;
        this.state = state;
        this.price = price;
        this.Id = id;
    }

    protected FragmentService(Parcel in) {
        this.serviceName = in.readString();
        this.periodicity = in.readInt();
        this.lastTime = in.readInt();
        this.state = in.readInt()!=0;
        this.price = in.readDouble();
        Id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.serviceName);
        dest.writeInt(this.periodicity);
        dest.writeInt(this.lastTime);
        dest.writeInt(this.state ?1:0);
        dest.writeDouble(this.price);
        dest.writeString(this.Id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FragmentService> CREATOR = new Creator<FragmentService>() {
        @Override
        public FragmentService createFromParcel(Parcel in) {
            return new FragmentService(in);
        }

        @Override
        public FragmentService[] newArray(int size) {
            return new FragmentService[size];
        }
    };


}
