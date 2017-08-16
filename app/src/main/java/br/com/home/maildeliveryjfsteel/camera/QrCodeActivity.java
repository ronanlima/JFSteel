//package br.com.home.maildeliveryjfsteel.camera;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.KeyEvent;
//
//import com.journeyapps.barcodescanner.CaptureActivity;
//import com.journeyapps.barcodescanner.CaptureManager;
//import com.journeyapps.barcodescanner.CompoundBarcodeView;
//
//import br.com.home.maildeliveryjfsteel.R;
//
///**
// * Created by ronanlima on 16/05/17.
// */
//
//public class QrCodeActivity extends CaptureActivity implements CompoundBarcodeView.TorchListener {
//
//    private CaptureManager captureManager;
//    private CompoundBarcodeView barcodeView;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.qrcode);
//
//        barcodeView = (CompoundBarcodeView) findViewById(R.id.zxing_barcode_scanner);
//        barcodeView.setTorchListener(this);
//        captureManager = new CaptureManager(this, barcodeView);
//        captureManager.initializeFromIntent(getIntent(), savedInstanceState);
//        captureManager.decode();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        captureManager.onResume();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        captureManager.onPause();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        captureManager.onDestroy();
//        finish();
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
//
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        captureManager.onSaveInstanceState(outState);
//        super.onSaveInstanceState(outState);
//    }
//
//    @Override
//    public void onTorchOn() {
//
//    }
//
//    @Override
//    public void onTorchOff() {
//
//    }
//
//}
