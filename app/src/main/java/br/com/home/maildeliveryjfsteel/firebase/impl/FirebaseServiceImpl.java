package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;

import java.util.List;

import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;

/**
 * Created by Ronan.lima on 16/08/17.
 */

public class FirebaseServiceImpl<T> implements FirebaseService<T> {
    private Context mContext;

    public FirebaseServiceImpl(Context context) {
        this.mContext = context;
    }

    @Override
    public void save(List<T> obj) {

    }

    @Override
    public void uploadPhoto(T obj, String uriPhotoDisp, String namePhoto) {

    }

    @Override
    public void updateFields(T obj, String downloadUrl) {

    }

    public Context getmContext() {
        return mContext;
    }
}
