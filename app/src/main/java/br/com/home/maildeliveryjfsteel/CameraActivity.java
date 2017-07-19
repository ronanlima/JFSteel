package br.com.home.maildeliveryjfsteel;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_camera);
        releaseCameraAndPreview();

        btnPhoto = (Button) findViewById(R.id.btn_capturar_foto);
        btnQrCode = (Button) findViewById(R.id.btn_capturar_qrcode);
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
                        init();
                    }
                });
            } else {
                Toast.makeText(getApplicationContext(), "Não autorizou usar a câmera", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (temPermissaoFuncionalidade(10, android.Manifest.permission.CAMERA)) {
            init();
        }
    }

    private void init() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.camera_preview);
        getCameraInstance();
        cameraPreview = new CameraPreview(this, camera);
        frame.addView(cameraPreview);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                camera.takePicture(null, null, pictureCallback);
            }
        });
        initPictureCallback();
    }

    /**
     * Instancia o callback da ação de tirar foto
     */
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

    /**
     * Cria o arquivo de imagem com nomenclatura única e o retorna para escrita dos bytes
     * @param type
     * @return
     */
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
     *
     */
    private void releaseCameraAndPreview() {
        if (cameraPreview != null) {
            cameraPreview.removeCallbacks(new Runnable() {
                @Override
                public void run() {
                    cameraPreview.getmHolder().removeCallback(cameraPreview);
                }
            });
            cameraPreview = null;
        }
        releaseCamera();
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
            camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK); //abre a câmera traseira
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
}
