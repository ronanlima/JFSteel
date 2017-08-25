package br.com.home.maildeliveryjfsteel.persistence.dto;

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
