package br.com.home.maildeliveryjfsteel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;

import com.crashlytics.android.Crashlytics;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.async.FirebaseAsyncParam;
import br.com.home.maildeliveryjfsteel.async.SaveFirebaseAsync;
import br.com.home.maildeliveryjfsteel.camera.HandlerQrCodeActivity;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseContaNormalImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNoQrCodeImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNotaImpl;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBNotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryNoQrCode;
import br.com.home.maildeliveryjfsteel.utils.AlertUtils;

public class MainActivity extends AppCompatActivity {
    public static final long DEFAULT_TIMESTAMP = 0l;
    private DialogFragment dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (!isMatriculaNotNull()) {
            dialog = MatriculaDialogFragment.newInstance(setListener(), 0);
            dialog.setCancelable(false);
        } else {
            Long timestampUltimoRegistroLido = getTimestampUltimoRegistroLido();
            long dataAtual = System.currentTimeMillis();
            if (dataAtual < timestampUltimoRegistroLido) {
                JFSteelDialog dialog = AlertUtils.criarAlerta(getString(R.string.titulo_dispositivo_horario_invalido), getString(R.string.msg_dispositivo_horario_invalido), JFSteelDialog.TipoAlertaEnum.ALERTA, false, new JFSteelDialog.OnClickDialog() {
                    @Override
                    public void onClickPositive(View v, String tag) {

                    }

                    @Override
                    public void onClickNegative(View v, String tag) {
                        finish();
                    }

                    @Override
                    public void onClickNeutral(View v, String tag) {

                    }
                });
                dialog.show(getSupportFragmentManager(), "dialog");
            } else {
                startActivity(new Intent(this, HandlerQrCodeActivity.class));
                new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBContaNormal(getBaseContext()).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseContaNormalImpl(getBaseContext())));
                new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryDBNotaServico(getBaseContext()).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseNotaImpl(getBaseContext())));
                new SaveFirebaseAsync().execute(new FirebaseAsyncParam(new MailDeliveryNoQrCode(getBaseContext()).findBySit(MailDeliverDBService.SIT_FALSE), new FirebaseNoQrCodeImpl(getBaseContext())));
                finish();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dialog != null) {
            dialog.show(getSupportFragmentManager(), "dialogMatricula");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog = null;
        }
    }

    public MatriculaDialogFragment.ClickButtonEntrar setListener() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {
                Crashlytics.setUserIdentifier(matricula);
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
        edit.apply();
    }

    private Long getTimestampUltimoRegistroLido() {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        return sp.getLong(getString(R.string.sp_ultimo_registro_lido), DEFAULT_TIMESTAMP);
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
