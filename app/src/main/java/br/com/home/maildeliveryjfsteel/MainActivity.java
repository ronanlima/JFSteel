package br.com.home.maildeliveryjfsteel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;

import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogFragment dialog = MatriculaDialogFragment.newInstance(setListener());
        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "dialogMatricula");
    }

    public MatriculaDialogFragment.ClickButtonEntrar setListener() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {
                startActivity(new Intent(getApplicationContext(), CameraActivity.class));
                finish();
            }
        };
    }
}
