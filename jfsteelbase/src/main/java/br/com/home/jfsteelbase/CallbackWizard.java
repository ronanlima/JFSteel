package br.com.home.jfsteelbase;

import android.os.Bundle;

import java.io.Serializable;

/**
 * Created by Ronan.lima on 03/08/17.
 */

public interface CallbackWizard extends Serializable {
    /**
     *
     */
    long serialVersionUID = 1L;
    void backToMainApplication(Bundle bundle);
}
