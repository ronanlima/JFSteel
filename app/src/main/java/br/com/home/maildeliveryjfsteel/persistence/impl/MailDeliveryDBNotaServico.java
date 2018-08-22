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
import br.com.home.maildeliveryjfsteel.persistence.dto.NotaServico;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class MailDeliveryDBNotaServico implements MailDeliverDBService<NotaServico> {

    public static final String TAG = MailDeliveryDBNotaServico.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "notaServico";
    protected Context mContext;

    public MailDeliveryDBNotaServico(Context context) {
        this.mContext = context;
    }

    /**
     * Salva ou atualiza registro no banco de dados sqlite
     *
     * @param item
     * @return
     */
    @Override
    public long save(NotaServico item) {
        long id = 0;
        if (item.getId() != null) {
            id = item.getId();
        }

        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getWritableDatabase();
        try {
            if (id != 0) {
                String _id = String.valueOf(id);
                String[] whereArgs = new String[]{_id};
                return db.update(TABLE_REGISTRO_ENTREGA, item.getValuesInsert(mContext), "_id=?", whereArgs);
            } else {
                return db.insert(TABLE_REGISTRO_ENTREGA, null, item.getValuesInsert(mContext));
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
    public List<NotaServico> findAll() {
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getReadableDatabase();

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
    public List<NotaServico> findByAgrupador(String prefix) {
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getReadableDatabase();

        try {
            Cursor c = db.query(TABLE_REGISTRO_ENTREGA, null, mContext.getResources().getString(R.string.prefix_agrupador) + " like '" + prefix + "%'", null, null, null, null);
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
     * @param qrCode
     * @return
     */
    @Override
    public List<NotaServico> findByQrCode(String qrCode) {
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getReadableDatabase();

        try {
            Cursor c = db.query(TABLE_REGISTRO_ENTREGA, null, mContext.getResources().getString(R.string.dados_qr_code) + " = " + qrCode + "", null, null, null, null);
            return toList(c);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } finally {
            db.close();
        }
    }

    @Override
    public List<NotaServico> findByQrCodeAndSit(String qrCode, int sitFirebase) {
        SQLiteDatabase db = new ManagerVersionsDB(mContext, DB_NAME, null, DB_VERSION).getReadableDatabase();

        try {
            StringBuilder clause = new StringBuilder();
            clause.append(mContext.getResources().getString(R.string.dados_qr_code)).append(" = '");
            clause.append(qrCode).append("' and ");
            clause.append(mContext.getResources().getString(R.string.sit_salvo_firebase));
            clause.append(" = ").append(sitFirebase);
            Cursor c = db.query(TABLE_REGISTRO_ENTREGA, null, clause.toString(), null, null, null, null);
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
     *
     * @param situacao
     * @return
     */
    @Override
    public List<NotaServico> findBySit(int situacao) {
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
    public List<NotaServico> toList(Cursor c) {
        List<NotaServico> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                NotaServico r = new NotaServico();
                r.setId(c.getLong(c.getColumnIndex("_id")));
                r.setDadosQrCode(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.dados_qr_code))));
                r.setTimesTamp(c.getLong(c.getColumnIndex(mContext.getResources().getString(R.string.hora_entrega))));
                r.setPrefixAgrupador(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.prefix_agrupador))));
                r.setIdFoto(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.id_foto))));
                r.setLatitude(c.getDouble(c.getColumnIndex(mContext.getResources().getString(R.string.latitude))));
                r.setLongitude(c.getDouble(c.getColumnIndex(mContext.getResources().getString(R.string.longitude))));
                r.setUriFotoDisp(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.uri_foto_disp))));
                r.setUrlStorageFoto(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.url_storage_foto))));
                r.setEnderecoManual(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.endereco_manual))));
                r.setLeitura(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.leitura))));
                r.setMedidorVizinho(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.medidor_vizinho))));
                r.setMedidorExterno(c.getInt(c.getColumnIndex(mContext.getResources().getString(R.string.medidor_externo))) == 1 ? "Sim" : "Não");
                r.setLocalEntregaCorresp(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.local_entrega_corresp))));
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
