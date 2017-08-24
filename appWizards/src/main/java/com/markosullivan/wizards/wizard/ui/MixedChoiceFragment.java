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

package com.markosullivan.wizards.wizard.ui;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import com.markosullivan.wizards.R;
import com.markosullivan.wizards.wizard.model.MixedNotaServicoChoicePage;
import com.markosullivan.wizards.wizard.model.Page;

import java.util.ArrayList;
import java.util.List;

import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;

public class MixedChoiceFragment extends ListFragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mChoiceMedidor;
    private List<String> mChoicesResidencia;
    private String mKey;
    private Page mPage;

    public static MixedChoiceFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        MixedChoiceFragment fragment = new MixedChoiceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public MixedChoiceFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = mCallbacks.onGetPage(mKey);

        MixedNotaServicoChoicePage mixedChoicePage = (MixedNotaServicoChoicePage) mPage;

        mChoicesResidencia = new ArrayList<String>();
        for (int i = 0; i < mixedChoicePage.getOptionCount(); i++) {
            mChoicesResidencia.add(mixedChoicePage.getOptionAt(i));
        }

        mChoiceMedidor = mixedChoicePage.getMedidor();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mixed_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());
        ((TextView) rootView.findViewById(R.id.titulo_medidor)).setText(mChoiceMedidor);

        final CheckBox check = (CheckBox) rootView.findViewById(R.id.check_medidor);
        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                mPage.getData().putString(SECOND_DATA_KEY, mChoiceMedidor);
                mPage.notifyDataChanged();
            }
        });

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                mChoicesResidencia));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
                for (int i = 0; i < mChoicesResidencia.size(); i++) {
                    if (mChoicesResidencia.get(i).equals(selection)) {
                        listView.setItemChecked(i, true);
                        break;
                    }
                }
                String med = mPage.getData().getString(SECOND_DATA_KEY);
                if (med != null && !med.isEmpty()) {
                    check.setChecked(true);
                }
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mPage.getData().putString(Page.SIMPLE_DATA_KEY,
                getListAdapter().getItem(position).toString());
        mPage.notifyDataChanged();
    }
}
