package br.com.home.maildeliveryjfsteel;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = CameraPreview.class.getCanonicalName().toUpperCase();

    private Camera mCamera;
    private SurfaceHolder mHolder;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (surfaceHolder == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        try {
            mCamera.setPreviewDisplay(surfaceHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.e(TAG, "Erro ao startar o preview da câmera: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        mCamera.release();
    }
}
