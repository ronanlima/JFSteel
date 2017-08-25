package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class ContaNormal extends GenericDelivery {

    private boolean isContaProtocolada;
    private boolean isContaColetiva;

    public ContaNormal(String dadosQrCode, Long timesTamp, String prefixAgrupador, String idFoto, Double latitude, Double longitude, String uriFotoDisp, String enderecoManual, int sitSalvoFirebase, String localEntregaCorresp, String urlStorageFoto) {
        super(dadosQrCode, timesTamp, prefixAgrupador, idFoto, latitude, longitude, uriFotoDisp, enderecoManual, sitSalvoFirebase, localEntregaCorresp, urlStorageFoto);
    }

    public ContaNormal() {
    }

    @Override
    public ContentValues getValuesInsert() {
        ContentValues values = super.getValuesInsert();
        values.put(COLUNA_CONTA_COLETIVA, isContaColetiva() ? 1 : 0);
        values.put(COLUNA_CONTA_PROTOCOLADA, isContaProtocolada() ? 1 : 0);
        return values;
    }

    public boolean isContaProtocolada() {
        return isContaProtocolada;
    }

    public void setContaProtocolada(boolean contaProtocolada) {
        isContaProtocolada = contaProtocolada;
    }

    public boolean isContaColetiva() {
        return isContaColetiva;
    }

    public void setContaColetiva(boolean contaColetiva) {
        isContaColetiva = contaColetiva;
    }
}
