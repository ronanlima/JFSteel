package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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

    public FirebaseNoQrCodeImpl(Context context) {
        super(context);

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
                                updateFields(item, item.getUrlStorageFoto(), item.getKeyRealtimeFb(), true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                updateFields(item, item.getUrlStorageFoto(), item.getKeyRealtimeFb(), true);
                                Crashlytics.log(HIGH_PRIORITY, TAG, "Falha ao atualizar o registro = " + item.getKeyRealtimeFb() + ". Causa = " + e.getMessage());
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
                            Crashlytics.log(HIGH_PRIORITY, TAG, e.getMessage());
                            Toast.makeText(getmContext(), getmContext().getResources().getString(R.string.msg_falha_salvar_servidor), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    public GenericDTO createDTO(GenericDelivery ct) {
        NoQrCodeDTO dto = new NoQrCodeDTO(ct.getTimesTamp(), ct.getIdFoto(), ct.getLatitude(), ct.getLongitude(), ct.getEnderecoManual(), ct.getLocalEntregaCorresp());
        dto.setComentario(((NoQrCode) ct).getComentario());
        dto.setExisteConta(((NoQrCode) ct).getExisteConta());
        dto.setMedidor(((NoQrCode) ct).getMedidor());
        return dto;
    }

    @Override
    public void uploadPhoto(final NoQrCode item, final String uriPhotoDisp, String namePhoto, final DatabaseReference key) {
        StorageReference storageReference = storage.getReference().child(getmContext().getResources().getString(R.string.firebase_storage_no_qrcode)).child(matricula).child(namePhoto);

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
                            updateFields(item, downloadUrl, key.getKey(), true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            updateFields(item, downloadUrl, key.getKey(), true);
                            Crashlytics.log(HIGH_PRIORITY, TAG, e.getMessage());
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    String msg = "";
                    if (e.getMessage() != null && !e.getMessage().isEmpty()) {
                        msg = e.getMessage();
                    }
                    updateFields(item, msg, key.getKey(), true);
                    Crashlytics.log(HIGH_PRIORITY, TAG, msg);
                }
            });
        } else {
            final String msgPadraoImagemInexistente = String.format(getmContext().getResources().getString(R.string.msg_padrao_imagem_inexistente_dispositivo), uriPhotoDisp);
            key.child(getmContext().getResources().getString(R.string.url_storage_foto)).setValue(msgPadraoImagemInexistente).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    updateFields(item, msgPadraoImagemInexistente, key.getKey(), true);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    updateFields(item, msgPadraoImagemInexistente, key.getKey(), true);
                    Crashlytics.log(HIGH_PRIORITY, TAG, e.getMessage());
                }
            });
        }
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
        db.save(ct);
    }

}

class NoQrCodeDTO extends GenericDTO {
    private String medidor;
    private int existeConta;
    private String comentario;

    public NoQrCodeDTO(Long timesTamp, String idFoto, Double latitude, Double longitude, String enderecoManual, String localEntregaCorresp) {
        setTimeStamp(timesTamp);
        setIdFoto(idFoto);
        if (latitude != 0d) {
            setLatitude(latitude);
            setLongitude(longitude);
        } else {
            setEnderecoManual(enderecoManual);
        }
        setLocalEntrega(localEntregaCorresp);
    }

    public NoQrCodeDTO() {
    }

    public String getMedidor() {
        return medidor;
    }

    public void setMedidor(String medidor) {
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
