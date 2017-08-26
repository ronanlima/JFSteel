package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;
import android.content.Context;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class NoQrCode extends GenericDelivery {
    private int medidor;
    private int existeConta;
    private String comentario;

    public NoQrCode(Context context, String dadosQrCode, Long timesTamp, String prefixAgrupador, String idFoto, Double latitude, Double longitude, String uriFotoDisp, String enderecoManual, int sitSalvoFirebase, String localEntregaCorresp, String urlStorageFoto) {
        super(context, dadosQrCode, timesTamp, prefixAgrupador, idFoto, latitude, longitude, uriFotoDisp, enderecoManual, sitSalvoFirebase, localEntregaCorresp, urlStorageFoto);
    }

    public NoQrCode() {
    }

    @Override
    public ContentValues getValuesInsert() {
        ContentValues values = super.getValuesInsert();
        values.put(getContext().getResources().getString(R.string.medidor), getMedidor());
        values.put(getContext().getResources().getString(R.string.existe_conta), getExisteConta());
        if (getComentario() != null && !getComentario().trim().isEmpty()) {
            values.put(getContext().getResources().getString(R.string.comentario), getComentario());
        }
        return values;
    }

    public int getMedidor() {
        return medidor;
    }

    public void setMedidor(int medidor) {
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
