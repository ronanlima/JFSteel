package br.com.home.maildeliveryjfsteel;

import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getCanonicalName().toUpperCase();
    private static final int MEDIA_TYPE_IMAGE = 1;

    private Camera camera;
    private CameraPreview cameraPreview;
    private Button btnPhoto, btnQrCode;
    private Camera.PictureCallback pictureCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        btnPhoto = (Button) findViewById(R.id.btn_capturar_foto);
        btnQrCode = (Button) findViewById(R.id.btn_capturar_qrcode);

        getCameraInstance();

        cameraPreview = new CameraPreview(this, camera);
        FrameLayout frame = (FrameLayout) findViewById(R.id.camera_preview);
        frame.addView(cameraPreview);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initPictureCallback();
            }
        });
    }

    private void initPictureCallback() {
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.e(TAG, "Verifique a permissão de escrita para o app.");
                    return;
                }

                try {
                    FileOutputStream fos = new FileOutputStream(pictureFile);
                    fos.write(data);
                    fos.close();
                } catch (FileNotFoundException e) {
                    Log.e(TAG, "Arquivo não encontrado: " + e.getMessage());
                } catch (IOException e) {
                    Log.e(TAG, "Falha ao escrever o arquivo de imagem: " + e.getMessage());
                }
            }
        };
    }

    private File getOutputMediaFile(int type) {
        if (Environment.getExternalStorageState() != null && !Environment.getExternalStorageState().isEmpty()) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    , String.valueOf(BuildConfig.APPLICATION_ID.lastIndexOf(".")));
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "Falha ao criar o diretório para salvar a imagem");
                    return null;
                }
            }

            //FIXME caso dê erro na formatação, remover o zz da String
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmsszz").format(new Date());
            if (type == MEDIA_TYPE_IMAGE) {
                return new File(mediaStorageDir.getPath() + File.separator + "JFSteel_" + timeStamp + ".jpeg");
            }
        }
        return null;
    }

    /**
     * Libera a câmera do dispositivo
     */
    private void releaseCamera() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    /**
     * GetCameraInstance
     */
    private void getCameraInstance() {
        camera = null;
        try {
            camera = Camera.open(0); //abre a câmera traseira
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }
}
