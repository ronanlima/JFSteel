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
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;
import br.com.home.maildeliveryjfsteel.persistence.dto.NoQrCode;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryNoQrCode;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public class FirebaseNoQrCodeImpl extends FirebaseServiceImpl<NoQrCode> {
    private static final String TAG = FirebaseNoQrCodeImpl.class.getCanonicalName().toUpperCase();
    private String matricula;

    public FirebaseNoQrCodeImpl(Context context, ServiceNotification listener) {
        super(context, listener);

        SharedPreferences sp = context.getSharedPreferences(BuildConfig.APPLICATION_ID, context.MODE_PRIVATE);
        this.matricula = sp.getString(context.getResources().getString(R.string.sp_matricula), null);
    }

    @Override
    public void save(List<NoQrCode> list) {
        if (list != null) {
            DatabaseReference reference = database.getReference(getmContext().getResources().getString(R.string.firebase_no_noqrcode));
            for (final NoQrCode item : list) {
                if (item.getKeyRealtimeFb() != null && !item.getKeyRealtimeFb().trim().isEmpty()) {
                    if (item.getUrlStorageFoto() != null && !item.getUrlStorageFoto().trim().isEmpty()) {
                        reference.child(item.getKeyRealtimeFb()).setValue(item.getUrlStorageFoto()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete() && task.isSuccessful()) {
                                    updateFields(item, item.getUrlStorageFoto(), item.getKeyRealtimeFb(), true);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Falha ao atualizar o registro = " + item.getKeyRealtimeFb() + ". Causa = " + e.getMessage());
                            }
                        });
                    }
                } else {
                    final DatabaseReference key = reference.child(matricula).push();
                    key.setValue(createDTO(item)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && task.isComplete()) {
                                if (item.getUriFotoDisp() != null && !item.getUriFotoDisp().isEmpty()) {
                                    uploadPhoto(item, item.getUriFotoDisp(), item.getIdFoto(), key);
                                } else {
                                    updateFields(item, null, key.getKey(), true);
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
        NoQrCodeDTO dto = new NoQrCodeDTO(ct.getDadosQrCode(), ct.getIdFoto(), ct.getLatitude(), ct.getLongitude(),
                ct.getEnderecoManual(), ct.getTimesTamp(), ct.getLocalEntregaCorresp());
        dto.setComentario(((NoQrCode) ct).getComentario());
        dto.setExisteConta(((NoQrCode) ct).getExisteConta());
        dto.setMedidor(((NoQrCode) ct).getMedidor());
        return dto;
    }

    @Override
    public void uploadPhoto(final NoQrCode item, String uriPhotoDisp, String namePhoto, final DatabaseReference key) {
        StorageReference storageReference = storage.getReference().child(getmContext().getResources().getString(R.string.firebase_storage_no_qrcode)).child(matricula).child(namePhoto);

        Bitmap bitmap = BitmapFactory.decodeFile(uriPhotoDisp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
        byte[] data = baos.toByteArray();
        ByteArrayInputStream in = new ByteArrayInputStream(data);

        UploadTask uploadTask = storageReference.putStream(in);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final String downloadUrl = taskSnapshot.getDownloadUrl().toString();
                key.child(getmContext().getResources().getString(R.string.url_storage_foto)).setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isComplete() && task.isSuccessful()) {
                            updateFields(item, downloadUrl, key.getKey(), true);
                        } else {
                            updateFields(item, downloadUrl, key.getKey(), false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateFields(item, downloadUrl, key.getKey(), false);
                        Log.e(TAG, e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateFields(item, null, key.getKey(), false);
            }
        });
    }

    @Override
    public void updateFields(NoQrCode ct, String downloadUrl, String key, boolean canUpdateColumnSitFirebase) {
        MailDeliveryNoQrCode db = new MailDeliveryNoQrCode(getmContext());
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
    }

}

class NoQrCodeDTO extends GenericDTO {
    private int medidor;
    private int existeConta;
    private String comentario;

    public NoQrCodeDTO(String dadosQrCode, String idFoto, double latitude, double longitude, String enderecoManual, long timeStamp, String localEntrega) {
        super(dadosQrCode, idFoto, latitude, longitude, enderecoManual, timeStamp, localEntrega);
    }

    public NoQrCodeDTO() {
    }

    public int getMedidor() {
        return medidor;
    }

    public void setMedidor(int medidor) {
        this.medidor = medidor;
    }

    public int getExisteConta() {
        return existeConta;
    }

    public void setExisteConta(int existeConta) {
        this.existeConta = existeConta;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
