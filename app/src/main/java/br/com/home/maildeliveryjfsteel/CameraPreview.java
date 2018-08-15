package br.com.home.maildeliveryjfsteel;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.crashlytics.android.Crashlytics;

import java.io.IOException;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getCanonicalName().toUpperCase();

    private Camera mCamera;
    private SurfaceHolder mHolder;

    public CameraPreview(Context context, Camera cameraInstance) {
        super(context);

        setmCamera(cameraInstance);
        initHolder();
    }

    public void initHolder() {
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (getmCamera() != null) {
            try {
                getmCamera().setPreviewDisplay(surfaceHolder);
                getmCamera().startPreview();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                Crashlytics.logException(e);
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (surfaceHolder == null) {
            return;
        }

        try {
            getmCamera().stopPreview();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            Crashlytics.logException(e);
        }

        try {
            getmCamera().setPreviewDisplay(surfaceHolder);
            getmCamera().startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao startar o preview da câmera: " + e.getMessage());
            Crashlytics.logException(e);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }

    public Camera getmCamera() {
        return mCamera;
    }

    public void setmCamera(Camera mCamera) {
        this.mCamera = mCamera;
    }

    public SurfaceHolder getmHolder() {
        return mHolder;
    }
}
