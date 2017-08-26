package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;
import android.content.Context;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class NotaServico extends GenericDelivery {
    private String leitura;
    private String medidorVizinho;
    private String medidorExterno;

    public NotaServico(Context context, String dadosQrCode, Long timesTamp, String prefixAgrupador, String idFoto, Double latitude, Double longitude, String uriFotoDisp, String enderecoManual, int sitSalvoFirebase, String localEntregaCorresp, String urlStorageFoto) {
        super(context, dadosQrCode, timesTamp, prefixAgrupador, idFoto, latitude, longitude, uriFotoDisp, enderecoManual, sitSalvoFirebase, localEntregaCorresp, urlStorageFoto);
    }

    public NotaServico() {
    }

    @Override
    public ContentValues getValuesInsert() {
        ContentValues values = super.getValuesInsert();
        if (getLeitura() != null) {
            values.put(getContext().getResources().getString(R.string.leitura), getLeitura());
        }
        if (getMedidorExterno() != null) {
            values.put(getContext().getResources().getString(R.string.medidor_externo), getMedidorExterno().equalsIgnoreCase("sim") ? 1 : 0);

        }
        if (getMedidorVizinho() != null) {
            values.put(getContext().getResources().getString(R.string.medidor_vizinho), getMedidorVizinho());

        }
        return values;
    }

    public String getLeitura() {
        return leitura;
    }

    public void setLeitura(String leitura) {
        this.leitura = leitura;
    }

    public String getMedidorVizinho() {
        return medidorVizinho;
    }

    public void setMedidorVizinho(String medidorVizinho) {
        this.medidorVizinho = medidorVizinho;
    }

    public String getMedidorExterno() {
        return medidorExterno;
    }

    public void setMedidorExterno(String medidorExterno) {
        this.medidorExterno = medidorExterno;
    }

}
