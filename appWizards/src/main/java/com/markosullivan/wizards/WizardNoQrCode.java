package com.markosullivan.wizards;

import android.content.Context;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.CustomerPageContaNoQrCode;
import com.markosullivan.wizards.wizard.model.PageList;

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
    protected PageList onNewRootPageList() {
        return new PageList(

                new CustomerPageContaNoQrCode(this, TITLE_PAGE_ENTREGA)
                        .setChoices(choicesResidencias)
                        .setRequired(true)

        );
    }
}