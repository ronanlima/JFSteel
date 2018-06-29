package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.Log;
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
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public class FirebaseContaNormalImpl extends FirebaseServiceImpl<ContaNormal> {
    public static final String TAG = FirebaseContaNormalImpl.class.getCanonicalName().toUpperCase();

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
                if (ct.getKeyRealtimeFb() != null && !ct.getKeyRealtimeFb().trim().isEmpty()) {
                    if (ct.getUrlStorageFoto() != null && !ct.getUrlStorageFoto().trim().isEmpty()) {
                        reference.child(ct.getKeyRealtimeFb()).setValue(ct.getUrlStorageFoto()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                updateFields(ct, ct.getUrlStorageFoto(), ct.getKeyRealtimeFb(), true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Falha ao atualizar o registro = " + ct.getKeyRealtimeFb() + ". Causa = " + e.getMessage());
                                updateFields(ct, ct.getUrlStorageFoto(), ct.getKeyRealtimeFb(), true);
                            }
                        });
                    } else {
                        uploadPhoto(ct, ct.getUriFotoDisp(), ct.getIdFoto(), reference.child(ct.getKeyRealtimeFb()));
                    }
                } else {
                    final DatabaseReference key = reference.child(matricula).push();
                    key.setValue(createDTO(ct)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && task.isComplete()) {
                                if (ct.getUriFotoDisp() != null && !ct.getUriFotoDisp().isEmpty()) {
                                    uploadPhoto(ct, ct.getUriFotoDisp(), ct.getIdFoto(), key);
                                } else {
                                    updateFields(ct, null, key.getKey(), true);
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
    }

    @Override
    public GenericDTO createDTO(GenericDelivery ct) {
        ContaNormalDTO dto = new ContaNormalDTO(ct.getDadosQrCode(), ct.getIdFoto(), ct.getLatitude(), ct.getLongitude(),
                ct.getEnderecoManual(), ct.getTimesTamp(), ct.getLocalEntregaCorresp());
        dto.setContaColetiva(((ContaNormal) ct).isContaColetiva());
        dto.setContaProtocolada(((ContaNormal) ct).isContaProtocolada());
        return dto;
    }

    @Override
    public void uploadPhoto(final ContaNormal ct, final String uriPhotoDisp, String namePhoto, final DatabaseReference key) {
        StorageReference storageReference = storage.getReference().child(getmContext().getResources().getString(R.string.firebase_storage_conta)).child(matricula).child(namePhoto);

        Bitmap bitmap = BitmapFactory.decodeFile(uriPhotoDisp);
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            byte[] data = baos.toByteArray();
            ByteArrayInputStream in = new ByteArrayInputStream(data);

            UploadTask uploadTask = storageReference.putStream(in);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String downloadUrl = taskSnapshot.getDownloadUrl() != null ? taskSnapshot.getDownloadUrl().toString()
                            : String.format(getmContext().getResources().getString(R.string.msg_imagem_enviada_mas_nao_salva), uriPhotoDisp);
                    key.child(getmContext().getResources().getString(R.string.url_storage_foto)).setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            updateFields(ct, downloadUrl, key.getKey(), true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            updateFields(ct, downloadUrl, key.getKey(), true);
                            Log.e(TAG, e.getMessage());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String msg = "";
                    if (e != null && e.getMessage() != null && !e.getMessage().isEmpty()) {
                        msg = e.getMessage();
                    }
                    updateFields(ct, msg, key.getKey(), true);
                }
            });
        } else {
            final String msgPadraoImagemInexistente = String.format(getmContext().getResources().getString(R.string.msg_padrao_imagem_inexistente_dispositivo), uriPhotoDisp);
            key.child(getmContext().getResources().getString(R.string.url_storage_foto)).setValue(msgPadraoImagemInexistente).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateFields(ct, msgPadraoImagemInexistente, key.getKey(), true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateFields(ct, msgPadraoImagemInexistente, key.getKey(), true);
                    Log.e(TAG, e.getMessage());
                }
            });
        }
    }

    /**
     * Atualiza o campo sitSalvoFirebase para 1 - true e talvez o campo com a url da imagem no storage
     * e atualiza o registro no sqlite.
     *
     * @param ct
     * @param downloadUrl
     * @param key
     */
    @Override
    public void updateFields(ContaNormal ct, String downloadUrl, String key, boolean canUpdateColumnSitFirebase) {
        MailDeliveryDBContaNormal db = new MailDeliveryDBContaNormal(getmContext());
        if (canUpdateColumnSitFirebase) {
            ct.setSitSalvoFirebase(1);
        }
        ct.setKeyRealtimeFb(key);
        if (downloadUrl != null) {
            ct.setUrlStorageFoto(downloadUrl);
        } else {
            ct.setUrlStorageFoto(null);
        }
        if (ct.getContext() == null) {
            ct.setContext(getmContext());
        }
        db.save(ct);
        if (getListenerService() != null) {
            getListenerService().notifyEndService();
        }
    }

}

class ContaNormalDTO extends GenericDTO {
    private boolean isContaProtocolada;
    private boolean isContaColetiva;

    public ContaNormalDTO(String dadosQrCode, String idFoto, double latitude, double longitude, String enderecoManual, long timeStamp, String localEntrega) {
        super(dadosQrCode, idFoto, latitude, longitude, enderecoManual, timeStamp, localEntrega);
    }

    public void setContaProtocolada(boolean contaProtocolada) {
        this.isContaProtocolada = contaProtocolada;
    }

    public void setContaColetiva(boolean contaColetiva) {
        this.isContaColetiva = contaColetiva;
    }
}
