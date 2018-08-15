package br.com.home.maildeliveryjfsteel.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.markosullivan.wizards.MainActivityWizard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.home.maildeliveryjfsteel.BuildConfig;
import br.com.home.maildeliveryjfsteel.MyLocation;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.activity.HelloWorldActivity;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.fragment.MatriculaDialogFragment;
import br.com.home.maildeliveryjfsteel.utils.AlertUtils;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CAMPO_INSTALACAO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.CAMERA_PERMISSION;

/**
 * Created by ronanlima on 17/05/17.
 */

public class HandlerQrCodeActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    public static final String TAG = HandlerQrCodeActivity.class.getCanonicalName().toUpperCase();

    public static final int REQUEST_CODE_WIZARD = 999;
    public static final int REQUEST_CODE_CAMERA = 810;
    public static final int LENGTH_GRUPO_A_REAVISO = 6;
    public static final int LENGTH_CONTA_NORMAL = 5;

    /**
     * Os dois tipos de desligamento (desligamento com 3o campo sendo instalação e desligamento grupo A, onde o primeiro campo é vazio)
     * não precisam ir para a tela de 'conta protocolada', pois ela já é uma conta protocolada
     */
    public static final int LENGTH_CONTA_DESLIGAMENTO = 4;

    /**
     * Pelo que foi conversado com o Marcelo, comunicado importante e reaviso, devem ter o mesmo tratamento
     * para perguntas ao entregador: não exibir a tela de conta protocolada/coletiva.
     */
    public static final int LENGTH_COMUNICADO_IMPORTANTE = 2;
    public static final int LENGTH_NOTA_SERVICO = 10;

    private Context mContext = this;
    private ZXingScannerView scannerView;
    private String resultQrCode;
    private Location location;
    private boolean isWizardRespondido = false;
    private ImageView imgSettings;
    private DialogFragment dialog;
    private boolean isNegouAlgumaPermissao = false;
    private int countPermission = 0;
    private MyLocation.LocationResult locationResult;
    private MyLocation myLocation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        locationResult = new MyLocation.LocationResult() {

            @Override
            public void gotLocation(Location location) {
                if (location != null) {
                    showToast(location.getProvider() + ", " + location.getLatitude() + ", " + location.getLongitude());
                }
                setLocation(location);
            }

        };

        myLocation = new MyLocation();
        myLocation.getLocation(this, this, locationResult);

        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)) {
            initScanner();
        }
    }

    @Override
    protected void onResume() {
        if (!isNegouAlgumaPermissao) {
            boolean isPermissaoCameraConcedida = PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA);
            if (isPermissaoCameraConcedida && scannerView != null && !isWizardRespondido) {
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else if (isPermissaoCameraConcedida && scannerView == null) {
                initScanner();
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            }
        }
        super.onResume();
    }

    @Override
    public void handleResult(Result result) {
        myLocation.getLocation(mContext, HandlerQrCodeActivity.this, locationResult);
        if (result != null) {
            saveTimestampRegister();
            if (resultQrCode != null && result.getText().equals(resultQrCode)) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_qrcode_repetido), Toast.LENGTH_LONG).show();
            }
            resultQrCode = result.getText();
            Log.d("HandlerQrCodeActivity", resultQrCode);
            if (resultQrCode != null && !resultQrCode.isEmpty()) {
                continuaFluxoEntrega();
            }
        } else {
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_falha_leitura_qrcode), Toast.LENGTH_LONG).show();
        }
    }

    private void saveTimestampRegister() {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putLong(getString(R.string.sp_ultimo_registro_lido), System.currentTimeMillis());
        edit.apply();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_WIZARD && resultCode == Activity.RESULT_OK) {
            if (scannerView != null) {
                scannerView.stopCamera();
            }
            String strLatitude = getResources().getString(R.string.latitude);
            String strLongitude = getResources().getString(R.string.longitude);
            if (getLocation() != null) {
                data.putExtra(strLatitude, getLocation().getLatitude());
                data.putExtra(strLongitude, getLocation().getLongitude());
            } else {
                data.putExtra(strLatitude, 0d);
                data.putExtra(strLongitude, 0d);
            }
            data.setClass(this, HelloWorldActivity.class);
            startActivityForResult(data, REQUEST_CODE_CAMERA);
            isWizardRespondido = true;
        } else if (requestCode == REQUEST_CODE_WIZARD && Activity.RESULT_CANCELED == resultCode) {
            isWizardRespondido = false;
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            isWizardRespondido = false;
            resultQrCode = null;
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_CANCELED) {
            isWizardRespondido = false;
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * Tenta converter a data recebida no formato Android, caso não dê certo,
     * tenta converter no formato ios.
     *
     * @param data
     * @return
     */
    public static Date transformaData(String data) {
        Date dt2 = null;
        if (data != null && !data.trim().equals("")) {
            try {
                dt2 = parse(data, "dd/MM/yyyy");
            } catch (ParseException e) {
                Crashlytics.logException(e);
                try {
                    dt2 = parse(data, "yyyy-MM-dd");
                } catch (ParseException e1) {
                    Crashlytics.logException(e);
                    dt2 = null;
                }
            }
        }
        return dt2;
    }

    private static Date parse(String data, String formato)
            throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(formato);
        df.setLenient(false);
        return df.parse(data);
    }

    /**
     * Com base no código lido, dá o tratamento correto.
     */
    private void continuaFluxoEntrega() {
        String[] tipoCodigo = resultQrCode.split(";");
        switch (tipoCodigo.length) {
            case LENGTH_NOTA_SERVICO:
                iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_nota), tipoCodigo[1]);
                break;
            case LENGTH_GRUPO_A_REAVISO:
                iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[2]);
                break;
            case LENGTH_CONTA_NORMAL:
                if (transformaData(tipoCodigo[0]) != null && transformaData(tipoCodigo[1]) != null) {
                    iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_normal), tipoCodigo[2]);
                } else if (tipoCodigo[0].indexOf("-") != -1) {
                    iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[2]);
                } else {
                    showToast(getResources().getString(R.string.msg_falha_leitura_conta));
                    continueReading();
                }
                break;
            case LENGTH_CONTA_DESLIGAMENTO:
                if (transformaData(tipoCodigo[0]) != null) {
                    iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_desligamento), tipoCodigo[2]);
                } else if (tipoCodigo[0] == null || (tipoCodigo[0] != null && tipoCodigo[0].isEmpty())) {
                    iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[2]);
                } else {
                    showToast(getResources().getString(R.string.msg_falha_leitura_conta));
                    continueReading();
                }
                break;
            case LENGTH_COMUNICADO_IMPORTANTE:
                iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[1]);
                break;
            default:
                showToast(getResources().getString(R.string.msg_falha_leitura_conta));
                continueReading();
                break;
        }
    }

    private void continueReading() {
        isWizardRespondido = false;
        resultQrCode = null;
        onResume();
    }

    private void initScanner() {
        if (scannerView == null) {
            setContentView(R.layout.activity_scanner);
            scannerView = (ZXingScannerView) findViewById(R.id.zxing_my_scanner);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_sem_conta);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getBaseContext(), MainActivityWizard.class);
                    i.putExtra(EXTRA_TIPO_CONTA, getBaseContext().getResources().getString(R.string.tipo_conta_no_qrcode));
                    startActivityForResult(i, REQUEST_CODE_WIZARD);
                }
            });
            List<BarcodeFormat> formatList = new ArrayList<>();
            formatList.add(BarcodeFormat.AZTEC);
            scannerView.setFormats(formatList);

            imgSettings = (ImageView) findViewById(R.id.img_reset_matricula);
            imgSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = MatriculaDialogFragment.newInstance(resetMatricula(), R.style.DialogAppTheme);
                    dialog.show(getSupportFragmentManager(), "dialogMatricula");
                }
            });
        }
    }

    /**
     * Salva a matrícula informada no sharedPreferences para consulta no próximo acesso.
     *
     * @param matricula
     */
    private void saveMatricula(String matricula) {
        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(getResources().getString(R.string.sp_matricula), matricula);
        edit.commit();
    }

    public MatriculaDialogFragment.ClickButtonEntrar resetMatricula() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                dialog = null;
                if (matricula != null && !matricula.isEmpty()) {
                    saveMatricula(matricula);
                    showToast(mContext.getResources().getString(R.string.msg_matricula_atualizada));
                } else {
                    showToast(mContext.getResources().getString(R.string.msg_matricula_nao_atualizada));
                }
            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length != 0) {
            if (!PermissionUtils.isPermissaoConcedida(grantResults)) {
                isNegouAlgumaPermissao = true;
                JFSteelDialog dialog = AlertUtils.criarAlerta(mContext.getResources().getString(R.string.titulo_alerta_permissoes), mContext.getResources().getString(R.string.msg_permissao), JFSteelDialog.TipoAlertaEnum.ALERTA, false, new JFSteelDialog.OnClickDialog() {
                    @Override
                    public void onClickPositive(View v, String tag) {

                    }

                    @Override
                    public void onClickNegative(View v, String tag) {
                        finish();
                    }

                    @Override
                    public void onClickNeutral(View v, String tag) {

                    }
                });
                dialog.show(getSupportFragmentManager(), "dialog");
            } else {
                for (int i = 0; i < permissions.length; i++) {
                    if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                        countPermission = 0;
                        break;
                    }
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        if (scannerView != null) {
            scannerView.invalidate();
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                scannerView.stopCameraPreview();
                scannerView.stopCamera();
            }
        }
        if (dialog != null) {
            dialog = null;
        }
        super.onStop();
    }

    /**
     * private void removeLocationUpdates() {
     * locationManager.removeUpdates(this);
     * }
     */

    @Override
    protected void onPause() {
        if (scannerView != null) {
            scannerView.stopCameraPreview();
            scannerView.stopCamera();
        }
        if (dialog != null) {
            dialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        scannerView = null;
        super.onDestroy();
    }

    /**
     * Inicia o fluxo de leitura de conta normal
     */
    private void iniciarFluxoWizard(String tipoConta, String campoInstalacao) {
        Log.d("HandlerQrCodeActivity", resultQrCode);
        Intent i = new Intent(this, MainActivityWizard.class);
        i.putExtra(getResources().getString(R.string.dados_qr_code), resultQrCode);
        i.putExtra(EXTRA_TIPO_CONTA, tipoConta);
        i.putExtra(EXTRA_CAMPO_INSTALACAO, campoInstalacao);
        startActivityForResult(i, REQUEST_CODE_WIZARD);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Exibe toast
     *
     * @param s
     */
    public void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}