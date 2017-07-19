package br.com.home.maildeliveryjfsteel.activity;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 18/07/17.
 */

public class CameraTestActivity extends AppCompatActivity implements Camera.PictureCallback, SurfaceHolder.Callback{

    private Camera mCamera;
    private SurfaceView mCameraPreview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_test);

        mCameraPreview = (SurfaceView) findViewById(R.id.preview_view);
        final SurfaceHolder surfaceHolder = mCameraPreview.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temPermissaoFuncionalidade(10, android.Manifest.permission.CAMERA)) {
            openCamera();
        }
    }

    private boolean temPermissaoFuncionalidade(int codReferencia, String... permissoes) {
        List<String> permissoesNegadas = new ArrayList<>();
        for (String acesso : permissoes) {
            if (ActivityCompat.checkSelfPermission(this, acesso) != PackageManager.PERMISSION_GRANTED) {
                permissoesNegadas.add(acesso);
            }
        }

        if(!permissoesNegadas.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(permissoesNegadas.toArray(new String[]{}), codReferencia);
            }
            return false;
        }
        return true;
    }

    public boolean isPermissaoConcedida(int[] permissoes) {
        boolean isPermissaoConcedida = false;

        for (int i = 0; i < permissoes.length; i++) {
            if (permissoes[i] > 0) {
                isPermissaoConcedida = true;
            }
        }
        return isPermissaoConcedida;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 10) {
            if (isPermissaoConcedida(grantResults)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        openCamera();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Não autorizou usar a câmera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
                mCamera.setPreviewDisplay(mCameraPreview.getHolder());
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPictureTaken(byte[] bytes, Camera camera) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
                mCamera.startPreview();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

    }
}
