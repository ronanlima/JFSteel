package br.com.home.maildeliveryjfsteel.view;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.widget.ImageView;

import br.com.home.maildeliveryjfsteel.enums.EnumFlash;

/**
 * Created by Ronan.lima on 21/07/17.
 */

public class CameraImageView extends ImageView {
    private Context mContext;
    private EnumFlash flashEnum;
    private Camera camera;
    private int aux = EnumFlash.ATIVADO.ordinal();

    public CameraImageView(Context context) {
        super(context);
        this.mContext = context;
        aux = EnumFlash.ATIVADO.ordinal();
    }

    public CameraImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        flashEnum = EnumFlash.getBy(aux);
        flashEnum.setmCamera(getCamera());
        flashEnum.setmContext(mContext);
        flashEnum.setmImage(this);
        aux = flashEnum.configure();
        return super.performClick();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
