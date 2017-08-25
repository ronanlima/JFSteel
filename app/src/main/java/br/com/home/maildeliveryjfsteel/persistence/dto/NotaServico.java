package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class NotaServico extends GenericDelivery {
    public static final String COLUNA_LEITURA = "leitura";
    public static final String COLUNA_MEDIDOR_EXTERNO = "medidorVizinho";
    public static final String COLUNA_MEDIDOR_VIZINHO = "medidorExterno";
    private String leitura;
    private String medidorVizinho;
    private String medidorExterno;
    private static final String[] colunasNota = {COLUNA_LEITURA, COLUNA_MEDIDOR_EXTERNO, COLUNA_MEDIDOR_VIZINHO};

    public NotaServico(String dadosQrCode, Long timesTamp, String prefixAgrupador, String idFoto, Double latitude, Double longitude, String uriFotoDisp, String enderecoManual, int sitSalvoFirebase, String localEntregaCorresp, String urlStorageFoto) {
        super(dadosQrCode, timesTamp, prefixAgrupador, idFoto, latitude, longitude, uriFotoDisp, enderecoManual, sitSalvoFirebase, localEntregaCorresp, urlStorageFoto);
    }

    public NotaServico() {
    }

    @Override
    public ContentValues getValuesInsert() {
        ContentValues values = super.getValuesInsert();
        if (getLeitura() != null) {
            values.put(COLUNA_LEITURA, getLeitura());
        }
        if (getMedidorExterno() != null) {
            values.put(COLUNA_MEDIDOR_EXTERNO, getMedidorExterno().equalsIgnoreCase("sim") ? 1 : 0);

        }
        if (getMedidorVizinho() != null) {
            values.put(COLUNA_MEDIDOR_VIZINHO, getMedidorVizinho());

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
