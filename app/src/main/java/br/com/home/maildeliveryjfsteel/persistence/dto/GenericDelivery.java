package br.com.home.maildeliveryjfsteel.persistence.dto;

import android.content.ContentValues;
import android.content.Context;

import br.com.home.maildeliveryjfsteel.R;

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
    private String keyRealtimeFb;

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

    public ContentValues getValuesInsert(Context context) {
        ContentValues values = new ContentValues();
        if (getId() != null) {
            values.put(context.getString(R.string.id_sqlite), getId());
        }
        if (getKeyRealtimeFb() != null) {
            values.put(context.getString(R.string.key_realtime_fb), getKeyRealtimeFb());
        }
        if (getDadosQrCode() != null) {
            values.put(context.getString(R.string.dados_qr_code), getDadosQrCode());
        }
        if (getTimesTamp() != null && getTimesTamp() != 0l) {
            values.put(context.getString(R.string.hora_entrega), getTimesTamp());
        }
        if (getPrefixAgrupador() != null) {
            values.put(context.getString(R.string.prefix_agrupador), getPrefixAgrupador());
        }
        if (getIdFoto() != null) {
            values.put(context.getString(R.string.id_foto), getIdFoto());
        }
        if (getLatitude() != 0) {
            values.put(context.getString(R.string.latitude), getLatitude());
            values.put(context.getString(R.string.longitude), getLongitude());
        } else {
            values.put(context.getString(R.string.endereco_manual), getEnderecoManual());
        }
        if (getUriFotoDisp() != null) {
            values.put(context.getString(R.string.uri_foto_disp), getUriFotoDisp());
        }
        values.put(context.getString(R.string.sit_salvo_firebase), getSitSalvoFirebase());
        if (getLocalEntregaCorresp() != null) {
            values.put(context.getString(R.string.local_entrega_corresp), getLocalEntregaCorresp());
        }
        if (getUrlStorageFoto() != null) {
            values.put(context.getString(R.string.url_storage_foto), getUrlStorageFoto());
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

    public String getKeyRealtimeFb() {
        return keyRealtimeFb;
    }

    public void setKeyRealtimeFb(String keyRealtimeFb) {
        this.keyRealtimeFb = keyRealtimeFb;
    }
}
