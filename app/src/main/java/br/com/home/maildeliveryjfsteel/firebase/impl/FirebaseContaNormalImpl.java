package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public class FirebaseContaNormalImpl implements FirebaseService<ContaNormal> {
    private Context mContext;
    private String matricula;

    public FirebaseContaNormalImpl(Context mContext) {
        this.mContext = mContext;

        SharedPreferences sp = mContext.getSharedPreferences(BuildConfig.APPLICATION_ID, mContext.MODE_PRIVATE);
        this.matricula = sp.getString(mContext.getResources().getString(R.string.sp_matricula), null);
    }

    @Override
    public void save(final List<ContaNormal> list) {
        DatabaseReference reference = database.getReference(mContext.getResources().getString(R.string.firebase_no_contas));
        for (final ContaNormal ct : list) {
            reference.child(matricula).push().setValue(ct).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful() && task.isComplete()) {
                        if (ct.getUriFotoDisp() != null && !ct.getUriFotoDisp().isEmpty()) {
                            uploadPhoto(ct, ct.getUriFotoDisp(), ct.getIdFoto());
                        } else {
                            updateFields(ct, null);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_falha_salvar_servidor), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void uploadPhoto(final ContaNormal ct, String uriPhotoDisp, String namePhoto) {
        StorageReference storageReference = storage.getReference().child(mContext.getResources().getString(R.string.firebase_storage_conta)).child(namePhoto);

        Bitmap bitmap = BitmapFactory.decodeFile(uriPhotoDisp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        UploadTask uploadTask = storageReference.putStream(in);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                updateFields(ct, taskSnapshot.getDownloadUrl().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateFields(ct, null);
            }
        });
    }

    /**
     * Atualiza o campo sitSalvoFirebase para 1 - true e talvez o campo com a url da imagem no storage
     * e atualiza o registro no sqlite.
     *
     * @param ct
     * @param downloadUrl
     */
    @Override
    public void updateFields(ContaNormal ct, String downloadUrl) {
        MailDeliveryDBContaNormal db = new MailDeliveryDBContaNormal(mContext);
        ct.setSitSalvoFirebase(1);
        if (downloadUrl != null) {
            ct.setUrlStorageFoto(downloadUrl);
        } else {
            ct.setUrlStorageFoto(null);
        }
        db.save(ct);
    }

}
