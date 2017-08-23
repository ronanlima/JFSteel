package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;

/**
 * Created by Ronan.lima on 16/08/17.
 */

public class FirebaseServiceImpl<T> implements FirebaseService<T> {
    private Context mContext;
    private ServiceNotification listenerService;

    public FirebaseServiceImpl(Context context, ServiceNotification listenerService) {
        this.mContext = context;
        this.listenerService = listenerService;
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

    /**
     * Listener utilizado para notificar a Activity que chamou os métodos dessa classe, do seu término
     */
    public interface ServiceNotification extends Serializable {
        void notifyEndService();
    }

    public Context getmContext() {
        return mContext;
    }

    public ServiceNotification getListenerService() {
        return listenerService;
    }
}
