package br.com.home.maildeliveryjfsteel.test;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import java.util.Arrays;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;

import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.CAMERA_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;

//import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentResult;
//import br.com.home.maildeliveryjfsteel.camera.QrCodeActivity;

/**
 * Created by Ronan.lima on 03/08/17.
 */

public class GerenciaQrCodeTest extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context mContext = this;
//    private IntentIntegrator intentIntegrator;
    private String resultQrCode;
    private GoogleApiClient apiClient;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)) {
//            initIntentIntegrator();
            initApiClient();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (PermissionUtils.justCheckPermission(this, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION).isEmpty()) {
//            initIntentIntegrator();
//            initApiClient();
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (!PermissionUtils.isPermissaoConcedida(grantResults)) {
            JFSteelDialog dialog = criarAlerta(mContext.getResources().getString(R.string.titulo_alerta_permissoes), mContext.getResources().getString(R.string.msg_permissao), JFSteelDialog.TipoAlertaEnum.ALERTA, false, new JFSteelDialog.OnClickDialog() {
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
        } /**else if (requestCode == CAMERA_PERMISSION) {
            initIntentIntegrator();
        }*/
    }

    /**
     * @param titulo
     * @param msg
     * @param tipo
     * @param isBotaoNegativoPositivo
     * @param listener
     * @return
     */
    protected static JFSteelDialog criarAlerta(String titulo, String msg,
                                               JFSteelDialog.TipoAlertaEnum tipo,
                                               boolean isBotaoNegativoPositivo,
                                               JFSteelDialog.OnClickDialog listener) {
        Bundle parametros = new Bundle();
        parametros.putString(JFSteelDialog.TITULO_DIALOG, titulo);
        parametros.putString(JFSteelDialog.DESCRICAO_DIALOG, msg);
        parametros.putSerializable(JFSteelDialog.TIPO_ALERTA, tipo);
        parametros.putBoolean(JFSteelDialog.POSITIVO_NEGATIVO_BOTAO, isBotaoNegativoPositivo);
        parametros.putSerializable(JFSteelDialog.OnClickDialog.ON_CLICK_LISTENER_ARG, listener);

        JFSteelDialog dialog = new JFSteelDialog();
        dialog.setArguments(parametros);

        return dialog;
    }

//    private void initIntentIntegrator() {
//        if (intentIntegrator == null) {
//            intentIntegrator = new IntentIntegrator(GerenciaQrCodeTest.this);
//            intentIntegrator.setCaptureActivity(QrCodeActivity.class);
//            intentIntegrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
//            intentIntegrator.addExtra("SCAN_WIDTH", 50);
//            intentIntegrator.addExtra("SCAN_HEIGHT", 75);
//            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
//        }
//        intentIntegrator.initiateScan();
//    }

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
    }

    /**
     * Inicializa o objeto para recuperar a apiClient do Google, para utilizar as informações de gps.
     */
    private void initApiClient() {
        if (apiClient == null) {
            apiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            apiClient.connect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
//        if (result != null) {
//            if (result.getContents() == null) {
//                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_falha_leitura_qrcode), Toast.LENGTH_LONG).show();
//            } else {
//                resultQrCode = result.getContents();
//                if (!resultQrCode.isEmpty()) {
//                    Toast.makeText(getApplicationContext(), resultQrCode, Toast.LENGTH_LONG).show();
//                    Log.d("HandlerQrCodeActivity", resultQrCode);
//
//                    Intent i = new Intent(this, MainActivityWizard.class);
//                    i.putExtra("dadosQrCode", resultQrCode);
//                    startActivity(i);
//                }
//            }
//        }
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
