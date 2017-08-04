package br.com.home.maildeliveryjfsteel.utils;

import android.os.Bundle;

import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;

/**
 * Created by Ronan.lima on 04/08/17.
 */

public class AlertUtils {

    /**
     * @param titulo
     * @param msg
     * @param tipo
     * @param isBotaoNegativoPositivo
     * @param listener
     * @return
     */
    public static JFSteelDialog criarAlerta(String titulo, String msg, JFSteelDialog.TipoAlertaEnum tipo, boolean isBotaoNegativoPositivo, JFSteelDialog.OnClickDialog listener) {
        Bundle parametros = new Bundle();
        parametros.putString(JFSteelDialog.TITULO_DIALOG, titulo);
        parametros.putString(JFSteelDialog.DESCRICAO_DIALOG, msg);
        parametros.putSerializable(JFSteelDialog.TIPO_ALERTA, tipo);
        parametros.putBoolean(JFSteelDialog.POSITIVO_NEGATIVO_BOTAO, isBotaoNegativoPositivo);
        parametros.putSerializable(JFSteelDialog.OnClickDialog.ON_CLICK_LISTENER_ARG, listener);

        JFSteelDialog dialog = new JFSteelDialog();
        dialog.setArguments(parametros);

        return dialog;
    }

}
