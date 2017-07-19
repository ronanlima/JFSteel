package br.com.home.maildeliveryjfsteel.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.CameraPreview;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getCanonicalName().toUpperCase();
    private static final int MEDIA_TYPE_IMAGE = 1;
    public static final int CAMERA_PERMISSION = 10;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 11;
    public static final int READ_EXTERNAL_STORAGE_PERMISSION = 12;

    private Context mContext = this;
    private Camera camera;
    private CameraPreview cameraPreview;
    private Button btnPhoto, btnQrCode;
    private Camera.PictureCallback pictureCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_camera);

        btnPhoto = (Button) findViewById(R.id.btn_capturar_foto);
        btnQrCode = (Button) findViewById(R.id.btn_capturar_qrcode);
        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA)) {
            init();
        }
    }

    public boolean isPermissaoConcedida(int[] permissoes) {
        boolean isPermissaoConcedida = false;

        for (int i = 0; i < permissoes.length; i++) {
            if (permissoes[i] > PackageManager.PERMISSION_DENIED) {
                isPermissaoConcedida = true;
            }
        }
        return isPermissaoConcedida;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION) {
            if (isPermissaoConcedida(grantResults)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                });
            } else {
                createAlertDialo("Ok", mContext.getResources().getString(R.string.msg_permissao)).show();
            }
        }
    }

    /**
     * Cria alertDialog informativo para o usuário.
     * @param nameBtn
     * @param msg
     * @return
     */
    private AlertDialog.Builder createAlertDialo(String nameBtn, String msg) {
        return new AlertDialog.Builder(mContext)
                .setCancelable(false)
                .setMessage(msg)
                .setNegativeButton(nameBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Inicializa a os componentes para utilização da câmera.
     */
    private void init() {
        releaseCameraAndPreview();
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
     *
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
            camera.setDisplayOrientation(90); // exibe verticalmente
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
