package br.com.home.maildeliveryjfsteel.persistence.dto;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class ContaNormal extends GenericDelivery {
    private boolean isContaProtocolada;
    private boolean isContaColetiva;

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
