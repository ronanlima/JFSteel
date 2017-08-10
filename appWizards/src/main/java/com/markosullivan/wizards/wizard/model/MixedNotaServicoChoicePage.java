/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.markosullivan.wizards.wizard.model;

import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.markosullivan.wizards.wizard.ui.MixedChoiceFragment;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * A page offering the user a number of mutually exclusive choices.
 */
public class MixedNotaServicoChoicePage extends Page {
    public static final String SECOND_DATA_KEY = "x";
    protected String medidor;
    protected ArrayList<String> tiposResidencia = new ArrayList<String>();

    public MixedNotaServicoChoicePage(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return MixedChoiceFragment.create(getKey());
    }

    public String getOptionAt(int position) {
        return tiposResidencia.get(position);
    }

    public int getOptionCount() {
        return tiposResidencia.size();
    }

    public String getMedidor() {
        return this.medidor;
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Tipo de residência", mData.getString(SIMPLE_DATA_KEY), getKey()));
        String medidor = mData.getString(SECOND_DATA_KEY);
        dest.add(new ReviewItem("Medidor externo", (medidor != null && !medidor.isEmpty()) ? "Sim" : "Não" , getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(SIMPLE_DATA_KEY));
    }

    public MixedNotaServicoChoicePage setChoicesResidencia(String... choices) {
        tiposResidencia.addAll(Arrays.asList(choices));
        return this;
    }

    public MixedNotaServicoChoicePage setChoiceMedidor(String choice) {
        medidor = choice;
        return this;
    }

    public MixedNotaServicoChoicePage setValue(String value) {
        mData.putString(SIMPLE_DATA_KEY, value);
        return this;
    }

    public MixedNotaServicoChoicePage setValueMedidor(String value) {
        mData.putString(SECOND_DATA_KEY, value);
        return this;
    }
}
