package br.com.home.maildeliveryjfsteel.persistence;

import android.database.Cursor;

import java.util.List;

/**
 * Created by Ronan.lima on 28/07/17.
 */

public interface MailDeliverDBService<T> {
    String DB_NAME = "jfsteel.sqlite";
    Integer DB_VERSION = 1;
    Integer SIT_FALSE = 0;
    Integer SIT_TRUE = 1;

    long save(T obj);

    List<T> findAll(String table);

    List<T> findByAgrupador(String table, String prefix);

    List<T> findBySit(int situacao);

    List<T> toList(Cursor c);

    void execSql(String sql, Object[] args);
}
