package br.com.home.maildeliveryjfsteel.camera;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.markosullivan.wizards.MainActivityWizard;

import java.io.Serializable;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by ronanlima on 17/05/17.
 */

public class HandlerQrCodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentIntegrator intentIntegrator = new IntentIntegrator(HandlerQrCodeActivity.this);
        intentIntegrator.setCaptureActivity(QrCodeActivity.class);
        intentIntegrator.addExtra("SCAN_MODE", "QR_CODE_MODE");
        intentIntegrator.addExtra("SCAN_WIDTH", 50);
        intentIntegrator.addExtra("SCAN_HEIGHT", 75);
        intentIntegrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        intentIntegrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Log.e("TAG", "Deu ruim, não conseguiu ler a mesa");
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.msg_falha_leitura_qrcode), Toast.LENGTH_LONG).show();
            } else {
                String resultQrCode = result.getContents();
                if (!resultQrCode.isEmpty()) {
                    Toast.makeText(getApplicationContext(), resultQrCode, Toast.LENGTH_LONG).show();
                    Log.d("TAG", resultQrCode);
                    startActivity(new Intent(this, MainActivityWizard.class));
                }
            }
        }
    }

    interface ReaderQrcode extends Serializable{
        void callListener(String qrcode);
    }
}
