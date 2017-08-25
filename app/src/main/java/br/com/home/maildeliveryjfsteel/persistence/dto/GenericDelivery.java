package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class GenericDelivery {

    public static final String COLUNA_ID = "_id";
    public static final String COLUNA_DADOS_QR_CODE = "dadosQrCode";
    public static final String COLUNA_HORA_ENTREGA = "horaEntrega";
    public static final String COLUNA_PREFIX = "prefixAgrupador";
    public static final String COLUNA_ID_FOTO = "idFoto";
    public static final String COLUNA_LATITUDE = "latitude";
    public static final String COLUNA_LONGITUDE = "longitude";
    public static final String COLUNA_URI_FOTO = "uriFotoDisp";
    public static final String COLUNA_ENDERECO_MANUAL = "enderecoManual";
    public static final String COLUNA_SIT_SALVO_FIREBASE = "sitSalvoFirebase";
    public static final String COLUNA_LOCAL_ENTREGA_CORRESP = "localEntregaCorresp";
    public static final String COLUNA_URL_STORAGE = "urlStorageFoto";
    public static final String COLUNA_CONTA_PROTOCOLADA = "contaProtocolada";
    public static final String COLUNA_CONTA_COLETIVA = "contaColetiva";

    private Long id;
    private String dadosQrCode;
    private Long timesTamp;
    private String prefixAgrupador; // recuperar esta informação de alguma parte do qr code
    private String idFoto;
    private double latitude;
    private double longitude;
    private String uriFotoDisp;
    private String urlStorageFoto;
    private String enderecoManual; // para quando o gps nao conseguir recuperar a coordenada.
    private int sitSalvoFirebase;
    private String localEntregaCorresp; // o tipo de residencia em que a conta foi entregue. Ex: condomínio, casa, poste, etc.
    public static final String[] colunas = {COLUNA_ID, COLUNA_DADOS_QR_CODE, COLUNA_HORA_ENTREGA, COLUNA_PREFIX,
            COLUNA_ID_FOTO, COLUNA_LATITUDE, COLUNA_LONGITUDE, COLUNA_URI_FOTO, COLUNA_ENDERECO_MANUAL, COLUNA_SIT_SALVO_FIREBASE, COLUNA_LOCAL_ENTREGA_CORRESP,
            COLUNA_URL_STORAGE, COLUNA_CONTA_PROTOCOLADA, COLUNA_CONTA_COLETIVA};

    public GenericDelivery(String dadosQrCode, Long timesTamp, String prefixAgrupador, String idFoto
            , Double latitude, Double longitude, String uriFotoDisp, String enderecoManual, int sitSalvoFirebase
            , String localEntregaCorresp, String urlStorageFoto) {
        setDadosQrCode(dadosQrCode);
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
        setUrlStorageFoto(urlStorageFoto);
    }

    public GenericDelivery() {
    }

    public ContentValues getValuesInsert() {
        ContentValues values = new ContentValues();
        if (getId() != null) {
            values.put(COLUNA_ID, getIdFoto());
        }
        if (getDadosQrCode() != null) {
            values.put(COLUNA_DADOS_QR_CODE, getDadosQrCode());
        }
        if (getTimesTamp() != null && getTimesTamp() != 0l) {
            values.put(COLUNA_HORA_ENTREGA, getTimesTamp());
        }
        if (getPrefixAgrupador() != null) {
            values.put(COLUNA_PREFIX, getPrefixAgrupador());
        }
        if (getIdFoto() != null) {
            values.put(COLUNA_ID_FOTO, getIdFoto());
        }
        if (getLatitude() != 0) {
            values.put(COLUNA_LATITUDE, getLatitude());
            values.put(COLUNA_LONGITUDE, getLongitude());
        } else {
            values.put(COLUNA_ENDERECO_MANUAL, getEnderecoManual());
        }
        if (getUriFotoDisp() != null) {
            values.put(COLUNA_URI_FOTO, getUriFotoDisp());
        }
        values.put(COLUNA_SIT_SALVO_FIREBASE, getSitSalvoFirebase());
        if (getLocalEntregaCorresp() != null) {
            values.put(COLUNA_LOCAL_ENTREGA_CORRESP, getLocalEntregaCorresp());
        }
        if (getUrlStorageFoto() != null) {
            values.put(COLUNA_URL_STORAGE, getUrlStorageFoto());
        }
        return values;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDadosQrCode() {
        return dadosQrCode;
    }

    public void setDadosQrCode(String dadosQrCode) {
        this.dadosQrCode = dadosQrCode;
    }

    public Long getTimesTamp() {
        return timesTamp;
    }

    public void setTimesTamp(Long timesTamp) {
        this.timesTamp = timesTamp;
    }

    public String getPrefixAgrupador() {
        return prefixAgrupador;
    }

    public void setPrefixAgrupador(String prefixAgrupador) {
        this.prefixAgrupador = prefixAgrupador;
    }

    public String getIdFoto() {
        return idFoto;
    }

    public void setIdFoto(String idFoto) {
        this.idFoto = idFoto;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSitSalvoFirebase() {
        return sitSalvoFirebase;
    }

    public void setSitSalvoFirebase(int sitSalvoFirebase) {
        this.sitSalvoFirebase = sitSalvoFirebase;
    }

    public String getUriFotoDisp() {
        return uriFotoDisp;
    }

    public void setUriFotoDisp(String uriFotoDisp) {
        this.uriFotoDisp = uriFotoDisp;
    }

    public String getUrlStorageFoto() {
        return urlStorageFoto;
    }

    public void setUrlStorageFoto(String urlStorageFoto) {
        this.urlStorageFoto = urlStorageFoto;
    }

    public String getEnderecoManual() {
        return enderecoManual;
    }

    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }

    public String getLocalEntregaCorresp() {
        return localEntregaCorresp;
    }

    public void setLocalEntregaCorresp(String localEntregaCorresp) {
        this.localEntregaCorresp = localEntregaCorresp;
    }
}
