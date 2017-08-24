package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;

import java.io.Serializable;
import java.util.List;

import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;

/**
 * Created by Ronan.lima on 16/08/17.
 */

public abstract class FirebaseServiceImpl<T> implements FirebaseService<T> {
    private Context mContext;
    private ServiceNotification listenerService;

    public FirebaseServiceImpl(Context context, ServiceNotification listenerService) {
        this.mContext = context;
        this.listenerService = listenerService;
    }

    public GenericDTO createDTO(GenericDelivery contaDelivery) {
        GenericDTO dto = new GenericDTO();
        dto.setDadosQrCode(contaDelivery.getDadosQrCode());
        dto.setIdFoto(contaDelivery.getIdFoto());
        dto.setTimeStamp(contaDelivery.getTimesTamp());
        if (contaDelivery.getLatitude() != 0d) {
            dto.setLatitude(contaDelivery.getLatitude());
            dto.setLongitude(contaDelivery.getLongitude());
        } else {
            dto.setLocalEntrega(contaDelivery.getLocalEntregaCorresp());
        }
        return dto;
    }

    /**
     * Listener utilizado para notificar a Activity que chamou os métodos dessa classe, do seu término
     */
    public interface ServiceNotification extends Serializable {
        void notifyEndService();
    }

    public Context getmContext() {
        return mContext;
    }

    public ServiceNotification getListenerService() {
        return listenerService;
    }
}

class GenericDTO {
    private String dadosQrCode;
    private String idFoto;
    private double latitude;
    private double longitude;
    private String enderecoManual;
    private long timeStamp;
    private String uriFotoDisp;
    private String localEntrega;

    public String getDadosQrCode() {
        return dadosQrCode;
    }

    public void setDadosQrCode(String dadosQrCode) {
        this.dadosQrCode = dadosQrCode;
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

    public String getEnderecoManual() {
        return enderecoManual;
    }

    public void setEnderecoManual(String enderecoManual) {
        this.enderecoManual = enderecoManual;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUriFotoDisp() {
        return uriFotoDisp;
    }

    public void setUriFotoDisp(String uriFotoDisp) {
        this.uriFotoDisp = uriFotoDisp;
    }

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }
}
