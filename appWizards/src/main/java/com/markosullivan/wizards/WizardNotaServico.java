package com.markosullivan.wizards;

import android.content.Context;
import android.os.Bundle;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.CustomerNotaServicoPage;
import com.markosullivan.wizards.wizard.model.MixedNotaServicoChoicePage;
import com.markosullivan.wizards.wizard.model.PageList;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_EXTERNO;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_MEDIDOR_VIZINHO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class WizardNotaServico extends AbstractWizardModel {
    public static final String TITLE_PAGE_ENTREGA = "Informações sobre a nota";
    public static final String TITLE_PAGE_SOBRE_RESIDENCIA = "Sobre a residência";
    public static final String[] choicesResidencias = {"Residencial", "Industrial", "Comercial", "Poste"};

    public WizardNotaServico(Context context) {
        super(context);
    }

    @Override
    public Bundle getBundleOfPages(Bundle bundle) {
        CustomerNotaServicoPage p = (CustomerNotaServicoPage) getPageList().get(0);
        if (p.getData().getString(EXTRA_LEITURA_DATA_KEY) != null && !p.getData().getString(EXTRA_LEITURA_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_LEITURA_DATA_KEY, p.getData().getString(EXTRA_LEITURA_DATA_KEY));
        }
        if (p.getData().getString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY) != null && !p.getData().getString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY, p.getData().getString(EXTRA_MEDIDOR_VIZINHO_DATA_KEY));
        }
        MixedNotaServicoChoicePage p2 = (MixedNotaServicoChoicePage) getPageList().get(1);
        if (p2.getData().getString(p2.SIMPLE_DATA_KEY) != null && !p2.getData().getString(p2.SIMPLE_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_LOCAL_ENTREGA_CORRESP, p2.getData().getString(p2.SIMPLE_DATA_KEY));
        }
        if (p2.getData().getString(SECOND_DATA_KEY) != null && !p2.getData().getString(SECOND_DATA_KEY).trim().isEmpty()) {
            bundle.putString(EXTRA_MEDIDOR_EXTERNO, p2.getData().getString(SECOND_DATA_KEY));
        }
        return bundle;
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new CustomerNotaServicoPage(this, TITLE_PAGE_ENTREGA),

                new MixedNotaServicoChoicePage(this, TITLE_PAGE_SOBRE_RESIDENCIA)
                        .setChoiceMedidor("Medidor externo")
                        .setChoicesResidencia(choicesResidencias)
                        .setRequired(true)
        );
    }
}