package br.com.home.maildeliveryjfsteel.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Ronan.lima on 20/10/17.
 */

public class WizardFragment extends Fragment {
    private Bundle mBundle;

    public boolean isCompleted() {
        return true;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle mBundle) {
        this.mBundle = mBundle;
    }
}
