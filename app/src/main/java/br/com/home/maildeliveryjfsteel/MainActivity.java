package br.com.home.maildeliveryjfsteel;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DialogFragment dialog = new MatriculaDialogFragment();
        Bundle b = new Bundle();
        b.putSerializable("listener", setListener());
        dialog.setArguments(b);
    }

    public MatriculaDialogFragment.ClickButtonEntrar setListener() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {

            }
        };
    }
}
