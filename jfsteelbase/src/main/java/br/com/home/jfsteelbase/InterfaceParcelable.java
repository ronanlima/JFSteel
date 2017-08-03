package br.com.home.jfsteelbase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created by Ronan.lima on 03/08/17.
 */

public class InterfaceParcelable implements Parcelable {
    private CallbackWizard listenerCallback;

    public InterfaceParcelable(CallbackWizard listenerCallback) {
        setListenerCallback(listenerCallback);
    }

    protected InterfaceParcelable(Parcel in) {
        CallbackWizard[] data = new CallbackWizard[1];
//        in.readSerializable();
        in.readArray((ClassLoader) Arrays.asList(data));
        in.readArrayList((ClassLoader) Arrays.asList(data));
        setListenerCallback(data[0]);
    }

    public static final Creator<InterfaceParcelable> CREATOR = new Creator<InterfaceParcelable>() {
        @Override
        public InterfaceParcelable createFromParcel(Parcel in) {
            return new InterfaceParcelable(in);
        }

        @Override
        public InterfaceParcelable[] newArray(int size) {
            return new InterfaceParcelable[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeArray(new Object[]{getListenerCallback()});
//        parcel.writeSerializable(getListenerCallback());
    }

    public CallbackWizard getListenerCallback() {
        return listenerCallback;
    }

    public void setListenerCallback(CallbackWizard listenerCallback) {
        this.listenerCallback = listenerCallback;
    }
}
