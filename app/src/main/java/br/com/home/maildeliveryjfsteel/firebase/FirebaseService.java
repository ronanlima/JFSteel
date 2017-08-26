package br.com.home.maildeliveryjfsteel.firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public interface FirebaseService<T> {
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    void save(List<T> obj);
    void uploadPhoto(T ojb, String uriPhotoDisp, String namePhoto, DatabaseReference key);
    void updateFields(T obj, String downloadUrl, String key, boolean canUpdateColumnSitFirebase);
}
