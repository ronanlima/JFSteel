package br.com.home.maildeliveryjfsteel.persistence;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
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
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Atualizando tabela da versão " + oldVersion + " para " + newVersion);
        try {
            for (int i = oldVersion; i < newVersion; i++) {
                String migration = String.format("from_%d_to_%d.sql", i, (i + 1));
                Log.d(TAG, "Procurando pelo arquivo de migração: " + migration);
                manageDB(mContext.getResources().openRawResource(R.raw.from_1_to_2), db);
//                readAndExecuteSQLScript(db, mContext, migration);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception executando o script de atualização: " + e);
        }
    }

    private void readAndExecuteSQLScript(SQLiteDatabase db, Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            Log.d(TAG, "O arquivo SQL está vazio");
            return;
        }

        Log.d(TAG, "Script encontrado. Executando...");
        AssetManager assetManager = context.getAssets();
        BufferedReader reader = null;

        try {
            InputStream is = assetManager.open(fileName);
            InputStreamReader isr = new InputStreamReader(is);
            reader = new BufferedReader(isr);
            executeSQLScript(db, reader);
        } catch (IOException e) {
            Log.e(TAG, "IOException: ", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "IOException: ", e);
                }
            }
        }
    }

    private void executeSQLScript(SQLiteDatabase db, BufferedReader reader) throws IOException {
        String line;
        StringBuilder statement = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            statement.append(line);
            statement.append("\n");
            if (line.endsWith(";")) {
                db.execSQL(statement.toString());
                statement = new StringBuilder();
            }
        }
    }

}
