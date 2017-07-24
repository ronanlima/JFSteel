package br.com.home.maildeliveryjfsteel.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.CameraPreview;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;
import br.com.home.maildeliveryjfsteel.view.CameraImageView;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class CameraActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = CameraActivity.class.getCanonicalName().toUpperCase();
    private static final int MEDIA_TYPE_IMAGE = 1;
    public static final int CAMERA_PERMISSION = 10;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION = 11;
    public static final int READ_EXTERNAL_STORAGE_PERMISSION = 12;
    public static final int GPS_PERMISSION = 13;
    public static final String APP_DIR = BuildConfig.APPLICATION_ID.substring(BuildConfig.APPLICATION_ID.lastIndexOf(".") + 1, BuildConfig.APPLICATION_ID.length());

    private Context mContext = this;
    private Camera camera;
    private CameraPreview cameraPreview;
    private ImageView btnPhoto;
    private CameraImageView btnFlash;
    private Camera.PictureCallback pictureCallback;
    private GoogleApiClient apiClient;
    private boolean isConnectWithApi = false;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_camera);

        btnPhoto = (ImageView) findViewById(R.id.btn_capturar_foto);
        btnFlash = (CameraImageView) findViewById(R.id.btn_flash);

        apiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        apiClient.disconnect();
    }

    /**
     * Verifica se o usuário concedeu a permissão solicitada.
     *
     * @param permissoes
     * @return
     */
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
                createAlertDialogFinish("Ok", mContext.getResources().getString(R.string.msg_permissao)).show();
            }
        } else if (requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (isPermissaoConcedida(grantResults)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        camera.takePicture(null, null, pictureCallback);
                    }
                });
            } else {
                createAlertDialogFinish("Ok", mContext.getResources().getString(R.string.msg_permissao)).show();
            }
        } else if (requestCode == GPS_PERMISSION) {
            if (isPermissaoConcedida(grantResults)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        init();
                    }
                });
            } else {
                createAlertDialogFinish("Ok", mContext.getResources().getString(R.string.msg_permissao)).show();
            }
        }
    }

    /**
     * Cria alertDialog informativo para o usuário.
     *
     * @param nameBtn
     * @param msg
     * @return
     */
    private AlertDialog.Builder createAlertDialogFinish(String nameBtn, String msg) {
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
        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA)) {
            init();
        }
    }

    /**
     * Inicializa a os componentes para utilização da câmera.
     */
    private void init() {
        FrameLayout frame = (FrameLayout) findViewById(R.id.camera_preview);
        getCameraInstance();
        if (cameraPreview == null) {
            cameraPreview = new CameraPreview(this, camera);
            frame.addView(cameraPreview);
            btnPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (PermissionUtils.validate(CameraActivity.this, WRITE_EXTERNAL_STORAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        camera.takePicture(null, null, pictureCallback);
                    }
                }
            });
            initPictureCallback();
        } else {
            cameraPreview.setmCamera(camera);
            cameraPreview.initHolder();
        }
    }

    /**
     * Instancia o callback da ação de tirar foto
     */
    private void initPictureCallback() {
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                final File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            createAlertDialogFinish("Ok", String.format(mContext.getResources().getString(R.string.msg_erro_gravar_arquivo), pictureFile.getPath()));
                        }
                    });
                    e.printStackTrace();
                    Log.e(TAG, "Falha ao escrever o arquivo de imagem: " + e.getMessage());
                }
                try {
                    camera.reconnect();
                    camera.startPreview();
                } catch (IOException e) {
                    e.printStackTrace();
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
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_DIR);
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.e(TAG, "Falha ao criar o diretório para salvar a imagem");
                    return null;
                }
            }

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
            configParametersCamera(1280, 720);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        }
    }

    /**
     * Configura parâmetros para uso da câmera
     */
    private void configParametersCamera(int width, int height) {
        Camera.Parameters params = camera.getParameters();
        params.setJpegQuality(100);
        List<String> focusMode = params.getSupportedFocusModes();
        if (focusMode.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            btnFlash.setVisibility(View.VISIBLE);
            btnFlash.setmContext(this);
            btnFlash.setCamera(camera);
        }
        List<String> flashMode = params.getSupportedFlashModes();
        if (flashMode.contains(Camera.Parameters.FLASH_MODE_OFF)) {
            params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        }
        List<Integer> pictureFormats = params.getSupportedPictureFormats();
        if (pictureFormats.contains(ImageFormat.JPEG)) {
            params.setPictureFormat(ImageFormat.JPEG);
        }
        for (Camera.Size size : params.getSupportedPreviewSizes()) {
            if (size.width == width && size.height == height) {
                params.setPreviewSize(width, height);
                break;
            }
        }
        for (Camera.Size size : params.getSupportedPictureSizes()) {
            if (size.width == width && size.height == height) {
                params.setPictureSize(width, height);
                break;
            }
        }

        camera.setParameters(params);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCameraAndPreview();
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        isConnectWithApi = true;
        if (!PermissionUtils.validate(this, GPS_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION)) {

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        isConnectWithApi = false;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        isConnectWithApi = false;
    }
}
