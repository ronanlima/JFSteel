package br.com.home.maildeliveryjfsteel.persistence.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.dto.NoQrCode;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class MailDeliveryNoQrCode extends SQLiteOpenHelper implements MailDeliverDBService<NoQrCode> {

    public static final String TAG = MailDeliveryNoQrCode.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "noQrCode";

    public MailDeliveryNoQrCode(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_REGISTRO_ENTREGA + " (_id integer primary key autoincrement," +
                "medidor text, horaEntrega timestamp, endereco text, existeConta integer, latitude real, " +
                "longitude real, comentario integer, enderecoManual text, sitSalvoFirebase integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }

    @Override
    public long save(NoQrCode item) {
        long id = 0;
        if (item.getId() != null) {
            id = item.getId();
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put("medidor", item.getMedidor());
            values.put("horaEntrega", item.getTimesTamp());
            values.put("enderecoManual", item.getEnderecoManual());
            values.put("latitude", item.getLatitude());
            values.put("longitude", item.getLongitude());
            values.put("existeConta", item.getExisteConta());
            values.put("comentario", item.getComentario());
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
    @Override
    public List<NoQrCode> findAll(String table) {
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
    @Override
    public List<NoQrCode> findByAgrupador(String table, String prefix) {
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
     * Busca os registros com o qrcode passado.
     *
     * @param table
     * @param qrCode
     * @return
     */
    @Override
    public List<NoQrCode> findByQrCode(String table, String qrCode) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(table, null, "dadosQrCode = " + qrCode + "", null, null, null, null);
            return toList(c);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    /**
     * Recuperar os registros conforme situacao passada.
     * @param situacao
     * @return
     */
    @Override
    public List<NoQrCode> findBySit(int situacao) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(TABLE_REGISTRO_ENTREGA, null, "sitSalvoFirebase = " + situacao + "", null, null, null, null);
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
    @Override
    public List<NoQrCode> toList(Cursor c) {
        List<NoQrCode> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                NoQrCode r = new NoQrCode();
                r.setId(c.getLong(c.getColumnIndex("_id")));
                r.setTimesTamp(c.getLong(c.getColumnIndex("horaEntrega")));
                r.setPrefixAgrupador(c.getString(c.getColumnIndex("prefixAgrupador")));
                r.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
                r.setLongitude(c.getDouble(c.getColumnIndex("longitude")));
                r.setEnderecoManual(c.getString(c.getColumnIndex("enderecoManual")));
                r.setExisteConta(c.getInt(c.getColumnIndex("existeConta")));
                r.setMedidor(c.getString(c.getColumnIndex("medidor")));
                r.setComentario(c.getString(c.getColumnIndex("comentario")));
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
    @Override
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

    @Override
    public String getTable() {
        return TABLE_REGISTRO_ENTREGA;
    }
}
