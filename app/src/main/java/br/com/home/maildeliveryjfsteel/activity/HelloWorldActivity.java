package br.com.home.maildeliveryjfsteel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.Date;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.async.FirebaseAsyncParam;
import br.com.home.maildeliveryjfsteel.async.SaveFirebaseAsync;
import br.com.home.maildeliveryjfsteel.camera.HandlerQrCodeActivity;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseContaNormalImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNoQrCodeImpl;
import br.com.home.maildeliveryjfsteel.firebase.impl.FirebaseNotaImpl;
import br.com.home.maildeliveryjfsteel.fragment.JFSteelDialog;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.dto.GenericDelivery;
import br.com.home.maildeliveryjfsteel.persistence.dto.NoQrCode;
import br.com.home.maildeliveryjfsteel.persistence.dto.NotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBContaNormal;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryDBNotaServico;
import br.com.home.maildeliveryjfsteel.persistence.impl.MailDeliveryNoQrCode;
import br.com.home.maildeliveryjfsteel.utils.AlertUtils;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_COMENTARIO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_ENDERECO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_EXTERNO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_VIZINHO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_NO_QR_CODE_POSSUI_CONTA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;

/**
 * Created by Ronan.lima on 04/08/17.
 */

public class HelloWorldActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        final String strLatitude = getResources().getString(R.string.latitude);

        if (!getIntent().getStringExtra(EXTRA_TIPO_CONTA).equals(getResources().getString(R.string.tipo_conta_no_qrcode))) {
            if (extras.getBoolean(EXTRA_CONTA_PROTOCOLADA) || extras.getBoolean(EXTRA_CONTA_COLETIVA)
                    || extras.getBoolean(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))) {
                if (extras.getDouble(strLatitude) != 0d) {
                    startCameraActivity(extras);
                } else {
                    JFSteelDialog alert = AlertUtils.criarAlertaComInputText(getResources().getString(R.string.titulo_pedido_localizacao),
                            getResources().getString(R.string.msg_falha_pegar_localizacao), new JFSteelDialog.OnClickDialog() {
                                @Override
                                public void onClickPositive(View v, String tag) {

                                }

                                @Override
                                public void onClickNegative(View v, String tag) {

                                }

                                @Override
                                public void onClickNeutral(View v, String tag) {
                                    extras.putString(getResources().getString(R.string.endereco_manual), tag);
                                    startCameraActivity(extras);
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
                    JFSteelDialog alert = AlertUtils.criarAlertaComInputText(getResources().getString(R.string.titulo_pedido_localizacao),
                            getResources().getString(R.string.msg_falha_pegar_localizacao), new JFSteelDialog.OnClickDialog() {
                                @Override
                                public void onClickPositive(View v, String tag) {

                                }

                                @Override
                                public void onClickNegative(View v, String tag) {

                                }

                                @Override
                                public void onClickNeutral(View v, String tag) {
                                    extras.putString(getResources().getString(R.string.endereco_manual), tag);
                                    startCameraActivity(extras);
                                }
                            });
                    alert.show(getSupportFragmentManager(), "alert");
                }
            }
        } else {
            if (extras.getDouble(strLatitude) != 0d) {
                saveRegistroEntrega(extras.getDouble(strLatitude), extras.getDouble(getResources().getString(R.string.longitude)), null, null);
            } else {
                JFSteelDialog alert = AlertUtils.criarAlertaComInputText(getResources().getString(R.string.titulo_pedido_localizacao),
                        getResources().getString(R.string.msg_falha_pegar_localizacao), new JFSteelDialog.OnClickDialog() {
                            @Override
                            public void onClickPositive(View v, String tag) {

                            }

                            @Override
                            public void onClickNegative(View v, String tag) {

                            }

                            @Override
                            public void onClickNeutral(View v, String tag) {
                                extras.putString(getResources().getString(R.string.endereco_manual), tag);
                                saveRegistroEntrega(0, 0, tag, null);
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
        MailDeliverDBService db;
        GenericDelivery conta;
        if (getIntent().getStringExtra(EXTRA_TIPO_CONTA).equals(getResources().getString(R.string.tipo_conta_normal))) {
            db = new MailDeliveryDBContaNormal(this);
            conta = new ContaNormal(this, qrCode, new Date().getTime(),
                    getIntent().getStringExtra(getString(R.string.prefix_agrupador)), null, latitude,
                    longitude, null, endereco, 0,
                    getIntent().getStringExtra(EXTRA_LOCAL_ENTREGA_CORRESP), null);
            ((ContaNormal) conta).setContaProtocolada(getIntent().getBooleanExtra(EXTRA_CONTA_PROTOCOLADA, false));
            ((ContaNormal) conta).setContaColetiva(getIntent().getBooleanExtra(EXTRA_CONTA_COLETIVA, false));
            db.save(conta);
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(db.findByQrCodeAndSit(qrCode, 0), new FirebaseContaNormalImpl(this, null)));

        } else if (getIntent().getStringExtra(EXTRA_TIPO_CONTA).equals(getResources().getString(R.string.tipo_conta_nota))) {
            db = new MailDeliveryDBNotaServico(this);
            conta = new NotaServico(this, qrCode, new Date().getTime(),
                    getIntent().getStringExtra(getString(R.string.prefix_agrupador)), null, latitude,
                    longitude, null, endereco, 0,
                    getIntent().getStringExtra(EXTRA_LOCAL_ENTREGA_CORRESP), null);
            ((NotaServico) conta).setMedidorVizinho(getIntent().getStringExtra(EXTRA_MEDIDOR_VIZINHO_DATA_KEY));
            ((NotaServico) conta).setMedidorExterno(getIntent().getStringExtra(EXTRA_MEDIDOR_EXTERNO));
            ((NotaServico) conta).setLeitura(getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY));
            db.save(conta);
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(db.findByQrCodeAndSit(qrCode, 0), new FirebaseNotaImpl(this, null)));

        } else {
            db = new MailDeliveryNoQrCode(this);
            conta = new NoQrCode(this, null, new Date().getTime(), null, null, latitude, longitude,
                    null, endereco, 0, getIntent().getStringExtra(EXTRA_ENDERECO_DATA_KEY), null);
            if (getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY) != null && !getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY).trim().isEmpty()) {
                ((NoQrCode) conta).setMedidor(Integer.valueOf(getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY)));
            }
            ((NoQrCode) conta).setComentario(getIntent().getStringExtra(EXTRA_COMENTARIO_DATA_KEY));
            if (getIntent().getStringExtra(EXTRA_NO_QR_CODE_POSSUI_CONTA) != null) {
                ((NoQrCode) conta).setExisteConta(getIntent().getStringExtra(EXTRA_NO_QR_CODE_POSSUI_CONTA).equalsIgnoreCase("sim") ? 1 : 0);
            }
            db.save(conta);
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(db.findBySit(0), new FirebaseNoQrCodeImpl(this, null)));
        }

        setResult(Activity.RESULT_OK);
        finish();
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
