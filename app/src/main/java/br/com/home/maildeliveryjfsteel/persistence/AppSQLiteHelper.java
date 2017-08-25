package br.com.home.maildeliveryjfsteel.persistence;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import br.com.home.maildeliveryjfsteel.R;

/**
 * Created by Ronan.lima on 25/08/17.
 */

public class AppSQLiteHelper extends SQLiteOpenHelper {
    public static final String TAG = AppSQLiteHelper.class.getCanonicalName().toUpperCase();

    String DB_NAME = "jfsteel.sqlite";
    Integer DB_VERSION = 1;
    private Context mContext;

    public AppSQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        InputStream createTables = mContext.getResources().openRawResource(R.raw.create_tables);

        try {
            AppSQLiteHelper.manageDB(createTables, db);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } catch (SQLException e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public static void manageDB(InputStream stream, SQLiteDatabase db) throws SQLException, IOException {
        InputStreamReader is = new InputStreamReader(stream);
        BufferedReader in = new BufferedReader(is);
        String str;
        while ((str = in.readLine()) != null) {
            if (!str.trim().equals("")) {
                db.execSQL(str);
            }
        }
        in.close();
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
