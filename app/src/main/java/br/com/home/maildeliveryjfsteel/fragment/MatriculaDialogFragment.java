package br.com.home.maildeliveryjfsteel.fragment;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class MatriculaDialogFragment extends DialogFragment {

    protected static final String ALERTA_VAZIO = "Os campos de matrícula devem ser informados";
    private EditText editMatricula, editConfMatricula;
    private Button btnEntrar;
    private ClickButtonEntrar listener;

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
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_matricula, container, false);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");
        listener = (ClickButtonEntrar) getArguments().getSerializable("listener");

        editMatricula = (EditText) v.findViewById(R.id.matricula);
        editConfMatricula = (EditText) v.findViewById(R.id.conf_matricula);
        btnEntrar = (Button) v.findViewById(R.id.btn_entrar);
        editMatricula.setTypeface(typeface);
        editConfMatricula.setTypeface(typeface);
        btnEntrar.setTypeface(typeface);
        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMatricula.getText() == null || editMatricula.getText().toString().isEmpty()
                        || editConfMatricula.getText() == null || editConfMatricula.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), ALERTA_VAZIO, Toast.LENGTH_SHORT).show();
                } else {
                    listener.nextActivity(editConfMatricula.getText().toString());
                }
            }
        });
        btnEntrar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (editMatricula.getText() == null || editMatricula.getText().toString().isEmpty()
                        || editConfMatricula.getText() == null || editConfMatricula.getText().toString().isEmpty()) {
                    Toast.makeText(getActivity(), ALERTA_VAZIO, Toast.LENGTH_SHORT).show();
                } else {
                    listener.nextActivity(editConfMatricula.getText().toString());
                }
                return true;
            }
        });

        return v;
    }

    public void validaClick(View v) {
        if (editMatricula.getText() == null || editMatricula.getText().toString().isEmpty()
                || editConfMatricula.getText() == null || editConfMatricula.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), ALERTA_VAZIO, Toast.LENGTH_SHORT).show();
        } else {
            listener.nextActivity(editConfMatricula.getText().toString());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_dialog_matricula, null));
        return builder.create();
    }

    public interface ClickButtonEntrar extends Serializable {
        void nextActivity(String matricula);
    }
}
