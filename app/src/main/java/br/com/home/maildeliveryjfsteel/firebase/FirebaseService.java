package br.com.home.maildeliveryjfsteel.firebase;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public interface FirebaseService<T> {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    void save(List<T> obj);
    void uploadPhoto(T obj, String uriPhotoDisp, String namePhoto);
    void updateFields(T obj, String downloadUrl);
}
