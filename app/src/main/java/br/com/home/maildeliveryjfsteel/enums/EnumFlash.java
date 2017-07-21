package br.com.home.maildeliveryjfsteel.enums;

import android.content.Context;
import android.hardware.Camera;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 21/07/17.
 */

public enum EnumFlash {
    DESATIVADO(R.drawable.ic_flash_off, "off") {
        @Override
        public int configure() {
            configureCamera();
            return ATIVADO.ordinal();
        }
    }, ATIVADO(R.drawable.ic_flash_on, "on") {
        @Override
        public int configure() {
            configureCamera();
            return AUTOMATICO.ordinal();
        }
    }, AUTOMATICO(R.drawable.ic_flash_auto, "auto") {
        @Override
        public int configure() {
            configureCamera();
            return DESATIVADO.ordinal();
        }
    };

    public static final int enumDesativado = 0;
    public static final int enumAtivado = 1;
    public static final int enumAut = 2;

    public static EnumFlash getBy(int valor) {
        switch (valor) {
            case enumDesativado:
                return EnumFlash.DESATIVADO;
            case enumAtivado:
                return EnumFlash.ATIVADO;
            case enumAut:
                return EnumFlash.AUTOMATICO;
            default:
                return EnumFlash.DESATIVADO;
        }
    }

    EnumFlash(int drawable, String mode) {
        setDrawable(drawable);
        setMode(mode);
    }

    private Context mContext;
    private int drawable;
    private String mode;
    private Camera mCamera;
    private ImageView mImage;

    public void configureCamera() {
        Camera.Parameters p = getmCamera().getParameters();
        p.setFlashMode(getMode());
        getmCamera().setParameters(p);
        getmImage().setImageDrawable(ContextCompat.getDrawable(getmContext(), getDrawable()));
    }

    public abstract int configure();

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(int drawable) {
        this.drawable = drawable;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Camera getmCamera() {
        return mCamera;
    }

    public void setmCamera(Camera mCamera) {
        this.mCamera = mCamera;
    }

    public ImageView getmImage() {
        return mImage;
    }

    public void setmImage(ImageView mImage) {
        this.mImage = mImage;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
