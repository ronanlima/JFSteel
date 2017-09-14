package com.markosullivan.wizards;

import android.content.Context;
import android.os.Bundle;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.MultipleFixedChoicePage;
import com.markosullivan.wizards.wizard.model.PageList;
import com.markosullivan.wizards.wizard.model.SingleFixedChoicePage;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_CONDOMINIO_PORTARIA;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_ENTREGA_RECUSADA;

/**
 * Created by Ronan.lima on 01/08/17.
 */

public class WizardContaNormal extends AbstractWizardModel {
    public static final String TITLE_PAGE_ENTREGA = "Local de entrega";
    public static final String TITLE_PAGE_SOBRE_CONTA = "Sobre a conta";
    public static final String[] choicesEntrega = {"Caixa de correspondência", "Portão", "Embaixo da porta", "Em mãos",
            FIELD_LOCAL_ENTREGA_RECUSADA, FIELD_LOCAL_CONDOMINIO_PORTARIA, "Devolução"};
    public static final String[] choicesSobreConta = {"Está protocolada", "Coletiva"};

    public WizardContaNormal(Context context, boolean deveExibirTelaProtocolo) {
        super(context, deveExibirTelaProtocolo);
    }

    @Override
    public Bundle getBundleOfPages(Bundle bundle) {
        SingleFixedChoicePage p = (SingleFixedChoicePage) getPageList().get(0);
        bundle.putString(EXTRA_LOCAL_ENTREGA_CORRESP, p.getData().getString(p.SIMPLE_DATA_KEY));

        if (deveExibirTelaProtocolo) {
            MultipleFixedChoicePage p1 = (MultipleFixedChoicePage) getPageList().get(1);
            if (p1.getData().getStringArrayList(p1.SIMPLE_DATA_KEY) != null && !p1.getData().getStringArrayList(p1.SIMPLE_DATA_KEY).isEmpty()) {
                for (String op : p1.getData().getStringArrayList(p1.SIMPLE_DATA_KEY)) {
                    if (op.equals(WizardContaNormal.choicesSobreConta[0])) {
                        bundle.putBoolean(EXTRA_CONTA_PROTOCOLADA, true);
                    } else if (op.equals(WizardContaNormal.choicesSobreConta[1])) {
                        bundle.putBoolean(EXTRA_CONTA_COLETIVA, true);
                    }
                }
            }
        }
        return bundle;
    }

    @Override
    protected PageList onNewRootPageList() {
        if (!deveExibirTelaProtocolo) {
            return new PageList(

                    new SingleFixedChoicePage(this, TITLE_PAGE_ENTREGA)
                            .setChoices(choicesEntrega)
                            .setRequired(true)
            );
        }
        return new PageList(

                new SingleFixedChoicePage(this, TITLE_PAGE_ENTREGA)
                        .setChoices(choicesEntrega)
                        .setRequired(true),

                new MultipleFixedChoicePage(this, TITLE_PAGE_SOBRE_CONTA)
                        .setChoices(choicesSobreConta)
        );
    }
}
