package br.com.home.maildeliveryjfsteel.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

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
import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CAMPO_INSTALACAO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;

/**
 * Created by Ronan.lima on 04/04/2018.
 */

public class HandlerQrCodeFragment extends Fragment implements ZXingScannerView.ResultHandler {
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

    private ZXingScannerView scannerView;
    private String resultQrCode;
    private Location location;
    private ImageView imgSettings;
    private DialogFragment dialog;
    private MyLocation.LocationResult locationResult;
    private MyLocation myLocation;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_scanner, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
        myLocation.getLocation(getActivity().getBaseContext(), getActivity(), locationResult);
    }

    @Override
    public void onResume() {
        if (scannerView == null) {
            initScanner();
        }
        scannerView.setResultHandler(this);
        scannerView.startCamera();
        super.onResume();
    }

    @Override
    public void handleResult(Result result) {
        myLocation.getLocation(getActivity(), getActivity(), locationResult);
        if (result != null) {
            if (resultQrCode != null && result.getText().equals(resultQrCode)) {
                Toast.makeText(getActivity(), getResources().getString(R.string.msg_qrcode_repetido), Toast.LENGTH_LONG).show();
            }
            resultQrCode = result.getText();
            Log.d("HandlerQrCodeActivity", resultQrCode);
            if (resultQrCode != null && !resultQrCode.isEmpty()) {
                continuaFluxoEntrega();
            }
        } else {
            Toast.makeText(getActivity(), getResources().getString(R.string.msg_falha_leitura_qrcode), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
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
            data.setClass(getActivity(), HelloWorldActivity.class);
            startActivityForResult(data, REQUEST_CODE_CAMERA);
        } else if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            resultQrCode = null;
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
                iniciarFluxoWizard(getResources().getString(R.string.tipo_conta_grupo_a_reaviso), tipoCodigo[1]);
                break;
            default:
                showToast(getResources().getString(R.string.msg_falha_leitura_conta));
                resultQrCode = null;
                onResume();
                break;
        }
    }

    private void initScanner() {
        if (scannerView == null) {
            scannerView = (ZXingScannerView) getView().findViewById(R.id.zxing_my_scanner);
            FloatingActionButton fab = (FloatingActionButton) getView().findViewById(R.id.fab_sem_conta);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getActivity(), MainActivityWizard.class);
                    i.putExtra(EXTRA_TIPO_CONTA, getActivity().getResources().getString(R.string.tipo_conta_no_qrcode));
                    startActivityForResult(i, REQUEST_CODE_WIZARD);
                }
            });
            List<BarcodeFormat> formatList = new ArrayList<>();
            formatList.add(BarcodeFormat.AZTEC);
            scannerView.setFormats(formatList);

            imgSettings = (ImageView) getView().findViewById(R.id.img_reset_matricula);
            imgSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog = MatriculaDialogFragment.newInstance(resetMatricula(), R.style.DialogAppTheme);
                    dialog.show(getActivity().getSupportFragmentManager(), "dialogMatricula");
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
        SharedPreferences sp = getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString(getResources().getString(R.string.sp_matricula), matricula);
        edit.commit();
    }

    public MatriculaDialogFragment.ClickButtonEntrar resetMatricula() {
        return new MatriculaDialogFragment.ClickButtonEntrar() {
            @Override
            public void nextActivity(String matricula) {
                dialog.dismiss();
                dialog = null;
                saveMatricula(matricula);
                showToast(getActivity().getResources().getString(R.string.msg_matricula_atualizada));
            }
        };
    }

    @Override
    public void onStop() {
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

    @Override
    public void onPause() {
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
    public void onDestroy() {
        scannerView = null;

        super.onDestroy();
    }

    /**
     * Inicia o fluxo de leitura de conta normal
     */
    private void iniciarFluxoWizard(String tipoConta, String campoInstalacao) {
        Log.d("HandlerQrCodeActivity", resultQrCode);
        Intent i = new Intent(getActivity(), MainActivityWizard.class);
        i.putExtra(getResources().getString(R.string.dados_qr_code), resultQrCode);
        i.putExtra(EXTRA_TIPO_CONTA, tipoConta);
        i.putExtra(EXTRA_CAMPO_INSTALACAO, campoInstalacao);
        startActivityForResult(i, REQUEST_CODE_WIZARD);
    }

    /**
     * Exibe toast
     *
     * @param s
     */
    public void showToast(String s) {
        Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
