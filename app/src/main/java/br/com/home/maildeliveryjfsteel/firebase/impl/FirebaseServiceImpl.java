package br.com.home.maildeliveryjfsteel.firebase.impl;

import android.content.Context;

import java.io.File;

import br.com.home.jfsteelbase.AppExecutors;
import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;

/**
 * Created by Ronan.lima on 16/08/17.
 */

public abstract class FirebaseServiceImpl<T> implements FirebaseService<T> {
    private Context mContext;
    public static final Integer HIGH_PRIORITY = 1;
    public static final Integer DEFAULT_PRIORITY = 2;
    public static final Integer LOW_PRIORITY = 3;

    public FirebaseServiceImpl(Context context) {
        this.mContext = context;
    }

    public GenericDTO createDTO(GenericDelivery contaDelivery) {
        GenericDTO dto = new GenericDTO(contaDelivery.getDadosQrCode(), contaDelivery.getIdFoto(),
                contaDelivery.getLatitude(), contaDelivery.getLongitude(), contaDelivery.getEnderecoManual(),
                contaDelivery.getTimesTamp(), contaDelivery.getLocalEntregaCorresp());
        return dto;
    }

    public Context getmContext() {
        return mContext;
    }

    void deleteFile(final String uriPhotoDisp) {
        if (uriPhotoDisp != null && !uriPhotoDisp.trim().isEmpty()) {
            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    File f = new File(uriPhotoDisp);
                    boolean isDeleted = f.delete();
                }
            });
        }
    }
}

class GenericDTO {
    private String dadosQrCode;
    private String idFoto;
    private double latitude;
    private double longitude;
    private String enderecoManual;
    private long timeStamp;
    private String localEntrega;

    public GenericDTO(String dadosQrCode, String idFoto, double latitude, double longitude,
                      String enderecoManual, long timeStamp, String localEntrega) {
        setDadosQrCode(dadosQrCode);
        setIdFoto(idFoto);
        if (latitude != 0d) {
            setLatitude(latitude);
            setLongitude(longitude);
        } else {
            setEnderecoManual(enderecoManual);
        }
        setTimeStamp(timeStamp);
        setLocalEntrega(localEntrega);
    }

    public GenericDTO() {
    }

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

    public String getLocalEntrega() {
        return localEntrega;
    }

    public void setLocalEntrega(String localEntrega) {
        this.localEntrega = localEntrega;
    }
}
