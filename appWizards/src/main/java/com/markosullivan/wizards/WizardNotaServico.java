package com.markosullivan.wizards;

import android.content.Context;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.CustomerNotaServicoPage;
import com.markosullivan.wizards.wizard.model.MixedNotaServicoChoicePage;
import com.markosullivan.wizards.wizard.model.PageList;

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