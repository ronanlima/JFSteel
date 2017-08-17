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

import com.markosullivan.wizards.wizard.ui.CustomerNotaServicoFragment;

import java.util.ArrayList;

/**
 * A page asking for a name and an email.
 */
public class CustomerPageContaNoQrCode extends SingleFixedChoicePage {
    public static final String LEITURA_DATA_KEY = "leitura";
    public static final String ENDERECO_DATA_KEY = "medidorVizinho";
    public static final String COMENTARIO_DATA_KEY = "medidorVizinho";

    public CustomerPageContaNoQrCode(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public int getOptionCount() {
        return super.getOptionCount();
    }

    @Override
    public String getOptionAt(int position) {
        return super.getOptionAt(position);
    }

    @Override
    public Fragment createFragment() {
        return CustomerNotaServicoFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Leitura do medidor", mData.getString(LEITURA_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Endereço", mData.getString(ENDERECO_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Comentário", mData.getString(COMENTARIO_DATA_KEY), getKey(), -1));

        StringBuilder sb = new StringBuilder();

        ArrayList<String> selections = mData.getStringArrayList(Page.SIMPLE_DATA_KEY);
        if (selections != null && selections.size() > 0) {
            for (String selection : selections) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(selection);
            }
        }

        dest.add(new ReviewItem("Possui conta", sb.toString(), getKey()));
    }

    @Override
    public boolean isCompleted() {
        return !TextUtils.isEmpty(mData.getString(ENDERECO_DATA_KEY));
    }
}