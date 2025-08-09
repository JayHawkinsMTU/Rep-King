package dev.jayhawkins.repking.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase?) {
        TODO("Not yet implemented")
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        TODO("Not yet implemented")
    }

    private fun createTables(db: SQLiteDatabase?) {
        // GUID stored as a hyphenated string
        // User is an entity
        val createUsersTable = """
            CREATE TABLE users(
                user_id VARCHAR(36) PRIMARY KEY
            )
        """.trimIndent()
        db?.execSQL(createUsersTable)
        // Name of user stored using "Mutable Property" pattern
        val createUserNamesTable = """
            CREATE TABLE user_names(
                user_name_id VARCHAR(36) PRIMARY KEY,
                name VARCHAR(32) NOT NULL,
                prior_user_name_id VARCHAR(36),
                user_id VARCHAR(36) NOT NULL,
                FOREIGN KEY (user_id) REFERENCES users(user_id)
            )
        """.trimIndent()
        db?.execSQL(createUserNamesTable)


    }

    companion object {
        const val DATABASE_NAME = "rk_user.db"
        const val DATABASE_VERSION = 1
    }
}
