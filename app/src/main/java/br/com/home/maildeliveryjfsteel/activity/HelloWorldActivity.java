package br.com.home.maildeliveryjfsteel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import java.util.Arrays;
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

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_COMENTARIO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_DEVE_TIRAR_FOTO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_ENDERECO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_EXTERNO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_VIZINHO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_NO_QR_CODE_POSSUI_CONTA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_CONDOMINIO_PORTARIA;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_ENTREGA_RECUSADA;

/**
 * Created by Ronan.lima on 04/08/17.
 */

public class HelloWorldActivity extends AppCompatActivity {

    private String tipoConta;
    private String dadosQrCode;
    private JFSteelDialog alert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle extras = getIntent().getExtras();
        final String strLatitude = getResources().getString(R.string.latitude);
        tipoConta = extras.getString(EXTRA_TIPO_CONTA);
        dadosQrCode = extras.getString(getResources().getString(R.string.dados_qr_code));

        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))) {
            startCameraActivity(extras);
        } else {
            if (extras.getBoolean(EXTRA_DEVE_TIRAR_FOTO, false)) {
                startCameraActivity(extras);
            } else {
                if (!tipoConta.equals(getResources().getString(R.string.tipo_conta_no_qrcode))) {
                    if (extras.getBoolean(EXTRA_CONTA_PROTOCOLADA) || extras.getBoolean(EXTRA_CONTA_COLETIVA)
                            || extras.getString(EXTRA_LOCAL_ENTREGA_CORRESP).equals(FIELD_LOCAL_ENTREGA_RECUSADA)
                            || extras.getString(EXTRA_LOCAL_ENTREGA_CORRESP).equals(FIELD_LOCAL_CONDOMINIO_PORTARIA)) {
                        startCameraActivity(extras);
                    } else {
                        saveRegistroEntrega(extras.getDouble(strLatitude, 0d), extras.getDouble(getResources().getString(R.string.longitude), 0d), null, dadosQrCode);
                    }
                } else {
                    saveRegistroEntrega(extras.getDouble(strLatitude, 0d), extras.getDouble(getResources().getString(R.string.longitude), 0d), null, dadosQrCode);
                }

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
        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_normal))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_desligamento))) {
            db = new MailDeliveryDBContaNormal(this);
            conta = new ContaNormal(qrCode, new Date().getTime(),
                    getIntent().getStringExtra(getString(R.string.prefix_agrupador)), null, latitude,
                    longitude, null, endereco, 0,
                    getIntent().getStringExtra(EXTRA_LOCAL_ENTREGA_CORRESP), null);
            ((ContaNormal) conta).setContaProtocolada(getIntent().getBooleanExtra(EXTRA_CONTA_PROTOCOLADA, false));
            ((ContaNormal) conta).setContaColetiva(getIntent().getBooleanExtra(EXTRA_CONTA_COLETIVA, false));
            db.save(conta);
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(Arrays.asList(conta), new FirebaseContaNormalImpl(this)));

        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_nota))) {
            db = new MailDeliveryDBNotaServico(this);
            conta = new NotaServico(qrCode, new Date().getTime(),
                    getIntent().getStringExtra(getString(R.string.prefix_agrupador)), null, latitude,
                    longitude, null, endereco, 0,
                    getIntent().getStringExtra(EXTRA_LOCAL_ENTREGA_CORRESP), null);
            ((NotaServico) conta).setMedidorVizinho(getIntent().getStringExtra(EXTRA_MEDIDOR_VIZINHO_DATA_KEY));
            ((NotaServico) conta).setMedidorExterno(getIntent().getStringExtra(EXTRA_MEDIDOR_EXTERNO));
            ((NotaServico) conta).setLeitura(getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY));
            db.save(conta);
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(Arrays.asList(conta), new FirebaseNotaImpl(this)));

        } else {
            db = new MailDeliveryNoQrCode(this);
            conta = new NoQrCode(System.currentTimeMillis(), null, null, latitude, longitude,
                    null, endereco, 0, getIntent().getStringExtra(EXTRA_ENDERECO_DATA_KEY));
            if (getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY) != null && !getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY).trim().isEmpty()) {
                ((NoQrCode) conta).setMedidor(getIntent().getStringExtra(EXTRA_LEITURA_DATA_KEY));
            }
            ((NoQrCode) conta).setComentario(getIntent().getStringExtra(EXTRA_COMENTARIO_DATA_KEY));
            if (getIntent().getStringExtra(EXTRA_NO_QR_CODE_POSSUI_CONTA) != null) {
                ((NoQrCode) conta).setExisteConta(getIntent().getStringExtra(EXTRA_NO_QR_CODE_POSSUI_CONTA).equalsIgnoreCase("sim") ? 1 : 0);
            }
            db.save(conta);
            new SaveFirebaseAsync().execute(new FirebaseAsyncParam(Arrays.asList(conta), new FirebaseNoQrCodeImpl(this)));
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

    @Override
    protected void onPause() {
        super.onPause();
        if (alert != null) {
            alert.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (alert != null) {
            alert.show(getSupportFragmentManager(), "alert");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alert != null) {
            alert = null;
        }
    }
}
