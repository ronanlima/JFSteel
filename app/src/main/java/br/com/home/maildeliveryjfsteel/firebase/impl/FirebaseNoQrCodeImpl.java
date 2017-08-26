package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;

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
                final DatabaseReference key = reference.child(matricula).push();
                key.setValue(createDTO(item)).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && task.isComplete()) {
                            updateFields(item, null, key.getKey(), true);
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
    public GenericDTO createDTO(GenericDelivery contaDelivery) {
        NoQrCodeDTO dto = (NoQrCodeDTO) super.createDTO(contaDelivery);
        dto.setComentario(((NoQrCode) contaDelivery).getComentario());
        dto.setExisteConta(((NoQrCode) contaDelivery).getExisteConta());
        dto.setMedidor(((NoQrCode) contaDelivery).getMedidor());
        return dto;
    }

    @Override
    public void uploadPhoto(NoQrCode ct, String uriPhotoDisp, String namePhoto, DatabaseReference key) {

    }

    @Override
    public void updateFields(NoQrCode ct, String downloadUrl, String key, boolean canUpdateColumnSitFirebase) {
        MailDeliveryNoQrCode db = new MailDeliveryNoQrCode(getmContext());
        ct.setSitSalvoFirebase(1);
        ct.setKeyRealtimeFb(key);
        db.save(ct);
    }

}

class NoQrCodeDTO extends GenericDTO {
    private String medidor;
    private int existeConta;
    private String comentario;

    public NoQrCodeDTO(String dadosQrCode, String idFoto, double latitude, double longitude, String enderecoManual, long timeStamp, String uriFotoDisp, String localEntrega) {
        super(dadosQrCode, idFoto, latitude, longitude, enderecoManual, timeStamp, uriFotoDisp, localEntrega);
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
