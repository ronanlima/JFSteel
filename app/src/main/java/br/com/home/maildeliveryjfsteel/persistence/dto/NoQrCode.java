package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;
import android.content.Context;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class NoQrCode extends GenericDelivery {
    private String medidor;
    private int existeConta;
    private String comentario;

    public NoQrCode(Long timesTamp, String prefixAgrupador, String idFoto, Double latitude, Double longitude, String uriFotoDisp, String enderecoManual, int sitSalvoFirebase, String localEntregaCorresp) {
        setTimesTamp(timesTamp);
        setPrefixAgrupador(prefixAgrupador);
        setIdFoto(idFoto);
        if (latitude != 0d) {
            setLatitude(latitude);
            setLongitude(longitude);
        } else {
            setEnderecoManual(enderecoManual);
        }
        setUriFotoDisp(uriFotoDisp);
        setSitSalvoFirebase(sitSalvoFirebase);
        setLocalEntregaCorresp(localEntregaCorresp);
    }

    public NoQrCode() {
    }

    @Override
    public ContentValues getValuesInsert(Context context) {
        ContentValues values = super.getValuesInsert(context);
        values.put(context.getString(R.string.medidor), getMedidor());
        values.put(context.getString(R.string.existe_conta), getExisteConta());
        if (getComentario() != null && !getComentario().trim().isEmpty()) {
            values.put(context.getString(R.string.comentario), getComentario());
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
