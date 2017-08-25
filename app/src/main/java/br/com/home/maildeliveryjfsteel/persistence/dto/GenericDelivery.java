package br.com.home.maildeliveryjfsteel.persistence.dto;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class GenericDelivery {
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
