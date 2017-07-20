package br.com.home.maildeliveryjfsteel.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;

public class MainActivity extends AppCompatActivity {

    public static final String MATRICULA = "matricula";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (!isMatriculaNotNull()) {
            DialogFragment dialog = MatriculaDialogFragment.newInstance(setListener());
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "dialogMatricula");
        } else {
            startActivity(new Intent(this, CameraActivity.class));
            finish();
        }
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
     * @param matricula
     */
    private void saveMatricula(String matricula) {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(MATRICULA, matricula);
        edit.commit();
    }

    /**
     * Verifica a existência de matrícula para não pedir novamente.
     * @return
     */
    private boolean isMatriculaNotNull() {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        String matricula = sp.getString(MATRICULA, null);
        return matricula != null && !matricula.isEmpty();
    }
}