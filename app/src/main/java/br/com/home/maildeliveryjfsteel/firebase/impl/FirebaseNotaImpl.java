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
import br.com.home.maildeliveryjfsteel.persistence.dto.NotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBNotaServico;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public class FirebaseNotaImpl extends FirebaseServiceImpl<NotaServico> {
    private static final String TAG = FirebaseNotaImpl.class.getCanonicalName().toUpperCase();

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
                if (nota.getKeyRealtimeFb() != null && !nota.getKeyRealtimeFb().trim().isEmpty()) {
                    if (nota.getUrlStorageFoto() != null && !nota.getUrlStorageFoto().trim().isEmpty()) {
                        reference.child(nota.getKeyRealtimeFb()).setValue(nota.getUrlStorageFoto()).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete() && task.isSuccessful()) {
                                    updateFields(nota, nota.getUrlStorageFoto(), nota.getKeyRealtimeFb(), true);
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e(TAG, "Falha ao atualizar o registro = " + nota.getKeyRealtimeFb() + ". Causa = " + e.getMessage());
                            }
                        });
                    } else {
                        uploadPhoto(nota, nota.getUriFotoDisp(), nota.getIdFoto(), reference.child(nota.getKeyRealtimeFb()));
                    }
                } else {
                    final DatabaseReference key = reference.child(matricula).push();
                    key.setValue(createDTO(nota)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful() && task.isComplete()) {
                                if (nota.getUriFotoDisp() != null && !nota.getUriFotoDisp().isEmpty()) {
                                    uploadPhoto(nota, nota.getUriFotoDisp(), nota.getIdFoto(), key);
                                } else {
                                    updateFields(nota, null, key.getKey(), true);
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
        NotaServicoDTO dto = new NotaServicoDTO(ct.getDadosQrCode(), ct.getIdFoto(), ct.getLatitude(),
                ct.getLongitude(), ct.getEnderecoManual(), ct.getTimesTamp(), ct.getLocalEntregaCorresp());
        dto.setLeitura(((NotaServico) ct).getLeitura());
        dto.setMedidorExterno(((NotaServico) ct).getMedidorExterno());
        dto.setMedidorVizinho(((NotaServico) ct).getMedidorVizinho());
        return dto;
    }

    @Override
    public void uploadPhoto(final NotaServico nota, String uriPhotoDisp, String namePhoto, final DatabaseReference key) {
        StorageReference storageReference = storage.getReference().child(getmContext().getResources().getString(R.string.firebase_storage_nota_servico)).child(matricula).child(namePhoto);

        Bitmap bitmap = BitmapFactory.decodeFile(uriPhotoDisp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
                            updateFields(nota, downloadUrl, key.getKey(), true);
                        } else {
                            updateFields(nota, downloadUrl, key.getKey(), false);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        updateFields(nota, downloadUrl, key.getKey(), false);
                        Log.e(TAG, e.getMessage());
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                updateFields(nota, null, key.getKey(), false);
            }
        });
    }

    @Override
    public void updateFields(NotaServico nota, String downloadUrl, String key, boolean canUpdateColumnSitFirebase) {
        MailDeliveryDBNotaServico db = new MailDeliveryDBNotaServico(getmContext());
        if (canUpdateColumnSitFirebase) {
            nota.setSitSalvoFirebase(1);
        }
        nota.setKeyRealtimeFb(key);
        if (downloadUrl != null) {
            nota.setUrlStorageFoto(downloadUrl);
        } else {
            nota.setUrlStorageFoto(null);
        }
        if (nota.getContext() == null) {
            nota.setContext(getmContext());
        }
        db.save(nota);
        if (getListenerService() != null) {
            getListenerService().notifyEndService();
        }
    }
}

class NotaServicoDTO extends GenericDTO {
    private String leitura;
    private String medidorVizinho;
    private String medidorExterno;

    public NotaServicoDTO(String dadosQrCode, String idFoto, double latitude, double longitude, String enderecoManual, long timeStamp, String localEntrega) {
        super(dadosQrCode, idFoto, latitude, longitude, enderecoManual, timeStamp, localEntrega);
    }

    public NotaServicoDTO() {
    }

    public String getLeitura() {
        return leitura;
    }

    public void setLeitura(String leitura) {
        this.leitura = leitura;
    }

    public String getMedidorVizinho() {
        return medidorVizinho;
    }

    public void setMedidorVizinho(String medidorVizinho) {
        this.medidorVizinho = medidorVizinho;
    }

    public String getMedidorExterno() {
        return medidorExterno;
    }

    public void setMedidorExterno(String medidorExterno) {
        this.medidorExterno = medidorExterno;
    }
}
