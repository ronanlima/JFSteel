package br.com.home.maildeliveryjfsteel.persistence.dto;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class NotaServico extends GenericDelivery {
    private String leitura;
    private String medidorVizinho;
    private String medidorExterno;

    public NotaServico() {
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
