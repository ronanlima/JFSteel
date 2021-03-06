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

import com.markosullivan.wizards.wizard.ui.CustomerNoQrCodeFragment;

import java.util.ArrayList;

import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_COMENTARIO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_ENDERECO_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.EXTRA_LEITURA_DATA_KEY;

/**
 * A page asking for a name and an email.
 */
public class CustomerPageContaNoQrCode extends SingleFixedChoicePage {

    public CustomerPageContaNoQrCode(ModelCallbacks callbacks, String title) {
        super(callbacks, title);
    }

    @Override
    public Fragment createFragment() {
        return CustomerNoQrCodeFragment.create(getKey());
    }

    @Override
    public void getReviewItems(ArrayList<ReviewItem> dest) {
        dest.add(new ReviewItem("Leitura do medidor", mData.getString(EXTRA_LEITURA_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Endereço", mData.getString(EXTRA_ENDERECO_DATA_KEY), getKey(), -1));
        dest.add(new ReviewItem("Comentário", mData.getString(EXTRA_COMENTARIO_DATA_KEY), getKey(), -1));
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
        boolean isMedidorEmpty = TextUtils.isEmpty(mData.getString(EXTRA_LEITURA_DATA_KEY));
        boolean isEnderecoEmpty = TextUtils.isEmpty(mData.getString(EXTRA_ENDERECO_DATA_KEY));

        if (!isMedidorEmpty) {
            return true;
        } else if (isMedidorEmpty && isEnderecoEmpty) {
            return false;
        } else if (isMedidorEmpty && !isEnderecoEmpty) {
            return true;
        }
        return false;
    }
}
