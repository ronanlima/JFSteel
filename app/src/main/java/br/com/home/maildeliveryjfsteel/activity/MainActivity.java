package br.com.home.maildeliveryjfsteel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.async.FirebaseAsyncParam;
import br.com.home.maildeliveryjfsteel.async.SaveFirebaseAsync;
import br.com.home.maildeliveryjfsteel.camera.HandlerQrCodeActivity;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseContaNormalImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNoQrCodeImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNotaImpl;
import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBNotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryNoQrCode;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (!isMatriculaNotNull()) {
            DialogFragment dialog = MatriculaDialogFragment.newInstance(setListener());
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "dialogMatricula");
        } else {
            startActivity(new Intent(this, HandlerQrCodeActivity.class));
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBContaNormal(getBaseContext()).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseContaNormalImpl(getBaseContext(), null)));
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBNotaServico(getBaseContext()).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseNotaImpl(getBaseContext(), null)));
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryNoQrCode(getBaseContext()).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseNoQrCodeImpl(getBaseContext(), null)));
            finish();
        }
    }

    public MatriculaDialogFragment.ClickButtonEntrar setListener() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {
                saveMatricula(matricula);
                startActivity(new Intent(getApplicationContext(), HandlerQrCodeActivity.class));
                finish();
            }
        };
    }

    /**
     * Salva a matrícula informada no sharedPreferences para consulta no próximo acesso.
     *
     * @param matricula
     */
    private void saveMatricula(String matricula) {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(getResources().getString(R.string.sp_matricula), matricula);
        edit.commit();
    }

    /**
     * Verifica a existência de matrícula para não pedir novamente.
     *
     * @return
     */
    private boolean isMatriculaNotNull() {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String matricula = sp.getString(getResources().getString(R.string.sp_matricula), null);
        return matricula != null && !matricula.isEmpty();
    }
}
