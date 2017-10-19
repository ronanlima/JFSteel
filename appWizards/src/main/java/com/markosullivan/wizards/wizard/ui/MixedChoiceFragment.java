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
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.markosullivan.wizards.R;
import com.markosullivan.wizards.wizard.model.MixedChoicePage;
import com.markosullivan.wizards.wizard.model.Page;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;

public class MixedChoiceFragment extends Fragment {
    private static final String ARG_KEY = "key";
    private static final String ARG_BOOLEAN = "question";

    private PageFragmentCallbacks mCallbacks;
    private List<String> mChoicesEntrega;
    private List<String> mChoicesTiposEntrega;
    private String mKey;
    private boolean mBoolean;
    private Page mPage;
    private ListAdapter listAdapterEntrega, listAdapterTipoEntrega;

    public static MixedChoiceFragment create(String key, boolean deveExibirPerguntasTipoConta) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);
        args.putBoolean(ARG_BOOLEAN, deveExibirPerguntasTipoConta);

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
        mBoolean = args.getBoolean(ARG_BOOLEAN);
        mPage = mCallbacks.onGetPage(mKey);

        MixedChoicePage mixedChoicePage = (MixedChoicePage) mPage;

        mChoicesEntrega = new ArrayList<String>();
        for (int i = 0; i < mixedChoicePage.getOptionResidenciaCount(); i++) {
            mChoicesEntrega.add(mixedChoicePage.getOptionResidenciaAt(i));
        }

        if (mBoolean) {
            mChoicesTiposEntrega = new ArrayList<String>();
            for (int i = 0; i < mixedChoicePage.getOptionTipoEntregaCount(); i++) {
                mChoicesTiposEntrega.add(mixedChoicePage.getOptionTipoEntregaAt(i));
            }
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mixed_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        final ListView listView = (ListView) rootView.findViewById(android.R.id.list);
        listAdapterEntrega = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_single_choice,
                android.R.id.text1,
                mChoicesEntrega);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(listAdapterEntrega);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPage.getData().putString(Page.SIMPLE_DATA_KEY,
                        listAdapterEntrega.getItem(position).toString());
                mPage.notifyDataChanged();
            }
        });

        final ListView listViewTipoEntrega = (ListView) rootView.findViewById(R.id.list_tipo_entrega);
        if (mBoolean) {
            listAdapterTipoEntrega = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_multiple_choice,
                    android.R.id.text1,
                    mChoicesTiposEntrega);
            listViewTipoEntrega.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
            listViewTipoEntrega.setAdapter(listAdapterTipoEntrega);

            listViewTipoEntrega.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    SparseBooleanArray checkedPositions = ((ListView) parent).getCheckedItemPositions();
                    ArrayList<String> selections = new ArrayList<String>();
                    for (int i = 0; i < checkedPositions.size(); i++) {
                        if (checkedPositions.valueAt(i)) {
                            selections.add(listAdapterTipoEntrega.getItem(checkedPositions.keyAt(i)).toString());
                        }
                    }

                    mPage.getData().putStringArrayList(SECOND_DATA_KEY, selections);
                    mPage.notifyDataChanged();
                }
            });
        }

        // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = mPage.getData().getString(Page.SIMPLE_DATA_KEY);
                for (int i = 0; i < mChoicesEntrega.size(); i++) {
                    if (mChoicesEntrega.get(i).equals(selection)) {
                        listView.setItemChecked(i, true);
                        break;
                    }
                }

                if (mBoolean) {
                    ArrayList<String> selectedItems = mPage.getData().getStringArrayList(
                            SECOND_DATA_KEY);
                    if (selectedItems == null || selectedItems.size() == 0) {
                        return;
                    }

                    Set<String> selectedSet = new HashSet<String>(selectedItems);

                    for (int i = 0; i < mChoicesTiposEntrega.size(); i++) {
                        if (selectedSet.contains(mChoicesTiposEntrega.get(i))) {
                            listViewTipoEntrega.setItemChecked(i, true);
                        }
                    }
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
}
