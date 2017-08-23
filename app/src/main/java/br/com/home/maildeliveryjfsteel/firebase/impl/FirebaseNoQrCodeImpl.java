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
                reference.child(matricula).push().setValue(item).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful() && task.isComplete()) {
                            updateFields(item, null);
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
    public void uploadPhoto(NoQrCode obj, String uriPhotoDisp, String namePhoto) {

    }

    @Override
    public void updateFields(NoQrCode obj, String downloadUrl) {
        MailDeliveryNoQrCode db = new MailDeliveryNoQrCode(getmContext());
        obj.setSitSalvoFirebase(1);
        db.save(obj);
    }
}
