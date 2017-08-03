package br.com.home.maildeliveryjfsteel.camera;

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
import java.util.Date;

import br.com.home.jfsteelbase.CallbackWizard;
import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.activity.CameraActivity;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;
import br.com.home.maildeliveryjfsteel.utils.PermissionUtils;

import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.CAMERA_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.GPS_PERMISSION;
import static br.com.home.maildeliveryjfsteel.utils.PermissionUtils.WRITE_EXTERNAL_STORAGE_PERMISSION;

/**
 * Created by ronanlima on 17/05/17.
 */

public class HandlerQrCodeActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private Context mContext = this;
    private IntentIntegrator intentIntegrator;
    private String resultQrCode;
    private GoogleApiClient apiClient;
    private Location location;
    private MainActivityWizard mainWizard;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainWizard = MainActivityWizard.newInstance(listenerTest(), resultQrCode);
        intentIntegrator = new IntentIntegrator(HandlerQrCodeActivity.this);
        intentIntegrator.setCaptureActivity(QrCodeActivity.class);
        intentIntegrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        intentIntegrator.addExtra("SCAN_WIDTH", 50);
        intentIntegrator.addExtra("SCAN_HEIGHT", 75);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION || requestCode == GPS_PERMISSION) {
            if (!PermissionUtils.isPermissaoConcedida(grantResults)) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        criarAlerta(mContext.getResources().getString(R.string.titulo_alerta_permissoes), mContext.getResources().getString(R.string.msg_permissao), JFSteelDialog.TipoAlertaEnum.ALERTA, true, new JFSteelDialog.OnClickDialog() {
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
                    }
                });
            }
        }
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

    @Override
    protected void onStart() {
        super.onStart();
        if (apiClient != null) {
            apiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (PermissionUtils.validate(this, CAMERA_PERMISSION, Manifest.permission.CAMERA)
                && PermissionUtils.validate(this, WRITE_EXTERNAL_STORAGE_PERMISSION, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            initApiClient();
            intentIntegrator.initiateScan();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_falha_leitura_qrcode), Toast.LENGTH_LONG).show();
            } else {
                resultQrCode = result.getContents();
                if (!resultQrCode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), resultQrCode, Toast.LENGTH_LONG).show();
                    Log.d("HandlerQrCodeActivity", resultQrCode);

                    Intent i = mainWizard.getIntent();
//                    Intent i = new Intent(this, mainWizard.getClass());
                    i.putExtra("dadosQrCode", resultQrCode);
                    startActivity(i);
                }
            }
        }
    }

    private CallbackWizard listenerTest() {
        return new CallbackWizard() {
            @Override
            public void backToMainApplication(final Bundle bundle) {
                if (bundle.getBoolean("contaProtocolada") || bundle.getBoolean("contaColetiva")) {
                    if (location != null) {
                        startCameraActivity(bundle);
                    } else {
                        JFSteelDialog alert = criarAlerta(mContext.getResources().getString(R.string.titulo_pedido_localizacao),
                                mContext.getResources().getString(R.string.msg_falha_pegar_localizacao),
                                JFSteelDialog.TipoAlertaEnum.ALERTA, true, new JFSteelDialog.OnClickDialog() {
                                    @Override
                                    public void onClickPositive(View v, String tag) {

                                    }

                                    @Override
                                    public void onClickNegative(View v, String tag) {
                                        bundle.putString("enderecoManual", tag);
                                        startCameraActivity(bundle);
                                    }

                                    @Override
                                    public void onClickNeutral(View v, String tag) {

                                    }
                                });
                        alert.show(getSupportFragmentManager(), "alert");
                    }
                } else {
                    if (resultQrCode.startsWith("contaNormal")) {
                        if (location != null) {
                            saveRegistroEntrega(location.getLatitude(), location.getLongitude(), null);
                        }
                    } else {
                        JFSteelDialog alert = criarAlerta(mContext.getResources().getString(R.string.titulo_pedido_localizacao),
                                mContext.getResources().getString(R.string.msg_falha_pegar_localizacao),
                                JFSteelDialog.TipoAlertaEnum.ALERTA, true, new JFSteelDialog.OnClickDialog() {
                                    @Override
                                    public void onClickPositive(View v, String tag) {

                                    }

                                    @Override
                                    public void onClickNegative(View v, String tag) {
                                        saveRegistroEntrega(0d, 0d, tag);
                                    }

                                    @Override
                                    public void onClickNeutral(View v, String tag) {

                                    }
                                });
                        alert.show(getSupportFragmentManager(), "alert");
                    }
                }
            }
        };
    }

    /**
     * @param latitude
     * @param longitude
     */
    private void saveRegistroEntrega(double latitude, double longitude, String endereco) {
        MailDeliveryDBContaNormal db = new MailDeliveryDBContaNormal(this);
        ContaNormal ct = new ContaNormal();
        ct.setSitSalvoFirebase(0);
        if (endereco == null) {
            ct.setLongitude(longitude);
            ct.setLatitude(latitude);
        } else {
            ct.setEnderecoManual(endereco);
        }
        ct.setDadosQrCode(resultQrCode);
        ct.setPrefixAgrupador("prefixAgrupador");
        ct.setTimesTamp(new Date().getTime());
        db.save(ct);
    }

    /**
     * @param bundle
     */
    private void startCameraActivity(Bundle bundle) {
        Intent i = new Intent(this, CameraActivity.class);
        i.putExtras(bundle);
        startActivity(i);
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
