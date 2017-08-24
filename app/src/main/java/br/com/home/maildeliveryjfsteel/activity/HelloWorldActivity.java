package br.com.home.maildeliveryjfsteel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Date;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.camera.HandlerQrCodeActivity;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;
import br.com.home.maildeliveryjfsteel.utils.AlertUtils;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;

/**
 * Created by Ronan.lima on 04/08/17.
 */

public class HelloWorldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        String strLatitude = getResources().getString(R.string.latitude);

        if (extras.getBoolean(EXTRA_CONTA_PROTOCOLADA) || extras.getBoolean(EXTRA_CONTA_COLETIVA)
                || extras.getBoolean(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))) {
            if (extras.getDouble(strLatitude) != 0d) {
                startCameraActivity(extras);
            } else {
                JFSteelDialog alert = AlertUtils.criarAlerta(getResources().getString(R.string.titulo_pedido_localizacao),
                        getResources().getString(R.string.msg_falha_pegar_localizacao),
                        JFSteelDialog.TipoAlertaEnum.ALERTA, true, new JFSteelDialog.OnClickDialog() {
                            @Override
                            public void onClickPositive(View v, String tag) {

                            }

                            @Override
                            public void onClickNegative(View v, String tag) {
                                extras.putString(getResources().getString(R.string.endereco_manual), tag);
                                startCameraActivity(extras);
                            }

                            @Override
                            public void onClickNeutral(View v, String tag) {

                            }
                        });
                alert.show(getSupportFragmentManager(), "alert");
            }
        } else {
            final String strDadosQrCode = getResources().getString(R.string.dados_qr_code);
            if (extras.getDouble(strLatitude) != 0d) {
                startCameraActivity(extras);
//                saveRegistroEntrega(extras.getDouble(strLatitude), extras.getDouble(getResources().getString(R.string.longitude)), null, extras.getString(strDadosQrCode));
//                finish();
            } else {
                JFSteelDialog alert = AlertUtils.criarAlerta(getResources().getString(R.string.titulo_pedido_localizacao),
                        getResources().getString(R.string.msg_falha_pegar_localizacao),
                        JFSteelDialog.TipoAlertaEnum.ALERTA, true, new JFSteelDialog.OnClickDialog() {
                            @Override
                            public void onClickPositive(View v, String tag) {

                            }

                            @Override
                            public void onClickNegative(View v, String tag) {
                                extras.putString(getResources().getString(R.string.endereco_manual), tag);
                                startCameraActivity(extras);
//                                saveRegistroEntrega(0d, 0d, tag, extras.getString(strDadosQrCode));
//                                finish();
                            }

                            @Override
                            public void onClickNeutral(View v, String tag) {

                            }
                        });
                alert.show(getSupportFragmentManager(), "alert");
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == HandlerQrCodeActivity.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            setResult(Activity.RESULT_OK);
            finish();
        } else if (requestCode == HandlerQrCodeActivity.REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_CANCELED) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    /**
     * @param latitude
     * @param longitude
     */
    private void saveRegistroEntrega(double latitude, double longitude, String endereco, String qrCode) {
        MailDeliveryDBContaNormal db = new MailDeliveryDBContaNormal(this);
        ContaNormal ct = new ContaNormal();
        ct.setSitSalvoFirebase(0);
        if (endereco == null) {
            ct.setLongitude(longitude);
            ct.setLatitude(latitude);
        } else {
            ct.setEnderecoManual(endereco);
        }
        ct.setDadosQrCode(qrCode);
        ct.setPrefixAgrupador(getResources().getString(R.string.prefix_agrupador));//FIXME arrumar um prefix valido
        ct.setTimesTamp(new Date().getTime());
        db.save(ct);
    }

    /**
     * @param bundle
     */
    private void startCameraActivity(Bundle bundle) {
        Intent i = new Intent(this, CameraActivity.class);
        i.putExtras(bundle);
        startActivityForResult(i, HandlerQrCodeActivity.REQUEST_CODE_CAMERA);
    }

}
