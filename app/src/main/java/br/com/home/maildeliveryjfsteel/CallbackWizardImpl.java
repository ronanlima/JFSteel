package br.com.home.maildeliveryjfsteel;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

import br.com.home.jfsteelbase.CallbackWizard;

/**
 * Created by Ronan.lima on 03/08/17.
 */

public class CallbackWizardImpl implements CallbackWizard, Parcelable {
    private BackToHandlerActivity listenerHandler;
    private CallbackWizardImpl instance;

    public synchronized CallbackWizardImpl getInstance(BackToHandlerActivity listenerHandler) {
        if (instance == null) {
            instance = new CallbackWizardImpl(listenerHandler);
        }
        return instance;
    }

    public CallbackWizardImpl(BackToHandlerActivity listenerHandler) {
        this.listenerHandler = listenerHandler;
    }

    protected CallbackWizardImpl(Parcel in) {
        Object[] obj = new Object[1];
        in.readByte();
        this.listenerHandler = (BackToHandlerActivity) obj[0];
    }

    public static final Creator<CallbackWizardImpl> CREATOR = new Creator<CallbackWizardImpl>() {
        @Override
        public CallbackWizardImpl createFromParcel(Parcel in) {
            return new CallbackWizardImpl(in);
        }

        @Override
        public CallbackWizardImpl[] newArray(int size) {
            return new CallbackWizardImpl[size];
        }
    };

    @Override
    public void backToMainApplication(Bundle bundle) {
        listenerHandler.backBundle(bundle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(this);
    }

    public interface BackToHandlerActivity extends Serializable {
        void backBundle(Bundle bundle);
    }
}
