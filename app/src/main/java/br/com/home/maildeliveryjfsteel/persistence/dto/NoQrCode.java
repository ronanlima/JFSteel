package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class NoQrCode extends GenericDelivery {
    public static final String COLUNA_MEDIDOR = "medidor";
    public static final String COLUNA_EXISTE_CONTA = "existeConta";
    public static final String COLUNA_COMENTARIO = "comentario";
    private String medidor;
    private int existeConta;
    private String comentario;
    public static final String[] colunasNoQr = {COLUNA_MEDIDOR, COLUNA_EXISTE_CONTA, COLUNA_COMENTARIO};

    public NoQrCode() {
    }

    @Override
    public ContentValues getValuesInsert() {
        ContentValues values = super.getValuesInsert();
        if (getMedidor() != null && !getMedidor().trim().isEmpty()) {
            values.put(COLUNA_MEDIDOR, getMedidor());
        }
        values.put(COLUNA_EXISTE_CONTA, getExisteConta());
        if (getComentario() != null && !getComentario().trim().isEmpty()) {
            values.put(COLUNA_COMENTARIO, getComentario());
        }
        return values;
    }

    public String getMedidor() {
        return medidor;
    }

    public void setMedidor(String medidor) {
        this.medidor = medidor;
    }

    public int getExisteConta() {
        return existeConta;
    }

    public void setExisteConta(int existeConta) {
        this.existeConta = existeConta;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }
}
