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
import br.com.home.maildeliveryjfsteel.persistence.dto.ContaNormal;

/**
 * Created by Ronan.lima on 27/07/17.
 */

public class MailDeliveryDBContaNormal extends SQLiteOpenHelper implements MailDeliverDBService<ContaNormal> {

    public static final String TAG = MailDeliveryDBContaNormal.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "registroEntrega";

    public MailDeliveryDBContaNormal(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_REGISTRO_ENTREGA + " (_id integer primary key autoincrement," +
                "dadosQrCode text, horaEntrega timestamp, prefixAgrupador text, idFoto text, sitSalvoFirebase integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // TODO encontrar um jeito de excluir o arquivo sqlite criado até o momento, para não ter que tratar atualização em desenvolvimento sem ter subido nenhuma versão release ainda.
    }

    /**
     * Salva ou atualiza registro no banco de dados sqlite
     *
     * @param item
     * @return
     */
    @Override
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
            String nomeFoto = item.getIdFoto().substring(0, item.getIdFoto().length() - 5);
            values.put("idFoto", nomeFoto);
            values.put("latitude", item.getLatitude());
            values.put("longitude", item.getLongitude());
            values.put("uriFotoDisp", item.getUriFotoDisp());
            values.put("urlStorageFoto", item.getUrlStorageFoto());
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
    @Override
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
    @Override
    public List<ContaNormal> toList(Cursor c) {
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
}
