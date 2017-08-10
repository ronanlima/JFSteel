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

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.dto.NoQrCode;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class MailDeliveryNoQrCode extends SQLiteOpenHelper implements MailDeliverDBService<NoQrCode> {

    public static final String TAG = MailDeliveryNoQrCode.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "noQrCode";
    protected Context mContext;

    public MailDeliveryNoQrCode(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder("create table if not exists ");
        builder.append(TABLE_REGISTRO_ENTREGA).append(" (_id integer primary key autoincrement, ");
        builder.append(mContext.getResources().getString(R.string.medidor)).append(" integer, ");
        builder.append(mContext.getResources().getString(R.string.hora_entrega)).append(" timestamp, ");
        builder.append(mContext.getResources().getString(R.string.endereco_manual)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.existe_conta)).append(" integer, ");
        builder.append(mContext.getResources().getString(R.string.latitude)).append(" real, ");
        builder.append(mContext.getResources().getString(R.string.longitude)).append(" real, ");
        builder.append(mContext.getResources().getString(R.string.comentario)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.sit_salvo_firebase)).append(" integer)");
        db.execSQL(builder.toString());
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
            values.put(mContext.getResources().getString(R.string.medidor), item.getMedidor());
            values.put(mContext.getResources().getString(R.string.hora_entrega), item.getTimesTamp());
            values.put(mContext.getResources().getString(R.string.endereco_manual), item.getEnderecoManual());
            values.put(mContext.getResources().getString(R.string.existe_conta), item.getExisteConta());
            values.put(mContext.getResources().getString(R.string.latitude), item.getLatitude());
            values.put(mContext.getResources().getString(R.string.longitude), item.getLongitude());
            values.put(mContext.getResources().getString(R.string.comentario), item.getComentario());
            values.put(mContext.getResources().getString(R.string.sit_salvo_firebase), item.getSitSalvoFirebase());
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
        return null;
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
        return null;
    }

    /**
     * Recuperar os registros conforme situacao passada.
     *
     * @param situacao
     * @return
     */
    @Override
    public List<NoQrCode> findBySit(int situacao) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(TABLE_REGISTRO_ENTREGA, null, mContext.getResources().getString(R.string.sit_salvo_firebase) + " = " + situacao + "", null, null, null, null);
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
                r.setMedidor(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.medidor))));
                r.setTimesTamp(c.getLong(c.getColumnIndex(mContext.getResources().getString(R.string.hora_entrega))));
                r.setEnderecoManual(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.endereco_manual))));
                r.setExisteConta(c.getInt(c.getColumnIndex(mContext.getResources().getString(R.string.existe_conta))));
                r.setLatitude(c.getDouble(c.getColumnIndex(mContext.getResources().getString(R.string.latitude))));
                r.setLongitude(c.getDouble(c.getColumnIndex(mContext.getResources().getString(R.string.longitude))));
                r.setComentario(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.comentario))));
                r.setSitSalvoFirebase(c.getType(c.getColumnIndex(mContext.getResources().getString(R.string.sit_salvo_firebase))));
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
