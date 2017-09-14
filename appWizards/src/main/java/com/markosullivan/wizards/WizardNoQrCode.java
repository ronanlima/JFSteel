package com.markosullivan.wizards;

import android.content.Context;
import android.os.Bundle;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.CustomerPageContaNoQrCode;
import com.markosullivan.wizards.wizard.model.PageList;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_COMENTARIO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_ENDERECO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_NO_QR_CODE_POSSUI_CONTA;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class WizardNoQrCode extends AbstractWizardModel {
    public static final String TITLE_PAGE_ENTREGA = "Informações sobre a conta";
    public static final String[] choicesResidencias = {"Possui conta"};

    public WizardNoQrCode(Context context) {
        super(context);
    }

    @Override
    public Bundle getBundleOfPages(Bundle bundle) {
        CustomerPageContaNoQrCode p = (CustomerPageContaNoQrCode) getPageList().get(0);
        if (p.getData().getString(EXTRA_LEITURA_DATA_KEY) != null && !p.getData().getString(EXTRA_LEITURA_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_LEITURA_DATA_KEY, p.getData().getString(EXTRA_LEITURA_DATA_KEY));
        }
        if (p.getData().getString(EXTRA_ENDERECO_DATA_KEY) != null && !p.getData().getString(EXTRA_ENDERECO_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_ENDERECO_DATA_KEY, p.getData().getString(EXTRA_ENDERECO_DATA_KEY));
        }
        if (p.getData().getString(EXTRA_COMENTARIO_DATA_KEY) != null && !p.getData().getString(EXTRA_COMENTARIO_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_COMENTARIO_DATA_KEY, p.getData().getString(EXTRA_COMENTARIO_DATA_KEY));
        }
        if (p.getData().getStringArrayList(p.SIMPLE_DATA_KEY) != null && !p.getData().getStringArrayList(p.SIMPLE_DATA_KEY).isEmpty()) {
            for (String op : p.getData().getStringArrayList(p.SIMPLE_DATA_KEY)) {
                if (op.equals(WizardNoQrCode.choicesResidencias[0])) {
                    bundle.putString(EXTRA_NO_QR_CODE_POSSUI_CONTA, "Sim");
                    break;
                }
            }
        }
        return bundle;
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new CustomerPageContaNoQrCode(this, TITLE_PAGE_ENTREGA)
                        .setChoices(choicesResidencias)
                        .setRequired(true)

        );
    }
}