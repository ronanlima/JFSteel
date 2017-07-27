package br.com.home.maildeliveryjfsteel.persistence.dto;

import br.com.home.maildeliveryjfsteel.persistence.TipoResidencia;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class Nota extends GenericDelivery {
    private String leitura;
    private String medidorVisivel;
    private boolean medidorExterno;
    private TipoResidencia tipoResidencia;

    public Nota() {
    }

    public String getLeitura() {
        return leitura;
    }

    public void setLeitura(String leitura) {
        this.leitura = leitura;
    }

    public String getMedidorVisivel() {
        return medidorVisivel;
    }

    public void setMedidorVisivel(String medidorVisivel) {
        this.medidorVisivel = medidorVisivel;
    }

    public boolean isMedidorExterno() {
        return medidorExterno;
    }

    public void setMedidorExterno(boolean medidorExterno) {
        this.medidorExterno = medidorExterno;
    }

    public TipoResidencia getTipoResidencia() {
        return tipoResidencia;
    }

    public void setTipoResidencia(TipoResidencia tipoResidencia) {
        this.tipoResidencia = tipoResidencia;
    }
}
