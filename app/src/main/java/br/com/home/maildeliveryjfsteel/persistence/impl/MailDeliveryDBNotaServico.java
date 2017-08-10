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
import br.com.home.maildeliveryjfsteel.persistence.TipoResidencia;
import br.com.home.maildeliveryjfsteel.persistence.dto.Nota;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class MailDeliveryDBNotaServico extends SQLiteOpenHelper implements MailDeliverDBService<Nota> {

    public static final String TAG = MailDeliveryDBNotaServico.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "notaServico";
    protected Context mContext;

    public MailDeliveryDBNotaServico(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        StringBuilder builder = new StringBuilder("create table if not exists ");
        builder.append(TABLE_REGISTRO_ENTREGA).append(" (_id integer primary key autoincrement, ");
        builder.append(mContext.getResources().getString(R.string.dados_qr_code)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.hora_entrega)).append(" timestamp, ");
        builder.append(mContext.getResources().getString(R.string.prefix_agrupador)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.id_foto)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.latitude)).append(" real, ");
        builder.append(mContext.getResources().getString(R.string.longitude)).append(" real, ");
        builder.append(mContext.getResources().getString(R.string.uri_foto_disp)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.url_storage_foto)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.endereco_manual)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.leitura)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.medidor_visivel)).append(" text, ");
        builder.append(mContext.getResources().getString(R.string.medidor_externo)).append(" integer, ");
        builder.append(mContext.getResources().getString(R.string.tipo_residencia)).append(" integer, ");
        builder.append(mContext.getResources().getString(R.string.sit_salvo_firebase)).append(" integer)");
        db.execSQL(builder.toString());
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
    public long save(Nota item) {
        long id = 0;
        if (item.getId() != null) {
            id = item.getId();
        }

        SQLiteDatabase db = getWritableDatabase();
        try {
            ContentValues values = new ContentValues();
            values.put(mContext.getResources().getString(R.string.dados_qr_code), item.getDadosQrCode());
            values.put(mContext.getResources().getString(R.string.hora_entrega), item.getTimesTamp());
            values.put(mContext.getResources().getString(R.string.prefix_agrupador), item.getPrefixAgrupador());
            String nomeFoto = item.getIdFoto().substring(0, item.getIdFoto().length() - 5);
            values.put(mContext.getResources().getString(R.string.id_foto), nomeFoto);
            values.put(mContext.getResources().getString(R.string.latitude), item.getLatitude());
            values.put(mContext.getResources().getString(R.string.longitude), item.getLongitude());
            values.put(mContext.getResources().getString(R.string.uri_foto_disp), item.getUriFotoDisp());
            values.put(mContext.getResources().getString(R.string.url_storage_foto), item.getUrlStorageFoto());
            values.put(mContext.getResources().getString(R.string.endereco_manual), item.getEnderecoManual());
            values.put(mContext.getResources().getString(R.string.leitura), item.getLeitura());
            values.put(mContext.getResources().getString(R.string.medidor_visivel), item.getMedidorVisivel());
            values.put(mContext.getResources().getString(R.string.medidor_externo), item.getMedidorExterno());
            values.put(mContext.getResources().getString(R.string.tipo_residencia), item.getTipoResidencia().ordinal());
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
    public List<Nota> findAll(String table) {
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
    public List<Nota> findByAgrupador(String table, String prefix) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(table, null, mContext.getResources().getString(R.string.prefix_agrupador) + " like '" + prefix + "%'", null, null, null, null);
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
    public List<Nota> findByQrCode(String table, String qrCode) {
        SQLiteDatabase db = getReadableDatabase();

        try {
            Cursor c = db.query(table, null, mContext.getResources().getString(R.string.dados_qr_code) + " = " + qrCode + "", null, null, null, null);
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
    public List<Nota> findBySit(int situacao) {
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
    public List<Nota> toList(Cursor c) {
        List<Nota> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Nota r = new Nota();
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
                r.setMedidorVisivel(c.getString(c.getColumnIndex(mContext.getResources().getString(R.string.medidor_visivel))));
                r.setMedidorExterno(c.getInt(c.getColumnIndex(mContext.getResources().getString(R.string.medidor_externo))));
                r.setTipoResidencia(TipoResidencia.getByIndex(c.getInt(c.getColumnIndex(mContext.getResources().getString(R.string.tipo_residencia)))));
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
