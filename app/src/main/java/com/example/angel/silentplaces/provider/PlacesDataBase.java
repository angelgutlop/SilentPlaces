package com.example.angel.silentplaces.provider;

import android.database.sqlite.SQLiteDatabase;

import net.simonvt.schematic.annotation.Database;
import net.simonvt.schematic.annotation.OnUpgrade;
import net.simonvt.schematic.annotation.Table;

import timber.log.Timber;

@Database(version = PlacesDataBase.VERSION, fileName = "places.db")

public class PlacesDataBase {

    public static final int VERSION = 2;


    static String[] MIGRATIONS = {
            // Borra columnas
            "CREATE TABLE t_backup(placeID);" +
                    "INSERT INTO t_backup SELECT placeID FROM places;" +
                    "DROP TABLE places;" +
                    "CREATE TABLE places (placeID); " +
                    "INSERT INTO places SELECT placeID FROM t_backup;" +
                    "DROP TABLE t_backup;",
    };


    @Table(PlacesContract.class)
    public static final String PLACES = "places";


    @OnUpgrade
    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            String migration = MIGRATIONS[i - 1];
            db.beginTransaction();
            try {
                executeBatchSQL(db, migration);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Timber.e(e, "Error executing database migration: %s", migration);
                break;
            } finally {
                db.endTransaction();
            }
        }
    }

    private static void executeBatchSQL(SQLiteDatabase database, String sqlQueries) {

        String[] queries = sqlQueries.split(";");

        for (String query : queries) {
            database.execSQL(query);
        }
    }
}
