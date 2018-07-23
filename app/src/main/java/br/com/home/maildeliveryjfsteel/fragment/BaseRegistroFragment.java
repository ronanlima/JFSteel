package br.com.home.maildeliveryjfsteel.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseRegistroFragment<T> extends Fragment {
    public static final String ARGUMENT_LAYOUT_RES_ID = "layoutResId";
    protected SQLiteDatabase db;
    private int layoutResId;
    private RecyclerView mRecyclerView;
//    private


    public BaseRegistroFragment() {
    }

    public BaseRegistroFragment(int layoutResId) {
        this.layoutResId = layoutResId;
    }

    public static BaseRegistroFragment newInstance(int layoutResId) {
        BaseRegistroFragment f = new BaseRegistroFragment();
        Bundle b = new Bundle();
        b.putInt(ARGUMENT_LAYOUT_RES_ID, layoutResId);

        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        layoutResId = getArguments().getInt(ARGUMENT_LAYOUT_RES_ID);

        View v = inflater.inflate(layoutResId, container, false);
        return v;
    }
}
