package br.com.home.maildeliveryjfsteel.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.fragment.MixedChoiceFragment;
import br.com.home.maildeliveryjfsteel.fragment.WizardFragment;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_DEVE_TIRAR_FOTO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_TIPO_CONTA;
import static br.com.home.jfsteelbase.ConstantsUtil.SIMPLE_DATA_KEY;

/**
 * Created by Ronan.lima on 20/10/17.
 */

public class WizardActivity extends FragmentActivity {
    private String tipoConta;
    private Intent mIntent;
    private WizardFragment mFragment;
    private Button mNextButton;
    private Button mPhotoButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wizard);

        mIntent = getIntent();
        tipoConta = mIntent.getStringExtra(EXTRA_TIPO_CONTA);

        if (tipoConta.equals(getResources().getString(R.string.tipo_conta_normal))) {
//            mWizardModel = new WizardContaNormal(this, true);
            mFragment = MixedChoiceFragment.create(SIMPLE_DATA_KEY, true);
//            mWizardModel = new SingleWizard(this, true);
        } /*else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))
                || tipoConta.equals(getResources().getString(R.string.tipo_conta_desligamento))) {
            mWizardModel = new WizardContaNormal(this, false);
        } else if (tipoConta.equals(getResources().getString(R.string.tipo_conta_nota))) {
            mWizardModel = new WizardNotaServico(this);
        } else {
            mWizardModel = new WizardNoQrCode(this);
        }*/

        mNextButton = (Button) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFragment.isCompleted()) {
                    finishWizard(false);
                } else {
                    Toast.makeText(getBaseContext(), "É necessário responder o questionário antes de continuar", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (!tipoConta.equals(getResources().getString(R.string.tipo_conta_grupo_a_reaviso))) {
            mPhotoButton = (Button) findViewById(R.id.photo_button);
            mPhotoButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mFragment.isCompleted()) {
                        finishWizard(true);
                    } else {
                        Toast.makeText(getBaseContext(), "É necessário responder o questionário antes de continuar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.container_fragment, mFragment).commit();
    }

    private void finishWizard(boolean value) {
        mIntent.putExtras(mFragment.getBundle());
        mIntent.putExtra(EXTRA_DEVE_TIRAR_FOTO, value);
        setResult(Activity.RESULT_OK, mIntent);
        finish();
    }
}
