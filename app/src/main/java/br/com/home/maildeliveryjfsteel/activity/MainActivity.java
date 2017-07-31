package br.com.home.maildeliveryjfsteel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.markosullivan.wizards.MainActivityWizard;

import java.util.List;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.async.FirebaseAsyncParam;
import br.com.home.maildeliveryjfsteel.async.SaveFirebaseAsync;
import br.com.home.maildeliveryjfsteel.camera.HandlerQrCodeActivity;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseContaNormalImpl;
import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;

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
//            startActivity(new Intent(this, MainActivityWizard.class));
//            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBContaNormal(this).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseContaNormalImpl(this)));
////            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBContaNormal(this).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseContaNormalImpl(this)));
////            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBContaNormal(this).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseContaNormalImpl(this)));
//            startActivity(new Intent(this, CameraActivity.class));
//            finish();
        }
    }

    @Override
    protected void onActivityResult(int codigo, int codigoRetorno, Intent it) {
        super.onActivityResult(codigo, codigoRetorno, it);
        if (codigo == IntentIntegrator.REQUEST_CODE) {
            IntentResult scanResult = IntentIntegrator.parseActivityResult(codigo, codigoRetorno, it);
            if (scanResult == null || scanResult.getContents() == null) {
                return;
            }

            final String codBarra = scanResult.getContents();

//            Intent i = new Intent(getApplicationContext(), ConfirmarTransferenciaSegundoFator.class);
//            i.putExtra("qrCodeContent", codBarra);
//            i.putExtra("conta", conta_string);
//            startActivity(i);
//            overridePendingTransition(R.anim.para_cima_entra, R.anim.fica);
        }
    }

    /**
     * Recuperar os registros no db sqlite que tenham a coluna sitSalvoFirebase = 0
     */
    private List<GenericDelivery> getRegistersContaNormalNotSaved() {
        MailDeliverDBService db = new MailDeliveryDBContaNormal(this);
        return db.findBySit(MailDeliverDBService.SIT_FALSE);
    }

    public MatriculaDialogFragment.ClickButtonEntrar setListener() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {
                saveMatricula(matricula);
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
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
