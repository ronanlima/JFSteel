package br.com.home.maildeliveryjfsteel.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.markosullivan.wizards.MainActivityWizard;

import java.util.Arrays;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.activity.HelloWorldActivity;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.utils.AlertUtils;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;

import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.CAMERA_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;

/**
 * Created by ronanlima on 17/05/17.
 */

public class HandlerQrCodeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final int REQUEST_CODE_WIZARD = 999;

    private Context mContext = this;
    private IntentIntegrator intentIntegrator;
    private String resultQrCode;
    private GoogleApiClient apiClient;
    private Location location;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)) {
            initIntentIntegrator();
            initApiClient();
        }
    }

    /**
     * Inicia a leitura do qr code
     */
    private void initIntentIntegrator() {
        if (intentIntegrator == null) {
            intentIntegrator = new IntentIntegrator(HandlerQrCodeActivity.this);
            intentIntegrator.setCaptureActivity(QrCodeActivity.class);
            intentIntegrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
            intentIntegrator.addExtra("SCAN_WIDTH", 50);
            intentIntegrator.addExtra("SCAN_HEIGHT", 75);
            intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        }
        intentIntegrator.initiateScan();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
        } else if (requestCode == CAMERA_PERMISSION) {
            initIntentIntegrator();
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
    }

    @Override
    protected void onDestroy() {
        apiClient.unregisterConnectionCallbacks(this);
        apiClient = null;
        intentIntegrator = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_WIZARD && resultCode == Activity.RESULT_OK) {
            if (location != null) {
                data.putExtra("latitude", location.getLatitude());
                data.putExtra("longitude", location.getLongitude());
            } else {
                data.putExtra("latitude", 0d);
                data.putExtra("longitude", 0d);
            }
            data.putExtra("dadosQrCode", resultQrCode);
            data.setClass(this, HelloWorldActivity.class);
            startActivity(data);
        } else if (Activity.RESULT_CANCELED == resultCode) {
            onBackPressed();
        } else {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_falha_leitura_qrcode), Toast.LENGTH_LONG).show();
                } else {
                    resultQrCode = result.getContents();
                    if (!resultQrCode.isEmpty()) {
                        Toast.makeText(getApplicationContext(), resultQrCode, Toast.LENGTH_LONG).show();
                        Log.d("HandlerQrCodeActivity", resultQrCode);
                        Intent i = new Intent(this, MainActivityWizard.class);
                        i.putExtra("dadosQrCode", resultQrCode);
                        intentIntegrator = null;
                        startActivityForResult(i, 999);
                    }
                }
            } else {
                // This is important, otherwise the result will not be passed to the fragment
                super.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (intentIntegrator == null && qrCodeRead) {
//            initIntentIntegrator();
//            qrCodeRead = false;
//        }
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
