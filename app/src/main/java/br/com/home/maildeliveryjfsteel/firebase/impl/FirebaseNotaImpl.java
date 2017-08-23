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
import br.com.home.maildeliveryjfsteel.persistence.dto.NotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBNotaServico;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public class FirebaseNotaImpl extends FirebaseServiceImpl<NotaServico> {
    private String matricula;

    public FirebaseNotaImpl(Context context, ServiceNotification listener) {
        super(context, listener);

        SharedPreferences sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID, context.MODE_PRIVATE);
        this.matricula = sp.getString(context.getResources().getString(R.string.sp_matricula), null);
    }

    @Override
    public void save(List<NotaServico> list) {
        if (list != null) {
            DatabaseReference reference = database.getReference(getmContext().getResources().getString(R.string.firebase_no_notas));
            for (final NotaServico nota : list) {
                reference.child(matricula).push().setValue(nota).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && task.isComplete()) {
                            if (nota.getUriFotoDisp() != null && !nota.getUriFotoDisp().isEmpty()) {
                                uploadPhoto(nota, nota.getUriFotoDisp(), nota.getIdFoto());
                            } else {
                                updateFields(nota, null);
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getmContext(), getmContext().getResources().getString(R.string.msg_falha_salvar_servidor), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public void uploadPhoto(final NotaServico nota, String uriPhotoDisp, String namePhoto) {
        StorageReference storageReference = storage.getReference().child(getmContext().getResources().getString(R.string.firebase_storage_nota_servico)).child(namePhoto);

        Bitmap bitmap = BitmapFactory.decodeFile(uriPhotoDisp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        UploadTask uploadTask = storageReference.putStream(in);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                updateFields(nota, taskSnapshot.getDownloadUrl().toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateFields(nota, null);
            }
        });
    }

    @Override
    public void updateFields(NotaServico nota, String downloadUrl) {
        MailDeliveryDBNotaServico db = new MailDeliveryDBNotaServico(getmContext());
        nota.setSitSalvoFirebase(1);
        if (downloadUrl != null) {
            nota.setUrlStorageFoto(downloadUrl);
        } else {
            nota.setUrlStorageFoto(null);
        }
        db.save(nota);
    }
}
