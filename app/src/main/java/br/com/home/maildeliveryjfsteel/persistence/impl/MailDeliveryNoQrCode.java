package br.com.home.maildeliveryjfsteel.persistence.impl;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.List;

import br.com.home.maildeliveryjfsteel.R;
import br.com.home.maildeliveryjfsteel.persistence.MailDeliverDBService;
import br.com.home.maildeliveryjfsteel.persistence.ManagerVersionsDB;
import br.com.home.maildeliveryjfsteel.persistence.dto.NoQrCode;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class MailDeliveryNoQrCode implements MailDeliverDBService<NoQrCode> {

    public static final String TAG = MailDeliveryNoQrCode.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "noQrCode";
    protected Context mContext;

    public MailDeliveryNoQrCode(Context context) {
        this.mContext = context;
    }

    @Override
    public long save(NoQrCode item) {
        long id = 0;
        if (item.getId() != null) {
            id = item.getId();
        }

        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getWritableDatabase();
        try {

            if (id != 0) {
                String _id = String.valueOf(id);
                String[] whereArgs = new String[]{_id};
                int qtdUpdate = db.update(TABLE_REGISTRO_ENTREGA, item.getValuesInsert(mContext), "_id=?", whereArgs);
                return qtdUpdate;
            } else {
                long idInserted = db.insert(TABLE_REGISTRO_ENTREGA, null, item.getValuesInsert(mContext));
                return idInserted;
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
     * @return
     */
    @Override
    public List<NoQrCode> findAll() {
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getWritableDatabase();

        try {
            Cursor c = db.query(TABLE_REGISTRO_ENTREGA, null, null, null, null, null, null);
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
     * @param prefix
     * @return
     */
    @Override
    public List<NoQrCode> findByAgrupador(String prefix) {
        return null;
    }

    /**
     * Busca os registros com o qrcode passado.
     *
     * @param qrCode
     * @return
     */
    @Override
    public List<NoQrCode> findByQrCode(String qrCode) {
        return null;
    }

    /**
     * Como esse tipo de registro não possui qr code, a chamada é repassada para o método findBySit.
     *
     * @param qrCode
     * @param sitFirebase
     * @return
     */
    @Override
    public List<NoQrCode> findByQrCodeAndSit(String qrCode, int sitFirebase) {
        return findBySit(sitFirebase);
    }

    /**
     * Recuperar os registros conforme situacao passada.
     *
     * @param situacao
     * @return
     */
    @Override
    public List<NoQrCode> findBySit(int situacao) {
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getReadableDatabase();

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
                r.setLocalEntregaCorresp(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.local_entrega_corresp))));
                r.setExisteConta(c.getInt(c.getColumnIndex(mContext.getResources().getString(R.string.existe_conta))));
                r.setLatitude(c.getDouble(c.getColumnIndex(mContext.getResources().getString(R.string.latitude))));
                r.setLongitude(c.getDouble(c.getColumnIndex(mContext.getResources().getString(R.string.longitude))));
                r.setComentario(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.comentario))));
                r.setSitSalvoFirebase(c.getInt(c.getColumnIndex(mContext.getResources().getString(R.string.sit_salvo_firebase))));
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
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getWritableDatabase();

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
