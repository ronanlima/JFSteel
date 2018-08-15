package br.com.home.maildeliveryjfsteel.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

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
import br.com.home.maildeliveryjfsteel.async.FirebaseAsyncParam;
import br.com.home.maildeliveryjfsteel.async.SaveFirebaseAsync;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseContaNormalImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNoQrCodeImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNotaImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseServiceImpl;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.dto.NotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBNotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryNoQrCode;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;
import br.com.home.maildeliveryjfsteel.view.CameraImageView;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CAMPO_INSTALACAO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_EXTERNO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_VIZINHO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.CAMERA_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.WRITE_EXTERNAL_STORAGE_PERMISSION;

/**
 * Created by Ronan.lima on 15/07/17.
 */

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = CameraActivity.class.getCanonicalName().toUpperCase();

    public static final String APP_DIR = BuildConfig.APPLICATION_ID.substring(BuildConfig.APPLICATION_ID.lastIndexOf(".") + 1, BuildConfig.APPLICATION_ID.length());
    public static final String FILE_PREFIX = "JFSteel_";

    private Context mContext = this;
    private Camera camera;
    private CameraPreview cameraPreview;
    private ImageView btnPhoto, btnFinalizarCaptura;
    private CameraImageView btnFlash;
    private Camera.PictureCallback pictureCallback;
    private MailDeliverDBService db;
    private FirebaseServiceImpl fService;
    private int countPhoto = 0;
    private String dadosQrCode;
    private String matricula;
    private String instalacao;
    private String tipoConta;
    private MediaPlayer mediaPlayer;
    private boolean safeToTakePicture = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        setContentView(R.layout.activity_camera);

        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        this.matricula = sp.getString(getResources().getString(R.string.sp_matricula), null);

        dadosQrCode = getIntent().getStringExtra(getResources().getString(R.string.dados_qr_code));
        instalacao = getIntent().getStringExtra(EXTRA_CAMPO_INSTALACAO);
        tipoConta = getIntent().getStringExtra(EXTRA_TIPO_CONTA);

        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_normal))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_desligamento))) {
            db = new MailDeliveryDBContaNormal(this);
            fService = new FirebaseContaNormalImpl(this, null);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_nota))) {
            db = new MailDeliveryDBNotaServico(this);
            fService = new FirebaseNotaImpl(this, null);
        } else {
            db = new MailDeliveryNoQrCode(this);
            fService = new FirebaseNoQrCodeImpl(this, null);
        }

        btnPhoto = (ImageView) findViewById(R.id.btn_capturar_foto);
        btnFinalizarCaptura = (ImageView) findViewById(R.id.btn_finalizar_captura);
        btnFlash = (CameraImageView) findViewById(R.id.btn_flash);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION || requestCode == WRITE_EXTERNAL_STORAGE_PERMISSION
                || requestCode == GPS_PERMISSION) {
            if (!PermissionUtils.isPermissaoConcedida(grantResults)) {
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
        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
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
                    if (safeToTakePicture) {
                        if (PermissionUtils.validate(CameraActivity.this, WRITE_EXTERNAL_STORAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            camera.takePicture(null, null, pictureCallback);
                            safeToTakePicture = false;
                        }
                    } else {
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.msg_salvando_foto_anterior), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            initPictureCallback();
            btnFinalizarCaptura.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (countPhoto != 0) {
                        new SaveFirebaseAsync().execute(new FirebaseAsyncParam(db.findByQrCodeAndSit(dadosQrCode, 0), fService));
                        setResult(Activity.RESULT_OK);
                        finish();
                    } else {
                        Toast.makeText(getBaseContext(), mContext.getResources().getString(R.string.msg_nenhuma_foto_capturada), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            cameraPreview.setmCamera(camera);
            cameraPreview.initHolder();
        }
        safeToTakePicture = true;
    }

    /**
     * Cria um listener para notificar ao final do envio do registro para o firebase e atualização da
     * base local.
     *
     * @return
     */
    private FirebaseServiceImpl.ServiceNotification createListenerService() {
        return new FirebaseServiceImpl.ServiceNotification() {
            @Override
            public void notifyEndService() {
                setResult(Activity.RESULT_OK);
                finish();
            }
        };
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * Instancia o callback da ação de tirar foto
     */
    private void initPictureCallback() {
        pictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                if (Environment.getExternalStorageState() != null && !Environment.getExternalStorageState().isEmpty()) {
                    File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), APP_DIR);
                    if (!mediaStorageDir.exists()) {
                        if (!mediaStorageDir.mkdirs()) {
                            Log.e(TAG, "Falha ao criar o diretório para salvar a imagem");
                            return;
                        }
                    }

                    long dateTime = new Date().getTime();
                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmsszz").format(dateTime);
                    final File pictureFile = new File(mediaStorageDir.getPath() + File.separator + matricula + "-" + instalacao + "-" + FILE_PREFIX + timeStamp + ".jpeg");

                    try {
                        FileOutputStream fos = new FileOutputStream(pictureFile);
                        fos.write(data);
                        fos.close();
                        countPhoto++;
                    } catch (FileNotFoundException e) {
                        Log.e(TAG, "Arquivo não encontrado: " + e.getMessage());
                        Crashlytics.logException(e);
                    } catch (IOException e) {
                        Crashlytics.logException(e);
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
                    shootSound();
                    saveIntoSqlite(pictureFile, dateTime);
                } else {
                    String msg = "Verifique a permissão de escrita para o app.";
                    Log.e(TAG, msg);
                    Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
                }
                safeToTakePicture = true;
            }
        };
    }

    public void shootSound() {
        AudioManager meng = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int volume = meng.getStreamVolume(AudioManager.STREAM_NOTIFICATION);

        if (mediaPlayer == null)
            mediaPlayer = MediaPlayer.create(mContext, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
        if (mediaPlayer != null)
            mediaPlayer.start();
    }

    private void saveIntoSqlite(File file, long dateTime) {
        if (tipoConta.equals(mContext.getResources().getString(R.string.tipo_conta_normal))
                || tipoConta.equals(mContext.getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(mContext.getResources().getString(R.string.tipo_conta_desligamento))) {
            ContaNormal r = new ContaNormal(getBaseContext(), getIntent().getStringExtra(mContext.getResources().getString(R.string.dados_qr_code)), dateTime,
                    mContext.getResources().getString(R.string.prefix_agrupador), file.getName(), getIntent().getDoubleExtra(mContext.getResources().getString(R.string.latitude), 0d),
                    getIntent().getDoubleExtra(mContext.getResources().getString(R.string.longitude), 0d), file.getAbsolutePath(),
                    getIntent().getStringExtra(mContext.getResources().getString(R.string.endereco_manual)), 0,
                    getIntent().getStringExtra(EXTRA_LOCAL_ENTREGA_CORRESP), null);
            r.setContaProtocolada(getIntent().getBooleanExtra(EXTRA_CONTA_PROTOCOLADA, false));
            r.setContaColetiva(getIntent().getBooleanExtra(EXTRA_CONTA_COLETIVA, false));
            db.save(r);
        } else if (tipoConta.equals(mContext.getResources().getString(R.string.tipo_conta_nota))) {
            NotaServico ns = new NotaServico(getBaseContext(), getIntent().getStringExtra(mContext.getResources().getString(R.string.dados_qr_code)), dateTime,
                    mContext.getResources().getString(R.string.prefix_agrupador), file.getName(), getIntent().getDoubleExtra(mContext.getResources().getString(R.string.latitude), 0d),
                    getIntent().getDoubleExtra(mContext.getResources().getString(R.string.longitude), 0d), file.getAbsolutePath(),
                    getIntent().getStringExtra(mContext.getResources().getString(R.string.endereco_manual)), 0,
                    getIntent().getStringExtra(EXTRA_LOCAL_ENTREGA_CORRESP), null);
            ns.setMedidorExterno(getIntent().getStringExtra(EXTRA_MEDIDOR_EXTERNO));
            ns.setMedidorVizinho(getIntent().getStringExtra(EXTRA_MEDIDOR_VIZINHO_DATA_KEY));
            ns.setLeitura(getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY));
            db.save(ns);
        }
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

}
