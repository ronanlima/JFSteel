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
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;
import br.com.home.maildeliveryjfsteel.persistence.dto.NotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public class FirebaseContaNormalImpl extends FirebaseServiceImpl<ContaNormal> {
    private String matricula;

    public FirebaseContaNormalImpl(Context context, ServiceNotification listener) {
        super(context, listener);

        SharedPreferences sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID, context.MODE_PRIVATE);
        this.matricula = sp.getString(context.getResources().getString(R.string.sp_matricula), null);
    }

    @Override
    public void save(final List<ContaNormal> list) {
        if (list != null) {
            DatabaseReference reference = database.getReference(getmContext().getResources().getString(R.string.firebase_no_contas));
            for (final ContaNormal ct : list) {
                reference.child(matricula).push().setValue(createDTO(ct)).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        Toast.makeText(getmContext(), getmContext().getResources().getString(R.string.msg_falha_salvar_servidor), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    public GenericDTO createDTO(GenericDelivery ct) {
        ContaNormalFB dto = new ContaNormalFB(ct.getDadosQrCode(), ct.getIdFoto(), ct.getLatitude(), ct.getLongitude(),
                ct.getEnderecoManual(), ct.getTimesTamp(), ct.getUriFotoDisp(), ct.getLocalEntregaCorresp());
        dto.setContaColetiva(((ContaNormal) ct).isContaColetiva());
        dto.setContaProtocolada(((ContaNormal) ct).isContaProtocolada());
        return dto;
    }

    @Override
    public void uploadPhoto(final ContaNormal ct, String uriPhotoDisp, String namePhoto) {
        StorageReference storageReference = storage.getReference().child(getmContext().getResources().getString(R.string.firebase_storage_conta)).child(namePhoto);

        Bitmap bitmap = BitmapFactory.decodeFile(uriPhotoDisp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
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
        MailDeliveryDBContaNormal db = new MailDeliveryDBContaNormal(getmContext());
        ct.setSitSalvoFirebase(1);
        if (downloadUrl != null) {
            ct.setUrlStorageFoto(downloadUrl);
        } else {
            ct.setUrlStorageFoto(null);
        }
        db.save(ct);
        if (getListenerService() != null) {
            getListenerService().notifyEndService();
        }
    }

}

class ContaNormalFB extends GenericDTO {
    private boolean isContaProtocolada;
    private boolean isContaColetiva;

    public ContaNormalFB(String dadosQrCode, String idFoto, double latitude, double longitude, String enderecoManual, long timeStamp, String uriFotoDisp, String localEntrega) {
        super(dadosQrCode, idFoto, latitude, longitude, enderecoManual, timeStamp, uriFotoDisp, localEntrega);
    }

    public void setContaProtocolada(boolean contaProtocolada) {
        this.isContaProtocolada = contaProtocolada;
    }

    public void setContaColetiva(boolean contaColetiva) {
        this.isContaColetiva = contaColetiva;
    }
}
