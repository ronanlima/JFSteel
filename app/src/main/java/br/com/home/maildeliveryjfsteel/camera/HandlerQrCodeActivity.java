package br.com.home.maildeliveryjfsteel.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.markosullivan.wizards.MainActivityWizard;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.activity.CameraActivity;
import br.com.home.maildeliveryjfsteel.activity.HelloWorldActivity;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.utils.AlertUtils;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CAMPO_INSTALACAO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.CAMERA_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;

/**
 * Created by ronanlima on 17/05/17.
 */

public class HandlerQrCodeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener,
        ZXingScannerView.ResultHandler {

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
    private GoogleApiClient apiClient;
    private Location location;
    private boolean isWizardRespondido = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)) {
            initApiClient();
            initScanner();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isPermissaoCameraConcedida = PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA);
        if (scannerView != null && isPermissaoCameraConcedida && !isWizardRespondido) {
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        } else if (isPermissaoCameraConcedida && scannerView == null) {
            initScanner();
            scannerView.setResultHandler(this);
            scannerView.startCamera();
        }
        if (PermissionUtils.validate(this, GPS_PERMISSION, Manifest.permission.ACCESS_FINE_LOCATION)) {
            initApiClient();
        }
    }

    @Override
    public void handleResult(Result result) {
        if (result != null) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if (requestCode == REQUEST_CODE_WIZARD && resultCode == Activity.RESULT_OK) {
            if (scannerView != null) {
                scannerView.stopCamera();
            }
            String strLatitude = getResources().getString(R.string.latitude);
            String strLongitude = getResources().getString(R.string.longitude);
            if (location != null) {
                data.putExtra(strLatitude, location.getLatitude());
                data.putExtra(strLongitude, location.getLongitude());
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
                try {
                    dt2 = parse(data, "yyyy-MM-dd");
                } catch (ParseException e1) {
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
                }
                break;
            case LENGTH_CONTA_DESLIGAMENTO:
                if (transformaData(tipoCodigo[0]) != null) {
                    iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_desligamento), tipoCodigo[2]);
                } else if (tipoCodigo[0] == null || (tipoCodigo[0] != null && tipoCodigo[0].isEmpty())) {
                    iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[2]);
                } else {
                    showToast(getResources().getString(R.string.msg_falha_leitura_conta));
                }
                break;
            case LENGTH_COMUNICADO_IMPORTANTE:
                // entrar no fluxo de reaviso (sem conta protocolada/coletiva).
//                iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_nota));
                iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[1]);
                break;
            default:
                showToast(getResources().getString(R.string.msg_falha_leitura_conta));
                isWizardRespondido = false;
                resultQrCode = null;
                onResume();
                break;
        }
    }

    private void iniciaFluxoGrupoAReaviso(String tipoConta, String campoInstalacao) {
        Intent i = new Intent(this, CameraActivity.class);
        i.putExtra(getResources().getString(R.string.dados_qr_code), resultQrCode);
        i.putExtra(EXTRA_TIPO_CONTA, tipoConta);
        i.putExtra(EXTRA_CAMPO_INSTALACAO, campoInstalacao);
        startActivityForResult(i, REQUEST_CODE_CAMERA);
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
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length != 0) {
            if (!PermissionUtils.isPermissaoConcedida(grantResults)) {
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
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (apiClient != null) {
            apiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (apiClient != null) {
            apiClient.disconnect();
        }
        if (scannerView != null) {
            scannerView.invalidate();
            if (Build.VERSION_CODES.LOLLIPOP <= Build.VERSION.SDK_INT) {
                scannerView.stopCameraPreview();
                scannerView.stopCamera();
            }
            scannerView = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (scannerView != null) {
            scannerView.stopCameraPreview();
            scannerView.stopCamera();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (apiClient != null) {
            apiClient.unregisterConnectionCallbacks(this);
        }
        apiClient = null;
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
     * Inicializa o objeto para recuperar a apiClient do Google, para utilizar as informações de gps.
     */
    private void initApiClient() {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(mContext)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            apiClient.connect();
        }
    }

    /**
     * Exibe toast
     *
     * @param s
     */
    private void showToast(String s) {
        Toast.makeText(mContext, s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtils.requestPermissions(this, GPS_PERMISSION, Arrays.asList(Manifest.permission.ACCESS_COARSE_LOCATION));
            return;
        }

        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        if (location == null) {
            showToast(mContext.getResources().getString(R.string.msg_falha_pegar_localizacao));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showToast(connectionResult.getErrorMessage());
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

}
