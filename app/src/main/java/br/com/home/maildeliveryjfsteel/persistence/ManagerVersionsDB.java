package br.com.home.maildeliveryjfsteel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by Ronan.lima on 25/08/17.
 */

public class ManagerVersionsDB extends AppSQLiteHelper {
    public ManagerVersionsDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
