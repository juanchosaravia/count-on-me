package com.droidcba.countonme.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import org.jetbrains.anko.db.*

/**
 *
 * @author juancho.
 */
class CountOnMeDbHelper : ManagedSQLiteOpenHelper {

    private constructor(ctx: Context) : super(ctx, "countonme.db", null, 1)

    companion object {
        private var instance: CountOnMeDbHelper? = null

        fun getInstance(ctx: Context): CountOnMeDbHelper {
            if (instance == null) {
                instance = CountOnMeDbHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(DbSchema.TABLE_GROUP_NAME, true,
                BaseColumns._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                DbSchema.GroupColumns.DESC to TEXT + NOT_NULL)

        db.createTable(DbSchema.TABLE_ITEM_NAME, true,
                BaseColumns._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                DbSchema.ItemColumns.GROUP_ID to INTEGER + NOT_NULL,
                DbSchema.ItemColumns.DESC to TEXT + NOT_NULL)

        db.createTable(DbSchema.TABLE_COUNTS_NAME, true,
                BaseColumns._ID to INTEGER + PRIMARY_KEY + AUTOINCREMENT,
                DbSchema.CountsColumns.ITEM_ID to INTEGER + NOT_NULL,
                DbSchema.CountsColumns.COUNT to INTEGER + NOT_NULL,
                DbSchema.CountsColumns.YEAR to INTEGER + NOT_NULL,
                DbSchema.CountsColumns.MONTH to INTEGER + NOT_NULL,
                DbSchema.CountsColumns.DAY to INTEGER + NOT_NULL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
    }
}