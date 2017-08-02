package com.markosullivan.wizards;

import android.content.Context;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.MultipleFixedChoicePage;
import com.markosullivan.wizards.wizard.model.PageList;
import com.markosullivan.wizards.wizard.model.SingleFixedChoicePage;

/**
 * Created by Ronan.lima on 01/08/17.
 */

public class WizardContaNormal extends AbstractWizardModel {
    public static final String TITLE_PAGE_ENTREGA = "Local de entrega";
    public static final String TITLE_PAGE_SOBRE_CONTA = "Sobre a conta";
    public static final String[] choicesEntrega = {"Caixa de correspondência", "Portão", "Embaixo da porta", "Em mãos",
            "Caixa do medidor"};
    public static final String[] choicesSobreConta = {"Está protocolada", "Coletiva"};

    public WizardContaNormal(Context context) {
        super(context);
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new SingleFixedChoicePage(this, TITLE_PAGE_ENTREGA)
                        .setChoices(choicesEntrega)
                        .setRequired(true),

                new MultipleFixedChoicePage(this, TITLE_PAGE_SOBRE_CONTA)
                        .setChoices(choicesSobreConta)
        );
    }
}
