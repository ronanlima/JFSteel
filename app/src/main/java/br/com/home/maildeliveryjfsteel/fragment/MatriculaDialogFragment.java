package br.com.home.maildeliveryjfsteel.fragment;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class MatriculaDialogFragment extends DialogFragment {

    protected static final String ALERTA_VAZIO = "Os campos de matrícula devem ser informados.";
    protected static final String ALERTA_MATRICULA_DIFERENTE = "A matrícula está inválida. Verifique o número informado.";
    private EditText editMatricula, editConfMatricula;
    private Button btnEntrar;
    private ClickButtonEntrar listener;

    /**
     * Mapeia o click no botão de entrar para validar os campos informados e prosseguir para a próxima
     * tela.
     */
    View.OnClickListener btnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (editMatricula.getText() == null || editMatricula.getText().toString().isEmpty()
                    || editConfMatricula.getText() == null || editConfMatricula.getText().toString().isEmpty()) {
                Toast.makeText(getActivity(), ALERTA_VAZIO, Toast.LENGTH_SHORT).show();
            } else if (!editMatricula.getText().toString().equals(editConfMatricula.getText().toString())) {
                Toast.makeText(getActivity(), ALERTA_MATRICULA_DIFERENTE, Toast.LENGTH_SHORT).show();
            } else {
                listener.nextActivity(editConfMatricula.getText().toString());
            }
        }
    };

    public static MatriculaDialogFragment newInstance(ClickButtonEntrar listenerEntrar) {
        MatriculaDialogFragment mdf = new MatriculaDialogFragment();

        Bundle b = new Bundle();
        b.putSerializable("listener", listenerEntrar);
        mdf.setArguments(b);

        return mdf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Holo_Light_Panel);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.fragment_dialog_matricula, null);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        listener = (ClickButtonEntrar) getArguments().getSerializable("listener");
        builder.setView(v);

        initFields(v, typeface);
        return builder.create();
    }

    /**
     * Inicializa os campos
     * @param v
     * @param typeface
     */
    private void initFields(View v, Typeface typeface) {
        editMatricula = (EditText) v.findViewById(R.id.matricula);
        editConfMatricula = (EditText) v.findViewById(R.id.conf_matricula);
        btnEntrar = (Button) v.findViewById(R.id.btn_entrar);
        editMatricula.setTypeface(typeface);
        editConfMatricula.setTypeface(typeface);
        btnEntrar.setTypeface(typeface);
        btnEntrar.setOnClickListener(btnListener);
    }

    public interface ClickButtonEntrar extends Serializable {
        void nextActivity(String matricula);
    }
}
