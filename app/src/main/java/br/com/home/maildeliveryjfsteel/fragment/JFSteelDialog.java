package br.com.home.maildeliveryjfsteel.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 02/08/17.
 */

public class JFSteelDialog extends DialogFragment {

    public static final String TITULO_DIALOG = "TITULO_DIALOG";
    public static final String DESCRICAO_DIALOG = "DESCRICAO_DIALOG";
    public static final String TIPO_ALERTA = "TIPO_ALERTA";
    public static final String POSITIVO_NEGATIVO_BOTAO = "POSITIVO_NEGATIVO_BOTAO";
    public static final String NOME_NEGATIVO_BOTAO = "NOME_NEGATIVO_BOTAO";
    public static final String NOME_NEUTRO_BOTAO = "NOME_NEUTRO_BOTAO";
    public static final String NOME_POSITIVO_BOTAO = "NOME_POSITIVO_BOTAO";

    private EditText enderecoEditText;
    private TextView tituloTextView;
    private TextView descricaoAlertaTextView;
    private ImageView alertaImageView;
    private Button simButton;
    private Button naoButton;
    private Button neutroButton;

    private OnClickDialog onClickDialog;
    private Boolean isPositivoNegativoButton;
    private TipoAlertaEnum tipoAlertaEnum;

    private ViewGroup layoutEntradaDados;

    /**
     * private Button pdfButton;
     * private Button imgButton;
     * <p>
     * private ViewGroup layoutExportar;
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alert_dialog, viewGroup, false);

        tituloTextView = (TextView) view.findViewById(R.id.titulo);
        descricaoAlertaTextView = (TextView) view.findViewById(R.id.descricao_alerta);
        alertaImageView = (ImageView) view.findViewById(R.id.img_alert);
        simButton = (Button) view.findViewById(R.id.bt_sim);
        naoButton = (Button) view.findViewById(R.id.bt_nao);
        neutroButton = (Button) view.findViewById(R.id.bt_neutro);

        enderecoEditText = (EditText) view.findViewById(R.id.edit_dados);

        layoutEntradaDados = (ViewGroup) view.findViewById(R.id.layout_entrada_dados);
        layoutEntradaDados.setVisibility(View.GONE);

        /**pdfButton = (Button) view.findViewById(R.id.bt_pdf);
         imgButton = (Button) view.findViewById(R.id.bt_img);

         layoutExportar = (ViewGroup) view.findViewById(R.id.layout_opcoes_exportar);
         layoutExportar.setVisibility(View.GONE); */

        onClickDialog = (OnClickDialog) getArguments().getSerializable(OnClickDialog.ON_CLICK_LISTENER_ARG);

        isPositivoNegativoButton = getArguments().getBoolean(POSITIVO_NEGATIVO_BOTAO);
        tipoAlertaEnum = (TipoAlertaEnum) getArguments().getSerializable(TIPO_ALERTA);

        if (isPositivoNegativoButton != null && !isPositivoNegativoButton) {
            simButton.setVisibility(View.GONE);
            neutroButton.setVisibility(View.GONE);
            naoButton.setText("OK");
        }

        naoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (tipoAlertaEnum == TipoAlertaEnum.ENTRADA_DADOS) {
                    if (!getEnderecoEditText().getText().toString().isEmpty()) {
                        InputMethodManager inputManager =
                                (InputMethodManager) getContext().
                                        getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputManager.hideSoftInputFromWindow(
                                getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        onClickDialog.onClickNeutral(v, getEnderecoEditText().getText().toString());
                        dismiss();
                    } else {
                        getEnderecoEditText().setHint(getResources().getString(R.string.hint_alert_endereco));
                    }
                } else {
                    onClickDialog.onClickNeutral(v, getTag());
                    dismiss();
                }

                if (onClickDialog != null) {
                    onClickDialog.onClickNegative(v, getTag());
                }
                dismiss();
            }
        });

        simButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickDialog != null) {
                    onClickDialog.onClickPositive(v, getTag());
                }
                dismiss();
            }
        });

        neutroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (onClickDialog != null) {
                    onClickDialog.onClickNeutral(v, getTag());
                    dismiss();
                }
            }
        });

        String titulo = getArguments().getString(TITULO_DIALOG);
        String descricao = getArguments().getString(DESCRICAO_DIALOG);
        String nomeBotaoPositivo = getArguments().getString(NOME_POSITIVO_BOTAO);
        String nomeBotaoNegativo = getArguments().getString(NOME_NEGATIVO_BOTAO);
        String nomeBotaoNeutro = getArguments().getString(NOME_NEUTRO_BOTAO);

        if (nomeBotaoPositivo != null && !nomeBotaoNegativo.isEmpty()) {
            simButton.setText(nomeBotaoPositivo);
        }

        if (nomeBotaoNegativo != null && !nomeBotaoNegativo.isEmpty()) {
            naoButton.setText(nomeBotaoNegativo);
        }

        if (nomeBotaoNeutro != null && !nomeBotaoNeutro.isEmpty()) {
            neutroButton.setText(nomeBotaoNeutro);
            neutroButton.setVisibility(View.VISIBLE);
        }

        tituloTextView.setText(titulo);
        descricaoAlertaTextView.setText(descricao);

        switch (tipoAlertaEnum) {
            case SUCESSO: {
                alertaImageView.setImageResource(R.mipmap.ic_sucesso);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    tituloTextView.setBackgroundColor(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorPrimaryDark));
                }
                break;
            }
            case ALERTA: {
                alertaImageView.setImageResource(R.mipmap.ic_alert);
//                alertaImageView.setColorFilter(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAlert));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    tituloTextView.setBackground(ContextCompat.getDrawable(getActivity().getBaseContext(), R.color.colorAlert));
                }
                break;
            }
            case EXPORTAR: {
                alertaImageView.setImageResource(R.mipmap.ic_alert);
//                alertaImageView.setColorFilter(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAlert));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    tituloTextView.setBackground(ContextCompat.getDrawable(getActivity().getBaseContext(), R.color.colorAlert));
                }
                /**layoutExportar.setVisibility(View.VISIBLE);*/
                break;
            }
            case INFORMACAO: {
                alertaImageView.setImageResource(R.mipmap.ic_alert);
//                alertaImageView.setColorFilter(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAlertInfo));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    tituloTextView.setBackground(ContextCompat.getDrawable(getActivity().getBaseContext(), R.color.colorAlertInfo));
                }
                break;
            }
            case ERRO: {
                alertaImageView.setImageResource(R.mipmap.ic_alert);
//                alertaImageView.setColorFilter(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAlertErro));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    tituloTextView.setBackground(ContextCompat.getDrawable(getActivity().getBaseContext(), R.color.colorAlertErro));
                }
                break;
            }
            case ENTRADA_DADOS: {
                alertaImageView.setImageResource(R.mipmap.ic_alert);
//                alertaImageView.setColorFilter(ContextCompat.getColor(getActivity().getBaseContext(), R.color.colorAlertErro));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    tituloTextView.setBackground(ContextCompat.getDrawable(getActivity().getBaseContext(), R.color.colorAlertErro));
                }
                layoutEntradaDados.setVisibility(View.VISIBLE);
                break;
            }
        }

        /**pdfButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        onClickDialog.onClickTipoArquivo(R.id.bt_pdf);
        dismiss();
        }
        });

         imgButton.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View v) {
        onClickDialog.onClickTipoArquivo(R.id.bt_img);
        dismiss();
        }
        });*/

        return view;
    }

    public EditText getEnderecoEditText() {
        return enderecoEditText;
    }

    public interface OnClickDialog extends Serializable {

        String ON_CLICK_LISTENER_ARG = "OnClickListener";

        void onClickPositive(View v, String tag);

        void onClickNegative(View v, String tag);

        void onClickNeutral(View v, String tag);

//        void onClickTipoArquivo(int idChecked);

    }

    public enum TipoAlertaEnum implements Serializable {
        SUCESSO, ALERTA, INFORMACAO, ERRO, EXPORTAR, ENTRADA_DADOS;
    }

}
