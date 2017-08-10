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
import br.com.home.maildeliveryjfsteel.persistence.TipoResidencia;
import br.com.home.maildeliveryjfsteel.persistence.dto.Nota;

/**
 * Created by Ronan.lima on 10/08/17.
 */

public class MailDeliveryDBNotaServico extends SQLiteOpenHelper implements MailDeliverDBService<Nota> {

    public static final String TAG = MailDeliveryDBNotaServico.class.getCanonicalName().toUpperCase();
    public static final String TABLE_REGISTRO_ENTREGA = "notaServico";

    public MailDeliveryDBNotaServico(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + TABLE_REGISTRO_ENTREGA + " (_id integer primary key autoincrement," +
                "dadosQrCode text, horaEntrega timestamp, prefixAgrupador text, idFoto text, latitude real, " +
                "longitude real, uriFotoDisp text, urlStorageFoto text, enderecoManual text, leitura text, " +
                "medidorVisivel String, medidorExterno integer, tipoResidencia integer, sitSalvoFirebase integer)");
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
            values.put("dadosQrCode", item.getDadosQrCode());
            values.put("horaEntrega", item.getTimesTamp());
            values.put("prefixAgrupador", item.getPrefixAgrupador());
            String nomeFoto = item.getIdFoto().substring(0, item.getIdFoto().length() - 5);
            values.put("idFoto", nomeFoto);
            values.put("latitude", item.getLatitude());
            values.put("longitude", item.getLongitude());
            values.put("uriFotoDisp", item.getUriFotoDisp());
            values.put("urlStorageFoto", item.getUrlStorageFoto());
            values.put("enderecoManual", item.getEnderecoManual());
            values.put("leitura", item.getLeitura());
            values.put("medidorVisivel", item.getMedidorVisivel());
            values.put("medidorExterno", item.getMedidorExterno());
            values.put("tipoResidencia", item.getTipoResidencia().ordinal());
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
    public List<Nota> findByQrCode(String table, String qrCode) {
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
     *
     * @param situacao
     * @return
     */
    @Override
    public List<Nota> findBySit(int situacao) {
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
    public List<Nota> toList(Cursor c) {
        List<Nota> list = new ArrayList<>();
        if (c.moveToFirst()) {
            do {
                Nota r = new Nota();
                r.setId(c.getLong(c.getColumnIndex("_id")));
                r.setDadosQrCode(c.getString(c.getColumnIndex("dadosQrCode")));
                r.setTimesTamp(c.getLong(c.getColumnIndex("horaEntrega")));
                r.setPrefixAgrupador(c.getString(c.getColumnIndex("prefixAgrupador")));
                r.setIdFoto(c.getString(c.getColumnIndex("idFoto")));
                r.setLatitude(c.getDouble(c.getColumnIndex("latitude")));
                r.setLongitude(c.getDouble(c.getColumnIndex("longitude")));
                r.setUriFotoDisp(c.getString(c.getColumnIndex("uriFotoDisp")));
                r.setUrlStorageFoto(c.getString(c.getColumnIndex("urlStorageFoto")));
                r.setEnderecoManual(c.getString(c.getColumnIndex("enderecoManual")));
                r.setLeitura(c.getString(c.getColumnIndex("leitura")));
                r.setMedidorVisivel(c.getString(c.getColumnIndex("medidorVisivel")));
                r.setMedidorExterno(c.getInt(c.getColumnIndex("medidorExterno")));
                r.setTipoResidencia(TipoResidencia.getByIndex(c.getInt(c.getColumnIndex("tipoResidencia"))));
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
