package br.com.home.maildeliveryjfsteel.persistence;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public enum TipoResidencia {
    RESIDENCIAL(0, "Residencial"), COMERCIAL(1, "Comercial"), INDUSTRIAL(2, "Industrial"), POSTE(3, "Poste");

    private Integer id;
    private String desc;

    TipoResidencia(Integer id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
