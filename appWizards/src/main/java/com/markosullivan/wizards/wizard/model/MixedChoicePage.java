package com.markosullivan.wizards.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.markosullivan.wizards.wizard.ui.MixedChoiceFragment;

import java.util.ArrayList;
import java.util.Arrays;

import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;

/**
 * Created by Admin on 19/10/2017.
 */

public class MixedChoicePage extends SingleFixedChoicePage {
    protected ArrayList<String> tiposResidencia = new ArrayList<String>();
    protected ArrayList<String> tiposEntrega = new ArrayList<String>();
    protected boolean deveExibirPerguntasTipoConta;

    public MixedChoicePage(ModelCallbacks callbacks, String title, boolean deveExibirPerguntasTipoConta) {
        super(callbacks, title);
        this.deveExibirPerguntasTipoConta = deveExibirPerguntasTipoConta;
    }

    @Override
    public Fragment createFragment() {
        return MixedChoiceFragment.create(getKey(), deveExibirPerguntasTipoConta);
    }

    public String getOptionResidenciaAt(int position) {
        return tiposResidencia.get(position);
    }

    public int getOptionResidenciaCount() {
        return tiposResidencia.size();
    }

    public String getOptionTipoEntregaAt(int position) {
        return tiposEntrega.get(position);
    }

    public int getOptionTipoEntregaCount() {
        return tiposEntrega.size();
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem(getTitle(), mData.getString(SIMPLE_DATA_KEY), getKey()));
        StringBuilder sb = new StringBuilder();

        if (isDeveExibirPerguntasTipoConta()) {
            ArrayList<String> selections = mData.getStringArrayList(SECOND_DATA_KEY);
            if (selections != null && selections.size() > 0) {
                for (String selection : selections) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(selection);
                }
            }

            dest.add(new ReviewItem(getTitle(), sb.toString(), getKey()));
        }
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public MixedChoicePage setChoicesResidencia(String... choices) {
        tiposResidencia.addAll(Arrays.asList(choices));
        return this;
    }

    public MixedChoicePage setChoicesTiposEntrega(String... choices) {
        tiposEntrega.addAll(Arrays.asList(choices));
        return this;
    }

    public MixedChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    public MixedChoicePage setValueTipoEntrega(String value) {
        mData.putString(SECOND_DATA_KEY, value);
        return this;
    }

    public boolean isDeveExibirPerguntasTipoConta() {
        return deveExibirPerguntasTipoConta;
    }
}
