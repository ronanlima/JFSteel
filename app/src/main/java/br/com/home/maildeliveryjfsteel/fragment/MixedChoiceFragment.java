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

package br.com.home.maildeliveryjfsteel.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.WizardCallback;

import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_CONDOMINIO_PORTARIA;
import static br.com.home.jfsteelbase.ConstantsUtil.FIELD_LOCAL_ENTREGA_RECUSADA;
import static br.com.home.jfsteelbase.ConstantsUtil.SECOND_DATA_KEY;
import static br.com.home.jfsteelbase.ConstantsUtil.SIMPLE_DATA_KEY;

public class MixedChoiceFragment extends WizardFragment {
    private static final String ARG_BOOLEAN = "question";
    public static final String[] optsEntrega = {"Portão", "Embaixo da porta", "Em mãos",
            FIELD_LOCAL_ENTREGA_RECUSADA, FIELD_LOCAL_CONDOMINIO_PORTARIA, "Devolução", "Caixa de correspondência"};
    public static final String[] optsConta = {"Está protocolada", "Coletiva"};
    public static final String TITLE_PAGE_ENTREGA = "Local de entrega";

    private WizardCallback mCallback;

    private List<String> mChoicesEntrega;
    private List<String> mChoicesTiposEntrega;
    private boolean mBoolean;
    private ListAdapter listAdapterEntrega, listAdapterTipoEntrega;

    public static MixedChoiceFragment create(String key, boolean deveExibirPerguntasTipoConta) {
        Bundle args = new Bundle();
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
        mBoolean = args.getBoolean(ARG_BOOLEAN);

        mChoicesEntrega = Arrays.asList(optsEntrega);

        if (mBoolean) {
            mChoicesTiposEntrega = Arrays.asList(optsConta);
        }

        setBundle(new Bundle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mixed_page, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(TITLE_PAGE_ENTREGA);

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
                getBundle().putString(SIMPLE_DATA_KEY,
                        listAdapterEntrega.getItem(position).toString());
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

                    getBundle().putStringArrayList(SECOND_DATA_KEY, selections);
                }
            });
        }

        // Pre-select currently selected item.
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                String selection = getBundle().getString(SIMPLE_DATA_KEY);
                for (int i = 0; i < mChoicesEntrega.size(); i++) {
                    if (mChoicesEntrega.get(i).equals(selection)) {
                        listView.setItemChecked(i, true);
                        break;
                    }
                }

                if (mBoolean) {
                    ArrayList<String> selectedItems = getBundle().getStringArrayList(
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
    public boolean isCompleted() {
        if (getBundle().get(SIMPLE_DATA_KEY) != null && !getBundle().get(SIMPLE_DATA_KEY).toString().isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof WizardCallback)) {
            throw new ClassCastException("Activity must implement WizardCallback");
        }

        mCallback = (WizardCallback) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
}
