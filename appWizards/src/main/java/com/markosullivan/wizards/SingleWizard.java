package com.markosullivan.wizards;

import android.content.Context;
import android.os.Bundle;

import com.markosullivan.wizards.wizard.model.AbstractWizardModel;
import com.markosullivan.wizards.wizard.model.MixedChoicePage;
import com.markosullivan.wizards.wizard.model.PageList;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_COLETIVA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_CONTA_PROTOCOLADA;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LOCAL_ENTREGA_CORRESP;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_CONDOMINIO_PORTARIA;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_ENTREGA_RECUSADA;
import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;

/**
 * Created by Ronan.lima on 01/08/17.
 */

public class SingleWizard extends AbstractWizardModel {
    public static final String TITLE_PAGE_ENTREGA = "Local de entrega";
    public static final String[] choicesEntrega = {"Portão", "Embaixo da porta", "Em mãos",
            FIELD_LOCAL_ENTREGA_RECUSADA, FIELD_LOCAL_CONDOMINIO_PORTARIA, "Devolução", "Caixa de correspondência"};
    public static final String[] choicesSobreConta = {"Está protocolada", "Coletiva"};

    public SingleWizard(Context context, boolean deveExibirTelaProtocolo) {
        super(context, deveExibirTelaProtocolo);
    }

    @Override
    public Bundle getBundleOfPages(Bundle bundle) {
        MixedChoicePage p = (MixedChoicePage) getPageList().get(0);
        bundle.putString(EXTRA_LOCAL_ENTREGA_CORRESP, p.getData().getString(p.SIMPLE_DATA_KEY));

        if (deveExibirTelaProtocolo) {
            if (p.getData().getStringArrayList(SECOND_DATA_KEY) != null && !p.getData().getStringArrayList(SECOND_DATA_KEY).isEmpty()) {
                for (String op : p.getData().getStringArrayList(SECOND_DATA_KEY)) {
                    if (op.equals(SingleWizard.choicesSobreConta[0])) {
                        bundle.putBoolean(EXTRA_CONTA_PROTOCOLADA, true);
                    } else if (op.equals(SingleWizard.choicesSobreConta[1])) {
                        bundle.putBoolean(EXTRA_CONTA_COLETIVA, true);
                    }
                }
            }
        }
        return bundle;
    }

    @Override
    protected PageList onNewRootPageList() {
        return new PageList(

                new MixedChoicePage(this, TITLE_PAGE_ENTREGA, deveExibirTelaProtocolo)
                        .setChoicesResidencia(choicesEntrega)
                        .setChoicesTiposEntrega(choicesSobreConta)
                        .setRequired(true)
        );
    }
}
