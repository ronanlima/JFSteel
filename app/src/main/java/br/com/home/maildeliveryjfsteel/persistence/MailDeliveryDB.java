package br.com.home.maildeliveryjfsteel.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class MailDeliveryDB extends SQLiteOpenHelper {

    public static final String TAG = MailDeliveryDB.class.getCanonicalName().toUpperCase();
    public static final Integer DB_VERSION = 1;
    public static final String DB_NAME = "jfsteel.sqlite";
    public static final String TABLE_REGISTRO_ENTREGA = "registroEntrega";

    public MailDeliveryDB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_REGISTRO_ENTREGA + " (_id integer primary key autoincrement," +
                "dadosQrCode text, horaEntrega timestamp, prefixAgrupador text, idFoto text, sitSalvoFirebase integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    /**
     * Salva ou atualiza registro no banco de dados sqlite
     *
     * @param item
     * @return
     */
    public long save(ContaNormal item) {
        long id = 0;
        if (item.getId() != null) {
            id = item.getId();
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("dadosQrCode", item.getDadosQrCode());
            values.put("horaEntrega", item.getTimesTamp());
            values.put("prefixAgrupador", item.getPrefixAgrupador());
            values.put("idFoto", item.getIdFoto());
            values.put("sitSalvoFirebase", item.getSitSalvoFirebase());
            if (id != 0) {
                String _id = String.valueOf(id);
                String[] whereArgs = new String[]{_id};
                return db.update(TABLE_REGISTRO_ENTREGA, values, "_id=?", whereArgs);
            } else {
                return db.insert(TABLE_REGISTRO_ENTREGA, null, values);
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
            return 0;
        } finally {
            db.close();
        }
    }

    /**
     * Busca todos os registros para a tabela passada.
     *
     * @param table
     * @return
     */
    public List<ContaNormal> findAll(String table) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(table, null, null, null, null, null, null);
            return toList(c);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Busca os registros com o prefixo passado.
     *
     * @param table
     * @param prefix
     * @return
     */
    public List<ContaNormal> findByAgrupador(String table, String prefix) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(table, null, "prefixAgrupador like '" + prefix + "%'", null, null, null, null);
            return toList(c);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Retorna uma lista de registros
     *
     * @param c
     * @return
     */
    private List<ContaNormal> toList(Cursor c) {
        List<ContaNormal> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                ContaNormal r = new ContaNormal();
                r.setId(c.getLong(c.getColumnIndex("_id")));
                r.setDadosQrCode(c.getString(c.getColumnIndex("dadosQrCode")));
                r.setTimesTamp(c.getLong(c.getColumnIndex("horaEntrega")));
                r.setPrefixAgrupador(c.getString(c.getColumnIndex("prefixAgrupador")));
                r.setIdFoto(c.getString(c.getColumnIndex("idFoto")));
                r.setSitSalvoFirebase(c.getType(c.getColumnIndex("sitSalvoFirebase")));
                list.add(r);
            } while (c.moveToNext());
        }
        return list;
    }

    /**
     * Executa um sql qualquer passado
     *
     * @param sql
     * @param args
     */
    public void execSql(String sql, Object[] args) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.execSQL(sql, args);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            db.close();
        }
    }
}
