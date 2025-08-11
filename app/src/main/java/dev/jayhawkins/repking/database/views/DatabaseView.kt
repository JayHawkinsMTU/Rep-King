package dev.jayhawkins.repking.database.views

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase

abstract class DatabaseView {
    /**
     * Query executed to produce view of database entity
     */
    abstract val viewQuery: String

    /**
     * Selection arguments for query. Sanitized. Replaces '?'s in query
     */
    var selectionArgs: Array<String> = emptyArray()

    /**
     * Applies data from a SQL query to the view. Call in constructor.
     *
     * @property db A readable database
     */
    fun execQuery(db: SQLiteDatabase) {
        loadView(db.rawQuery(viewQuery, selectionArgs))
    }

    /**
     * Uses query results to apply data to view
     *
     * @property cur Cursor viewing "viewQuery" results
     */
    abstract fun loadView(cur: Cursor)
}