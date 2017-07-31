package br.com.home.maildeliveryjfsteel.async;

import android.os.AsyncTask;

import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;

/**
 * Created by Ronan.lima on 29/07/17.
 */

public class SaveFirebaseAsync extends AsyncTask<FirebaseAsyncParam, Void, Void> {

    @Override
    protected Void doInBackground(FirebaseAsyncParam... firebaseAsyncParams) {
        FirebaseService fService = firebaseAsyncParams[0].getfService();
        fService.save(firebaseAsyncParams[0].getGenericDTO());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }
}
